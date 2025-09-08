package jforgame.threadmodel.actor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MailboxConfigTest {

    public static void main(String[] args) {
        new MailboxConfigTest().testAllMailboxTypes();
    }

    public void testAllMailboxTypes() {
        System.out.println("=== 开始测试邮箱配置系统 ===");
        
        ActorThreadModel actorSystem = new ActorThreadModel();
        
        try {
            // 测试1: 有界邮箱
            testBoundedMailbox(actorSystem);
            
            // 测试2: 无界邮箱
            testUnboundedMailbox(actorSystem);
            
            // 测试3: 优先级邮箱
            testPriorityMailbox(actorSystem);
            
            System.out.println("=== 所有测试通过 ===");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            actorSystem.shutDown();
        }
    }

    private void testBoundedMailbox(ActorThreadModel actorSystem) throws InterruptedException {
        System.out.println("\n--- 测试有界邮箱 ---");
        
        CountDownLatch latch = new CountDownLatch(5);
        AtomicInteger counter = new AtomicInteger(0);
        
        // 创建有界邮箱Actor
        ActorProps boundedProps = ActorProps.create("bounded-test")
            .withMailbox(MailboxType.BOUNDED, 10);
        TestActor boundedActor = new TestActor(actorSystem, boundedProps, counter, latch);
        
        // 发送消息
        for (int i = 0; i < 5; i++) {
            boundedActor.tell(new TestMail("bounded-msg-" + i));
        }
        
        // 等待处理完成
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        if (completed && counter.get() == 5) {
            System.out.println("✓ 有界邮箱测试通过，处理了 " + counter.get() + " 条消息");
        } else {
            throw new RuntimeException("有界邮箱测试失败");
        }
    }

    private void testUnboundedMailbox(ActorThreadModel actorSystem) throws InterruptedException {
        System.out.println("\n--- 测试无界邮箱 ---");
        
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger counter = new AtomicInteger(0);
        
        // 创建无界邮箱Actor
        ActorProps unboundedProps = ActorProps.create("unbounded-test")
            .withMailbox(MailboxType.UNBOUNDED);
        TestActor unboundedActor = new TestActor(actorSystem, unboundedProps, counter, latch);
        
        // 发送大量消息
        for (int i = 0; i < 10; i++) {
            unboundedActor.tell(new TestMail("unbounded-msg-" + i));
        }
        
        // 等待处理完成
        boolean completed = latch.await(3, TimeUnit.SECONDS);
        if (completed && counter.get() == 10) {
            System.out.println("✓ 无界邮箱测试通过，处理了 " + counter.get() + " 条消息");
        } else {
            throw new RuntimeException("无界邮箱测试失败");
        }
    }

    private void testPriorityMailbox(ActorThreadModel actorSystem) throws InterruptedException {
        System.out.println("\n--- 测试优先级邮箱 ---");
        
        CountDownLatch latch = new CountDownLatch(5);
        StringBuilder processOrder = new StringBuilder();
        
        // 创建优先级邮箱Actor
        ActorProps priorityProps = ActorProps.create("priority-test")
            .withMailbox(MailboxType.PRIORITY, 20);
        PriorityTestActor priorityActor = new PriorityTestActor(actorSystem, priorityProps, processOrder, latch);
        
        // 发送不同优先级的消息（故意乱序发送）
        priorityActor.tell(new PriorityTestMail("low", 1));
        priorityActor.tell(new PriorityTestMail("high", 10));
        priorityActor.tell(new PriorityTestMail("medium", 5));
        priorityActor.tell(new PriorityTestMail("highest", 20));
        priorityActor.tell(new PriorityTestMail("lowest", 0));
        
        // 等待处理完成
        boolean completed = latch.await(3, TimeUnit.SECONDS);
        String result = processOrder.toString();
        
        if (completed && result.contains("highest") && result.indexOf("highest") < result.indexOf("low")) {
            System.out.println("✓ 优先级邮箱测试通过，处理顺序: " + result);
        } else {
            throw new RuntimeException("优先级邮箱测试失败，处理顺序: " + result);
        }
    }

    // 测试Actor类
    class TestActor extends AbsActor {
        private final AtomicInteger counter;
        private final CountDownLatch latch;

        public TestActor(ActorThreadModel actorSystem, ActorProps props, AtomicInteger counter, CountDownLatch latch) {
            super(actorSystem, props);
            this.counter = counter;
            this.latch = latch;
        }

        @Override
        public void receive(Mail mail) {
            if (mail instanceof TestMail) {
                TestMail testMail = (TestMail) mail;
                counter.incrementAndGet();
                System.out.println("  处理消息: " + testMail.getMessage());
                latch.countDown();
            }
        }
    }

    // 优先级测试Actor类
    class PriorityTestActor extends AbsActor {
        private final StringBuilder processOrder;
        private final CountDownLatch latch;

        public PriorityTestActor(ActorThreadModel actorSystem, ActorProps props, StringBuilder processOrder, CountDownLatch latch) {
            super(actorSystem, props);
            this.processOrder = processOrder;
            this.latch = latch;
        }

        @Override
        public void receive(Mail mail) {
            if (mail instanceof PriorityTestMail) {
                PriorityTestMail priorityMail = (PriorityTestMail) mail;
                if (processOrder.length() > 0) {
                    processOrder.append(" -> ");
                }
                processOrder.append(priorityMail.getMessage()).append("(").append(priorityMail.getPriority()).append(")");
                System.out.println("  处理优先级消息: " + priorityMail.getMessage() + " (优先级: " + priorityMail.getPriority() + ")");
                latch.countDown();
            }
        }
    }

    // 测试邮件类
    class TestMail extends Mail {
        private final String message;

        public TestMail(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public void action() {
            // 空实现
        }
    }

    // 优先级测试邮件类
    class PriorityTestMail extends Mail implements PriorityMail {
        private final String message;
        private final int priority;

        public PriorityTestMail(String message, int priority) {
            this.message = message;
            this.priority = priority;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public void action() {
            // 空实现
        }
    }
}
