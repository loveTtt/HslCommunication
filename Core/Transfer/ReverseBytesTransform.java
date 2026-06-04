/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.Core.Transfer.ByteTransformBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;

public class ReverseBytesTransform
extends ByteTransformBase {
    public ReverseBytesTransform() {
    }

    public ReverseBytesTransform(DataFormat dataFormat) {
        super(dataFormat);
    }

    @Override
    public short TransInt16(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[1 + index], buffer[0 + index]};
        return Utilities.getShort(tmp, 0);
    }

    @Override
    public int TransUInt16(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[1 + index], buffer[0 + index]};
        return Utilities.getUShort(tmp, 0);
    }

    @Override
    public int TransInt32(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[3 + index], buffer[2 + index], buffer[1 + index], buffer[0 + index]};
        return Utilities.getInt(this.ByteTransDataFormat4(tmp), 0);
    }

    @Override
    public long TransUInt32(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[3 + index], buffer[2 + index], buffer[1 + index], buffer[0 + index]};
        return Utilities.getUInt(this.ByteTransDataFormat4(tmp), 0);
    }

    @Override
    public long TransInt64(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[7 + index], buffer[6 + index], buffer[5 + index], buffer[4 + index], buffer[3 + index], buffer[2 + index], buffer[1 + index], buffer[0 + index]};
        return Utilities.getLong(this.ByteTransDataFormat8(tmp), 0);
    }

    @Override
    public float TransSingle(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[3 + index], buffer[2 + index], buffer[1 + index], buffer[0 + index]};
        return Utilities.getFloat(this.ByteTransDataFormat4(tmp), 0);
    }

    @Override
    public double TransDouble(byte[] buffer, int index) {
        byte[] tmp = new byte[]{buffer[7 + index], buffer[6 + index], buffer[5 + index], buffer[4 + index], buffer[3 + index], buffer[2 + index], buffer[1 + index], buffer[0 + index]};
        return Utilities.getDouble(this.ByteTransDataFormat8(tmp), 0);
    }

    @Override
    public byte[] TransByte(short[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 2];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            Utilities.bytesReverse(tmp);
            System.arraycopy(tmp, 0, buffer, 2 * i, tmp.length);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(int[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 4];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            Utilities.bytesReverse(tmp);
            System.arraycopy(this.ByteTransDataFormat4(tmp), 0, buffer, 4 * i, tmp.length);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(long[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 8];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            Utilities.bytesReverse(tmp);
            System.arraycopy(this.ByteTransDataFormat8(tmp), 0, buffer, 8 * i, tmp.length);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(float[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 4];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            Utilities.bytesReverse(tmp);
            System.arraycopy(this.ByteTransDataFormat4(tmp), 0, buffer, 4 * i, tmp.length);
        }
        return buffer;
    }

    @Override
    public byte[] TransByte(double[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 8];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            Utilities.bytesReverse(tmp);
            System.arraycopy(this.ByteTransDataFormat8(tmp), 0, buffer, 8 * i, tmp.length);
        }
        return buffer;
    }

    @Override
    public IByteTransform CreateByDateFormat(DataFormat dataFormat) {
        ReverseBytesTransform transform = new ReverseBytesTransform(dataFormat);
        transform.setIsStringReverse(this.getIsStringReverse());
        return transform;
    }
}

