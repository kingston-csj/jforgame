# jforgame-threadmodel

`jforgame-threadmodel` is a thread model module that provides Actor model and keyword-based thread dispatch capabilities, designed specifically for game servers.

## Module Positioning

- Does not感知 socket, session, message 等业务语义 (does not understand business semantics like socket, session, message)
- Only responsible for receiving `Runnable` and executing according to its own model
- Focuses on basic concurrency issues: task queuing, thread execution, shutdown strategy, concurrency conflict avoidance
- Supports hash-based dispatch or Actor scheduling based on key

## Core Concepts

### ThreadModel Interface

`ThreadModel` is the top-level abstraction for thread models, defining three core methods:

```java
public interface ThreadModel {
    void accept(Runnable task);    // Accept new task
    void shutDown();               // Shutdown thread model
    boolean isShutdown();          // Check if shutdown
}
```

### Common Implementations

| Implementation | Use Case |
|----------------|----------|
| `DispatchThreadModel` | Dispatch to fixed worker threads by keyword, suitable for IO-intensive tasks |
| `MonitoredDispatchThreadModel` | Dispatch model with monitoring capability, can detect task timeout |
| `ActorSystem` | Serial task processing through Actor mailbox, reduces thread hot-cold imbalance |

## Actor Model

The Actor model is a concurrent programming model where each Actor has its own mailbox, achieving concurrency control through message passing.

### Core Components

```
ActorSystem (Actor System)
    ├── BaseActor (Actor Instance)
    │     └── Mailbox (Mailbox)
    │           ├── UnboundedMailbox (Unbounded Mailbox)
    │           ├── BoundedMailbox (Bounded Mailbox)
    │           └── PriorityMailbox (Priority Mailbox)
    ├── SharedActor (Shared Actor)
    └── Mail (Message)
          ├── SimpleMail (Simple Message)
          └── PriorityMail (Priority Message)
```

### ActorSystem Usage Example

```java
// Create Actor system
ActorSystem actorSystem = new ActorSystem();

// Create Actor
Actor playerActor = actorSystem.createActor("/player/10001");

// Send message
playerActor.tell(new SimpleMail("login", "ip", "192.168.1.1"));

// Get Actor
Actor actor = actorSystem.getActor("/player/10001");

// Remove Actor (avoid memory leak)
actorSystem.removeActor("/player/10001");

// Shutdown system
actorSystem.shutDown();
```

### Custom Actor

```java
public class PlayerActor extends BaseActor {
    
    @Override
    public void run() {
        // Process messages in mailbox
        super.run();
    }
    
    // Handle specific business logic
    public void onLogin(String ip) {
        System.out.println("Player login from: " + ip);
    }
}
```

### Message Definition

```java
// Simple message
public class LoginMail extends SimpleMail {
    public LoginMail(String ip) {
        super("login", ip);
    }
    
    @Override
    public void action() {
        // Get current Actor
        BaseActor receiver = (BaseActor) getReceiver();
        // Handle login logic
        String ip = (String) getContent()[0];
        ((PlayerActor) receiver).onLogin(ip);
    }
}

// Priority message
public class HighPriorityMail extends PriorityMail {
    public HighPriorityMail(Object... content) {
        super("highPriority", HIGH_PRIORITY, content);
    }
}
```

## Message Dispatch Model

Keyword-based dispatch thread model with a predefined set of worker threads, each thread bound to a task queue.

### DispatchThreadModel Usage Example

```java
// Create dispatch model
DispatchThreadModel model = new DispatchThreadModel();

// Submit task
model.accept(new BaseDispatchTask() {
    @Override
    public void action() {
        // Business logic
    }
});

// Dispatch to specified thread (based on dispatchKey % thread count)
BaseDispatchTask task = new BaseDispatchTask();
task.setDispatchKey(playerId);  // Tasks for the same player will be dispatched to the same thread
model.accept(task);
```

### Monitored Dispatch Model

`MonitoredDispatchThreadModel` adds task timeout monitoring on top of `DispatchThreadModel`:

```java
// Create monitored model, monitor interval 2 seconds, max execution time 1 second
MonitoredDispatchThreadModel model = new MonitoredDispatchThreadModel(
    4,                          // Thread count
    2000,                       // Monitor interval (milliseconds)
    1000                        // Max execution time (milliseconds)
);
```

When task execution times out, thread stack information is automatically printed.
