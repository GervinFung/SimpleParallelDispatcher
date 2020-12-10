package app;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.Queue;

public final class SharedResources {
    //Buffer for storing item generated
    private final Queue<ItemType> itemBuffer = new LinkedList<>();
    //Semaphore for buffer
    private final Semaphore semBuffer = new Semaphore(10);
    //Semaphore for accessing buffer
    //so only 1 thread can access buffer at a time
    private final Semaphore semAccessBuffer = new Semaphore(1);

    protected Queue<ItemType> getItemBuffer () {
        return this.itemBuffer;
    }
    protected Semaphore getSemBuffer() {
        return this.semBuffer;
    }
    protected Semaphore getSemAccessBuffer() {
        return this.semAccessBuffer;
    }
}