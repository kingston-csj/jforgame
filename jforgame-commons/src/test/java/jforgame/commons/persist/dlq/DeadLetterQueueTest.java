package jforgame.commons.persist.dlq;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.SavingStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DeadLetterQueue}
 */
public class DeadLetterQueueTest {

    private DeadLetterQueue dlq;

    @Before
    public void setUp() {
        dlq = new DeadLetterQueue(3); // max 3 retries
    }

    @Test
    public void testSaveSuccess() {
        String key = "Entity@1";
        dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
        dlq.onSaveSuccess(key);
        assertEquals(0, dlq.getRetryCount(key));
    }

    @Test
    public void testSaveFailureUnderThreshold() {
        String key = "Entity@1";
        // 1st failure
        boolean shouldRetry = dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail1"));
        assertFalse(shouldRetry);
        assertEquals(1, dlq.getRetryCount(key));

        // 2nd failure
        shouldRetry = dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail2"));
        assertFalse(shouldRetry);
        assertEquals(2, dlq.getRetryCount(key));
    }

    @Test
    public void testSaveFailureExceedsThreshold() {
        String key = "Entity@1";
        TestEntity entity = new TestEntity(1);

        // 3 failures to trigger DLQ
        dlq.onSaveFailure(key, entity, new Exception("fail1"));
        dlq.onSaveFailure(key, entity, new Exception("fail2"));
        boolean shouldMoveToDLQ = dlq.onSaveFailure(key, entity, new Exception("fail3"));

        assertTrue(shouldMoveToDLQ);
        assertTrue(dlq.isDead(key));
        assertEquals(1, dlq.getDeadLetterCount());
    }

    @Test
    public void testRetryForeverMode() {
        DeadLetterQueue unlimitedDlq = new DeadLetterQueue(0); // 0 means retry forever
        String key = "Entity@1";

        for (int i = 0; i < 100; i++) {
            boolean shouldMove = unlimitedDlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
            assertFalse(shouldMove);
        }
        assertEquals(0, unlimitedDlq.getDeadLetterCount());
    }

    @Test
    public void testGetDeadLetters() {
        String key = "Entity@1";
        TestEntity entity = new TestEntity(1);
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, entity, new Exception("fail"));
        }

        List<DeadLetter> deadLetters = dlq.getDeadLetters();
        assertEquals(1, deadLetters.size());
        assertEquals(key, deadLetters.get(0).getKey());
    }

    @Test
    public void testReprocessSuccess() {
        String key = "Entity@1";
        TestEntity entity = new TestEntity(1);
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, entity, new Exception("fail"));
        }

        SavingStrategy successStrategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> e) throws Exception {
                // Success
            }
        };

        boolean result = dlq.reprocess(key, successStrategy);
        assertTrue(result);
        assertEquals(0, dlq.getDeadLetterCount());
    }

    @Test
    public void testReprocessFailure() {
        String key = "Entity@1";
        TestEntity entity = new TestEntity(1);
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, entity, new Exception("fail"));
        }

        SavingStrategy failStrategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> e) throws Exception {
                throw new Exception("reprocess failed");
            }
        };

        boolean result = dlq.reprocess(key, failStrategy);
        assertFalse(result);
        // Should still be in DLQ
        assertEquals(1, dlq.getDeadLetterCount());
    }

    @Test
    public void testReprocessNonExistentKey() {
        SavingStrategy strategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> e) throws Exception {
            }
        };
        assertFalse(dlq.reprocess("nonexistent", strategy));
    }

    @Test
    public void testBatchReprocess() {
        // Add 2 dead letters
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure("Entity@1", new TestEntity(1), new Exception("fail"));
            dlq.onSaveFailure("Entity@2", new TestEntity(2), new Exception("fail"));
        }
        assertEquals(2, dlq.getDeadLetterCount());

        SavingStrategy successStrategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> e) throws Exception {
            }
        };

        DeadLetterQueue.BatchReprocessResult result = dlq.batchReprocess(successStrategy);
        assertEquals(2, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailCount());
        assertTrue(result.allSuccess());
    }

    @Test
    public void testReset() {
        String key = "Entity@1";
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
        }
        assertEquals(1, dlq.getDeadLetterCount());

        dlq.reset();
        assertEquals(0, dlq.getDeadLetterCount());
        assertEquals(0, dlq.getRetryCount(key));
    }

    @Test
    public void testRemoveDeadLetter() {
        String key = "Entity@1";
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
        }
        assertNotNull(dlq.removeDeadLetter(key));
        assertNull(dlq.removeDeadLetter(key));
    }

    @Test
    public void testDeadLetterListener() {
        AtomicInteger onDeadCount = new AtomicInteger(0);
        AtomicInteger reprocessCount = new AtomicInteger(0);

        dlq.addListener(new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                onDeadCount.incrementAndGet();
            }

            @Override
            public void onDeadLetterReprocessed(DeadLetter deadLetter, boolean success) {
                reprocessCount.incrementAndGet();
            }
        });

        String key = "Entity@1";
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
        }

        assertEquals(1, onDeadCount.get());

        SavingStrategy successStrategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> e) throws Exception {
            }
        };
        dlq.reprocess(key, successStrategy);
        assertEquals(1, reprocessCount.get());
    }

    @Test
    public void testMaxRetryCountGetterSetter() {
        assertEquals(3, dlq.getMaxRetryCount());
        dlq.setMaxRetryCount(5);
        assertEquals(5, dlq.getMaxRetryCount());
    }

    @Test
    public void testToString() {
        String str = dlq.toString();
        assertNotNull(str);
        assertTrue(str.contains("DeadLetterQueue"));
    }

    // Test entity class
    private static class TestEntity implements Entity<Integer> {
        private final Integer id;

        TestEntity(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }
}
