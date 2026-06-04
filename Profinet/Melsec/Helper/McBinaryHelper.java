/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.IReadWriteMc;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class McBinaryHelper {
    public static byte[] PackMcCommand(byte[] mcCore, byte networkNumber, byte networkStationNumber) {
        byte[] _PLCCommand = new byte[11 + mcCore.length];
        _PLCCommand[0] = 80;
        _PLCCommand[1] = 0;
        _PLCCommand[2] = networkNumber;
        _PLCCommand[3] = -1;
        _PLCCommand[4] = -1;
        _PLCCommand[5] = 3;
        _PLCCommand[6] = networkStationNumber;
        _PLCCommand[7] = (byte)((_PLCCommand.length - 9) % 256);
        _PLCCommand[8] = (byte)((_PLCCommand.length - 9) / 256);
        _PLCCommand[9] = 10;
        _PLCCommand[10] = 0;
        System.arraycopy(mcCore, 0, _PLCCommand, 11, mcCore.length);
        return _PLCCommand;
    }

    public static OperateResult CheckResponseContentHelper(byte[] content) {
        int errorCode = Utilities.getUShort(content, 9);
        if (errorCode != 0) {
            return new OperateResult(errorCode, MelsecHelper.GetErrorDescription(errorCode));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static byte[] BuildReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        byte[] command = new byte[]{1, 4, isBit ? (byte)1 : 0, 0, Utilities.getBytes(addressData.getAddressStart())[0], Utilities.getBytes(addressData.getAddressStart())[1], Utilities.getBytes(addressData.getAddressStart())[2], (byte)addressData.getMcDataType().getDataCode(), (byte)(addressData.getLength() % 256), (byte)(addressData.getLength() / 256)};
        return command;
    }

    public static byte[] BuildWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        if (value == null) {
            value = new byte[]{};
        }
        byte[] command = new byte[10 + value.length];
        command[0] = 1;
        command[1] = 20;
        command[2] = 0;
        command[3] = 0;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = (byte)addressData.getMcDataType().getDataCode();
        command[8] = (byte)(value.length / 2 % 256);
        command[9] = (byte)(value.length / 2 / 256);
        System.arraycopy(value, 0, command, 10, value.length);
        return command;
    }

    public static byte[] BuildWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) {
            value = new boolean[]{};
        }
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData(value);
        byte[] command = new byte[10 + buffer.length];
        command[0] = 1;
        command[1] = 20;
        command[2] = 1;
        command[3] = 0;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = (byte)addressData.getMcDataType().getDataCode();
        command[8] = (byte)(value.length % 256);
        command[9] = (byte)(value.length / 256);
        System.arraycopy(buffer, 0, command, 10, buffer.length);
        return command;
    }

    public static byte[] BuildReadMcCoreExtendCommand(McAddressData addressData, short extend, boolean isBit) {
        byte[] command = new byte[]{1, 4, isBit ? (byte)-127 : -128, 0, 0, 0, Utilities.getBytes(addressData.getAddressStart())[0], Utilities.getBytes(addressData.getAddressStart())[1], Utilities.getBytes(addressData.getAddressStart())[2], (byte)addressData.getMcDataType().getDataCode(), 0, 0, Utilities.getBytes(extend)[0], Utilities.getBytes(extend)[1], -7, (byte)(addressData.getLength() % 256), (byte)(addressData.getLength() / 256)};
        return command;
    }

    public static byte[] BuildReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 4];
        command[0] = 3;
        command[1] = 4;
        command[2] = 0;
        command[3] = 0;
        command[4] = (byte)address.length;
        command[5] = 0;
        for (int i = 0; i < address.length; ++i) {
            command[i * 4 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];
            command[i * 4 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 4 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 4 + 9] = (byte)address[i].getMcDataType().getDataCode();
        }
        return command;
    }

    public static byte[] BuildReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 6];
        command[0] = 6;
        command[1] = 4;
        command[2] = 0;
        command[3] = 0;
        command[4] = (byte)address.length;
        command[5] = 0;
        for (int i = 0; i < address.length; ++i) {
            command[i * 6 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];
            command[i * 6 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 6 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 6 + 9] = (byte)address[i].getMcDataType().getDataCode();
            command[i * 6 + 10] = (byte)(address[i].getLength() % 256);
            command[i * 6 + 11] = (byte)(address[i].getLength() / 256);
        }
        return command;
    }

    public static byte[] BuildReadTag(String[] tags, short[] lengths) throws Exception {
        if (tags.length != lengths.length) {
            throw new Exception(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        ByteArrayOutputStream command = new ByteArrayOutputStream();
        command.write(26);
        command.write(4);
        command.write(0);
        command.write(0);
        command.write(Utilities.getBytes(tags.length)[0]);
        command.write(Utilities.getBytes(tags.length)[1]);
        command.write(0);
        command.write(0);
        for (int i = 0; i < tags.length; ++i) {
            byte[] tagBuffer = tags[i].getBytes(StandardCharsets.UTF_16);
            command.write(Utilities.getBytes(tagBuffer.length / 2)[0]);
            command.write(Utilities.getBytes(tagBuffer.length / 2)[1]);
            command.write(tagBuffer, 0, tagBuffer.length);
            command.write(1);
            command.write(0);
            command.write(Utilities.getBytes(lengths[i] * 2)[0]);
            command.write(Utilities.getBytes(lengths[i] * 2)[1]);
        }
        byte[] buffer = command.toByteArray();
        command.close();
        return buffer;
    }

    public static OperateResultExOne<byte[]> BuildReadMemoryCommand(String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[]{19, 6, Utilities.getBytes(add)[0], Utilities.getBytes(add)[1], Utilities.getBytes(add)[2], Utilities.getBytes(add)[3], (byte)(length % 256), (byte)(length / 256)};
            return OperateResultExOne.CreateSuccessResult(command);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildReadSmartModule(short module, String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[]{1, 6, 0, 0, Utilities.getBytes(add)[0], Utilities.getBytes(add)[1], Utilities.getBytes(add)[2], Utilities.getBytes(add)[3], (byte)(length % 256), (byte)(length / 256), Utilities.getBytes(module)[0], Utilities.getBytes(module)[1]};
            return OperateResultExOne.CreateSuccessResult(command);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> ExtraTagData(byte[] content) {
        try {
            int count = Utilities.getUShort(content, 0);
            int index = 2;
            ArrayList<Byte> array = new ArrayList<Byte>(20);
            for (int i = 0; i < count; ++i) {
                int length = Utilities.getUShort(content, index + 2);
                Utilities.ArrayListAddArray(array, SoftBasic.BytesArraySelectMiddle(content, index + 4, length));
                index += 4 + length;
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static OperateResultExOne<byte[]> ReadTags(IReadWriteMc mc, String[] tags, short[] length) {
        byte[] coreResult = new byte[]{};
        try {
            coreResult = McBinaryHelper.BuildReadTag(tags, length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return McBinaryHelper.ExtraTagData(mc.ExtractActualData((byte[])read.Content, false));
    }

    public static byte[] ExtractActualDataHelper(byte[] response, boolean isBit) {
        if (isBit) {
            byte[] Content = new byte[response.length * 2];
            for (int i = 0; i < response.length; ++i) {
                if ((response[i] & 0x10) == 16) {
                    Content[i * 2 + 0] = 1;
                }
                if ((response[i] & 1) != 1) continue;
                Content[i * 2 + 1] = 1;
            }
            return Content;
        }
        return response;
    }
}

