/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.ModbusAddress;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Serial.SoftCRC16;
import HslCommunication.Serial.SoftLRC;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class ModbusInfo {
    public static final byte ReadCoil = 1;
    public static final byte ReadDiscrete = 2;
    public static final byte ReadRegister = 3;
    public static final byte ReadInputRegister = 4;
    public static final byte WriteOneCoil = 5;
    public static final byte WriteOneRegister = 6;
    public static final byte WriteCoil = 15;
    public static final byte WriteRegister = 16;
    public static final byte ReadFileRecord = 20;
    public static final byte WriteFileRecord = 21;
    public static final byte WriteMaskRegister = 22;
    public static final byte ReadWrite = 23;
    public static final byte NoMean = 23;
    public static final byte FunctionCodeNotSupport = 1;
    public static final byte FunctionCodeOverBound = 2;
    public static final byte FunctionCodeQuantityOver = 3;
    public static final byte FunctionCodeReadWriteException = 4;

    private static void CheckModbusAddressStart(ModbusAddress mAddress, boolean isStartWithZero) throws Exception {
        if (!isStartWithZero) {
            if (mAddress.getAddress() < 1) {
                throw new Exception(StringResources.Language.ModbusAddressMustMoreThanOne());
            }
            mAddress.setAddress(mAddress.getAddress() - 1);
        }
    }

    public static OperateResultExOne<byte[][]> BuildReadModbusCommand(String address, short length, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            return ModbusInfo.BuildReadModbusCommand(mAddress, length);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[][]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[][]> BuildReadModbusCommand(ModbusAddress mAddress, short length) {
        ArrayList<byte[]> commands = new ArrayList<byte[]>();
        if (mAddress.getFunction() == 1 || mAddress.getFunction() == 2 || mAddress.getFunction() == 3 || mAddress.getFunction() == 4 || Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            OperateResultExTwo<int[], int[]> bytes = HslHelper.SplitReadLength(mAddress.getAddress(), length, (short)(mAddress.getFunction() == 1 || mAddress.getFunction() == 2 ? 2000 : 120));
            for (int i = 0; i < ((int[])bytes.Content1).length; ++i) {
                byte[] buffer = new byte[]{(byte)mAddress.getStation(), (byte)mAddress.getFunction(), Utilities.getBytes(((int[])bytes.Content1)[i])[1], Utilities.getBytes(((int[])bytes.Content1)[i])[0], Utilities.getBytes(((int[])bytes.Content2)[i])[1], Utilities.getBytes(((int[])bytes.Content2)[i])[0]};
                commands.add(buffer);
            }
        } else {
            byte[] buffer = new byte[]{(byte)mAddress.getStation(), (byte)mAddress.getFunction(), Utilities.getBytes(mAddress.getAddress())[1], Utilities.getBytes(mAddress.getAddress())[0], Utilities.getBytes(length)[1], Utilities.getBytes(length)[0]};
            commands.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToArray(commands));
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(String address, boolean[] values, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            if (mAddress.getFunction() == 1) {
                mAddress.setFunction(defaultFunction);
            }
            return ModbusInfo.BuildWriteBoolModbusCommand(mAddress, values);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(String address, boolean value, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            if (address.indexOf(46) <= 0) {
                ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
                ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
                return ModbusInfo.BuildWriteBoolModbusCommand(mAddress, value);
            }
            int bitIndex = Integer.parseInt(address.substring(address.indexOf(46) + 1));
            if (bitIndex < 0 || bitIndex > 15) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusBitIndexOverstep());
            }
            int orMask = 1 << bitIndex;
            int andMask = ~orMask;
            if (!value) {
                orMask = 0;
            }
            return ModbusInfo.BuildWriteMaskModbusCommand(address.substring(0, address.indexOf(46)), (short)andMask, (short)orMask, station, isStartWithZero, (byte)22);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(ModbusAddress mAddress, boolean[] values) {
        try {
            byte[] data = SoftBasic.BoolArrayToByte(values);
            byte[] content = new byte[7 + data.length];
            content[0] = (byte)mAddress.getStation();
            content[1] = (byte)mAddress.getFunction();
            content[2] = Utilities.getBytes(mAddress.getAddress())[1];
            content[3] = Utilities.getBytes(mAddress.getAddress())[0];
            content[4] = (byte)(values.length / 256);
            content[5] = (byte)(values.length % 256);
            content[6] = (byte)data.length;
            System.arraycopy(data, 0, content, 7, data.length);
            return OperateResultExOne.CreateSuccessResult(content);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolModbusCommand(ModbusAddress mAddress, boolean value) {
        byte[] content = new byte[6];
        content[0] = (byte)mAddress.getStation();
        content[1] = (byte)mAddress.getFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        if (value) {
            content[4] = -1;
            content[5] = 0;
        } else {
            content[4] = 0;
            content[5] = 0;
        }
        return OperateResultExOne.CreateSuccessResult(content);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(String address, byte[] values, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == 3) {
                mAddress.setFunction(defaultFunction);
            }
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            return ModbusInfo.BuildWriteWordModbusCommand(mAddress, values);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(String address, short value, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == 3) {
                mAddress.setFunction(defaultFunction);
            }
            if (mAddress.getWriteFunction() == 16) {
                ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
                byte[] buffer = new byte[]{BitConverter.GetBytes(value)[1], BitConverter.GetBytes(value)[0]};
                return ModbusInfo.BuildWriteWordModbusCommand(mAddress, buffer);
            }
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            return ModbusInfo.BuildWriteOneRegisterModbusCommand(mAddress, value);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteMaskModbusCommand(String address, short andMask, short orMask, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, station, defaultFunction);
            if (mAddress.getFunction() == 3) {
                mAddress.setFunction(defaultFunction);
            }
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            return ModbusInfo.BuildWriteMaskModbusCommand(mAddress, andMask, orMask);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteWordModbusCommand(ModbusAddress mAddress, byte[] values) {
        byte[] content = new byte[7 + values.length];
        content[0] = (byte)mAddress.getStation();
        content[1] = mAddress.getWriteFunction() < 0 ? (byte)mAddress.getFunction() : (byte)mAddress.getWriteFunction();
        content[2] = Utilities.getBytes(mAddress.getAddress())[1];
        content[3] = Utilities.getBytes(mAddress.getAddress())[0];
        content[4] = (byte)(values.length / 2 / 256);
        content[5] = (byte)(values.length / 2 % 256);
        content[6] = (byte)values.length;
        System.arraycopy(values, 0, content, 7, values.length);
        return OperateResultExOne.CreateSuccessResult(content);
    }

    public static OperateResultExOne<byte[]> BuildWriteMaskModbusCommand(ModbusAddress mAddress, short andMask, short orMask) {
        byte[] content = new byte[]{(byte)mAddress.getStation(), (byte)mAddress.getFunction(), Utilities.getBytes(mAddress.getAddress())[1], Utilities.getBytes(mAddress.getAddress())[0], Utilities.getBytes(andMask)[1], Utilities.getBytes(andMask)[0], Utilities.getBytes(orMask)[1], Utilities.getBytes(orMask)[0]};
        return OperateResultExOne.CreateSuccessResult(content);
    }

    public static OperateResultExOne<byte[]> BuildWriteOneRegisterModbusCommand(ModbusAddress mAddress, short value) {
        byte[] content = new byte[]{(byte)mAddress.getStation(), (byte)mAddress.getFunction(), Utilities.getBytes(mAddress.getAddress())[1], Utilities.getBytes(mAddress.getAddress())[0], Utilities.getBytes(value)[1], Utilities.getBytes(value)[0]};
        return OperateResultExOne.CreateSuccessResult(content);
    }

    public static OperateResultExOne<byte[]> BuildReadWriteModbusCommand(String readAddress, short length, String writeAddress, byte[] value, byte station, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(readAddress, station, defaultFunction);
            ModbusInfo.CheckModbusAddressStart(mAddress, isStartWithZero);
            ModbusAddress mAddress2 = new ModbusAddress(writeAddress, station, defaultFunction);
            ModbusInfo.CheckModbusAddressStart(mAddress2, isStartWithZero);
            byte[] buffer = new byte[11 + value.length];
            buffer[0] = (byte)mAddress.getStation();
            buffer[1] = (byte)mAddress.getFunction();
            buffer[2] = BitConverter.GetBytes(mAddress.getAddress())[1];
            buffer[3] = BitConverter.GetBytes(mAddress.getAddress())[0];
            buffer[4] = BitConverter.GetBytes(length)[1];
            buffer[5] = BitConverter.GetBytes(length)[0];
            buffer[6] = BitConverter.GetBytes(mAddress2.getAddress())[1];
            buffer[7] = BitConverter.GetBytes(mAddress2.getAddress())[0];
            buffer[8] = (byte)(value.length / 2 / 256);
            buffer[9] = (byte)(value.length / 2 % 256);
            buffer[10] = (byte)value.length;
            Utilities.ByteArrayCopyTo(value, buffer, 11);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response) {
        try {
            if ((response[1] & 0xFF) >= 128) {
                return new OperateResultExOne<byte[]>(response[2], ModbusInfo.GetDescriptionByErrorCode(response[2]));
            }
            if (response.length > 3) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 3));
            }
            return OperateResultExOne.CreateSuccessResult(new byte[0]);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static byte[] PackCommandToTcp(byte[] value, int id) {
        byte[] buffer = new byte[value.length + 6];
        buffer[0] = Utilities.getBytes(id)[1];
        buffer[1] = Utilities.getBytes(id)[0];
        buffer[4] = Utilities.getBytes(value.length)[1];
        buffer[5] = Utilities.getBytes(value.length)[0];
        System.arraycopy(value, 0, buffer, 6, value.length);
        return buffer;
    }

    public static byte[] ExplodeTcpCommandToCore(byte[] value) {
        return SoftBasic.BytesArrayRemoveBegin(value, 6);
    }

    public static byte[] ExplodeRtuCommandToCore(byte[] value) {
        return SoftBasic.BytesArrayRemoveLast(value, 2);
    }

    public static byte[] PackCommandToRtu(byte[] value) {
        return SoftCRC16.CRC16(value);
    }

    public static byte[] TransRtuToAsciiPackCommand(byte[] value) {
        byte[] modbus_lrc = SoftLRC.LRC(value);
        byte[] modbus_ascii = SoftBasic.BytesToAsciiBytes(modbus_lrc);
        return SoftBasic.SpliceTwoByteArray(new byte[]{58}, modbus_ascii, new byte[]{13, 10});
    }

    public static OperateResultExOne<byte[]> TransAsciiPackCommandToRtu(byte[] value) {
        try {
            if (value[0] != 58 || value[value.length - 2] != 13 || value[value.length - 1] != 10) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusAsciiFormatCheckFailed() + SoftBasic.ByteToHexString(value, ' '));
            }
            byte[] modbus_core = SoftBasic.AsciiBytesToBytes(SoftBasic.BytesArrayRemoveDouble(value, 1, 2));
            if (!SoftLRC.CheckLRC(modbus_core)) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusLRCCheckFailed() + SoftBasic.ByteToHexString(modbus_core, ' '));
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveLast(modbus_core, 1));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + SoftBasic.ByteToHexString(value, ' '));
        }
    }

    public static OperateResultExOne<ModbusAddress> AnalysisAddress(String address, byte defaultStation, boolean isStartWithZero, byte defaultFunction) {
        try {
            ModbusAddress mAddress = new ModbusAddress(address, defaultStation, defaultFunction);
            if (!isStartWithZero) {
                if (mAddress.getAddress() < 1) {
                    throw new Exception(StringResources.Language.ModbusAddressMustMoreThanOne());
                }
                mAddress.setAddress((short)(mAddress.getAddress() - 1));
            }
            return OperateResultExOne.CreateSuccessResult(mAddress);
        }
        catch (Exception ex) {
            return new OperateResultExOne<ModbusAddress>(ex.getMessage());
        }
    }

    public static String GetDescriptionByErrorCode(byte code) {
        switch (code) {
            case 1: {
                return StringResources.Language.ModbusTcpFunctionCodeNotSupport();
            }
            case 2: {
                return StringResources.Language.ModbusTcpFunctionCodeOverBound();
            }
            case 3: {
                return StringResources.Language.ModbusTcpFunctionCodeQuantityOver();
            }
            case 4: {
                return StringResources.Language.ModbusTcpFunctionCodeReadWriteException();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadFileModbusCommand(int fileNumber, int address, int length, byte station, boolean addressStartWithZero) {
        OperateResultExTwo<int[], int[]> bytes = HslHelper.SplitReadLength(address, (short)length, (short)(Authorization.asdniasnfaksndiqwhawfskhfaiw() ? 124 : Integer.MAX_VALUE));
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        for (int i = 0; i < ((int[])bytes.Content1).length; ++i) {
            byte[] buffer = new byte[10];
            buffer[0] = station;
            buffer[1] = 20;
            buffer[2] = (byte)(buffer.length - 3);
            buffer[3] = 6;
            buffer[4] = BitConverter.GetBytes(fileNumber)[1];
            buffer[5] = BitConverter.GetBytes(fileNumber)[0];
            buffer[6] = BitConverter.GetBytes(((int[])bytes.Content1)[i])[1];
            buffer[7] = BitConverter.GetBytes(((int[])bytes.Content1)[i])[0];
            buffer[8] = BitConverter.GetBytes(((int[])bytes.Content2)[i])[1];
            buffer[9] = BitConverter.GetBytes(((int[])bytes.Content2)[i])[0];
            list.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(list);
    }

    public static OperateResultExOne<byte[]> BuildWriteFileModbusCommand(int fileNumber, int address, byte[] data, byte station, boolean addressStartWithZero) {
        if (data.length + 7 > 255) {
            return new OperateResultExOne<byte[]>("the data length must less than 248 bytes( 124 word)");
        }
        MemoryStream memoryStream = new MemoryStream();
        memoryStream.WriteByte(station);
        memoryStream.WriteByte(21);
        memoryStream.WriteByte((byte)(7 + data.length));
        memoryStream.WriteByte(6);
        memoryStream.WriteByte(BitConverter.GetBytes(fileNumber)[1]);
        memoryStream.WriteByte(BitConverter.GetBytes(fileNumber)[0]);
        memoryStream.WriteByte(BitConverter.GetBytes(address)[1]);
        memoryStream.WriteByte(BitConverter.GetBytes(address)[0]);
        memoryStream.WriteByte(BitConverter.GetBytes(data.length / 2)[1]);
        memoryStream.WriteByte(BitConverter.GetBytes(data.length / 2)[0]);
        memoryStream.Write(data);
        return OperateResultExOne.CreateSuccessResult(memoryStream.ToArray());
    }
}

