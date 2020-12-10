package app;

import java.util.concurrent.ThreadLocalRandom;

public final class Item extends Thread {
    private final int itemNameRange;
    private final SharedResources sr;

    public Item(final SharedResources sr, final int itemNameRange) {
        this.sr = sr;
        this.itemNameRange = itemNameRange;
    }

    private void storeInBuffer() {
        while (true) {
            try {
                //Acquire a permit from semBuffer semaphore
                this.sr.getSemBuffer().acquire();
                //Acquire a permit from semAccessBuffer semaphore
                this.sr.getSemAccessBuffer().acquire();

                final char itemName = (char)(ThreadLocalRandom.current().nextInt(65, 65 + itemNameRange));

                //Add item into buffer
                this.sr.getItemBuffer().add(new ItemType(itemName));
                System.out.println("\nArrival: Item " + itemName);

                //Release a permit from semAccessBuffer semaphore
                this.sr.getSemAccessBuffer().release();

                Item.sleep(1000);

            } catch (final InterruptedException e) {
                //If any thread has interrupted the executing Item thread,
                //The interrupted status of the Item thread is cleared when this exception is thrown
                System.out.println(e);
            }
        }
    }

    @Override
    public void run() {
        this.storeInBuffer();
    }
}