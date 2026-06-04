/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.IDataTransfer;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NetworkUdpDeviceBase
extends NetworkUdpBase
implements IReadWriteDevice {
    protected short WordLength = 1;
    private IByteTransform byteTransform;

    public IByteTransform getByteTransform() {
        return this.byteTransform;
    }

    public void setByteTransform(IByteTransform transform) {
        this.byteTransform = transform;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        OperateResultExOne<boolean[]> read = this.ReadBool(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((boolean[])read.Content)[0]);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return this.Write(address, new boolean[]{value});
    }

    @Override
    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address, Class<T> tClass) {
        IDataTransfer Content;
        OperateResultExOne result = new OperateResultExOne();
        try {
            Content = (IDataTransfer)tClass.newInstance();
        }
        catch (Exception ex) {
            Content = null;
        }
        OperateResultExOne<byte[]> read = this.Read(address, Content.getReadCount());
        if (read.IsSuccess) {
            Content.ParseSource((byte[])read.Content);
            result.Content = Content;
            result.IsSuccess = true;
        } else {
            result.ErrorCode = read.ErrorCode;
            result.Message = read.Message;
        }
        return result;
    }

    @Override
    public <T extends IDataTransfer> OperateResult WriteCustomer(String address, T data) {
        return this.Write(address, data.ToSource());
    }

    @Override
    public OperateResultExOne<Short> ReadInt16(String address) {
        OperateResultExOne<short[]> read = this.ReadInt16(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((short[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength)), new FunctionOperateExOne<byte[], short[]>(){

            @Override
            public short[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Integer> ReadUInt16(String address) {
        OperateResultExOne<int[]> read = this.ReadUInt16(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((int[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength)), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Integer> ReadInt32(String address) {
        OperateResultExOne<int[]> read = this.ReadInt32(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((int[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Long> ReadUInt32(String address) {
        OperateResultExOne<long[]> read = this.ReadUInt32(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((long[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Float> ReadFloat(String address) {
        OperateResultExOne<float[]> read = this.ReadFloat(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(Float.valueOf(((float[])read.Content)[0]));
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], float[]>(){

            @Override
            public float[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Long> ReadInt64(String address) {
        OperateResultExOne<long[]> read = this.ReadInt64(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((long[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 4)), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Double> ReadDouble(String address) {
        OperateResultExOne<double[]> read = this.ReadDouble(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((double[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 4)), new FunctionOperateExOne<byte[], double[]>(){

            @Override
            public double[] Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], String>(){

            @Override
            public String Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransString(content, 0, content.length, StandardCharsets.US_ASCII);
            }
        });
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, final Charset encoding) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], String>(){

            @Override
            public String Action(byte[] content) {
                return NetworkUdpDeviceBase.this.getByteTransform().TransString(content, 0, content.length, encoding);
            }
        });
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, short value) {
        return this.Write(address, new short[]{value});
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, int value) {
        return this.Write(address, new int[]{value});
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, float value) {
        return this.Write(address, new float[]{value});
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, long value) {
        return this.Write(address, new long[]{value});
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, double value) {
        return this.Write(address, new double[]{value});
    }

    @Override
    public OperateResult Write(String address, String value) {
        return this.Write(address, value, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] temp = this.getByteTransform().TransByte(value, encoding);
        if (this.WordLength == 1) {
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
        }
        return this.Write(address, temp);
    }

    @Override
    public OperateResult Write(String address, String value, int length) {
        return this.Write(address, value, length, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResult Write(String address, String value, int length, Charset encoding) {
        byte[] temp = this.getByteTransform().TransByte(value, encoding);
        if (this.WordLength == 1) {
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
        }
        temp = SoftBasic.ArrayExpandToLength(temp, length);
        return this.Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value) {
        byte[] temp = Utilities.csharpString2Byte(value);
        return this.Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value, int length) {
        byte[] temp = Utilities.csharpString2Byte(value);
        temp = SoftBasic.ArrayExpandToLength(temp, length * 2);
        return this.Write(address, temp);
    }

    @Override
    public OperateResult ConnectClose() {
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public String toString() {
        return "NetworkUdpDeviceBase<" + this.getByteTransform().getClass().toString() + ">[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

