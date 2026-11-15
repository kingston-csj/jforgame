package jforgame.commons.persist.dlq;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.strategy.SavingStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DeadLetterListener}
 */
public class DeadLetterListenerTest {

    private DeadLetterQueue dlq;

    @Before
    public void setUp() {
        dlq = new DeadLetterQueue(3);
    }

    @Test
    public void testOnDeadLetterCalled() {
        List<DeadLetter> receivedDeadLetters = new ArrayList<>();
        dlq.addListener(new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                receivedDeadLetters.add(deadLetter);
            }
        });

        String key = "TestEntity@1";
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure(key, new TestEntity(1), new Exception("fail"));
        }

        assertEquals(1, receivedDeadLetters.size());
        assertEquals(key, receivedDeadLetters.get(0).getKey());
    }

    @Test
    public void testOnDeadLetterReprocessedDefault() {
        AtomicBoolean called = new AtomicBoolean(false);
        dlq.addListener(new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                // Empty
            }

            @Override
            public void onDeadLetterReprocessed(DeadLetter deadLetter, boolean success) {
                called.set(true);
            }
        });

        // Create a dead letter first
        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure("TestEntity@1", new TestEntity(1), new Exception("fail"));
        }

        SavingStrategy successStrategy = new SavingStrategy() {
            @Override
            public void doSave(Entity<?> entity) throws Exception {
            }
        };
        dlq.reprocess("TestEntity@1", successStrategy);
        assertTrue(called.get());
    }

    @Test
    public void testRemoveListener() {
        List<DeadLetter> receivedDeadLetters = new ArrayList<>();
        DeadLetterListener listener = new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                receivedDeadLetters.add(deadLetter);
            }
        };
        dlq.addListener(listener);
        dlq.removeListener(listener);

        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure("TestEntity@1", new TestEntity(1), new Exception("fail"));
        }

        // Listener was removed, so should be empty
        assertEquals(0, receivedDeadLetters.size());
    }

    @Test
    public void testAddNullListener() {
        // Should not throw
        dlq.addListener(null);
    }

    @Test
    public void testListenerExceptionDoesNotBlock() {
        AtomicBoolean secondListenerCalled = new AtomicBoolean(false);
        dlq.addListener(new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                throw new RuntimeException("listener error");
            }
        });
        dlq.addListener(new DeadLetterListener() {
            @Override
            public void onDeadLetter(DeadLetter deadLetter) {
                secondListenerCalled.set(true);
            }
        });

        for (int i = 0; i < 3; i++) {
            dlq.onSaveFailure("TestEntity@1", new TestEntity(1), new Exception("fail"));
        }

        assertTrue(secondListenerCalled.get());
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
