package app;

import java.util.concurrent.Semaphore;

public final class Sorter extends Thread {

    private final int numberOfDispatcher;
    private final SharedResources sr;
    private final Dispatcher[] dispatchers;

    public Sorter(final SharedResources sr, final Dispatcher[] dispatchers) {
        this.sr = sr;
        this.dispatchers = dispatchers;
        this.numberOfDispatcher = this.dispatchers.length;
    }

    private void sortItem() {
        //Prevent sorter from taking empty buffer
        if (!this.sr.getItemBuffer().isEmpty()) {
            //Remove the first item from buffer
            final ItemType item = this.sr.getItemBuffer().poll();
            System.out.println("Sorter: Waiting Item...");
            final char itemName = item.getItemName();
            for (int i = 0; i < this.numberOfDispatcher; i++) {
                //ascii code for alphabet from A up to N alphabets
                final char item_NAME = (char)(i + 65);
                if (item_NAME == itemName) {
                    this.addToBag(this.sr, this.dispatchers[i], item);
                    break;
                }
            }
        }
    }

    private void addToBag(final SharedResources sr, final Dispatcher dispatcher, final ItemType item) {
        final Semaphore semBuffer = dispatcher.getSemaphoreForBag();
        final ItemType[] bag = dispatcher.getBag();
        final Semaphore semAccessBuffer = sr.getSemAccessBuffer();
        try {
            //Acquire a permit from parameter semBuffer
            semBuffer.acquire();
            //Acquire a permit from parameter semAccessBuffer
            semAccessBuffer.acquire();

            //Add item into bag
            final int index = dispatcher.getItemCounter();
            bag[index] = item;
            dispatcher.incrementItemCount();
            //Release a permit from parameter semAccessBuffer
            semAccessBuffer.release();

            System.out.println("Sorter: Item " + item.getItemName() + "(" + dispatcher.getItemCounter() + ")");

        } catch (final InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
            System.out.println(e);
        }
    }

    private void sortToBag() {
        while (true) {
            try {
                //Sort the item to bag
                this.sortItem();

                //Causes the executing Sorter thread to sleep for the 1 second
                Sorter.sleep(1000);

                //Release a permit from semBuffer semaphore
                this.sr.getSemBuffer().release();

            } catch (final InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
                System.out.println(e);
            }
        }
    }

    @Override
    public void run() {
         this.sortToBag();
    }
}