/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.FetchWriteMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.StringResources;

public class SiemensFetchWriteNet
extends NetworkDeviceBase {
    public SiemensFetchWriteNet() {
        this.WordLength = (short)2;
        this.setByteTransform(new ReverseBytesTransform());
    }

    public SiemensFetchWriteNet(String ipAddress, int port) {
        this.WordLength = (short)2;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new ReverseBytesTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FetchWriteMessage();
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
        OperateResultExOne<byte[]> command = SiemensFetchWriteNet.BuildReadCommand(address, length);
        if (!command.IsSuccess) {
            result.CopyErrorFromOther(command);
            return result;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (read.IsSuccess) {
            if (((byte[])read.Content)[8] == 0) {
                byte[] buffer = new byte[((byte[])read.Content).length - 16];
                System.arraycopy(read.Content, 16, buffer, 0, buffer.length);
                result.Content = buffer;
                result.IsSuccess = true;
            } else {
                result.ErrorCode = ((byte[])read.Content)[8];
                result.Message = "\u53d1\u751f\u4e86\u5f02\u5e38\uff0c\u5177\u4f53\u4fe1\u606f\u67e5\u627eFetch/Write\u534f\u8bae\u6587\u6863";
            }
        } else {
            result.ErrorCode = read.ErrorCode;
            result.Message = read.Message;
        }
        return result;
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], Byte>(){

            @Override
            public Byte Action(byte[] content) {
                return content[0];
            }
        });
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResult result = new OperateResult();
        OperateResultExOne<byte[]> command = SiemensFetchWriteNet.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            result.CopyErrorFromOther(command);
            return result;
        }
        OperateResultExOne<byte[]> write = this.ReadFromCoreServer((byte[])command.Content);
        if (write.IsSuccess) {
            if (((byte[])write.Content)[8] != 0) {
                result.Message = "\u5199\u5165\u6570\u636e\u5f02\u5e38\uff0c\u4ee3\u53f7\u4e3a\uff1a" + String.valueOf(((byte[])write.Content)[8]);
            } else {
                result.IsSuccess = true;
            }
        } else {
            result.ErrorCode = write.ErrorCode;
            result.Message = write.Message;
        }
        return result;
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return this.Write(address, SoftBasic.BoolArrayToByte(values));
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    @Override
    public String toString() {
        return "SiemensFetchWriteNet";
    }

    public static int CalculateAddressStarted(String address) {
        if (address.indexOf(46) < 0) {
            return Integer.parseInt(address);
        }
        String[] temp = address.split("\\.");
        return Integer.parseInt(temp[0]);
    }

    public static OperateResultExThree<Byte, Integer, Integer> AnalysisAddress(String address) {
        OperateResultExThree<Byte, Integer, Integer> result;
        block9: {
            result = new OperateResultExThree<Byte, Integer, Integer>();
            try {
                result.Content3 = 0;
                if (address.charAt(0) == 'I') {
                    result.Content1 = (byte)3;
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(1));
                    break block9;
                }
                if (address.charAt(0) == 'Q') {
                    result.Content1 = (byte)4;
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(1));
                    break block9;
                }
                if (address.charAt(0) == 'M') {
                    result.Content1 = (byte)2;
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(1));
                    break block9;
                }
                if (address.charAt(0) == 'D' || address.substring(0, 2).equals("DB")) {
                    result.Content1 = (byte)1;
                    String[] adds = address.split("\\.");
                    result.Content3 = address.charAt(1) == 'B' ? Integer.valueOf(Integer.parseInt(adds[0].substring(2))) : Integer.valueOf(Integer.parseInt(adds[0].substring(1)));
                    if ((Integer)result.Content3 > 255) {
                        result.Message = StringResources.Language.SiemensDBAddressNotAllowedLargerThan255();
                        return result;
                    }
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(address.indexOf(46) + 1));
                    break block9;
                }
                if (address.charAt(0) == 'T') {
                    result.Content1 = (byte)7;
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(1));
                    break block9;
                }
                if (address.charAt(0) == 'C') {
                    result.Content1 = (byte)6;
                    result.Content2 = SiemensFetchWriteNet.CalculateAddressStarted(address.substring(1));
                    break block9;
                }
                return new OperateResultExThree<Byte, Integer, Integer>(StringResources.Language.NotSupportedDataType());
            }
            catch (Exception ex) {
                result.Message = ex.getMessage();
                return result;
            }
        }
        result.IsSuccess = true;
        return result;
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, int count) {
        OperateResultExThree<Byte, Integer, Integer> analysis = SiemensFetchWriteNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[16];
        _PLCCommand[0] = 83;
        _PLCCommand[1] = 53;
        _PLCCommand[2] = 16;
        _PLCCommand[3] = 1;
        _PLCCommand[4] = 3;
        _PLCCommand[5] = 5;
        _PLCCommand[6] = 3;
        _PLCCommand[7] = 8;
        _PLCCommand[8] = (Byte)analysis.Content1;
        _PLCCommand[9] = ((Integer)analysis.Content3).byteValue();
        _PLCCommand[10] = (byte)((Integer)analysis.Content2 / 256);
        _PLCCommand[11] = (byte)((Integer)analysis.Content2 % 256);
        if ((Byte)analysis.Content1 == 1 || (Byte)analysis.Content1 == 6 || (Byte)analysis.Content1 == 7) {
            if (count % 2 != 0) {
                return new OperateResultExOne<byte[]>(StringResources.Language.SiemensReadLengthMustBeEvenNumber());
            }
            _PLCCommand[12] = (byte)(count / 2 / 256);
            _PLCCommand[13] = (byte)(count / 2 % 256);
        } else {
            _PLCCommand[12] = (byte)(count / 256);
            _PLCCommand[13] = (byte)(count % 256);
        }
        _PLCCommand[14] = -1;
        _PLCCommand[15] = 2;
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        OperateResultExThree<Byte, Integer, Integer> analysis = SiemensFetchWriteNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[16 + data.length];
        _PLCCommand[0] = 83;
        _PLCCommand[1] = 53;
        _PLCCommand[2] = 16;
        _PLCCommand[3] = 1;
        _PLCCommand[4] = 3;
        _PLCCommand[5] = 3;
        _PLCCommand[6] = 3;
        _PLCCommand[7] = 8;
        _PLCCommand[8] = (Byte)analysis.Content1;
        _PLCCommand[9] = ((Integer)analysis.Content3).byteValue();
        _PLCCommand[10] = (byte)((Integer)analysis.Content2 / 256);
        _PLCCommand[11] = (byte)((Integer)analysis.Content2 % 256);
        if ((Byte)analysis.Content1 == 1 || (Byte)analysis.Content1 == 6 || (Byte)analysis.Content1 == 7) {
            if (data.length % 2 != 0) {
                return new OperateResultExOne<byte[]>(StringResources.Language.SiemensReadLengthMustBeEvenNumber());
            }
            _PLCCommand[12] = (byte)(data.length / 2 / 256);
            _PLCCommand[13] = (byte)(data.length / 2 % 256);
        } else {
            _PLCCommand[12] = (byte)(data.length / 256);
            _PLCCommand[13] = (byte)(data.length % 256);
        }
        _PLCCommand[14] = -1;
        _PLCCommand[15] = 2;
        System.arraycopy(data, 0, _PLCCommand, 16, data.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }
}

