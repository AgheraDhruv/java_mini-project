package service;

/**
 * TransactionBuffer — bounded shared array buffer coordinating producers
 * and consumers using wait() and notifyAll() inside synchronized blocks.
 */
public class TransactionBuffer {

    private final String[] buffer;
    private int count = 0;
    private int putIndex = 0;
    private int takeIndex = 0;

    public TransactionBuffer(int capacity) {
        this.buffer = new String[capacity];
    }

    /**
     * Inserts an item into the buffer. Blocks via wait() if buffer is full.
     */
    public synchronized void put(String transaction) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[putIndex] = transaction;
        putIndex = (putIndex + 1) % buffer.length;
        count++;
        notifyAll();
    }

    /**
     * Removes an item from the buffer. Blocks via wait() if buffer is empty.
     */
    public synchronized String take() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        String item = buffer[takeIndex];
        takeIndex = (takeIndex + 1) % buffer.length;
        count--;
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return count;
    }
}
