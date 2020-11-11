package app;

import java.util.Random;

public final class Item extends Thread {

    private final Random randomNumber;
    private final String allItemName;
    private final SharedResources sr;

    public Item(final SharedResources sr, final String allItemName) {
        this.sr = sr;
        this.allItemName = allItemName;
        this.randomNumber = new Random();
    }

    private String generateItem() {
        final char c = this.allItemName.toCharArray()[this.randomNumber.nextInt(this.allItemName.toCharArray().length)];
        return Character.toString(c);
    }

    private void storeInBuffer() {
        while (true) {
            try {
                //Acquire a permit from semBuffer semaphore
                this.sr.getSemBuffer().acquire();
                //Acquire a permit from semAccessBuffer semaphore
                this.sr.getSemAccessBuffer().acquire();

                final String itemName = generateItem();

                //Add item into buffer
                this.sr.getItemBuffer().add(new ItemType(itemName));
                System.out.println("\nArrival: Item " + itemName);

                //Release a permit from semAccessBuffer semaphore
                this.sr.getSemAccessBuffer().release();

                Item.sleep(1000);

            } catch (InterruptedException e) {
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

