/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.Core.Transfer.DataFormat;
import java.nio.charset.Charset;

public interface IByteTransform {
    public boolean TransBool(byte[] var1, int var2);

    public boolean[] TransBool(byte[] var1, int var2, int var3);

    public byte TransByte(byte[] var1, int var2);

    public byte[] TransByte(byte[] var1, int var2, int var3);

    public short TransInt16(byte[] var1, int var2);

    public short[] TransInt16(byte[] var1, int var2, int var3);

    public int TransUInt16(byte[] var1, int var2);

    public int[] TransUInt16(byte[] var1, int var2, int var3);

    public int TransInt32(byte[] var1, int var2);

    public int[] TransInt32(byte[] var1, int var2, int var3);

    public long TransUInt32(byte[] var1, int var2);

    public long[] TransUInt32(byte[] var1, int var2, int var3);

    public long TransInt64(byte[] var1, int var2);

    public long[] TransInt64(byte[] var1, int var2, int var3);

    public float TransSingle(byte[] var1, int var2);

    public float[] TransSingle(byte[] var1, int var2, int var3);

    public double TransDouble(byte[] var1, int var2);

    public double[] TransDouble(byte[] var1, int var2, int var3);

    public String TransString(byte[] var1, int var2, int var3, Charset var4);

    public byte[] TransByte(boolean var1);

    public byte[] TransByte(boolean[] var1);

    public byte[] TransByte(byte var1);

    public byte[] TransByte(short var1);

    public byte[] TransByte(short[] var1);

    public byte[] TransByte(int var1);

    public byte[] TransByte(int[] var1);

    public byte[] TransByte(long var1);

    public byte[] TransByte(long[] var1);

    public byte[] TransByte(float var1);

    public byte[] TransByte(float[] var1);

    public byte[] TransByte(double var1);

    public byte[] TransByte(double[] var1);

    public byte[] TransByte(String var1, Charset var2);

    public void setDataFormat(DataFormat var1);

    public DataFormat getDataFormat();

    public boolean getIsStringReverse();

    public void setIsStringReverse(boolean var1);

    public IByteTransform CreateByDateFormat(DataFormat var1);
}

