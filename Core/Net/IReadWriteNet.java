/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.Core.Types.IDataTransfer;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.LogNet.Core.ILogNet;
import java.nio.charset.Charset;

public interface IReadWriteNet {
    public void setLogNet(ILogNet var1);

    public String getConnectionId();

    public void setConnectionId(String var1);

    public OperateResultExOne<byte[]> Read(String var1, short var2);

    public OperateResult Write(String var1, byte[] var2);

    public OperateResultExOne<boolean[]> ReadBool(String var1, short var2);

    public OperateResultExOne<Boolean> ReadBool(String var1);

    public OperateResult Write(String var1, boolean[] var2);

    public OperateResult Write(String var1, boolean var2);

    public OperateResultExOne<Short> ReadInt16(String var1);

    public OperateResultExOne<short[]> ReadInt16(String var1, short var2);

    public OperateResultExOne<Integer> ReadUInt16(String var1);

    public OperateResultExOne<int[]> ReadUInt16(String var1, short var2);

    public OperateResultExOne<Integer> ReadInt32(String var1);

    public OperateResultExOne<int[]> ReadInt32(String var1, short var2);

    public OperateResultExOne<Long> ReadUInt32(String var1);

    public OperateResultExOne<long[]> ReadUInt32(String var1, short var2);

    public OperateResultExOne<Long> ReadInt64(String var1);

    public OperateResultExOne<long[]> ReadInt64(String var1, short var2);

    public OperateResultExOne<Float> ReadFloat(String var1);

    public OperateResultExOne<float[]> ReadFloat(String var1, short var2);

    public OperateResultExOne<Double> ReadDouble(String var1);

    public OperateResultExOne<double[]> ReadDouble(String var1, short var2);

    public OperateResultExOne<String> ReadString(String var1, short var2);

    public OperateResultExOne<String> ReadString(String var1, short var2, Charset var3);

    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String var1, Class<T> var2);

    public OperateResult Write(String var1, short var2);

    public OperateResult Write(String var1, short[] var2);

    public OperateResult Write(String var1, int var2);

    public OperateResult Write(String var1, int[] var2);

    public OperateResult Write(String var1, long var2);

    public OperateResult Write(String var1, long[] var2);

    public OperateResult Write(String var1, float var2);

    public OperateResult Write(String var1, float[] var2);

    public OperateResult Write(String var1, double var2);

    public OperateResult Write(String var1, double[] var2);

    public OperateResult Write(String var1, String var2);

    public OperateResult Write(String var1, String var2, int var3);

    public OperateResult Write(String var1, String var2, Charset var3);

    public OperateResult Write(String var1, String var2, int var3, Charset var4);

    public <T extends IDataTransfer> OperateResult WriteCustomer(String var1, T var2);

    public OperateResult ConnectClose();
}

