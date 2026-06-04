/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.ReadWriteNetHelper;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.ModBus.IModbus;
import HslCommunication.ModBus.ModbusInfo;
import HslCommunication.Serial.SoftCRC16;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ModbusHelper {
    public static OperateResultExOne<byte[]> ExtraRtuResponseContent(byte[] send, byte[] response, boolean crcCheck) {
        if (response == null || response.length < 5) {
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "5 Content: " + SoftBasic.ByteToHexString(response, ' '));
        }
        byte[] responseCopy = Utilities.CopyFrom(response);
        for (int i = 0; i < 2; ++i) {
            if (responseCopy[1] == 1 || responseCopy[1] == 2 || responseCopy[1] == 3 || responseCopy[1] == 4 || responseCopy[1] == 23) {
                if (responseCopy.length > 5 + (responseCopy[2] & 0xFF)) {
                    responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 5 + (responseCopy[2] & 0xFF));
                }
            } else if (responseCopy[1] == 5 || responseCopy[1] == 6 || responseCopy[1] == 15 || responseCopy[1] == 16) {
                if (responseCopy.length > 8) {
                    responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 8);
                }
            } else if ((responseCopy[1] & 0xFF) > 128 && responseCopy.length > 5) {
                responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 5);
            }
            if (!crcCheck || SoftCRC16.CheckCRC16(responseCopy)) break;
            if (i != 0) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusCRCCheckFailed() + SoftBasic.ByteToHexString(response, ' '));
            }
            responseCopy = SoftBasic.BytesArrayRemoveBegin(response, 1);
        }
        if (send[0] != responseCopy[0]) {
            return new OperateResultExOne<byte[]>("Station not match, request: " + send[0] + ", but response is " + responseCopy[0]);
        }
        if ((send[1] & 0xFF) + 128 == (responseCopy[1] & 0xFF)) {
            return new OperateResultExOne<byte[]>(responseCopy[2] & 0xFF, ModbusInfo.GetDescriptionByErrorCode(responseCopy[2]));
        }
        if (send[1] != responseCopy[1]) {
            return new OperateResultExOne<byte[]>(responseCopy[1], "Receive Command Check Failed: ");
        }
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeRtuCommandToCore(responseCopy));
    }

    private static boolean CheckFileAddress(String address) {
        Pattern pattern = Pattern.compile("file=", 2);
        return pattern.matcher(address).find();
    }

    public static OperateResultExOne<byte[]> Read(IModbus modbus, String address, short length) {
        if (ModbusHelper.CheckFileAddress(address)) {
            int fileNumber = 0;
            int station = modbus.getStation();
            OperateResultExTwo<Integer, String> extra1 = HslHelper.ExtractParameter(address, "file");
            if (extra1.IsSuccess) {
                fileNumber = (Integer)extra1.Content1;
                address = (String)extra1.Content2;
            }
            OperateResultExTwo<Integer, String> extra2 = HslHelper.ExtractParameter(address, "s");
            if (extra2.IsSuccess) {
                station = (Integer)extra2.Content1;
                address = (String)extra2.Content2;
            }
            return ModbusHelper.ReadFile(modbus, (byte)station, fileNumber, Integer.parseInt(address), length);
        }
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)3);
        if (!modbusAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(modbusAddress);
        }
        OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand((String)modbusAddress.Content, length, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)3);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> resultArray = new ArrayList<Byte>();
        for (int i = 0; i < ((byte[][])command.Content).length; ++i) {
            OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(((byte[][])command.Content)[i]);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(resultArray, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(resultArray));
    }

    public static OperateResult Write(IModbus modbus, String address, byte[] value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)16);
        if (!modbusAddress.IsSuccess) {
            return modbusAddress;
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand((String)modbusAddress.Content, value, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)16);
        if (!command.IsSuccess) {
            return command;
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, short value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)6);
        if (!modbusAddress.IsSuccess) {
            return modbusAddress;
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand((String)modbusAddress.Content, value, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)6);
        if (!command.IsSuccess) {
            return command;
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }

    public static OperateResult WriteMask(IModbus modbus, String address, short andMask, short orMask) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)22);
        if (!modbusAddress.IsSuccess) {
            return modbusAddress;
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteMaskModbusCommand((String)modbusAddress.Content, andMask, orMask, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)22);
        if (!command.IsSuccess) {
            return command;
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }

    public static OperateResultExOne<boolean[]> ReadBoolHelper(IModbus modbus, String address, short length, byte function) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, function);
        if (!modbusAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(modbusAddress);
        }
        if (((String)modbusAddress.Content).indexOf(46) > 0) {
            String[] addressSplits = Utilities.SplitDot(address);
            int bitIndex = 0;
            try {
                String[] modbusSplits = Utilities.SplitDot((String)modbusAddress.Content);
                bitIndex = Integer.parseInt(modbusSplits[1]);
            }
            catch (Exception ex) {
                return new OperateResultExOne<boolean[]>("Bit Index format wrong, " + ex.getMessage());
            }
            short len = (short)((length + bitIndex + 15) / 16);
            OperateResultExOne<byte[]> read = modbus.Read(addressSplits[0], len);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord((byte[])read.Content)), bitIndex, length));
        }
        OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand((String)modbusAddress.Content, length, modbus.getStation(), modbus.getAddressStartWithZero(), function);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Boolean> resultArray = new ArrayList<Boolean>();
        for (int i = 0; i < ((byte[][])command.Content).length; ++i) {
            OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(((byte[][])command.Content)[i]);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            int bitLength = (((byte[][])command.Content)[i][4] & 0xFF) * 256 + (((byte[][])command.Content)[i][5] & 0xFF);
            Utilities.ArrayListAddArray(resultArray, SoftBasic.ByteToBoolArray((byte[])read.Content, bitLength));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBools(resultArray));
    }

    public static OperateResult Write(IModbus modbus, String address, boolean[] values) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)15);
        if (!modbusAddress.IsSuccess) {
            return modbusAddress;
        }
        if (((String)modbusAddress.Content).indexOf(46) > 0) {
            return ReadWriteNetHelper.WriteBoolWithWord(modbus, address, values, 16, true, Utilities.SplitDot((String)modbusAddress.Content)[1]);
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand((String)modbusAddress.Content, values, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)15);
        if (!command.IsSuccess) {
            return command;
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, boolean value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, (byte)5);
        if (!modbusAddress.IsSuccess) {
            return modbusAddress;
        }
        if (address.indexOf(46) > 0 && !modbus.getEnableWriteMaskCode()) {
            return ModbusHelper.Write(modbus, address, new boolean[]{value});
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand((String)modbusAddress.Content, value, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)5);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> write = modbus.ReadFromCoreServer((byte[])command.Content);
        if (write.IsSuccess) {
            return write;
        }
        if (address.indexOf(46) > 0 && write.ErrorCode == 1) {
            modbus.setEnableWriteMaskCode(false);
            return ModbusHelper.Write(modbus, address, new boolean[]{value});
        }
        return write;
    }

    public static OperateResultExOne<byte[]> ReadWrite(IModbus modbus, String readAddress, short length, String writeAddress, byte[] value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(readAddress, (byte)23);
        if (!modbusAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(modbusAddress);
        }
        OperateResultExOne<String> modbusAddress2 = modbus.TranslateToModbusAddress(writeAddress, (byte)23);
        if (!modbusAddress2.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(modbusAddress2);
        }
        OperateResultExOne<byte[]> command = ModbusInfo.BuildReadWriteModbusCommand((String)modbusAddress.Content, length, (String)modbusAddress2.Content, value, modbus.getStation(), modbus.getAddressStartWithZero(), (byte)23);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }

    public static String TransAddressToModbus(String station, String address, String[] code, int[] offset, FunctionOperateExOne<String, Integer> prase) throws Exception {
        for (int i = 0; i < code.length; ++i) {
            if (!address.matches("^" + code[i] + "[0-9]+")) continue;
            return station + (prase.Action(address.substring(code[i].length())) + offset[i]);
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }

    public static String TransPointAddressToModbus(String station, String address, String[] code, int[] offset, FunctionOperateExOne<String, Integer> prase) throws Exception {
        int index = address.indexOf(46);
        if (index > 0) {
            String tail = address.substring(index);
            address = address.substring(0, index);
            return ModbusHelper.TransAddressToModbus(station, address, code, offset, prase) + tail;
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }

    public static OperateResultExOne<byte[]> ReadFile(IModbus modbus, byte station, int fileNumber, int address, int length) {
        OperateResultExOne<ArrayList<byte[]>> command = ModbusInfo.BuildReadFileModbusCommand(fileNumber, address, length, station, modbus.getAddressStartWithZero());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return ModbusHelper.CreateReadFileResult(modbus.ReadFromCoreServer((ArrayList)command.Content));
    }

    private static OperateResultExOne<byte[]> CreateReadFileResult(OperateResultExOne<byte[]> read) {
        byte len;
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content).length < 2) {
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "2");
        }
        MemoryStream ms = new MemoryStream();
        for (int index = 0; index < ((byte[])read.Content).length; index += len + 1) {
            len = ((byte[])read.Content)[index];
            ms.Write(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, index + 2, len - 1));
        }
        return OperateResultExOne.CreateSuccessResult(ms.ToArray());
    }

    public static OperateResult WriteFile(IModbus modbus, byte station, int fileNumber, int address, byte[] data) {
        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteFileModbusCommand(fileNumber, address, data, station, modbus.getAddressStartWithZero());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return modbus.ReadFromCoreServer((byte[])command.Content);
    }
}

