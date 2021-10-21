package item;

import abstractProcess.IRun;
import sharedResources.SharedResources;
import util.RunUtils;

import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public final record Item(SharedResources sharedResources, int itemNameRange) implements IRun {

    private void storeInBuffer() {
        while (true) {
            try {
                //Acquire a permit from semBuffer semaphore
                this.sharedResources.acquireSemBuffer();
                //Acquire a permit from semAccessBuffer semaphore
                this.sharedResources.acquireSemAccessBuffer();

                final char itemName = (char) (ThreadLocalRandom.current().nextInt(RunUtils.MAX_ALPHABET_RANGE, RunUtils.MAX_ALPHABET_RANGE + itemNameRange));

                //Add item into buffer
                this.sharedResources.addItemType(itemName);

                System.out.println(MessageFormat.format("\nArrival: Item {0}", itemName));

                //Release a permit from semAccessBuffer semaphore
                this.sharedResources.releaseSemAccessBuffer();

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