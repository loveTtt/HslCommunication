/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleHybirdLock {
    private Lock queueLock = new ReentrantLock();

    public void Enter() {
        this.queueLock.lock();
    }

    public void Leave() {
        this.queueLock.unlock();
    }
}

