package dispatch;

import abstractProcess.IRun;
import item.ItemType;
import sharedResources.SharedResources;
import util.RunUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public final class Dispatcher implements IRun {

    private final static int COUNTER_INITIAL_VALUE = 0;

    private final List<ItemType> bag;
    private final LinkedList<List<ItemType>> unlimitedBag;
    private final Semaphore semBag;
    private final AtomicInteger counter;
    private final int asciiAlphabetInt;

    public Dispatcher(final int asciiAlphabetInt) {
        this.bag = Arrays.asList(new ItemType[RunUtils.MAX_NUM]);
        this.semBag = new Semaphore(RunUtils.MAX_NUM);
        this.unlimitedBag = new LinkedList<>();
        this.counter = new AtomicInteger(COUNTER_INITIAL_VALUE);
        this.asciiAlphabetInt = asciiAlphabetInt;
    }

    public void addToBag (final int index, final ItemType itemType) {
        this.bag.set(index, itemType);
    }
    public void acquireSemForBag() throws InterruptedException{
        this.semBag.acquire();
    }
    public void incrementItemCount() {
        this.counter.getAndIncrement();
    }
    public int getItemCounter() {
        return this.counter.get();
    }
    public int getAsciiAlphabetInt() {
        return this.asciiAlphabetInt;
    }

    private void dispatchItem() {
        while (true) {
            try {
                if (RunUtils.MAX_NUM == this.counter.get()) {
                    final char bagName = this.bag.get(0).getItemName();
                    this.unlimitedBag.add(this.bag);

                    System.out.println("\nBag " + bagName + "(" + this.unlimitedBag.size() + ") is full");
                    System.out.println("Dispatcher " + bagName +  ": Moving Bag " + bagName + "(" + this.unlimitedBag.size() + ") to conveyor...");

                    this.counter.set(COUNTER_INITIAL_VALUE);
                    this.semBag.release(RunUtils.MAX_NUM);

                    System.out.println("Bag " + bagName + "(" + this.unlimitedBag.size() + ") is successfully dispatched");

                    Thread.sleep(RunUtils.DELAY);
                }
            } catch (final InterruptedException e) {
                //If any thread has interrupted the executing Dispatcher thread,
                //The interrupted status of the Dispatcher thread is cleared when this exception is thrown
                e.printStackTrace();
            }
        }
    }

    public static void startAllDispatcher(final List<Dispatcher> dispatcherThread) {
        dispatcherThread.forEach(Dispatcher::start);
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().execute(this::dispatchItem);
    }
}