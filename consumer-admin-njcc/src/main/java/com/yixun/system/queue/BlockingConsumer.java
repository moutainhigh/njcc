package com.yixun.system.queue;

public abstract class BlockingConsumer extends QueueConsumer
{
  public void run()
  {
    try
    {
      while (!Thread.currentThread().isInterrupted()) {
        Object message = this.queue.take();
        processMessage(message);
      }
    }
    catch (InterruptedException e) {
    }
    finally {
      clean();
    }
  }

  protected abstract void processMessage(Object paramObject);

  protected abstract void clean();
}