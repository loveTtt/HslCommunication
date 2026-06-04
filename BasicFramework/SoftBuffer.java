/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

import HslCommunication.Core.Thread.SimpleHybirdLock;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.IDataTransfer;

public class SoftBuffer {
    private int capacity = 10;
    private byte[] buffer;
    private SimpleHybirdLock hybirdLock;
    private IByteTransform byteTransform;
    private boolean isBoolReverseByWord = false;

    public SoftBuffer() {
        this.buffer = new byte[this.capacity];
        this.hybirdLock = new SimpleHybirdLock();
        this.byteTransform = new RegularByteTransform();
    }

    public SoftBuffer(int capacity) {
        this.buffer = new byte[capacity];
        this.capacity = capacity;
        this.hybirdLock = new SimpleHybirdLock();
        this.byteTransform = new RegularByteTransform();
    }

    public void SetBool(boolean value, int destIndex) {
        this.SetBool(new boolean[]{value}, destIndex);
    }

    public void SetBool(boolean[] value, int destIndex) {
        if (value != null) {
            try {
                this.hybirdLock.Enter();
                for (int i = 0; i < value.length; ++i) {
                    int byteIndex = (destIndex + i) / 8;
                    int offset = (destIndex + i) % 8;
                    if (this.isBoolReverseByWord) {
                        byteIndex = byteIndex % 2 == 0 ? ++byteIndex : --byteIndex;
                    }
                    this.buffer[byteIndex] = value[i] ? (byte)(this.buffer[byteIndex] | this.getOrByte(offset)) : (byte)(this.buffer[byteIndex] & this.getAndByte(offset));
                }
                this.hybirdLock.Leave();
            }
            catch (Exception ex) {
                this.hybirdLock.Leave();
                throw ex;
            }
        }
    }

    public boolean GetBool(int destIndex) throws IndexOutOfBoundsException {
        return this.GetBool(destIndex, 1)[0];
    }

    public boolean[] GetBool(int destIndex, int length) {
        boolean[] result = new boolean[length];
        try {
            this.hybirdLock.Enter();
            for (int i = 0; i < length; ++i) {
                int byteIndex = (destIndex + i) / 8;
                int offect = (destIndex + i) % 8;
                if (this.isBoolReverseByWord) {
                    byteIndex = byteIndex % 2 == 0 ? ++byteIndex : --byteIndex;
                }
                result[i] = (this.buffer[byteIndex] & this.getOrByte(offect)) == this.getOrByte(offect);
            }
            this.hybirdLock.Leave();
        }
        catch (Exception ex) {
            this.hybirdLock.Leave();
            throw ex;
        }
        return result;
    }

    private byte getAndByte(int offset) {
        switch (offset) {
            case 0: {
                return -2;
            }
            case 1: {
                return -3;
            }
            case 2: {
                return -5;
            }
            case 3: {
                return -9;
            }
            case 4: {
                return -17;
            }
            case 5: {
                return -33;
            }
            case 6: {
                return -65;
            }
            case 7: {
                return 127;
            }
        }
        return -1;
    }

    private byte getOrByte(int offset) {
        switch (offset) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 4;
            }
            case 3: {
                return 8;
            }
            case 4: {
                return 16;
            }
            case 5: {
                return 32;
            }
            case 6: {
                return 64;
            }
            case 7: {
                return -128;
            }
        }
        return 0;
    }

    public void SetBytes(byte[] data, int destIndex) {
        if (destIndex < this.capacity && destIndex >= 0 && data != null) {
            this.hybirdLock.Enter();
            if (data.length + destIndex > this.buffer.length) {
                System.arraycopy(data, 0, this.buffer, destIndex, this.buffer.length - destIndex);
            } else {
                System.arraycopy(data, 0, this.buffer, destIndex, data.length);
            }
            this.hybirdLock.Leave();
        }
    }

    public void SetBytes(byte[] data, int destIndex, int length) {
        if (destIndex < this.capacity && destIndex >= 0 && data != null) {
            if (length > data.length) {
                length = data.length;
            }
            this.hybirdLock.Enter();
            if (length + destIndex > this.buffer.length) {
                System.arraycopy(data, 0, this.buffer, destIndex, this.buffer.length - destIndex);
            } else {
                System.arraycopy(data, 0, this.buffer, destIndex, length);
            }
            this.hybirdLock.Leave();
        }
    }

    public void SetBytes(byte[] data, int sourceIndex, int destIndex, int length) {
        if (destIndex < this.capacity && destIndex >= 0 && data != null) {
            if (length > data.length) {
                length = data.length;
            }
            this.hybirdLock.Enter();
            System.arraycopy(data, sourceIndex, this.buffer, destIndex, length);
            this.hybirdLock.Leave();
        }
    }

    public byte[] GetBytes(int index, int length) {
        byte[] result = new byte[length];
        if (length > 0) {
            this.hybirdLock.Enter();
            if (index >= 0 && index + length <= this.buffer.length) {
                System.arraycopy(this.buffer, index, result, 0, length);
            }
            this.hybirdLock.Leave();
        }
        return result;
    }

    public byte[] GetBytes() {
        return this.GetBytes(0, this.capacity);
    }

    public void SetValue(byte value, int index) {
        this.SetBytes(new byte[]{value}, index);
    }

    public void SetValue(short[] values, int index) {
        this.SetBytes(this.byteTransform.TransByte(values), index);
    }

    public void SetValue(short value, int index) {
        this.SetValue(new short[]{value}, index);
    }

    public void SetValue(int[] values, int index) {
        this.SetBytes(this.byteTransform.TransByte(values), index);
    }

    public void SetValue(int value, int index) {
        this.SetValue(new int[]{value}, index);
    }

    public void SetValue(float[] values, int index) {
        this.SetBytes(this.byteTransform.TransByte(values), index);
    }

    public void SetValue(float value, int index) {
        this.SetValue(new float[]{value}, index);
    }

    public void SetValue(long[] values, int index) {
        this.SetBytes(this.byteTransform.TransByte(values), index);
    }

    public void SetValue(long value, int index) {
        this.SetValue(new long[]{value}, index);
    }

    public void SetValue(double[] values, int index) {
        this.SetBytes(this.byteTransform.TransByte(values), index);
    }

    public void SetValue(double value, int index) {
        this.SetValue(new double[]{value}, index);
    }

    public byte GetByte(int index) {
        return this.GetBytes(index, 1)[0];
    }

    public short[] GetInt16(int index, int length) {
        byte[] tmp = this.GetBytes(index, length * 2);
        return this.byteTransform.TransInt16(tmp, 0, length);
    }

    public short GetInt16(int index) {
        return this.GetInt16(index, 1)[0];
    }

    public int[] GetInt32(int index, int length) {
        byte[] tmp = this.GetBytes(index, length * 4);
        return this.byteTransform.TransInt32(tmp, 0, length);
    }

    public int GetInt32(int index) {
        return this.GetInt32(index, 1)[0];
    }

    public float[] GetSingle(int index, int length) {
        byte[] tmp = this.GetBytes(index, length * 4);
        return this.byteTransform.TransSingle(tmp, 0, length);
    }

    public float GetSingle(int index) {
        return this.GetSingle(index, 1)[0];
    }

    public long[] GetInt64(int index, int length) {
        byte[] tmp = this.GetBytes(index, length * 8);
        return this.byteTransform.TransInt64(tmp, 0, length);
    }

    public long GetInt64(int index) {
        return this.GetInt64(index, 1)[0];
    }

    public double[] GetDouble(int index, int length) {
        byte[] tmp = this.GetBytes(index, length * 8);
        return this.byteTransform.TransDouble(tmp, 0, length);
    }

    public double GetDouble(int index) {
        return this.GetDouble(index, 1)[0];
    }

    public <T extends IDataTransfer> T GetCustomer(Class<T> type, int index) throws InstantiationException, IllegalAccessException {
        IDataTransfer Content = (IDataTransfer)type.newInstance();
        byte[] read = this.GetBytes(index, Content.getReadCount());
        Content.ParseSource(read);
        return (T)Content;
    }

    public <T extends IDataTransfer> void SetCustomer(T data, int index) {
        this.SetBytes(data.ToSource(), index);
    }

    public IByteTransform getByteTransform() {
        return this.byteTransform;
    }

    public void setByteTransform(IByteTransform byteTransform) {
        this.byteTransform = byteTransform;
    }

    public boolean getIsBoolReverseByWord() {
        return this.isBoolReverseByWord;
    }

    public void setIsBoolReverseByWord(boolean value) {
        this.isBoolReverseByWord = value;
    }

    public String toString() {
        return "SoftBuffer[" + this.capacity + "][" + this.byteTransform.toString() + "]";
    }
}

