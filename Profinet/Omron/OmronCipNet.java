/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.AllenBradley.AllenBradleyNet;
import HslCommunication.Utilities;
import java.nio.charset.Charset;

public class OmronCipNet
extends AllenBradleyNet {
    public OmronCipNet() {
    }

    public OmronCipNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        if (length > 1) {
            return this.Read(new String[]{address}, new int[]{1});
        }
        return this.Read(new String[]{address}, new int[]{length});
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], short[]>(){

                @Override
                public short[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransInt16(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], short[]>(){

            @Override
            public short[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransInt16(content, startIndexSecond < 0 ? 0 : startIndexSecond * 2, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], int[]>(){

                @Override
                public int[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransUInt16(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransUInt16(content, startIndexSecond < 0 ? 0 : startIndexSecond * 2, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], int[]>(){

                @Override
                public int[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransInt32(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransInt32(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], long[]>(){

                @Override
                public long[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransUInt32(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransUInt32(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], float[]>(){

                @Override
                public float[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransSingle(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], float[]>(){

            @Override
            public float[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransSingle(content, startIndexSecond < 0 ? 0 : startIndexSecond * 4, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], long[]>(){

                @Override
                public long[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransInt64(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransInt64(content, startIndexSecond < 0 ? 0 : startIndexSecond * 8, length);
            }
        });
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        if (length == 1) {
            return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], double[]>(){

                @Override
                public double[] Action(byte[] content) {
                    return OmronCipNet.this.getByteTransform().TransDouble(content, 0, length);
                }
            });
        }
        int startIndex = 0;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractStartIndex(address);
        if (extra.IsSuccess) {
            startIndex = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        final int startIndexSecond = startIndex;
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], double[]>(){

            @Override
            public double[] Action(byte[] content) {
                return OmronCipNet.this.getByteTransform().TransDouble(content, startIndexSecond < 0 ? 0 : startIndexSecond * 8, length);
            }
        });
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        OperateResultExOne<byte[]> read = this.Read(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        int strLen = this.getByteTransform().TransUInt16((byte[])read.Content, 0);
        try {
            return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, 2, strLen, encoding));
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return this.WriteTag(address, (short)195, this.getByteTransform().TransByte(values), 1);
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return this.WriteTag(address, (short)196, this.getByteTransform().TransByte(values), 1);
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return this.WriteTag(address, (short)202, this.getByteTransform().TransByte(values), 1);
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return this.WriteTag(address, (short)197, this.getByteTransform().TransByte(values), 1);
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return this.WriteTag(address, (short)203, this.getByteTransform().TransByte(values), 1);
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        if (Utilities.IsStringNullOrEmpty(value)) {
            value = "";
        }
        byte[] content = SoftBasic.ArrayExpandToLengthEven(value.getBytes(encoding));
        byte[] data = new byte[2 + content.length];
        System.arraycopy(content, 0, data, 2, content.length);
        data[0] = Utilities.getBytes(data.length - 2)[0];
        data[1] = Utilities.getBytes(data.length - 2)[1];
        return super.WriteTag(address, (short)208, data, 1);
    }

    @Override
    public OperateResult Write(String address, byte value) {
        return this.WriteTag(address, (short)209, new byte[]{value});
    }

    @Override
    public String toString() {
        return "OmronCipNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

