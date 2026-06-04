/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.OmronFinsAddress;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.ReadWriteNetHelper;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.HslExtension;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Omron.IOmronFins;
import HslCommunication.Profinet.Omron.OmronCpuUnitData;
import HslCommunication.Profinet.Omron.OmronCpuUnitStatus;
import HslCommunication.Profinet.Omron.OmronPlcType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;
import java.util.Date;

public class OmronFinsNetHelper {
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(String[] address, OmronPlcType plcType) {
        ArrayList<byte[]> cmds = new ArrayList<byte[]>();
        ArrayList<String[]> splits = SoftBasic.ArraySplitByLength(address, 89);
        for (int i = 0; i < splits.size(); ++i) {
            String[] adds = splits.get(i);
            byte[] _PLCCommand = new byte[2 + 4 * adds.length];
            _PLCCommand[0] = 1;
            _PLCCommand[1] = 4;
            for (int j = 0; j < adds.length; ++j) {
                OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(adds[j], (short)1, plcType);
                if (!analysis.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(analysis);
                }
                _PLCCommand[2 + 4 * j] = ((OmronFinsAddress)analysis.Content).getWordCode();
                _PLCCommand[3 + 4 * j] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() / 16 / 256);
                _PLCCommand[4 + 4 * j] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() / 16 % 256);
                _PLCCommand[5 + 4 * j] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() % 16);
            }
            cmds.add(_PLCCommand);
        }
        return OperateResultExOne.CreateSuccessResult(cmds);
    }

    public static byte[] BuildReadCommand(OmronFinsAddress address, short length, boolean isBit) {
        byte[] _PLCCommand = new byte[]{1, 1, isBit ? address.getBitCode() : address.getWordCode(), (byte)(address.getAddressStart() / 16 / 256), (byte)(address.getAddressStart() / 16 % 256), (byte)(address.getAddressStart() % 16), (byte)(length / 256), (byte)(length % 256)};
        return _PLCCommand;
    }

    public static OperateResultExOne<byte[][]> BuildReadCommand(OmronPlcType plcType, String address, short length, boolean isBit, int splitLength) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, length, plcType);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        ArrayList<byte[]> cmds = new ArrayList<byte[]>();
        int[] lengths = SoftBasic.SplitIntegerToArray(length, isBit ? 1998 : splitLength);
        for (int i = 0; i < lengths.length; ++i) {
            cmds.add(OmronFinsNetHelper.BuildReadCommand((OmronFinsAddress)analysis.Content, (short)lengths[i], isBit));
            ((OmronFinsAddress)analysis.Content).setAddressStart(((OmronFinsAddress)analysis.Content).getAddressStart() + (isBit ? lengths[i] : lengths[i] * 16));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToArray(cmds));
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(OmronPlcType plcType, String address, byte[] value, boolean isBit) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, (short)0, plcType);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[8 + value.length];
        _PLCCommand[0] = 1;
        _PLCCommand[1] = 2;
        _PLCCommand[2] = isBit ? ((OmronFinsAddress)analysis.Content).getBitCode() : ((OmronFinsAddress)analysis.Content).getWordCode();
        _PLCCommand[3] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() / 16 / 256);
        _PLCCommand[4] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() / 16 % 256);
        _PLCCommand[5] = (byte)(((OmronFinsAddress)analysis.Content).getAddressStart() % 16);
        if (isBit) {
            _PLCCommand[6] = (byte)(value.length / 256);
            _PLCCommand[7] = (byte)(value.length % 256);
        } else {
            _PLCCommand[6] = (byte)(value.length / 2 / 256);
            _PLCCommand[7] = (byte)(value.length / 2 % 256);
        }
        System.arraycopy(value, 0, _PLCCommand, 8, value.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> ResponseValidAnalysis(byte[] response) {
        if (response.length >= 16) {
            byte[] buffer = new byte[]{response[15], response[14], response[13], response[12]};
            int err = Utilities.getInt(buffer, 0);
            if (err > 0) {
                return new OperateResultExOne<byte[]>(err, OmronFinsNetHelper.GetStatusDescription(err));
            }
            byte[] result = new byte[response.length - 16];
            System.arraycopy(response, 16, result, 0, result.length);
            return OmronFinsNetHelper.UdpResponseValidAnalysis(result);
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    public static OperateResultExOne<byte[]> UdpResponseValidAnalysis(byte[] response) {
        if (response.length >= 14) {
            int err = (response[12] & 0xFF) * 256 + (response[13] & 0xFF);
            if (HslExtension.GetBoolByIndex(response[12], 7)) {
                int mainCode = response[12] & 0x7F;
                int subCode = response[13] & 0x3F;
                return new OperateResultExOne<byte[]>(err, OmronFinsNetHelper.GetEndCodeDescription(mainCode, subCode));
            }
            if (response[10] == 1 & response[11] == 1 || response[10] == 1 & response[11] == 4 || response[10] == 2 & response[11] == 1 || response[10] == 3 & response[11] == 6 || response[10] == 5 & response[11] == 1 || response[10] == 5 & response[11] == 2 || response[10] == 6 & response[11] == 1 || response[10] == 6 & response[11] == 32 || response[10] == 7 & response[11] == 1 || response[10] == 9 & response[11] == 32 || response[10] == 33 & response[11] == 2 || response[10] == 34 & response[11] == 2) {
                try {
                    byte[] content = new byte[response.length - 14];
                    if (content.length > 0) {
                        System.arraycopy(response, 14, content, 0, content.length);
                    }
                    OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(content);
                    if (content.length == 0) {
                        success.IsSuccess = false;
                    }
                    success.ErrorCode = err;
                    success.Message = OmronFinsNetHelper.GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                    if (response[10] == 1 & response[11] == 4) {
                        byte[] buffer = content.length > 0 ? new byte[content.length * 2 / 3] : new byte[]{};
                        for (int i = 0; i < content.length / 3; ++i) {
                            buffer[i * 2 + 0] = content[i * 3 + 1];
                            buffer[i * 2 + 1] = content[i * 3 + 2];
                        }
                        success.Content = buffer;
                    }
                    return success;
                }
                catch (Exception ex) {
                    return new OperateResultExOne<byte[]>("UdpResponseValidAnalysis failed: " + ex.getMessage() + "\r\nContent: " + SoftBasic.ByteToHexString(response, ' '));
                }
            }
            OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(new byte[0]);
            success.ErrorCode = err;
            success.Message = OmronFinsNetHelper.GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
            return success;
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    public static String GetStatusDescription(int err) {
        switch (err) {
            case 0: {
                return StringResources.Language.OmronStatus0();
            }
            case 1: {
                return StringResources.Language.OmronStatus1();
            }
            case 2: {
                return StringResources.Language.OmronStatus2();
            }
            case 3: {
                return StringResources.Language.OmronStatus3();
            }
            case 20: {
                return StringResources.Language.OmronStatus20();
            }
            case 21: {
                return StringResources.Language.OmronStatus21();
            }
            case 22: {
                return StringResources.Language.OmronStatus22();
            }
            case 23: {
                return StringResources.Language.OmronStatus23();
            }
            case 24: {
                return StringResources.Language.OmronStatus24();
            }
            case 25: {
                return StringResources.Language.OmronStatus25();
            }
        }
        return StringResources.Language.UnknownError();
    }

    private static String GetEndCodeDescription(int mainCode, int subCode) {
        if (mainCode == 0) {
            if (subCode == 0) {
                return "Normal completion";
            }
            if (subCode == 1) {
                return "Data link status: Service was canceled";
            }
        } else if (mainCode == 1) {
            if (subCode == 1) {
                return "Local node is not participating in the network.";
            }
            if (subCode == 2) {
                return "Token does not arrive. [Set the local node to within the maximum node address.]";
            }
            if (subCode == 3) {
                return "Send was not possible during the specified number of retries.";
            }
            if (subCode == 4) {
                return "Cannot send because maximum number of event frames exceeded.";
            }
            if (subCode == 5) {
                return "Node address setting error occurred";
            }
            if (subCode == 6) {
                return "The same node address has been set twice in the same network";
            }
        } else if (mainCode == 2) {
            if (subCode == 1) {
                return "The destination node is not in the network";
            }
            if (subCode == 2) {
                return "There is no Unit with the specified unit address.";
            }
            if (subCode == 3) {
                return "The third node does not exist";
            }
            if (subCode == 4) {
                return "The destination node is busy";
            }
            if (subCode == 5) {
                return "The message was destroyed by noise.";
            }
        } else if (mainCode == 3) {
            if (subCode == 1) {
                return "An error occurred in the communications controller";
            }
            if (subCode == 2) {
                return "A CPU error occurred in the destination CPU Unit.";
            }
            if (subCode == 3) {
                return "A response was not returned because an error occurred in the Board";
            }
            if (subCode == 4) {
                return "The unit number was set incorrectly";
            }
        } else if (mainCode == 4) {
            if (subCode == 1) {
                return "The Unit/Board does not support the specified command code";
            }
            if (subCode == 2) {
                return "The command cannot be executed because the model or version is incorrect";
            }
        } else if (mainCode == 5) {
            if (subCode == 1) {
                return "The destination network or node address is not set in the routing tables";
            }
            if (subCode == 2) {
                return "Relaying is not possible because there are no routing tables";
            }
            if (subCode == 3) {
                return "There is an error in the routing tables";
            }
            if (subCode == 4) {
                return "An attempt was made to send to a network that was over 3 networks away";
            }
        } else if (mainCode == 16) {
            if (subCode == 1) {
                return "The command is longer than the maximum permissible length";
            }
            if (subCode == 2) {
                return "The command is shorter than the minimum permissible length";
            }
            if (subCode == 3) {
                return "The designated number of elements differs from the number of write data items";
            }
            if (subCode == 4) {
                return "An incorrect format was used";
            }
        } else if (mainCode == 17) {
            if (subCode == 1) {
                return "The specified word does not exist in the memory area or there is no EM Area";
            }
            if (subCode == 2) {
                return "The access size specification is incorrect or an odd word address is specified";
            }
            if (subCode == 3) {
                return "The start address in command process is beyond the accessible area";
            }
            if (subCode == 4) {
                return "The end address in command process is beyond the accessible area";
            }
        } else if (mainCode == 32) {
            if (subCode == 2) {
                return "The program area is protected";
            }
            if (subCode == 4) {
                return "The search data does not exist.";
            }
            if (subCode == 5) {
                return "A non-existing program number has been specified";
            }
        } else if (mainCode == 33) {
            if (subCode == 1) {
                return "The specified area is read-only.";
            }
            if (subCode == 2) {
                return "The program area is protected.";
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> Read(IOmronFins omron, String address, short length, int splits) {
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(omron.getPlcType(), address, length, false, splits);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> contentArray = new ArrayList<Byte>();
        for (int i = 0; i < ((byte[][])command.Content).length; ++i) {
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(((byte[][])command.Content)[i]);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(contentArray, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    public static OperateResultExOne<byte[]> Read(IOmronFins omron, String[] address) {
        OperateResultExOne<ArrayList<byte[]>> command = OmronFinsNetHelper.BuildReadCommand(address, omron.getPlcType());
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> contentArray = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(contentArray, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    public static OperateResult Write(IOmronFins omron, String address, byte[] value) {
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(omron.getPlcType(), address, value, false);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<boolean[]> ReadBool(IOmronFins omron, String address, short length, int splits) {
        boolean[] array;
        if (address.startsWith("DR") || address.startsWith("dr") || address.startsWith("IR") || address.startsWith("ir")) {
            if (!address.contains(".")) {
                address = address + ".0";
            }
            return HslHelper.ReadBool(omron, address, (short)length, 16, true);
        }
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, (short)length, omron.getPlcType());
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        ArrayList<Boolean> contentArray = new ArrayList<Boolean>();
        for (int len = 0; len < length; len += array.length) {
            byte[] cmd = OmronFinsNetHelper.BuildReadCommand((OmronFinsAddress)analysis.Content, (short)(length - len), true);
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(cmd);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content).length == 0) {
                return new OperateResultExOne<boolean[]>(read.ErrorCode, read.Message);
            }
            array = Utilities.getBoolArray((byte[])read.Content);
            Utilities.ArrayListAddArray(contentArray, array);
            ((OmronFinsAddress)analysis.Content).setAddressStart(((OmronFinsAddress)analysis.Content).getAddressStart() + array.length);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(contentArray));
    }

    public static OperateResult Write(IOmronFins omron, String address, boolean[] values) {
        if (address.startsWith("DR") || address.startsWith("dr") || address.startsWith("IR") || address.startsWith("ir")) {
            return new OperateResult("DR and IR address not support bit write");
        }
        if (omron.getPlcType() == OmronPlcType.CV && (address.startsWith("CIO") || address.startsWith("C"))) {
            return ReadWriteNetHelper.WriteBoolWithWord(omron, address, values, 16, true, "");
        }
        byte[] array = new byte[values.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = values[i] ? (byte)1 : 0;
        }
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(omron.getPlcType(), address, array, true);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    private static OperateResultExOne<Date> CreatePlcTime(byte[] buffer) {
        try {
            String hex = SoftBasic.ByteToHexString(buffer);
            Date current = new Date();
            int year = Convert.ToInt32(String.valueOf(current.getYear() + 1900).substring(0, 2) + hex.substring(0, 2));
            return OperateResultExOne.CreateSuccessResult(Utilities.getDateFrom(year, Convert.ToInt32(hex.substring(2, 4)), Convert.ToInt32(hex.substring(4, 6)), Convert.ToInt32(hex.substring(6, 8)), Convert.ToInt32(hex.substring(8, 10)), Convert.ToInt32(hex.substring(10, 12))));
        }
        catch (Exception ex) {
            return new OperateResultExOne<Date>("Prase Time failed: " + ex.getMessage() + Environment.NewLine + "Source: " + SoftBasic.ByteToHexString(buffer, ' '));
        }
    }

    public static OperateResult Run(IReadWriteDevice omron) {
        return omron.ReadFromCoreServer(new byte[]{4, 1, -1, -1, 4});
    }

    public static OperateResult Stop(IReadWriteDevice omron) {
        return omron.ReadFromCoreServer(new byte[]{4, 2, -1, -1});
    }

    public static OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData(IReadWriteDevice omron) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<OmronCpuUnitData>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{5, 1, 0});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(new OmronCpuUnitData((byte[])read.Content));
    }

    public static OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus(IReadWriteDevice omron) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<OmronCpuUnitStatus>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{6, 1});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(new OmronCpuUnitStatus((byte[])read.Content));
    }

    public static OperateResultExOne<Date> ReadCpuTime(IReadWriteDevice omron) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<Date>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{7, 1});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OmronFinsNetHelper.CreatePlcTime((byte[])read.Content);
    }
}

