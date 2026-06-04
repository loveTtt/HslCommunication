/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;
import java.nio.charset.Charset;

public class ByteTransformBase
implements IByteTransform {
    private DataFormat dataFormat = DataFormat.ABCD;
    private boolean IsStringReverse = false;

    public ByteTransformBase() {
        this.dataFormat = DataFormat.DCBA;
    }

    public ByteTransformBase(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Override
    public boolean TransBool(byte[] buffer, int index) {
        return SoftBasic.BoolOnByteIndex(buffer[index / 8], index % 8);
    }

    @Override
    public boolean[] TransBool(byte[] buffer, int index, int length) {
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; ++i) {
            result[i] = this.TransBool(buffer, i + index);
        }
        return result;
    }

    @Override
    public byte TransByte(byte[] buffer, int index) {
        return buffer[index];
    }

    @Override
    public byte[] TransByte(byte[] buffer, int index, int length) {
        byte[] tmp = new byte[length];
        System.arraycopy(buffer, index, tmp, 0, length);
        return tmp;
    }

    @Override
    public short TransInt16(byte[] buffer, int index) {
        return Utilities.getShort(buffer, index);
    }

    @Override
    public short[] TransInt16(byte[] buffer, int index, int length) {
        short[] tmp = new short[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransInt16(buffer, index + 2 * i);
        }
        return tmp;
    }

    @Override
    public int TransUInt16(byte[] buffer, int index) {
        return Utilities.getUShort(buffer, index);
    }

    @Override
    public int[] TransUInt16(byte[] buffer, int index, int length) {
        int[] tmp = new int[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransUInt16(buffer, index + 2 * i);
        }
        return tmp;
    }

    @Override
    public int TransInt32(byte[] buffer, int index) {
        return Utilities.getInt(this.ByteTransDataFormat4(buffer, index), 0);
    }

    @Override
    public int[] TransInt32(byte[] buffer, int index, int length) {
        int[] tmp = new int[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransInt32(buffer, index + 4 * i);
        }
        return tmp;
    }

    @Override
    public long TransUInt32(byte[] buffer, int index) {
        return Utilities.getUInt(this.ByteTransDataFormat4(buffer, index), 0);
    }

    @Override
    public long[] TransUInt32(byte[] buffer, int index, int length) {
        long[] tmp = new long[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransUInt32(buffer, index + 4 * i);
        }
        return tmp;
    }

    @Override
    public long TransInt64(byte[] buffer, int index) {
        return Utilities.getLong(this.ByteTransDataFormat8(buffer, index), 0);
    }

    @Override
    public long[] TransInt64(byte[] buffer, int index, int length) {
        long[] tmp = new long[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransInt64(buffer, index + 8 * i);
        }
        return tmp;
    }

    @Override
    public float TransSingle(byte[] buffer, int index) {
        return Utilities.getFloat(this.ByteTransDataFormat4(buffer, index), 0);
    }

    @Override
    public float[] TransSingle(byte[] buffer, int index, int length) {
        float[] tmp = new float[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransSingle(buffer, index + 4 * i);
        }
        return tmp;
    }

    @Override
    public double TransDouble(byte[] buffer, int index) {
        return Utilities.getDouble(this.ByteTransDataFormat8(buffer, index), 0);
    }

    @Override
    public double[] TransDouble(byte[] buffer, int index, int length) {
        double[] tmp = new double[length];
        for (int i = 0; i < length; ++i) {
            tmp[i] = this.TransDouble(buffer, index + 8 * i);
        }
        return tmp;
    }

    @Override
    public String TransString(byte[] buffer, int index, int length, Charset encoding) {
        byte[] tmp = this.TransByte(buffer, index, length);
        if (this.IsStringReverse) {
            return new String(SoftBasic.BytesReverseByWord(tmp), encoding);
        }
        return new String(tmp, encoding);
    }

    @Override
    public byte[] TransByte(boolean value) {
        return this.TransByte(new boolean[]{value});
    }

    @Override
    public byte[] TransByte(boolean[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            if (!values[i]) continue;
            buffer[i] = 1;
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(byte value) {
        return new byte[]{value};
    }

    @Override
    public byte[] TransByte(short value) {
        return this.TransByte(new short[]{value});
    }

    @Override
    public byte[] TransByte(short[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 2];
        for (int i = 0; i < values.length; ++i) {
            System.arraycopy(Utilities.getBytes(values[i]), 0, buffer, 2 * i, 2);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(int value) {
        return this.TransByte(new int[]{value});
    }

    @Override
    public byte[] TransByte(int[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 4];
        for (int i = 0; i < values.length; ++i) {
            System.arraycopy(this.ByteTransDataFormat4(Utilities.getBytes(values[i])), 0, buffer, 4 * i, 4);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(long value) {
        return this.TransByte(new long[]{value});
    }

    @Override
    public byte[] TransByte(long[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 8];
        for (int i = 0; i < values.length; ++i) {
            System.arraycopy(this.ByteTransDataFormat8(Utilities.getBytes(values[i])), 0, buffer, 8 * i, 8);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(float value) {
        return this.TransByte(new float[]{value});
    }

    @Override
    public byte[] TransByte(float[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 4];
        for (int i = 0; i < values.length; ++i) {
            System.arraycopy(this.ByteTransDataFormat4(Utilities.getBytes(values[i])), 0, buffer, 4 * i, 4);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(double value) {
        return this.TransByte(new double[]{value});
    }

    @Override
    public byte[] TransByte(double[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 8];
        for (int i = 0; i < values.length; ++i) {
            System.arraycopy(this.ByteTransDataFormat8(Utilities.getBytes(values[i])), 0, buffer, 8 * i, 8);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(String value, Charset encoding) {
        if (value == null) {
            return null;
        }
        byte[] buffer = value.getBytes(encoding);
        return this.IsStringReverse ? SoftBasic.BytesReverseByWord(buffer) : buffer;
    }

    @Override
    public boolean getIsStringReverse() {
        return this.IsStringReverse;
    }

    @Override
    public void setIsStringReverse(boolean value) {
        this.IsStringReverse = value;
    }

    @Override
    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    @Override
    public DataFormat getDataFormat() {
        return this.dataFormat;
    }

    protected byte[] ByteTransDataFormat4(byte[] value, int Index) {
        byte[] buffer = new byte[4];
        switch (this.dataFormat) {
            case ABCD: {
                buffer[0] = value[Index + 3];
                buffer[1] = value[Index + 2];
                buffer[2] = value[Index + 1];
                buffer[3] = value[Index + 0];
                break;
            }
            case BADC: {
                buffer[0] = value[Index + 2];
                buffer[1] = value[Index + 3];
                buffer[2] = value[Index + 0];
                buffer[3] = value[Index + 1];
                break;
            }
            case CDAB: {
                buffer[0] = value[Index + 1];
                buffer[1] = value[Index + 0];
                buffer[2] = value[Index + 3];
                buffer[3] = value[Index + 2];
                break;
            }
            case DCBA: {
                buffer[0] = value[Index + 0];
                buffer[1] = value[Index + 1];
                buffer[2] = value[Index + 2];
                buffer[3] = value[Index + 3];
            }
        }
        return buffer;
    }

    protected byte[] ByteTransDataFormat4(byte[] value) {
        return this.ByteTransDataFormat4(value, 0);
    }

    protected byte[] ByteTransDataFormat8(byte[] value, int Index) {
        byte[] buffer = new byte[8];
        switch (this.dataFormat) {
            case ABCD: {
                buffer[0] = value[Index + 7];
                buffer[1] = value[Index + 6];
                buffer[2] = value[Index + 5];
                buffer[3] = value[Index + 4];
                buffer[4] = value[Index + 3];
                buffer[5] = value[Index + 2];
                buffer[6] = value[Index + 1];
                buffer[7] = value[Index + 0];
                break;
            }
            case BADC: {
                buffer[0] = value[Index + 6];
                buffer[1] = value[Index + 7];
                buffer[2] = value[Index + 4];
                buffer[3] = value[Index + 5];
                buffer[4] = value[Index + 2];
                buffer[5] = value[Index + 3];
                buffer[6] = value[Index + 0];
                buffer[7] = value[Index + 1];
                break;
            }
            case CDAB: {
                buffer[0] = value[Index + 1];
                buffer[1] = value[Index + 0];
                buffer[2] = value[Index + 3];
                buffer[3] = value[Index + 2];
                buffer[4] = value[Index + 5];
                buffer[5] = value[Index + 4];
                buffer[6] = value[Index + 7];
                buffer[7] = value[Index + 6];
                break;
            }
            case DCBA: {
                buffer[0] = value[Index + 0];
                buffer[1] = value[Index + 1];
                buffer[2] = value[Index + 2];
                buffer[3] = value[Index + 3];
                buffer[4] = value[Index + 4];
                buffer[5] = value[Index + 5];
                buffer[6] = value[Index + 6];
                buffer[7] = value[Index + 7];
            }
        }
        return buffer;
    }

    protected byte[] ByteTransDataFormat8(byte[] value) {
        return this.ByteTransDataFormat8(value, 0);
    }

    @Override
    public IByteTransform CreateByDateFormat(DataFormat dataFormat) {
        return this;
    }

    public String toString() {
        return "ByteTransformBase[" + this.getDataFormat().toString() + "]";
    }
}

