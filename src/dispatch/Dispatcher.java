package dispatch;

import abstractProcess.IRun;
import item.ItemType;
import util.RunUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import java.text.MessageFormat;

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
                    final char bagName = this.bag.get(0).itemName();
                    this.unlimitedBag.add(this.bag);

                    System.out.println(MessageFormat.format("\nBag {0} ({1}) is full", bagName, this.unlimitedBag.size()));
                    System.out.println(MessageFormat.format("Dispatcher {0}: Moving Bag {0} ({1}) to conveyor...", bagName, this.unlimitedBag.size()));

                    this.counter.set(COUNTER_INITIAL_VALUE);
                    this.semBag.release(RunUtils.MAX_NUM);

                    System.out.println(MessageFormat.format("Bag {0} ({1}) is successfully dispatched", bagName, this.unlimitedBag.size()));

                    Thread.sleep(RunUtils.DELAY);
                }
            } catch (final InterruptedException e) {
                //If any thread has interrupted the executing Dispatcher thread,
                //The interrupted status of the Dispatcher thread is cleared when this exception is thrown
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().execute(this::dispatchItem);
    }
}