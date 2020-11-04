package app;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

//Class for type of item generated
final class ItemType {
    //Item's name
    String itemName;
    public ItemType(final String itemName) {
        this.itemName = itemName;
    }
}

final class SharedResources {

    //store number of dispatchers
    protected static void setNumberOfDispatcher(final int n) {
        //instantiate number of dispatcher
        sr = new SharedResources[n];
    }

    private static SharedResources[] sr;
    protected static SharedResources getResource(final int index) {
        return sr[index];
    }

    //Buffer for storing item generated
    private static final Queue<ItemType> itemBuffer = new LinkedList<>();
    protected static void addItemToBuffer(final ItemType item) {
        itemBuffer.add(item);
    }
    protected static Queue<ItemType> getItemBuffer () {
        return itemBuffer;
    }

    //Linkedlist to store item in bag
    private final List<ItemType> bag = new LinkedList<>();
    protected void addIntoBag(final ItemType item) {
        this.bag.add(item);
    }
    protected List<ItemType> getBag() {
        return this.bag;
    }

    //LinkedList for storing unlimited bag
    private final List<List<ItemType>> unlimitedBag = new LinkedList<>();
    protected List<List<ItemType>> getUnLimitedBag() {
        return this.unlimitedBag;
    }

    //Semaphore for bag
    private final Semaphore sem = new Semaphore(10);
    protected Semaphore getSemaphoreForBag() {
        return this.sem;
    }

    //Semaphore for buffer
    protected static final Semaphore semBuffer = new Semaphore(10);
    //Semaphore for accessing buffer
    //so only 1 thread can access buffer at a time
    protected static final Semaphore semAccessBuffer = new Semaphore(1);

    protected static void instantiateResources() {
        for (int i = 0; i < sr.length; i++)
            sr[i] = new SharedResources();
    }
}

final class Item extends Thread {

    private final Random randomNumber = new Random();

    private String allItemName;

    public Item(final String allItemName) {
        this.allItemName = allItemName;
    }
    //Random generate item
    private String generateItem() {
        final char c = this.allItemName.toCharArray()[this.randomNumber.nextInt(this.allItemName.toCharArray().length)];
        return Character.toString(c);
    }

    private void storeInBuffer() {
        while (true) {
            try {
                //Acquire a permit from semBuffer semaphore
                SharedResources.semBuffer.acquire();
                //Acquire a permit from semAccessBuffer semaphore
                SharedResources.semAccessBuffer.acquire();

                final String itemName = generateItem();

                //Add item into buffer
                SharedResources.addItemToBuffer(new ItemType(itemName));
                System.out.println("\nArrival: Item " + itemName);

                //Release a permit from semAccessBuffer semaphore
                SharedResources.semAccessBuffer.release();

                //Causes the executing Item thread to sleep for the 1 second
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                //If any thread has interrupted the executing Item thread,
                //The interrupted status of the Item thread is cleared when this exception is thrown
                System.out.println(e);//print out error
            }
        }
    }

    @Override
    public void run() {
        //Item thread will invoke this method when it starts
        this.storeInBuffer();
    }
}

final class Sorter extends Thread {

    //get Number of dispatchers
    private int numberOfDispatcher;

    public Sorter(final int numberOfDispatcher) {
        this.numberOfDispatcher = numberOfDispatcher;
    }

    private void sortItem() {
        //Prevent sorter from taking empty buffer
        if (!SharedResources.getItemBuffer().isEmpty()) {
            //Remove the first item from buffer
            final ItemType item = SharedResources.getItemBuffer().poll();
            System.out.println("Sorter: Waiting Item...");
            final String itemName = item.itemName;
            for (int i = 0; i < numberOfDispatcher; i++) {
                //ascii code for alphabet from A up to N alphabets
                final String item_NAME = Character.toString((char)(i + 65));
                if (item_NAME.equals(itemName)) {
                    addToBag(SharedResources.getResource(i).getSemaphoreForBag(), SharedResources.getResource(i).getBag(), item, SharedResources.semAccessBuffer);
                    break;
                }
            }
        }
    }

    private static void addToBag(final Semaphore semBuffer, final List<ItemType> list, final ItemType item, final Semaphore semAccessBuffer) {
        try {
            //Acquire a permit from parameter semBuffer
            semBuffer.acquire();
            //Acquire a permit from parameter semAccessBuffer
            semAccessBuffer.acquire();

            //Add item into bag
            list.add(item);
            //Release a permit from parameter semAccessBuffer
            semAccessBuffer.release();

            System.out.println("Sorter: Item " + item.itemName + "(" + list.size() + ")");

        } catch (InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
            System.out.println(e);//print out error
        }
    }

    private void sortToBag() {
        while (true) {
            try {
                //Sort the item to bag
                this.sortItem();

                //Causes the executing Sorter thread to sleep for the 1 second
                Thread.sleep(1000);

                //Release a permit from semBuffer semaphore
                SharedResources.semBuffer.release();

            } catch (InterruptedException e) {
            //If any thread has interrupted the executing Sorter thread,
            //The interrupted status of the Sorter thread is cleared when this exception is thrown
                System.out.println(e);//print out error
            }
        }
    }

    @Override
    public void run() {
         //Sorter thread will invoke this method when it starts
         this.sortToBag();
    }
}

//DispatcherA and DispatcherB will use this class
final class Dispatcher extends Thread{

    //Linkedlist to store item in bag
    private List <ItemType> bag = new LinkedList<>();
    //LinkedList to store all thr bags
    private List<List<ItemType>> unlimitedBag = new LinkedList<>();
    //Semaphore for bag
    private Semaphore semBag = new Semaphore(10);

    public Dispatcher(final List<ItemType> bag, final Semaphore semBag, final List<List<ItemType>> unlimitedBag) {
        this.bag = bag;
        this.semBag = semBag;
        this.unlimitedBag = unlimitedBag;
    }

    private void DispatchItem() {
        while (true) {
            try {
                if ("10".equals(Integer.toString(this.bag.size()))) {
                    final String bagName = this.bag.get(0).itemName;
                    //Add the bag into list so it has unlimited bag
                    this.unlimitedBag.add(this.bag);
                    System.out.println("\nBag " + bagName + "(" + this.unlimitedBag.size() + ") is full");
                    System.out.println("Dispatcher " + bagName +  ": Moving Bag " + bagName + "(" + this.unlimitedBag.size() + ") to conveyor...");
                    //Clear bag (to create new bag to add to unlimitedBag)
                    this.bag.clear();
                    //Release all 10 permits from semaphore sem
                    this.semBag.release(10);
                    System.out.println("Bag " + bagName + "(" + this.unlimitedBag.size() + ") is successfully dispatched");

                    //Causes the executing DispatcherA thread to sleep for the 1 second
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                //If any thread has interrupted the executing Dispatcher thread,
                //The interrupted status of the Dispatcher thread is cleared when this exception is thrown
                System.out.println(e);//print out error
            }
        }
    }

    @Override
    public void run() {
        //Dispatcher thread will invoke this method when it starts
        this.DispatchItem();
    }
}

public final class MyThread {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Once user has enter the number of dispatcher, they can enter CTRL + C to terminate the program");

        final int numberOfDispatcher = inquireNumberOfDispatcher();
        SharedResources.setNumberOfDispatcher(numberOfDispatcher);

        //Create all the item's name
        final String allItemName = getAlphabet(numberOfDispatcher);

        //Create sorterThread
        final Sorter sortItemThread = new Sorter(numberOfDispatcher);
        //Start sorterThread
        sortItemThread.start();

        //Create arrivalItemThread
        final Item arrivalItemThread = new Item(allItemName);
        //Start arrivalItemThread
        arrivalItemThread.start();

        //Create resources
        SharedResources.instantiateResources();
        //Create N dispatchers
        final Dispatcher[] dispatcherThread = new Dispatcher[numberOfDispatcher];

        //Instantiate and start all dispatchers
        for (int i = 0; i < dispatcherThread.length; i++) {
            dispatcherThread[i] = new Dispatcher(SharedResources.getResource(i).getBag(), SharedResources.getResource(i).getSemaphoreForBag(), SharedResources.getResource(i).getUnLimitedBag());
            dispatcherThread[i].start();
        }
        //Create shutDownThread
        final ShutDownThread shutDownThread = new ShutDownThread();
        //Thread will shut down console program by entering CTRL + C
        Runtime.getRuntime().addShutdownHook(shutDownThread);
    }

    //User defined cancel handler, terminate program
    final private static class ShutDownThread extends Thread {
        //By entering CTRL + C, it will terminate the program
        //The output below will be the prove that it worked
        @Override
        public void run() {
            System.out.println("\nPerforming shutdown...");
            System.out.println("All threads are shutdown");
        }
    }

    //Get all item's alphabet
    final private static String getAlphabet(final int n) {
        String x = "";
        //loop from 0 to N to get Alphabet from 65(A) to N+65 Alphabet
        for (int i = 0; i < n; i++)
            x += Character.toString((char)(i + 65));
        return x;
    }

    //Return true if input is integer and range from 1 to 26
    final private static boolean tryParseInt(final String input) {
        try {
            final int integer = Integer.parseInt(input);
            if (integer > 0 && integer <= 26)
                return true;
            else return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Inquire number of dispatcher user would like to have
    final private static int inquireNumberOfDispatcher() {
        Scanner scanner = new Scanner(System.in);
        boolean isInt = false;
        String input = "";
        while (!isInt) {
            System.out.print("Enter integer from 1 to 26 only: ");
            input = scanner.nextLine();
            isInt = tryParseInt(input);
        }
        scanner.close();
        return Integer.parseInt(input);
    }
}