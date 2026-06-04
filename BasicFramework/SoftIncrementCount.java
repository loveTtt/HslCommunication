/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

import HslCommunication.Core.Thread.SimpleHybirdLock;

public class SoftIncrementCount {
    private long start = 0L;
    private long current = 0L;
    private long max = Long.MAX_VALUE;
    private SimpleHybirdLock hybirdLock;
    private int IncreaseTick = 1;

    public SoftIncrementCount(long max) {
        this.max = max;
        this.current = this.start;
        this.hybirdLock = new SimpleHybirdLock();
    }

    public SoftIncrementCount(long max, long start) {
        this.start = start;
        this.max = max;
        this.current = start;
        this.hybirdLock = new SimpleHybirdLock();
    }

    public SoftIncrementCount(long max, long start, int tick) {
        this.start = start;
        this.max = max;
        this.IncreaseTick = tick;
        this.current = start;
        this.hybirdLock = new SimpleHybirdLock();
    }

    public int getIncreaseTick() {
        return this.IncreaseTick;
    }

    public void setIncreaseTick(int increaseTick) {
        this.IncreaseTick = increaseTick;
    }

    public long getMaxValue() {
        return this.max;
    }

    public long GetCurrentValue() {
        long value = 0L;
        this.hybirdLock.Enter();
        value = this.current;
        this.current += (long)this.IncreaseTick;
        if (this.current > this.max) {
            this.current = this.start;
        } else if (this.current < this.start) {
            this.current = this.max;
        }
        this.hybirdLock.Leave();
        return value;
    }

    public void ResetMaxValue(long max) {
        this.hybirdLock.Enter();
        if (max > this.start) {
            if (max < this.current) {
                this.current = this.start;
            }
            this.max = max;
        }
        this.hybirdLock.Leave();
    }

    public void ResetStartValue(long start) {
        this.hybirdLock.Enter();
        if (start < this.max) {
            if (this.current < start) {
                this.current = start;
            }
            this.start = start;
        }
        this.hybirdLock.Leave();
    }

    public void ResetCurrentValue() {
        this.hybirdLock.Enter();
        this.current = this.start;
        this.hybirdLock.Leave();
    }

    public void ResetCurrentValue(long value) {
        this.hybirdLock.Enter();
        this.current = value > this.max ? this.max : (value < this.start ? this.start : value);
        this.hybirdLock.Leave();
    }

    public String toString() {
        return "SoftIncrementCount[" + this.current + ']';
    }
}

