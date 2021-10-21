package sorter;

import abstractProcess.IRun;
import dispatch.Dispatcher;
import item.ItemType;
import sharedResources.SharedResources;
import util.RunUtils;

import java.util.List;
import java.util.concurrent.Executors;

import java.text.MessageFormat;

public final record Sorter(SharedResources sharedResources, List<Dispatcher> dispatchers) implements IRun {

    private void sortItem() {
        //Prevent sorter from taking empty buffer
        if (this.sharedResources.isItemBufferEmpty()) {
            return;
        }

        final ItemType item = this.sharedResources.pollItemType();
        System.out.println("Sorter: Waiting Item...");
        final char itemName = item.itemName();

        for (final Dispatcher dispatcher : this.dispatchers) {
            //ascii code for alphabet from A up to N alphabets
            final char itemNameReceived = (char) (dispatcher.getAsciiAlphabetInt() + RunUtils.MAX_ALPHABET_RANGE);
            if (itemNameReceived == itemName) {
                this.addToBag(this.sharedResources, this.dispatchers.get(dispatcher.getAsciiAlphabetInt()), item);
                return;
            }
        }
    }

    private void addToBag(final SharedResources sr, final Dispatcher dispatcher, final ItemType item) {
        try {
            //Acquire a permit from parameter semBuffer
            dispatcher.acquireSemForBag();
            //Acquire a permit from parameter semAccessBuffer
            sr.acquireSemAccessBuffer();

            //Add item into bag
            final int index = dispatcher.getItemCounter();
            dispatcher.addToBag(index, item);

            dispatcher.incrementItemCount();
            //Release a permit from parameter semAccessBuffer
            sr.releaseSemAccessBuffer();

            System.out.println(MessageFormat.format("Sorter: Item {0} ({1})", item.itemName(), dispatcher.getItemCounter()));

        } catch (final InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
            e.printStackTrace();
        }
    }

    private void sortToBag() {
        this.dispatchers.forEach(Dispatcher::start);
        while (true) {
            try {
                //Sort the item to bag
                this.sortItem();

                //Causes the executing Sorter thread to sleep for the 1 second
                Thread.sleep(RunUtils.DELAY);

                //Release a permit from semBuffer semaphore
                this.sharedResources.releaseSemBuffer();

            } catch (final InterruptedException e) {
                //If any thread has interrupted the executing Sorter thread,
                //The interrupted status of the Sorter thread is cleared when this exception is thrown
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().execute(this::sortToBag);
    }
}