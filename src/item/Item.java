package item;

import abstractProcess.IRun;
import sharedResources.SharedResources;
import util.RunUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public final class Item implements IRun {

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
                this.sr.acquireSemBuffer();
                //Acquire a permit from semAccessBuffer semaphore
                this.sr.acquireSemAccessBuffer();

                final char itemName = (char)(ThreadLocalRandom.current().nextInt(RunUtils.MAX_ALPHABET_RANGE, RunUtils.MAX_ALPHABET_RANGE + itemNameRange));

                //Add item into buffer
                this.sr.addItemType(itemName);
                System.out.println("\nArrival: Item " + itemName);

                //Release a permit from semAccessBuffer semaphore
                this.sr.releaseSemAccessBuffer();

                Thread.sleep(RunUtils.DELAY);

            } catch (final InterruptedException e) {
                //If any thread has interrupted the executing Item thread,
                //The interrupted status of the Item thread is cleared when this exception is thrown
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().execute(this::storeInBuffer);
    }
}