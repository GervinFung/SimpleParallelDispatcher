package app;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public final class Dispatcher extends Thread{
    public Dispatcher() {
        this.bag = new ItemType[10];
        this.semBag = new Semaphore(10);
        this.unlimitedBag = new LinkedList<>();
        this.counter = 0;
    }

    private final ItemType[] bag;
    private final LinkedList<ItemType[]> unlimitedBag;
    private final Semaphore semBag;
    private int counter;

    protected ItemType[] getBag() {
        return this.bag;
    }
    protected Semaphore getSemaphoreForBag() {
        return this.semBag;
    }
    protected void incrementItemCount() {
        this.counter++;
    }
    protected int getItemCounter() {
        return this.counter;
    }

    private void DispatchItem() {
        while (true) {
            try {
                if ("10".equals(Integer.toString(this.counter))) {
                    final String bagName = this.bag[0].getItemName();
                    this.unlimitedBag.add(this.bag);
                    System.out.println("\nBag " + bagName + "(" + this.unlimitedBag.size() + ") is full");
                    System.out.println("Dispatcher " + bagName +  ": Moving Bag " + bagName + "(" + this.unlimitedBag.size() + ") to conveyor...");
                    this.counter = 0;
                    this.semBag.release(10);
                    System.out.println("Bag " + bagName + "(" + this.unlimitedBag.size() + ") is successfully dispatched");
                    Dispatcher.sleep(1000);
                }
            } catch (InterruptedException e) {
                //If any thread has interrupted the executing Dispatcher thread,
                //The interrupted status of the Dispatcher thread is cleared when this exception is thrown
                System.out.println(e);
            }
        }
    }

    @Override
    public void run() {
        this.DispatchItem();
    }
}
