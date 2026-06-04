/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import java.nio.charset.StandardCharsets;

public class McAsciiHelper {
    public static byte[] BuildAsciiReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        byte[] command = new byte[]{48, 52, 48, 49, 48, 48, 48, isBit ? (byte)49 : 48, addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0], addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5], SoftBasic.BuildAsciiBytesFrom((short)addressData.getLength())[0], SoftBasic.BuildAsciiBytesFrom((short)addressData.getLength())[1], SoftBasic.BuildAsciiBytesFrom((short)addressData.getLength())[2], SoftBasic.BuildAsciiBytesFrom((short)addressData.getLength())[3]};
        return command;
    }

    public static byte[] BuildAsciiWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        value = MelsecHelper.TransByteArrayToAsciiByteArray(value);
        byte[] command = new byte[20 + value.length];
        command[0] = 49;
        command[1] = 52;
        command[2] = 48;
        command[3] = 49;
        command[4] = 48;
        command[5] = 48;
        command[6] = 48;
        command[7] = 48;
        command[8] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0];
        command[9] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short)(value.length / 4))[0];
        command[17] = SoftBasic.BuildAsciiBytesFrom((short)(value.length / 4))[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short)(value.length / 4))[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short)(value.length / 4))[3];
        System.arraycopy(value, 0, command, 20, value.length);
        return command;
    }

    public static byte[] BuildAsciiWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) {
            value = new boolean[]{};
        }
        byte[] buffer = new byte[value.length];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte) (value[i] ? 49 : 48);
        }
        byte[] command = new byte[20 + buffer.length];
        command[0] = 49;
        command[1] = 52;
        command[2] = 48;
        command[3] = 49;
        command[4] = 48;
        command[5] = 48;
        command[6] = 48;
        command[7] = 49;
        command[8] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0];
        command[9] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short)value.length)[0];
        command[17] = SoftBasic.BuildAsciiBytesFrom((short)value.length)[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short)value.length)[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short)value.length)[3];
        System.arraycopy(buffer, 0, command, 20, buffer.length);
        return command;
    }

    public static byte[] BuildAsciiReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 8];
        command[0] = 48;
        command[1] = 52;
        command[2] = 48;
        command[3] = 51;
        command[4] = 48;
        command[5] = 48;
        command[6] = 48;
        command[7] = 48;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte)address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte)address.length)[1];
        command[10] = 48;
        command[11] = 48;
        for (int i = 0; i < address.length; ++i) {
            command[i * 8 + 12] = address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0];
            command[i * 8 + 13] = address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1];
            command[i * 8 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];
            command[i * 8 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 8 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 8 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 8 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 8 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
        }
        return command;
    }

    public static byte[] BuildAsciiReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 12];
        command[0] = 48;
        command[1] = 52;
        command[2] = 48;
        command[3] = 54;
        command[4] = 48;
        command[5] = 48;
        command[6] = 48;
        command[7] = 48;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte)address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte)address.length)[1];
        command[10] = 48;
        command[11] = 48;
        for (int i = 0; i < address.length; ++i) {
            command[i * 12 + 12] = address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0];
            command[i * 12 + 13] = address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1];
            command[i * 12 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];
            command[i * 12 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 12 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 12 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 12 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 12 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
            command[i * 12 + 20] = SoftBasic.BuildAsciiBytesFrom((short)address[i].getLength())[0];
            command[i * 12 + 21] = SoftBasic.BuildAsciiBytesFrom((short)address[i].getLength())[1];
            command[i * 12 + 22] = SoftBasic.BuildAsciiBytesFrom((short)address[i].getLength())[2];
            command[i * 12 + 23] = SoftBasic.BuildAsciiBytesFrom((short)address[i].getLength())[3];
        }
        return command;
    }

    public static OperateResultExOne<byte[]> BuildAsciiReadMemoryCommand(String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[20];
            command[0] = 48;
            command[1] = 54;
            command[2] = 49;
            command[3] = 51;
            command[4] = 48;
            command[5] = 48;
            command[6] = 48;
            command[7] = 48;
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(add), 0, command, 8, 8);
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(length), 0, command, 16, 4);
            return OperateResultExOne.CreateSuccessResult(command);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildAsciiReadSmartModule(short module, String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[24];
            command[0] = 48;
            command[1] = 54;
            command[2] = 48;
            command[3] = 49;
            command[4] = 48;
            command[5] = 48;
            command[6] = 48;
            command[7] = 48;
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(add), 0, command, 8, 8);
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(length), 0, command, 16, 4);
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(module), 0, command, 20, 4);
            return OperateResultExOne.CreateSuccessResult(command);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static byte[] BuildAsciiReadMcCoreExtendCommand(McAddressData addressData, short extend, boolean isBit) {
        byte[] command = new byte[]{48, 52, 48, 49, 48, 48, 56, isBit ? (byte)49 : 48, 48, 48, 74, SoftBasic.BuildAsciiBytesFrom(extend)[1], SoftBasic.BuildAsciiBytesFrom(extend)[2], SoftBasic.BuildAsciiBytesFrom(extend)[3], 48, 48, 48, addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0], addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4], MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5], 48, 48, 48, SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[0], SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[1], SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[2], SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[3]};
        return command;
    }

    public static byte[] PackMcCommand(byte[] mcCore, byte networkNumber, byte networkStationNumber) {
        byte[] plcCommand = new byte[22 + mcCore.length];
        plcCommand[0] = 53;
        plcCommand[1] = 48;
        plcCommand[2] = 48;
        plcCommand[3] = 48;
        plcCommand[4] = MelsecHelper.BuildBytesFromData(networkNumber)[0];
        plcCommand[5] = MelsecHelper.BuildBytesFromData(networkNumber)[1];
        plcCommand[6] = 70;
        plcCommand[7] = 70;
        plcCommand[8] = 48;
        plcCommand[9] = 51;
        plcCommand[10] = 70;
        plcCommand[11] = 70;
        plcCommand[12] = MelsecHelper.BuildBytesFromData(networkStationNumber)[0];
        plcCommand[13] = MelsecHelper.BuildBytesFromData(networkStationNumber)[1];
        plcCommand[14] = MelsecHelper.BuildBytesFromData((short)(plcCommand.length - 18))[0];
        plcCommand[15] = MelsecHelper.BuildBytesFromData((short)(plcCommand.length - 18))[1];
        plcCommand[16] = MelsecHelper.BuildBytesFromData((short)(plcCommand.length - 18))[2];
        plcCommand[17] = MelsecHelper.BuildBytesFromData((short)(plcCommand.length - 18))[3];
        plcCommand[18] = 48;
        plcCommand[19] = 48;
        plcCommand[20] = 49;
        plcCommand[21] = 48;
        System.arraycopy(mcCore, 0, plcCommand, 22, mcCore.length);
        return plcCommand;
    }

    public static byte[] ExtractActualDataHelper(byte[] response, boolean isBit) {
        if (isBit) {
            byte[] Content = new byte[response.length];
            for (int i = 0; i < response.length; ++i) {
                Content[i] = response[i] == 48 ? (byte)0 : 1;
            }
            return Content;
        }
        return MelsecHelper.TransAsciiByteArrayToByteArray(response);
    }

    public static OperateResult CheckResponseContentHelper(byte[] content) {
        int errorCode = Integer.parseInt(new String(SoftBasic.BytesArraySelectMiddle(content, 18, 4), StandardCharsets.US_ASCII), 16);
        if (errorCode != 0) {
            return new OperateResult(errorCode, MelsecHelper.GetErrorDescription(errorCode));
        }
        return OperateResult.CreateSuccessResult();
    }
}

