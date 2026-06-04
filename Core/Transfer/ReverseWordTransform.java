/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.ByteTransformBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;

public class ReverseWordTransform
extends ByteTransformBase {
    public ReverseWordTransform() {
        this.setDataFormat(DataFormat.ABCD);
    }

    public ReverseWordTransform(DataFormat dataFormat) {
        super(dataFormat);
    }

    private byte[] ReverseBytesByWord(byte[] buffer, int index, int length) {
        int i;
        byte[] tmp = new byte[length];
        for (i = 0; i < length; ++i) {
            tmp[i] = buffer[index + i];
        }
        for (i = 0; i < length / 2; ++i) {
            byte b = tmp[i * 2 + 0];
            tmp[i * 2 + 0] = tmp[i * 2 + 1];
            tmp[i * 2 + 1] = b;
        }
        return tmp;
    }

    private byte[] ReverseBytesByWord(byte[] buffer) {
        return this.ReverseBytesByWord(buffer, 0, buffer.length);
    }

    @Override
    public short TransInt16(byte[] buffer, int index) {
        return Utilities.getShort(this.ReverseBytesByWord(buffer, index, 2), 0);
    }

    @Override
    public int TransUInt16(byte[] buffer, int index) {
        return Utilities.getUShort(this.ReverseBytesByWord(buffer, index, 2), 0);
    }

    public String TransString(byte[] buffer, int index, int length, String encoding) {
        byte[] tmp = this.TransByte(buffer, index, length);
        if (this.getIsStringReverse()) {
            return Utilities.getString(this.ReverseBytesByWord(tmp), "US-ASCII");
        }
        return Utilities.getString(tmp, "US-ASCII");
    }

    @Override
    public byte[] TransByte(boolean[] values) {
        return SoftBasic.BoolArrayToByte(values);
    }

    @Override
    public byte[] TransByte(short[] values) {
        if (values == null) {
            return null;
        }
        byte[] buffer = new byte[values.length * 2];
        for (int i = 0; i < values.length; ++i) {
            byte[] tmp = Utilities.getBytes(values[i]);
            System.arraycopy(tmp, 0, buffer, 2 * i, tmp.length);
        }
        return this.ReverseBytesByWord(buffer);
    }

    public byte[] TransByte(String value, String encoding) {
        if (value == null) {
            return null;
        }
        byte[] buffer = Utilities.getBytes(value, encoding);
        buffer = SoftBasic.ArrayExpandToLengthEven(buffer);
        if (this.getIsStringReverse()) {
            return this.ReverseBytesByWord(buffer);
        }
        return buffer;
    }

    @Override
    public IByteTransform CreateByDateFormat(DataFormat dataFormat) {
        ReverseWordTransform transform = new ReverseWordTransform(dataFormat);
        transform.setIsStringReverse(this.getIsStringReverse());
        return transform;
    }
}

