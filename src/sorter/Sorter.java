package sorter;

import abstractProcess.IRun;
import dispatch.Dispatcher;
import item.ItemType;
import sharedResources.SharedResources;
import util.RunUtils;

import java.util.List;
import java.util.concurrent.Executors;

public final class Sorter implements IRun {

    private final SharedResources sr;
    private final List<Dispatcher> dispatchers;

    public Sorter(final SharedResources sr, final List<Dispatcher> dispatchers) {
        this.sr = sr;
        this.dispatchers = dispatchers;
    }

    private void sortItem() {
        //Prevent sorter from taking empty buffer
        if (this.sr.isItemBufferEmpty()) {
            return;
        }

        final ItemType item = this.sr.pollItemType();
        System.out.println("Sorter: Waiting Item...");
        final char itemName = item.getItemName();

        for (final Dispatcher dispatcher : this.dispatchers) {
            //ascii code for alphabet from A up to N alphabets
            final char item_NAME = (char)(dispatcher.getAsciiAlphabetInt() + RunUtils.MAX_ALPHABET_RANGE);
            if (item_NAME == itemName) {
                this.addToBag(this.sr, this.dispatchers.get(dispatcher.getAsciiAlphabetInt()), item);
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

            System.out.println("Sorter: Item " + item.getItemName() + "(" + dispatcher.getItemCounter() + ")");

        } catch (final InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
            e.printStackTrace();
        }
    }

    private void sortToBag() {
        while (true) {
            try {
                //Sort the item to bag
                this.sortItem();

                //Causes the executing Sorter thread to sleep for the 1 second
                Thread.sleep(RunUtils.DELAY);

                //Release a permit from semBuffer semaphore
                this.sr.releaseSemBuffer();

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