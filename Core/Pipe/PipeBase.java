/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Pipe;

import HslCommunication.Core.Thread.SimpleHybirdLock;
import java.util.concurrent.atomic.AtomicInteger;

public class PipeBase {
    private SimpleHybirdLock hybirdLock;
    private AtomicInteger lockingTick = new AtomicInteger(0);

    public PipeBase() {
        this.hybirdLock = new SimpleHybirdLock();
    }

    public void PipeLockEnter() {
        this.lockingTick.getAndIncrement();
        this.hybirdLock.Enter();
    }

    public void PipeLockLeave() {
        this.hybirdLock.Leave();
        this.lockingTick.decrementAndGet();
    }

    public int getLockingTick() {
        return this.lockingTick.get();
    }
}

