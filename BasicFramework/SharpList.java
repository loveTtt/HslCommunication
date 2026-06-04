/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

import HslCommunication.Core.Thread.SimpleHybirdLock;
import java.lang.reflect.Array;

public class SharpList<T> {
    private Class<T> typeClass;
    private T[] array;
    private int capacity = 2048;
    private int count = 0;
    private int lastIndex = 0;
    private SimpleHybirdLock hybirdLock;

    public SharpList(Class<T> type, int count, boolean appendLast) {
        if (count > 8192) {
            this.capacity = 4096;
        }
        this.typeClass = type;
        this.array = (T[])Array.newInstance(type, this.capacity + count);
        this.hybirdLock = new SimpleHybirdLock();
        this.count = count;
        if (appendLast) {
            this.lastIndex = count;
        }
    }

    public int getCount() {
        return this.count;
    }

    public void AddValue(T value) {
        this.hybirdLock.Enter();
        if (this.lastIndex >= this.capacity + this.count) {
            T[] buffer = (T[])Array.newInstance(this.typeClass, this.capacity + this.count);
            System.arraycopy(this.array, this.capacity, buffer, 0, this.count);
            this.array = buffer;
            this.lastIndex = this.count;
        }
        this.array[this.lastIndex++] = value;
        this.hybirdLock.Leave();
    }

    public void Add(Iterable<T> values) {
        for (T value : values) {
            this.AddValue(value);
        }
    }

    public T[] ToArray() {
        T[] result = null;
        this.hybirdLock.Enter();
        if (this.lastIndex < this.count) {
            result = (T[])Array.newInstance(this.typeClass, this.lastIndex);
            System.arraycopy(this.array, 0, result, 0, this.lastIndex);
        } else {
            result = (T[])Array.newInstance(this.typeClass, this.count);
            System.arraycopy(this.array, this.lastIndex - this.count, result, 0, this.count);
        }
        this.hybirdLock.Leave();
        return result;
    }

    public T getByIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must larger than zero");
        }
        if (index >= this.count) {
            throw new IndexOutOfBoundsException("Index must smaller than array length");
        }
        T tmp = null;
        this.hybirdLock.Enter();
        tmp = this.lastIndex < this.count ? (T)this.array[index] : (T)this.array[index + this.lastIndex - this.count];
        this.hybirdLock.Leave();
        return tmp;
    }

    public T LastValue() {
        T result = null;
        this.hybirdLock.Enter();
        try {
            if (this.lastIndex - 1 < this.count + this.capacity) {
                result = this.array[this.lastIndex - 1];
            }
            this.hybirdLock.Leave();
        }
        catch (Exception ex) {
            this.hybirdLock.Leave();
            throw ex;
        }
        return result;
    }

    public void setByIndex(int index, T value) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must larger than zero");
        }
        if (index >= this.count) {
            throw new IndexOutOfBoundsException("Index must smaller than array length");
        }
        this.hybirdLock.Enter();
        if (this.lastIndex < this.count) {
            this.array[index] = value;
        } else {
            this.array[index + this.lastIndex - this.count] = value;
        }
        this.hybirdLock.Leave();
    }
}

