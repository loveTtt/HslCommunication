/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.YASKAWA.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslExtension;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.YASKAWA.Helper.IMemobus;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class MemobusHelper {
    public static byte[] PackCommandWithHeader(byte[] command, long id) {
        byte[] buffer = new byte[12 + command.length];
        buffer[0] = 17;
        buffer[1] = (byte)id;
        buffer[2] = 0;
        buffer[3] = 0;
        buffer[6] = BitConverter.GetBytes(buffer.length)[0];
        buffer[7] = BitConverter.GetBytes(buffer.length)[1];
        Utilities.ByteArrayCopyTo(command, buffer, 12);
        return buffer;
    }

    public static String GetErrorText(byte err) {
        switch (err) {
            case 1: {
                return StringResources.Language.Memobus01();
            }
            case 2: {
                return StringResources.Language.Memobus02();
            }
            case 3: {
                return StringResources.Language.Memobus03();
            }
            case 64: {
                return StringResources.Language.Memobus40();
            }
            case 65: {
                return StringResources.Language.Memobus41();
            }
            case 66: {
                return StringResources.Language.Memobus42();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        try {
            if (send.length > 15 && response.length > 15) {
                if (send[15] + 128 == response[15] && response.length >= 18) {
                    return new OperateResultExOne<byte[]>(response[17], MemobusHelper.GetErrorText(response[17]) + " Source: " + SoftBasic.ByteToHexString(response, ' '));
                }
                if (send[15] != response[15]) {
                    return new OperateResultExOne<byte[]>(response[15], "Send SFC not same as back SFC:" + SoftBasic.ByteToHexString(response, ' '));
                }
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 12));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("UnpackResponseContent failed: " + ex.getMessage() + "  Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    private static void SetByteHead(byte[] buffer, byte mfc, byte sfc, byte cpuTo, byte cpuFrom) {
        buffer[0] = BitConverter.GetBytes(buffer.length - 2)[0];
        buffer[1] = BitConverter.GetBytes(buffer.length - 2)[1];
        buffer[2] = mfc;
        buffer[3] = sfc;
        buffer[4] = (byte)((cpuTo << 4) + cpuFrom);
    }

    public static byte GetAddressDataType(String address) {
        int dataType = 77;
        if (address.charAt(0) == 'M' || address.charAt(0) == 'm') {
            dataType = 77;
        } else if (address.charAt(0) == 'G' || address.charAt(0) == 'g') {
            dataType = 71;
        } else if (address.charAt(0) == 'I' || address.charAt(0) == 'i') {
            dataType = 73;
        } else if (address.charAt(0) == 'O' || address.charAt(0) == 'o') {
            dataType = 79;
        } else if (address.charAt(0) == 'S' || address.charAt(0) == 's') {
            dataType = 83;
        }
        return (byte)dataType;
    }

    private static int CalculateBoolIndex(String address) {
        int indexPoint = (address = address.charAt(1) == 'B' || address.charAt(1) == 'b' ? address.substring(2) : address.substring(1)).indexOf(46);
        if (indexPoint > 0) {
            return Convert.ToInt32(address.substring(0, indexPoint)) * 16 + HslHelper.CalculateBitStartIndex(address.substring(indexPoint + 1));
        }
        return Convert.ToInt32(address.substring(0, address.length() - 1)) * 16 + HslHelper.CalculateBitStartIndex(address.substring(address.length() - 1));
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, short length) {
        if (HslExtension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = MemobusHelper.GetAddressDataType(address);
            if (address.charAt(1) == 'B' || address.charAt(1) == 'b' || address.indexOf(46) > 0) {
                int add = MemobusHelper.CalculateBoolIndex(address);
                byte[] buffer = new byte[16];
                MemobusHelper.SetByteHead(buffer, (byte)67, (byte)65, cpuTo, cpuFrom);
                buffer[6] = dataType;
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(add), buffer, 8);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), buffer, 12);
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            byte[] buffer = new byte[14];
            MemobusHelper.SetByteHead(buffer, (byte)67, (byte)73, cpuTo, cpuFrom);
            buffer[6] = dataType;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address.substring(1))), buffer, 8);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), buffer, 12);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        int add = 0;
        try {
            add = Integer.parseInt(address);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
        }
        if (add > 65535) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");
        }
        if (sfc == 1 || sfc == 2 || sfc == 3 || sfc == 4) {
            byte[] buffer = new byte[9];
            MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
            buffer[5] = BitConverter.GetBytes(add)[1];
            buffer[6] = BitConverter.GetBytes(add)[0];
            buffer[7] = BitConverter.GetBytes(length)[1];
            buffer[8] = BitConverter.GetBytes(length)[0];
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        if (sfc == 9 || sfc == 10) {
            byte[] buffer = new byte[10];
            MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
            buffer[6] = BitConverter.GetBytes(add)[0];
            buffer[7] = BitConverter.GetBytes(add)[1];
            buffer[8] = BitConverter.GetBytes(length)[0];
            buffer[9] = BitConverter.GetBytes(length)[1];
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return new OperateResultExOne<byte[]>("SFC:" + sfc + " " + StringResources.Language.NotSupportedFunction());
    }

    public static OperateResultExOne<byte[]> BuildReadRandomCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, short[] address) {
        byte[] buffer = new byte[8 + address.length * 2];
        MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; ++i) {
            buffer[8 + i * 2 + 0] = BitConverter.GetBytes(address[i])[0];
            buffer[8 + i * 2 + 1] = BitConverter.GetBytes(address[i])[1];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildReadRandomCommand(byte cpuTo, byte cpuFrom, String[] address) {
        byte[] buffer = new byte[8 + address.length * 6];
        MemobusHelper.SetByteHead(buffer, (byte)67, (byte)77, cpuTo, cpuFrom);
        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; ++i) {
            byte dataType;
            buffer[8 + i * 6 + 0] = dataType = MemobusHelper.GetAddressDataType(address[i]);
            buffer[8 + i * 6 + 1] = 2;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address[i].substring(1))), buffer, 8 + i * 6 + 2);
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, short address, boolean value) {
        byte[] buffer = new byte[9];
        MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[5] = BitConverter.GetBytes(address)[1];
        buffer[6] = BitConverter.GetBytes(address)[0];
        buffer[7] = (byte)(value ? 255 : 0);
        buffer[8] = 0;
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, boolean[] value) {
        if (HslExtension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = MemobusHelper.GetAddressDataType(address);
            if (address.charAt(1) == 'B' || address.charAt(1) == 'b' || address.indexOf(46) > 0) {
                int add = MemobusHelper.CalculateBoolIndex(address);
                byte[] buffer = new byte[16 + (value.length + 7) / 8];
                MemobusHelper.SetByteHead(buffer, (byte)67, (byte)79, cpuTo, cpuFrom);
                buffer[6] = dataType;
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(add), buffer, 8);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), buffer, 12);
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(value), buffer, 16);
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
        int add = 0;
        try {
            add = Integer.parseInt(address);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
        }
        if (add > 65535) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");
        }
        byte[] data = SoftBasic.BoolArrayToByte(value);
        byte[] buffer = new byte[9 + data.length];
        MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[5] = BitConverter.GetBytes(add)[1];
        buffer[6] = BitConverter.GetBytes(add)[0];
        buffer[7] = BitConverter.GetBytes(value.length)[1];
        buffer[8] = BitConverter.GetBytes(value.length)[0];
        Utilities.ByteArrayCopyTo(data, buffer, 9);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, int address, short value) {
        byte[] buffer = new byte[9];
        MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[5] = BitConverter.GetBytes(address)[1];
        buffer[6] = BitConverter.GetBytes(address)[0];
        buffer[7] = BitConverter.GetBytes(value)[1];
        buffer[8] = BitConverter.GetBytes(value)[0];
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, byte[] value) {
        if (HslExtension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = MemobusHelper.GetAddressDataType(address);
            byte[] buffer = new byte[14 + value.length];
            MemobusHelper.SetByteHead(buffer, (byte)67, (byte)75, cpuTo, cpuFrom);
            buffer[6] = dataType;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address.substring(1))), buffer, 8);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length / 2), buffer, 12);
            Utilities.ByteArrayCopyTo(SoftBasic.BytesReverseByWord(value), buffer, 14);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        int add = 0;
        try {
            add = Integer.parseInt(address);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
        }
        if (add > 65535) {
            return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");
        }
        if (sfc == 11) {
            byte[] buffer = new byte[10 + value.length];
            MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
            buffer[6] = BitConverter.GetBytes(add)[0];
            buffer[7] = BitConverter.GetBytes(add)[1];
            buffer[8] = BitConverter.GetBytes(value.length / 2)[0];
            buffer[9] = BitConverter.GetBytes(value.length / 2)[1];
            Utilities.ByteArrayCopyTo(SoftBasic.BytesReverseByWord(value), buffer, 10);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        if (sfc == 16) {
            byte[] buffer = new byte[9 + value.length];
            MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
            buffer[5] = BitConverter.GetBytes(add)[1];
            buffer[6] = BitConverter.GetBytes(add)[0];
            buffer[7] = BitConverter.GetBytes(value.length / 2)[1];
            buffer[8] = BitConverter.GetBytes(value.length / 2)[0];
            Utilities.ByteArrayCopyTo(value, buffer, 9);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return new OperateResultExOne<byte[]>("SFC:" + sfc + " " + StringResources.Language.NotSupportedFunction());
    }

    public static OperateResultExOne<byte[]> BuildWriteRandomCommand(byte mfc, byte sfc, byte cpuTo, byte cpuFrom, int[] address, byte[] value) {
        if (value.length != address.length * 2) {
            return new OperateResultExOne<byte[]>("value.Length must be twice as much as address.Length");
        }
        byte[] buffer = new byte[8 + address.length * 4];
        MemobusHelper.SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; ++i) {
            buffer[8 + i * 4 + 0] = BitConverter.GetBytes(address[i])[0];
            buffer[8 + i * 4 + 1] = BitConverter.GetBytes(address[i])[1];
            buffer[8 + i * 4 + 2] = value[i * 2 + 1];
            buffer[8 + i * 4 + 3] = value[i * 2 + 0];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<boolean[]> ReadBool(IMemobus memobus, String address, short length) {
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 1;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildReadCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, length);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (((byte[])read.Content)[3] == 65) {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectBegin(SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 8)), length));
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectBegin(SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 5)), length));
    }

    public static OperateResult Write(IMemobus memobus, String address, boolean value) {
        if (HslExtension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            return MemobusHelper.Write(memobus, address, new boolean[]{value});
        }
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 5;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), (short)Integer.parseInt(address), value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult Write(IMemobus memobus, String address, boolean[] value) {
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 15;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResult.CreateSuccessResult();
    }

    private static OperateResultExOne<byte[]> ExtraContent(String address, byte[] content) {
        if (content[2] == 32) {
            if (content[3] == 3 || content[3] == 4) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 5));
            }
            if (content[3] == 9 || content[3] == 10) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(content, 8)));
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 5));
        }
        if (content[2] == 67) {
            if (content[3] == 73 || content[3] == 77) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(content, 10)));
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 8));
        }
        return new OperateResultExOne<byte[]>("[" + address + "], mfc[" + content[2] + "] is not supported");
    }

    public static OperateResultExOne<byte[]> Read(IMemobus memobus, String address, short length) {
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 3;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildReadCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, length);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return MemobusHelper.ExtraContent(address, (byte[])read.Content);
    }

    public static OperateResult Write(IMemobus memobus, String address, byte[] value) {
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 16;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        if (sfc == 3) {
            sfc = 16;
        }
        if (sfc == 9) {
            sfc = 11;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult Write(IMemobus memobus, String address, short value) {
        byte mfc = 32;
        OperateResultExTwo<Integer, String> mfc_extra = HslHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = ((Integer)mfc_extra.Content1).byteValue();
            address = (String)mfc_extra.Content2;
        }
        byte sfc = 6;
        OperateResultExTwo<Integer, String> sfc_extra = HslHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = ((Integer)sfc_extra.Content1).byteValue();
            address = (String)sfc_extra.Content2;
        }
        if (sfc == 11 || sfc == 9) {
            return memobus.Write("x=" + sfc + ";" + address, memobus.getByteTransform().TransByte(value));
        }
        if (sfc == 3) {
            sfc = 6;
        }
        OperateResultExOne<byte[]> command = MemobusHelper.BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), Integer.parseInt(address), value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<byte[]> ReadRandom(IMemobus memobus, short[] address) {
        OperateResultExOne<byte[]> command = MemobusHelper.BuildReadRandomCommand((byte)32, (byte)13, memobus.getCpuTo(), memobus.getCpuFrom(), address);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 8)));
    }

    public static OperateResultExOne<byte[]> ReadRandom(IMemobus memobus, String[] address) {
        OperateResultExOne<byte[]> command = MemobusHelper.BuildReadRandomCommand(memobus.getCpuTo(), memobus.getCpuFrom(), address);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 8)));
    }

    public static OperateResult WriteRandom(IMemobus memobus, int[] address, byte[] value) {
        OperateResultExOne<byte[]> command = MemobusHelper.BuildWriteRandomCommand((byte)32, (byte)14, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResult.CreateSuccessResult();
    }
}

