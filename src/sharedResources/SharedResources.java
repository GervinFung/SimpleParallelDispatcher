package sharedResources;

import item.ItemType;
import util.RunUtils;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.Queue;

public final class SharedResources {

    //Buffer for storing item generated
    private final Queue<ItemType> itemBuffer;
    //Semaphore for buffer
    private final Semaphore semBuffer;
    //Semaphore for accessing buffer
    //so only 1 thread can access buffer at a time
    private final Semaphore semAccessBuffer;

    public SharedResources() {
        this.itemBuffer = new LinkedList<>();
        this.semBuffer = new Semaphore(RunUtils.MAX_NUM);
        this.semAccessBuffer = new Semaphore(1);
    }

    public void addItemType(final char itemName) {
        this.itemBuffer.add(new ItemType(itemName));
    }

    public boolean isItemBufferEmpty() {
        return this.itemBuffer.isEmpty();
    }

    public ItemType pollItemType() {
        return this.itemBuffer.poll();
    }

    public void acquireSemBuffer() throws InterruptedException {
        this.semBuffer.acquire();
    }
    public void releaseSemBuffer() {
        this.semBuffer.release();
    }

    public void acquireSemAccessBuffer() throws InterruptedException {
        this.semAccessBuffer.acquire();
    }
    public void releaseSemAccessBuffer() {
        this.semAccessBuffer.release();
    }
}