/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.AllenBradley.AllenBradleyDF1Serial;
import HslCommunication.Profinet.AllenBradley.IReadWriteCip;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AllenBradleyHelper {
    public static final int CIP_Execute_PCCC = 75;
    public static final int CIP_READ_DATA = 76;
    public static final int CIP_WRITE_DATA = 77;
    public static final int CIP_READ_WRITE_DATA = 78;
    public static final int CIP_READ_FRAGMENT = 82;
    public static final int CIP_WRITE_FRAGMENT = 83;
    public static final int CIP_READ_LIST = 85;
    public static final int CIP_MULTIREAD_DATA = 4096;
    public static final int CIP_Type_DATE = 8;
    public static final int CIP_Type_TIME = 9;
    public static final int CIP_Type_TimeAndDate = 10;
    public static final int CIP_Type_TimeOfDate = 11;
    public static final int CIP_Type_Bool = 193;
    public static final int CIP_Type_Byte = 194;
    public static final int CIP_Type_Word = 195;
    public static final int CIP_Type_DWord = 196;
    public static final int CIP_Type_LInt = 197;
    public static final int CIP_Type_USInt = 198;
    public static final int CIP_Type_UInt = 199;
    public static final int CIP_Type_UDint = 200;
    public static final int CIP_Type_ULint = 201;
    public static final int CIP_Type_Real = 202;
    public static final int CIP_Type_Double = 203;
    public static final int CIP_Type_Struct = 204;
    public static final int CIP_Type_String = 208;
    public static final int CIP_Type_D1 = 209;
    public static final int CIP_Type_D2 = 210;
    public static final int CIP_Type_BitArray = 211;
    public static final short OriginatorVendorID = 4105;
    public static final int OriginatorSerialNumber = -1046133237;

    private static byte[] BuildRequestPathCommand(String address) {
        return AllenBradleyHelper.BuildRequestPathCommand(address, false);
    }

    private static byte[] BuildRequestPathCommand(String address, boolean isConnectedAddress) {
        MemoryStream ms = new MemoryStream();
        int classid = -1;
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "class");
        if (extra.IsSuccess) {
            classid = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        if (classid != -1) {
            int instanceid;
            int n = instanceid = address.startsWith("0x") ? Convert.ToInt32(address.substring(2), 16) : Convert.ToInt32(address);
            if (classid < 256) {
                ms.WriteByte(32);
                ms.WriteByte((byte)classid);
            } else {
                ms.WriteByte(33);
                ms.WriteByte(0);
                ms.WriteByte(BitConverter.GetBytes(classid)[0]);
                ms.WriteByte(BitConverter.GetBytes(classid)[1]);
            }
            if (instanceid < 256) {
                ms.WriteByte(36);
                ms.WriteByte((byte)instanceid);
            } else {
                ms.WriteByte(37);
                ms.WriteByte(0);
                ms.WriteByte(BitConverter.GetBytes(instanceid)[0]);
                ms.WriteByte(BitConverter.GetBytes(instanceid)[1]);
            }
        } else {
            String[] tagNames = address.split("\\.");
            for (int i = 0; i < tagNames.length; ++i) {
                String strIndex = "";
                int indexFirst = tagNames[i].indexOf(91);
                int indexSecond = tagNames[i].indexOf(93);
                if (indexFirst > 0 && indexSecond > 0 && indexSecond > indexFirst) {
                    strIndex = tagNames[i].substring(indexFirst + 1, indexSecond);
                    tagNames[i] = tagNames[i].substring(0, indexFirst);
                }
                ms.WriteByte(145);
                byte[] nameBytes = tagNames[i].getBytes(StandardCharsets.UTF_8);
                ms.WriteByte((byte)nameBytes.length);
                ms.Write(nameBytes, 0, nameBytes.length);
                if (nameBytes.length % 2 == 1) {
                    ms.WriteByte(0);
                }
                if (Utilities.IsStringNullOrEmpty(strIndex)) continue;
                String[] indexes = strIndex.split(",");
                for (int j = 0; j < indexes.length; ++j) {
                    int index = Integer.parseInt(indexes[j]);
                    if (index < 256 && !isConnectedAddress) {
                        ms.WriteByte(40);
                        ms.WriteByte((byte)index);
                        continue;
                    }
                    if (index < 65536) {
                        ms.WriteByte(41);
                        ms.WriteByte(0);
                        ms.WriteByte(BitConverter.GetBytes(index)[0]);
                        ms.WriteByte(BitConverter.GetBytes(index)[1]);
                        continue;
                    }
                    ms.WriteByte(42);
                    ms.WriteByte(0);
                    ms.Write(BitConverter.GetBytes(index));
                }
            }
        }
        return ms.ToArray();
    }

    public static byte[] PackRequsetRead(String address, int length) {
        return AllenBradleyHelper.PackRequsetRead(address, length, false);
    }

    public static byte[] PackRequsetRead(String address, int length, boolean isConnectedAddress) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = 76;
        byte[] requestPath = AllenBradleyHelper.BuildRequestPathCommand(address, isConnectedAddress);
        System.arraycopy(requestPath, 0, buffer, ++offset, requestPath.length);
        buffer[1] = (byte)(((offset += requestPath.length) - 2) / 2);
        buffer[offset++] = Utilities.getBytes(length)[0];
        buffer[offset++] = Utilities.getBytes(length)[1];
        byte[] data = new byte[offset];
        System.arraycopy(buffer, 0, data, 0, offset);
        return data;
    }

    public static byte[] PackRequestReadSegment(String address, int startIndex, int length) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = 82;
        byte[] requestPath = AllenBradleyHelper.BuildRequestPathCommand(address);
        System.arraycopy(requestPath, 0, buffer, ++offset, requestPath.length);
        buffer[1] = (byte)(((offset += requestPath.length) - 2) / 2);
        buffer[offset++] = Utilities.getBytes(length)[0];
        buffer[offset++] = Utilities.getBytes(length)[1];
        buffer[offset++] = Utilities.getBytes(startIndex)[0];
        buffer[offset++] = Utilities.getBytes(startIndex)[1];
        buffer[offset++] = Utilities.getBytes(startIndex)[2];
        buffer[offset++] = Utilities.getBytes(startIndex)[3];
        byte[] data = new byte[offset];
        System.arraycopy(buffer, 0, data, 0, offset);
        return data;
    }

    public static byte[] PackRequestWrite(String address, int typeCode, byte[] value) {
        return AllenBradleyHelper.PackRequestWrite(address, typeCode, value, 1);
    }

    public static byte[] PackRequestWrite(String address, int typeCode, byte[] value, int length) {
        return AllenBradleyHelper.PackRequestWrite(address, typeCode, value, length, false);
    }

    public static byte[] PackRequestWrite(String address, int typeCode, byte[] value, int length, boolean isConnectedAddress) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = 77;
        byte[] requestPath = AllenBradleyHelper.BuildRequestPathCommand(address, isConnectedAddress);
        System.arraycopy(requestPath, 0, buffer, ++offset, requestPath.length);
        buffer[1] = (byte)(((offset += requestPath.length) - 2) / 2);
        buffer[offset++] = Utilities.getBytes(typeCode)[0];
        buffer[offset++] = Utilities.getBytes(typeCode)[1];
        buffer[offset++] = Utilities.getBytes(length)[0];
        buffer[offset++] = Utilities.getBytes(length)[1];
        if (value == null) {
            value = new byte[]{};
        }
        byte[] data = new byte[value.length + offset];
        System.arraycopy(buffer, 0, data, 0, offset);
        Utilities.ByteArrayCopyTo(value, data, offset);
        return data;
    }

    public static byte[] PackRequestWriteSegment(String address, int typeCode, byte[] value, int startIndex, int length, boolean isConnectedAddress) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = 83;
        byte[] requestPath = AllenBradleyHelper.BuildRequestPathCommand(address, isConnectedAddress);
        Utilities.ByteArrayCopyTo(requestPath, buffer, ++offset);
        buffer[1] = (byte)(((offset += requestPath.length) - 2) / 2);
        buffer[offset++] = BitConverter.GetBytes(typeCode)[0];
        buffer[offset++] = BitConverter.GetBytes(typeCode)[1];
        buffer[offset++] = BitConverter.GetBytes(length)[0];
        buffer[offset++] = BitConverter.GetBytes(length)[1];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[0];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[1];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[2];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[3];
        if (value == null) {
            value = new byte[]{};
        }
        byte[] data = new byte[value.length + offset];
        Array.Copy(buffer, 0, data, 0, offset);
        Utilities.ByteArrayCopyTo(value, data, offset);
        return data;
    }

    public static byte[] PackRequestReadModifyWrite(String address, int orMask, int andMask, boolean isConnectedAddress) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = 78;
        byte[] requestPath = AllenBradleyHelper.BuildRequestPathCommand(address, isConnectedAddress);
        Utilities.ByteArrayCopyTo(requestPath, buffer, ++offset);
        buffer[1] = (byte)(((offset += requestPath.length) - 2) / 2);
        buffer[offset++] = 4;
        buffer[offset++] = 0;
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(orMask), buffer, offset);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(andMask), buffer, offset += 4);
        return SoftBasic.BytesArraySelectBegin(buffer, offset += 4);
    }

    public static byte[] PackRequestReadModifyWrite(String address, int index, boolean value) {
        return AllenBradleyHelper.PackRequestReadModifyWrite(address, index, value, false);
    }

    public static byte[] PackRequestReadModifyWrite(String address, int index, boolean value, boolean isConnectedAddress) {
        if (index / 32 != 0) {
            address = address + "[" + index / 32 + "]";
        }
        index %= 32;
        if (value) {
            int orMask = 1;
            return AllenBradleyHelper.PackRequestReadModifyWrite(address, orMask <<= index, -1, isConnectedAddress);
        }
        int andMask = 1;
        andMask <<= index;
        return AllenBradleyHelper.PackRequestReadModifyWrite(address, 0, andMask ^= 0xFFFFFFFF, isConnectedAddress);
    }

    public static byte[] PackRequestWrite(String address, boolean value) throws IOException {
        OperateResultExTwo<String, Integer> analysis = AllenBradleyHelper.AnalysisArrayIndex(address);
        address = (String)analysis.Content1;
        int bitIndex = (Integer)analysis.Content2;
        return AllenBradleyHelper.PackRequestReadModifyWrite(address, bitIndex, value, false);
    }

    public static OperateResultExTwo<String, Integer> AnalysisArrayIndex(String address) {
        int arrayIndex = 0;
        if (!address.endsWith("]")) {
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        }
        int index = address.lastIndexOf(91);
        if (index < 0) {
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        }
        address = address.substring(0, address.length() - 1);
        try {
            arrayIndex = Integer.parseInt(address.substring(index + 1));
            address = address.substring(0, index);
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        }
        catch (Exception ex) {
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        }
    }

    public static byte[] PackCommandService(byte[] portSlot, byte[] ... cips) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(178);
        ms.write(0);
        ms.write(0);
        ms.write(0);
        ms.write(82);
        ms.write(2);
        ms.write(32);
        ms.write(6);
        ms.write(36);
        ms.write(1);
        ms.write(10);
        ms.write(240);
        ms.write(0);
        ms.write(0);
        int count = 0;
        if (cips.length == 1) {
            ms.write(cips[0], 0, cips[0].length);
            count += cips[0].length;
            if (cips[0].length % 2 == 1) {
                ms.write(0);
            }
        } else {
            int i;
            ms.write(10);
            ms.write(2);
            ms.write(32);
            ms.write(2);
            ms.write(36);
            ms.write(1);
            count += 8;
            ms.write(Utilities.getBytes((short)cips.length), 0, 2);
            short offect = (short)(2 + 2 * cips.length);
            count += 2 * cips.length;
            for (i = 0; i < cips.length; ++i) {
                ms.write(Utilities.getBytes(offect), 0, 2);
                offect = (short)(offect + cips[i].length);
            }
            for (i = 0; i < cips.length; ++i) {
                ms.write(cips[i], 0, cips[i].length);
                count += cips[i].length;
            }
        }
        ms.write((byte)((portSlot.length + 1) / 2));
        ms.write(0);
        ms.write(portSlot, 0, portSlot.length);
        if (portSlot.length % 2 == 1) {
            ms.write(0);
        }
        byte[] data = ms.toByteArray();
        ms.close();
        data[12] = Utilities.getBytes(count)[0];
        data[13] = Utilities.getBytes(count)[1];
        data[2] = Utilities.getBytes((short)(data.length - 4))[0];
        data[3] = Utilities.getBytes((short)(data.length - 4))[1];
        return data;
    }

    public static byte[] PackCommandSingleService(byte[] command, int code, boolean isConnected) {
        if (command == null) {
            command = new byte[]{};
        }
        byte[] buffer = isConnected ? new byte[6 + command.length] : new byte[4 + command.length];
        buffer[0] = BitConverter.GetBytes(code)[0];
        buffer[1] = BitConverter.GetBytes(code)[1];
        buffer[2] = BitConverter.GetBytes(buffer.length - 4)[0];
        buffer[3] = BitConverter.GetBytes(buffer.length - 4)[1];
        Utilities.ByteArrayCopyTo(command, buffer, isConnected ? 6 : 4);
        return buffer;
    }

    public static byte[] PackCommandSpecificData(byte[] ... service) {
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(10);
        ms.WriteByte(0);
        ms.WriteByte(Utilities.getBytes(service.length)[0]);
        ms.WriteByte(Utilities.getBytes(service.length)[1]);
        for (int i = 0; i < service.length; ++i) {
            ms.Write(service[i], 0, service[i].length);
        }
        return ms.ToArray();
    }

    private static byte[] PackExecutePCCC(byte[] pccc) {
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(75);
        ms.WriteByte(2);
        ms.WriteByte(32);
        ms.WriteByte(103);
        ms.WriteByte(36);
        ms.WriteByte(1);
        ms.WriteByte(7);
        ms.WriteByte(9);
        ms.WriteByte(16);
        ms.WriteByte(11);
        ms.WriteByte(70);
        ms.WriteByte(165);
        ms.WriteByte(193);
        ms.Write(pccc);
        byte[] buffer = ms.ToArray();
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)4105), buffer, 7);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(-1046133237), buffer, 9);
        return buffer;
    }

    public static OperateResultExOne<byte[]> PackExecutePCCCRead(int tns, String address, short length) {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalReadWithThreeAddressFields(tns, address, length);
        if (!command.IsSuccess) {
            return command;
        }
        return OperateResultExOne.CreateSuccessResult(AllenBradleyHelper.PackExecutePCCC((byte[])command.Content));
    }

    public static OperateResultExOne<byte[]> PackExecutePCCCWrite(int tns, String address, byte[] value) {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalWriteWithThreeAddressFields(tns, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        return OperateResultExOne.CreateSuccessResult(AllenBradleyHelper.PackExecutePCCC((byte[])command.Content));
    }

    public static OperateResultExOne<byte[]> PackExecutePCCCWrite(int tns, String address, int bitIndex, boolean value) {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalMaskWithThreeAddressFields(tns, address, bitIndex, value);
        if (!command.IsSuccess) {
            return command;
        }
        return OperateResultExOne.CreateSuccessResult(AllenBradleyHelper.PackExecutePCCC((byte[])command.Content));
    }

    public static byte[] RegisterSessionHandle() {
        return AllenBradleyHelper.RegisterSessionHandle(null);
    }

    public static byte[] RegisterSessionHandle(byte[] senderContext) {
        byte[] commandSpecificData = new byte[]{1, 0, 0, 0};
        return AllenBradleyHelper.PackRequestHeader(101, 0, commandSpecificData, senderContext);
    }

    public static byte[] UnRegisterSessionHandle(int sessionHandle) {
        return AllenBradleyHelper.PackRequestHeader(102, sessionHandle, new byte[0]);
    }

    public static OperateResult CheckResponse(byte[] response) {
        try {
            int status = Utilities.getInt(response, 8);
            if (status == 0) {
                return OperateResult.CreateSuccessResult();
            }
            String msg = "";
            switch (status) {
                case 1: {
                    msg = StringResources.Language.AllenBradleySessionStatus01();
                    break;
                }
                case 2: {
                    msg = StringResources.Language.AllenBradleySessionStatus02();
                    break;
                }
                case 3: {
                    msg = StringResources.Language.AllenBradleySessionStatus03();
                    break;
                }
                case 100: {
                    msg = StringResources.Language.AllenBradleySessionStatus64();
                    break;
                }
                case 101: {
                    msg = StringResources.Language.AllenBradleySessionStatus65();
                    break;
                }
                case 105: {
                    msg = StringResources.Language.AllenBradleySessionStatus69();
                    break;
                }
                default: {
                    msg = StringResources.Language.UnknownError();
                }
            }
            return new OperateResult(status, msg);
        }
        catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    public static byte[] PackRequestHeader(int command, int session, byte[] commandSpecificData) {
        return AllenBradleyHelper.PackRequestHeader(command, session, commandSpecificData, null);
    }

    public static byte[] PackRequestHeader(int command, int session, byte[] commandSpecificData, byte[] senderContext) {
        if (commandSpecificData == null) {
            commandSpecificData = new byte[]{};
        }
        byte[] buffer = new byte[commandSpecificData.length + 24];
        System.arraycopy(commandSpecificData, 0, buffer, 24, commandSpecificData.length);
        System.arraycopy(Utilities.getBytes(command), 0, buffer, 0, 2);
        System.arraycopy(Utilities.getBytes(commandSpecificData.length), 0, buffer, 2, 2);
        if (senderContext != null) {
            Utilities.ByteArrayCopyTo(senderContext, buffer, 12);
        }
        System.arraycopy(Utilities.getBytes(session), 0, buffer, 4, 4);
        return buffer;
    }

    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead) {
        int i;
        ArrayList<Byte> data = new ArrayList<Byte>();
        int offset = 38;
        boolean hasMoreData = false;
        short dataType = 0;
        short count = Utilities.getShort(response, 38);
        if (Utilities.getInt(response, 40) == 138) {
            offset = 44;
            int dataCount = Utilities.getShort(response, offset);
            for (i = 0; i < dataCount; ++i) {
                int offectStart = Utilities.getShort(response, offset + 2 + i * 2) + offset;
                int offectEnd = i == dataCount - 1 ? response.length : Utilities.getShort(response, offset + 4 + i * 2) + offset;
                short err = Utilities.getShort(response, offectStart + 2);
                switch (err) {
                    case 4: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                    }
                    case 5: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                    }
                    case 6: {
                        if (response[offset + 2] != -46 && response[offset + 2] != -52) break;
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley06());
                    }
                    case 10: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                    }
                    case 12: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0C());
                    }
                    case 19: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                    }
                    case 28: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                    }
                    case 30: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                    }
                    case 38: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                    }
                    case 0: {
                        break;
                    }
                    default: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
                    }
                }
                if (!isRead) continue;
                for (int j = offectStart + 6; j < offectEnd; ++j) {
                    data.add(response[j]);
                }
            }
        } else {
            byte err = response[offset + 4];
            switch (err) {
                case 4: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                }
                case 5: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                }
                case 6: {
                    hasMoreData = true;
                    break;
                }
                case 10: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                }
                case 12: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0C());
                }
                case 19: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                }
                case 28: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                }
                case 30: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                }
                case 38: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                }
                case 0: {
                    break;
                }
                default: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
                }
            }
            if (response[offset + 2] == -51 || response[offset + 2] == -45) {
                return OperateResultExThree.CreateSuccessResult(new byte[0], dataType, hasMoreData);
            }
            if (response[offset + 2] == -52 || response[offset + 2] == -46) {
                for (i = offset + 8; i < offset + 2 + count; ++i) {
                    data.add(response[i]);
                }
                dataType = Utilities.getShort(response, offset + 6);
            } else if (response[offset + 2] == -43) {
                for (i = offset + 6; i < offset + 2 + count; ++i) {
                    data.add(response[i]);
                }
            }
        }
        byte[] buffer = new byte[data.size()];
        for (i = 0; i < buffer.length; ++i) {
            buffer[i] = (Byte)data.get(i);
        }
        return OperateResultExThree.CreateSuccessResult(buffer, dataType, hasMoreData);
    }

    public static OperateResultExOne<String> ReadPlcType(IReadWriteDevice plc) {
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 06 00 01 02 20 01 24 01");
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(buffer);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (((byte[])read.Content).length > 59 && ((byte[])read.Content).length >= 59 + (((byte[])read.Content)[58] & 0xFF)) {
            return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, 59, ((byte[])read.Content)[58] & 0xFF, StandardCharsets.UTF_8));
        }
        return new OperateResultExOne<String>("Data is too short: " + SoftBasic.ByteToHexString((byte[])read.Content, ' '));
    }

    public static byte[] PackCleanCommandService(byte[] portSlot, byte[] ... cips) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(178);
        ms.write(0);
        ms.write(0);
        ms.write(0);
        if (cips.length == 1) {
            ms.write(cips[0], 0, cips[0].length);
        } else {
            int i;
            ms.write(10);
            ms.write(2);
            ms.write(32);
            ms.write(2);
            ms.write(36);
            ms.write(1);
            ms.write(Utilities.getBytes((short)cips.length), 0, 2);
            short offect = (short)(2 + 2 * cips.length);
            for (i = 0; i < cips.length; ++i) {
                ms.write(Utilities.getBytes(offect), 0, 2);
                offect = (short)(offect + cips[i].length);
            }
            for (i = 0; i < cips.length; ++i) {
                ms.write(cips[i], 0, cips[i].length);
            }
        }
        ms.write((byte)((portSlot.length + 1) / 2));
        ms.write(0);
        ms.write(portSlot, 0, portSlot.length);
        if (portSlot.length % 2 == 1) {
            ms.write(0);
        }
        byte[] data = ms.toByteArray();
        System.arraycopy(Utilities.getBytes((short)(data.length - 4)), 0, data, 2, 2);
        return data;
    }

    public static OperateResultExOne<Date> ReadDate(IReadWriteCip plc, String address) {
        OperateResultExOne<Long> read = plc.ReadInt64(address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1970, 0, 1);
        calendar.add(5, (int)((Long)read.Content / 1000000000L / 86400L));
        calendar.add(14, (int)((Long)read.Content / 1000000L % 86400000L));
        return OperateResultExOne.CreateSuccessResult(calendar.getTime());
    }

    public static OperateResult WriteDate(IReadWriteCip plc, String address, Date date) {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(1970, 0, 1);
        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(date.getYear() + 1900, date.getMonth(), date.getDate());
        long tick = (end.getTimeInMillis() - start.getTimeInMillis()) * 1000000L;
        return plc.WriteTag(address, (short)8, plc.getByteTransform().TransByte(tick), 1);
    }

    public static OperateResult WriteTimeAndDate(IReadWriteCip plc, String address, Date date) {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(1970, 0, 1);
        long tick = (date.getTime() - start.getTimeInMillis()) * 1000000L;
        return plc.WriteTag(address, (short)10, plc.getByteTransform().TransByte(tick), 1);
    }

    public static OperateResultExOne<Duration> ReadTime(IReadWriteCip plc, String address) {
        OperateResultExOne<Long> read = plc.ReadInt64(address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        long tick = (Long)read.Content / 1000000L;
        return OperateResultExOne.CreateSuccessResult(Duration.ofMillis(tick));
    }

    public static OperateResult WriteTime(IReadWriteCip plc, String address, Duration time) {
        return plc.WriteTag(address, (short)9, plc.getByteTransform().TransByte(time.toMillis() * 1000000L), 1);
    }

    public static OperateResult WriteTimeOfDate(IReadWriteCip plc, String address, Duration timeOfDate) {
        return plc.WriteTag(address, (short)11, plc.getByteTransform().TransByte(timeOfDate.toMillis() * 1000000L), 1);
    }
}

