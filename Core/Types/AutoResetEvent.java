/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AutoResetEvent {
    private final Semaphore event;
    private final Integer mutex;

    public AutoResetEvent(boolean signalled) {
        this.event = new Semaphore(signalled ? 1 : 0);
        this.mutex = -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set() {
        Integer n = this.mutex;
        synchronized (n) {
            if (this.event.availablePermits() == 0) {
                this.event.release();
            }
        }
    }

    public void reset() {
        this.event.drainPermits();
    }

    public void waitOne() throws InterruptedException {
        this.event.acquire();
    }

    public boolean waitOne(int timeout, TimeUnit unit) throws InterruptedException {
        return this.event.tryAcquire(timeout, unit);
    }

    public boolean isSignalled() {
        return this.event.availablePermits() > 0;
    }

    public boolean waitOne(int timeout) throws InterruptedException {
        return this.waitOne(timeout, TimeUnit.MILLISECONDS);
    }
}

