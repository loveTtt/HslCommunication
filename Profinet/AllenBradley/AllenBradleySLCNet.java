/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.AllenBradleySLCMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.Socket;

public class AllenBradleySLCNet
extends NetworkDeviceBase {
    public int SessionHandle = 0;

    public AllenBradleySLCNet() {
        this.WordLength = (short)2;
        this.setByteTransform(new RegularByteTransform());
    }

    public AllenBradleySLCNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AllenBradleySLCMessage();
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, SoftBasic.HexStringToBytes("01 01 00 00 00 00 00 00 00 00 00 00 00 04 00 05 00 00 00 00 00 00 00 00 00 00 00 00"), true, true);
        if (!read.IsSuccess) {
            return read;
        }
        this.SessionHandle = this.getByteTransform().TransInt32((byte[])read.Content, 4);
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> command = AllenBradleySLCNet.BuildReadCommand(address, length);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand((byte[])command.Content));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = AllenBradleySLCNet.ExtraActualContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = AllenBradleySLCNet.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand((byte[])command.Content));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = AllenBradleySLCNet.ExtraActualContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        OperateResultExTwo<String, Integer> bitAnalysis = AllenBradleySLCNet.AnalysisBitIndex(address);
        address = (String)bitAnalysis.Content1;
        int bitIndex = (Integer)bitAnalysis.Content2;
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray((byte[])read.Content)[bitIndex]);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        OperateResultExOne<byte[]> command = AllenBradleySLCNet.BuildWriteCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand((byte[])command.Content));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> extra = AllenBradleySLCNet.ExtraActualContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return extra;
        }
        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    private byte[] PackCommand(byte[] coreCmd) {
        byte[] cmd = new byte[28 + coreCmd.length];
        cmd[0] = 1;
        cmd[1] = 7;
        cmd[2] = (byte)(coreCmd.length / 256);
        cmd[3] = (byte)(coreCmd.length % 256);
        System.arraycopy(this.getByteTransform().TransByte(this.SessionHandle), 0, cmd, 4, 4);
        System.arraycopy(coreCmd, 0, cmd, 28, coreCmd.length);
        return cmd;
    }

    public static OperateResultExTwo<String, Integer> AnalysisBitIndex(String address) {
        int bitIndex = 0;
        int index = address.indexOf(47);
        if (index < 0) {
            index = address.indexOf(46);
        }
        if (index > 0) {
            bitIndex = Integer.parseInt(address.substring(index + 1));
            address = address.substring(0, index);
        }
        return OperateResultExTwo.CreateSuccessResult(address, bitIndex);
    }

    public static OperateResultExThree<Byte, Short, Short> AnalysisAddress(String address) {
        if (!address.contains(":")) {
            return new OperateResultExThree<Byte, Short, Short>("Address can't find ':', example : A9:0");
        }
        String[] adds = address.split(":");
        try {
            OperateResultExThree<Byte, Short, Short> result = new OperateResultExThree<Byte, Short, Short>();
            switch (adds[0].charAt(0)) {
                case 'A': {
                    result.Content1 = (byte)-114;
                    break;
                }
                case 'B': {
                    result.Content1 = (byte)-123;
                    break;
                }
                case 'N': {
                    result.Content1 = (byte)-119;
                    break;
                }
                case 'F': {
                    result.Content1 = (byte)-118;
                    break;
                }
                case 'S': {
                    if (adds[0].length() > 1 && adds[0].charAt(1) == 'T') {
                        result.Content1 = (byte)-115;
                        break;
                    }
                    result.Content1 = (byte)-124;
                    break;
                }
                case 'C': {
                    result.Content1 = (byte)-121;
                    break;
                }
                case 'I': {
                    result.Content1 = (byte)-125;
                    break;
                }
                case 'O': {
                    result.Content1 = (byte)-126;
                    break;
                }
                case 'R': {
                    result.Content1 = (byte)-120;
                    break;
                }
                case 'T': {
                    result.Content1 = (byte)-122;
                    break;
                }
                case 'L': {
                    result.Content1 = (byte)-111;
                    break;
                }
                default: {
                    throw new Exception("Address code wrong, must be A,B,N,F,S,C,I,O,R,T,ST,L");
                }
            }
            switch ((Byte)result.Content1) {
                case -124: {
                    result.Content2 = adds[0].length() == 1 ? (short)2 : Short.parseShort(adds[0].substring(1));
                    break;
                }
                case -126: {
                    result.Content2 = adds[0].length() == 1 ? (short)0 : Short.parseShort(adds[0].substring(1));
                    break;
                }
                case -125: {
                    result.Content2 = adds[0].length() == 1 ? (short)1 : Short.parseShort(adds[0].substring(1));
                    break;
                }
                case -115: {
                    result.Content2 = adds[0].length() == 2 ? (short)1 : Short.parseShort(adds[0].substring(2));
                    break;
                }
                default: {
                    result.Content2 = Short.parseShort(adds[0].substring(1));
                }
            }
            result.Content3 = Short.parseShort(adds[1]);
            result.IsSuccess = true;
            result.Message = StringResources.Language.SuccessText();
            return result;
        }
        catch (Exception ex) {
            return new OperateResultExThree<Byte, Short, Short>("Wrong Address format: " + ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length) {
        OperateResultExThree<Byte, Short, Short> analysis = AllenBradleySLCNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (length < 2) {
            length = (short)2;
        }
        if ((Byte)analysis.Content1 == -114) {
            analysis.Content3 = (short)((Short)analysis.Content3 / 2);
        }
        byte[] command = new byte[14];
        command[0] = 0;
        command[1] = 5;
        command[2] = 0;
        command[3] = 0;
        command[4] = 15;
        command[5] = 0;
        command[6] = 0;
        command[7] = 1;
        command[8] = -94;
        command[9] = (byte)length;
        command[10] = ((Short)analysis.Content2).byteValue();
        command[11] = (Byte)analysis.Content1;
        System.arraycopy(Utilities.getBytes((Short)analysis.Content3), 0, command, 12, 2);
        return OperateResultExOne.CreateSuccessResult(command);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value) {
        OperateResultExThree<Byte, Short, Short> analysis = AllenBradleySLCNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if ((Byte)analysis.Content1 == -114) {
            analysis.Content3 = (short)((Short)analysis.Content3 / 2);
        }
        byte[] command = new byte[18 + value.length];
        command[0] = 0;
        command[1] = 5;
        command[2] = 0;
        command[3] = 0;
        command[4] = 15;
        command[5] = 0;
        command[6] = 0;
        command[7] = 1;
        command[8] = -85;
        command[9] = -1;
        command[10] = Utilities.getBytes(value.length)[0];
        command[11] = Utilities.getBytes(value.length)[1];
        command[12] = ((Short)analysis.Content2).byteValue();
        command[13] = (Byte)analysis.Content1;
        System.arraycopy(Utilities.getBytes((Short)analysis.Content3), 0, command, 14, 2);
        command[16] = -1;
        command[17] = -1;
        System.arraycopy(value, 0, command, 18, value.length);
        return OperateResultExOne.CreateSuccessResult(command);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean value) {
        OperateResultExTwo<String, Integer> bitAnalysis = AllenBradleySLCNet.AnalysisBitIndex(address);
        address = (String)bitAnalysis.Content1;
        int bitIndex = (Integer)bitAnalysis.Content2;
        OperateResultExThree<Byte, Short, Short> analysis = AllenBradleySLCNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if ((Byte)analysis.Content1 == -114) {
            analysis.Content3 = (short)((Short)analysis.Content3 / 2);
        }
        int bitPow = 1 << bitIndex;
        byte[] command = new byte[20];
        command[0] = 0;
        command[1] = 5;
        command[2] = 0;
        command[3] = 0;
        command[4] = 15;
        command[5] = 0;
        command[6] = 0;
        command[7] = 1;
        command[8] = -85;
        command[9] = -1;
        command[10] = 2;
        command[11] = 0;
        command[12] = ((Short)analysis.Content2).byteValue();
        command[13] = (Byte)analysis.Content1;
        System.arraycopy(Utilities.getBytes((Short)analysis.Content3), 0, command, 14, 2);
        command[16] = Utilities.getBytes(bitPow)[0];
        command[17] = Utilities.getBytes(bitPow)[1];
        if (value) {
            command[18] = Utilities.getBytes(bitPow)[0];
            command[19] = Utilities.getBytes(bitPow)[1];
        }
        return OperateResultExOne.CreateSuccessResult(command);
    }

    public static OperateResultExOne<byte[]> ExtraActualContent(byte[] content) {
        if (content.length < 36) {
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + SoftBasic.ByteToHexString(content, ' '));
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 36));
    }
}

