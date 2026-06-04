/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Yokogawa;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.YokogawaLinkAddress;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.YokogawaLinkBinaryMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Yokogawa.YokogawaLinkHelper;
import HslCommunication.Profinet.Yokogawa.YokogawaSystemInfo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class YokogawaLinkTcp
extends NetworkDeviceBase {
    public byte CpuNumber = 1;

    public YokogawaLinkTcp() {
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        this.setByteTransform(transform);
        this.CpuNumber = 1;
    }

    public YokogawaLinkTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new YokogawaLinkBinaryMessage();
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command;
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
                return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
            }
            command = YokogawaLinkTcp.BuildReadSpecialModule(this.CpuNumber, address, length);
        } else {
            command = YokogawaLinkTcp.BuildReadCommand(this.CpuNumber, address, length, false);
        }
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return read;
            }
            OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
            if (!check.IsSuccess) {
                return check;
            }
            try {
                content.write((byte[])check.Content);
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return OperateResultExOne.CreateSuccessResult(content.toByteArray());
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command;
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
                return new OperateResult(StringResources.Language.InsufficientPrivileges());
            }
            command = YokogawaLinkTcp.BuildWriteSpecialModule(this.CpuNumber, address, value);
        } else {
            command = YokogawaLinkTcp.BuildWriteWordCommand(this.CpuNumber, address, value);
        }
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return YokogawaLinkTcp.CheckContent((byte[])read.Content);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = YokogawaLinkTcp.BuildReadCommand(this.CpuNumber, address, length, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            try {
                content.write((byte[])check.Content);
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] buffer = content.toByteArray();
        boolean[] array = new boolean[buffer.length];
        for (int i = 0; i < buffer.length; ++i) {
            array[i] = buffer[i] != 0;
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<byte[]> command = YokogawaLinkTcp.BuildWriteBoolCommand(this.CpuNumber, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return YokogawaLinkTcp.CheckContent((byte[])read.Content);
    }

    public OperateResultExOne<boolean[]> ReadRandomBool(String[] address) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<boolean[]>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<ArrayList<byte[]>> command = YokogawaLinkTcp.BuildReadRandomCommand(this.CpuNumber, address, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ByteArrayOutputStream lists = new ByteArrayOutputStream();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            byte[] content = (byte[])((ArrayList)command.Content).get(i);
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(content);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            try {
                lists.write((byte[])check.Content);
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] buffer = lists.toByteArray();
        boolean[] array = new boolean[buffer.length];
        for (int i = 0; i < buffer.length; ++i) {
            array[i] = buffer[i] != 0;
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public OperateResult WriteRandomBool(String[] address, boolean[] value) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        }
        if (address.length != value.length) {
            return new OperateResult(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        OperateResultExOne<byte[]> command = YokogawaLinkTcp.BuildWriteRandomBoolCommand(this.CpuNumber, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<ArrayList<byte[]>> command = YokogawaLinkTcp.BuildReadRandomCommand(this.CpuNumber, address, false);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ByteArrayOutputStream lists = new ByteArrayOutputStream();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            byte[] content = (byte[])((ArrayList)command.Content).get(i);
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(content);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            try {
                lists.write((byte[])check.Content);
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return OperateResultExOne.CreateSuccessResult(lists.toByteArray());
    }

    public OperateResultExOne<short[]> ReadRandomInt16(String[] address) {
        OperateResultExOne<byte[]> read = this.ReadRandom(address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt16((byte[])read.Content, 0, address.length));
    }

    public OperateResult WriteRandom(String[] address, byte[] value) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        }
        if (address.length * 2 != value.length) {
            return new OperateResult(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        OperateResultExOne<byte[]> command = YokogawaLinkTcp.BuildWriteRandomWordCommand(this.CpuNumber, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult WriteRandom(String[] address, short[] value) {
        return this.WriteRandom(address, this.getByteTransform().TransByte(value));
    }

    public OperateResult Start() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResult(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> command = YokogawaLinkTcp.BuildStartCommand(this.CpuNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult Stop() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResult(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> command = YokogawaLinkTcp.BuildStopCommand(this.CpuNumber);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult ModuleReset() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResult(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(new byte[]{97, this.CpuNumber, 0, 0}, false, true);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<Integer> ReadProgramStatus() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<Integer>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(new byte[]{98, this.CpuNumber, 0, 2, 0, 1});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(Integer.valueOf(((byte[])check.Content)[1]));
    }

    public OperateResultExOne<YokogawaSystemInfo> ReadSystemInfo() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<YokogawaSystemInfo>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(new byte[]{98, this.CpuNumber, 0, 2, 0, 2});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return YokogawaSystemInfo.Parse((byte[])check.Content);
    }

    public OperateResultExOne<Date> ReadDateTime() {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<Date>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(new byte[]{99, this.CpuNumber, 0, 0});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(new Date(2000 + this.getByteTransform().TransUInt16((byte[])check.Content, 0), this.getByteTransform().TransUInt16((byte[])check.Content, 2), this.getByteTransform().TransUInt16((byte[])check.Content, 4), this.getByteTransform().TransUInt16((byte[])check.Content, 6), this.getByteTransform().TransUInt16((byte[])check.Content, 8), this.getByteTransform().TransUInt16((byte[])check.Content, 10)));
    }

    public OperateResultExOne<byte[]> ReadSpecialModule(byte moduleUnit, byte moduleSlot, int dataPosition, short length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        ArrayList<byte[]> commands = YokogawaLinkTcp.BuildReadSpecialModule(this.CpuNumber, moduleUnit, moduleSlot, dataPosition, length);
        for (int i = 0; i < commands.size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(commands.get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> check = YokogawaLinkTcp.CheckContent((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            try {
                content.write((byte[])check.Content);
                continue;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return OperateResultExOne.CreateSuccessResult(content.toByteArray());
    }

    public static OperateResultExOne<byte[]> CheckContent(byte[] content) {
        if (content[1] != 0) {
            return new OperateResultExOne<byte[]>(YokogawaLinkHelper.GetErrorMsg(content[1]));
        }
        if (content.length > 4) {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 4));
        }
        return OperateResultExOne.CreateSuccessResult(new byte[0]);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte cpu, String address, short length, boolean isBit) {
        OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = ((Integer)cpuExtra.Content1).byteValue();
            address = (String)cpuExtra.Content2;
        }
        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, length);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExTwo<int[], int[]> splits = Authorization.asdniasnfaksndiqwhawfskhfaiw() ? (isBit ? HslHelper.SplitReadLength(((YokogawaLinkAddress)analysis.Content).getAddressStart(), length, (short)256) : HslHelper.SplitReadLength(((YokogawaLinkAddress)analysis.Content).getAddressStart(), length, (short)64)) : HslHelper.SplitReadLength(((YokogawaLinkAddress)analysis.Content).getAddressStart(), length, (short)Short.MAX_VALUE);
        ArrayList<byte[]> lists = new ArrayList<byte[]>();
        for (int i = 0; i < ((int[])splits.Content1).length; ++i) {
            ((YokogawaLinkAddress)analysis.Content).setAddressStart(((int[])splits.Content1)[i]);
            byte[] buffer = new byte[12];
            buffer[0] = (byte) (isBit ? 1 : 17);
            buffer[1] = cpu;
            buffer[2] = 0;
            buffer[3] = 8;
            System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 4, 6);
            buffer[10] = Utilities.getBytes(((int[])splits.Content2)[i])[1];
            buffer[11] = Utilities.getBytes(((int[])splits.Content2)[i])[0];
            lists.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(lists);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadRandomCommand(byte cpu, String[] address, boolean isBit) {
        ArrayList<String[]> splits = SoftBasic.ArraySplitByLength(String.class, address, 32);
        ArrayList<byte[]> lists = new ArrayList<byte[]>();
        for (int j = 0; j < splits.size(); ++j) {
            String[] addressSplit = splits.get(j);
            byte[] buffer = new byte[6 + 6 * addressSplit.length];
            buffer[0] = (byte) (isBit ? 4 : 20);
            buffer[1] = cpu;
            buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
            buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
            buffer[4] = Utilities.getBytes(addressSplit.length)[1];
            buffer[5] = Utilities.getBytes(addressSplit.length)[0];
            for (int i = 0; i < addressSplit.length; ++i) {
                OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(addressSplit[i], "cpu", cpu);
                if (cpuExtra.IsSuccess) {
                    cpu = ((Integer)cpuExtra.Content1).byteValue();
                    address[i] = (String)cpuExtra.Content2;
                }
                buffer[1] = cpu;
                OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(addressSplit[i], (short)1);
                if (!analysis.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(analysis);
                }
                System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 6 * i + 6, 6);
            }
            lists.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(lists);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte cpu, String address, boolean[] value) {
        OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = ((Integer)cpuExtra.Content1).byteValue();
            address = (String)cpuExtra.Content2;
        }
        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, (short)0);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = new byte[12 + value.length];
        buffer[0] = 2;
        buffer[1] = cpu;
        buffer[2] = 0;
        buffer[3] = (byte)(8 + value.length);
        System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 4, 6);
        buffer[10] = Utilities.getBytes(value.length)[1];
        buffer[11] = Utilities.getBytes(value.length)[0];
        for (int i = 0; i < value.length; ++i) {
            buffer[12 + i] = value[i] ? (byte)1 : 0;
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteRandomBoolCommand(byte cpu, String[] address, boolean[] value) {
        if (address.length != value.length) {
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        byte[] buffer = new byte[6 + address.length * 8 - 1];
        buffer[0] = 5;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = Utilities.getBytes(address.length)[1];
        buffer[5] = Utilities.getBytes(address.length)[0];
        for (int i = 0; i < address.length; ++i) {
            OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address[i], "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = ((Integer)cpuExtra.Content1).byteValue();
                address[i] = (String)cpuExtra.Content2;
            }
            buffer[1] = cpu;
            OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address[i], (short)0);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 6 + 8 * i, 6);
            buffer[12 + 8 * i] = value[i] ? (byte)1 : 0;
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(byte cpu, String address, byte[] value) {
        OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = ((Integer)cpuExtra.Content1).byteValue();
            address = (String)cpuExtra.Content2;
        }
        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, (short)0);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = new byte[12 + value.length];
        buffer[0] = 18;
        buffer[1] = cpu;
        buffer[2] = 0;
        buffer[3] = (byte)(8 + value.length);
        System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 4, 6);
        buffer[10] = Utilities.getBytes(value.length / 2)[1];
        buffer[11] = Utilities.getBytes(value.length / 2)[0];
        System.arraycopy(value, 0, buffer, 12, value.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteRandomWordCommand(byte cpu, String[] address, byte[] value) {
        if (address.length * 2 != value.length) {
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        byte[] buffer = new byte[6 + address.length * 8];
        buffer[0] = 21;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = Utilities.getBytes(address.length)[1];
        buffer[5] = Utilities.getBytes(address.length)[0];
        for (int i = 0; i < address.length; ++i) {
            OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address[i], "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = ((Integer)cpuExtra.Content1).byteValue();
                address[i] = (String)cpuExtra.Content2;
            }
            buffer[1] = cpu;
            OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address[i], (short)0);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            System.arraycopy(((YokogawaLinkAddress)analysis.Content).GetAddressBinaryContent(), 0, buffer, 6 + 8 * i, 6);
            buffer[12 + 8 * i] = value[i * 2 + 0];
            buffer[13 + 8 * i] = value[i * 2 + 1];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildStartCommand(byte cpu) {
        return OperateResultExOne.CreateSuccessResult(new byte[]{69, cpu, 0, 0});
    }

    public static OperateResultExOne<byte[]> BuildStopCommand(byte cpu) {
        return OperateResultExOne.CreateSuccessResult(new byte[]{70, cpu, 0, 0});
    }

    public static ArrayList<byte[]> BuildReadSpecialModule(byte cpu, byte moduleUnit, byte moduleSlot, int dataPosition, short length) {
        ArrayList<byte[]> lists = new ArrayList<byte[]>();
        OperateResultExTwo<int[], int[]> splits = HslHelper.SplitReadLength(dataPosition, length, (short)64);
        for (int i = 0; i < ((int[])splits.Content1).length; ++i) {
            byte[] buffer = new byte[10];
            buffer[0] = 49;
            buffer[1] = cpu;
            buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
            buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
            buffer[4] = moduleUnit;
            buffer[5] = moduleSlot;
            buffer[6] = Utilities.getBytes(((int[])splits.Content1)[i])[1];
            buffer[7] = Utilities.getBytes(((int[])splits.Content1)[i])[0];
            buffer[8] = Utilities.getBytes(((int[])splits.Content2)[i])[1];
            buffer[9] = Utilities.getBytes(((int[])splits.Content2)[i])[0];
            lists.add(buffer);
        }
        return lists;
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadSpecialModule(byte cpu, String address, short length) {
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            address = address.substring(8);
            OperateResultExTwo<Integer, String> cpuExtra = HslHelper.ExtractParameter(address, "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = ((Integer)cpuExtra.Content1).byteValue();
                address = (String)cpuExtra.Content2;
            }
            OperateResultExTwo<Integer, String> unitExtra = HslHelper.ExtractParameter(address, "unit");
            if (!unitExtra.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(unitExtra);
            }
            byte unit = ((Integer)unitExtra.Content1).byteValue();
            address = (String)unitExtra.Content2;
            OperateResultExTwo<Integer, String> slotExtra = HslHelper.ExtractParameter(address, "slot");
            if (!slotExtra.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(slotExtra);
            }
            byte slot = ((Integer)slotExtra.Content1).byteValue();
            address = (String)slotExtra.Content2;
            try {
                return OperateResultExOne.CreateSuccessResult(YokogawaLinkTcp.BuildReadSpecialModule(cpu, unit, slot, Integer.parseInt(address), length));
            }
            catch (Exception ex) {
                return new OperateResultExOne<ArrayList<byte[]>>("Address format wrong: " + ex.getMessage());
            }
        }
        return new OperateResultExOne<ArrayList<byte[]>>("Special module address must start with Special:");
    }

    public static byte[] BuildWriteSpecialModule(byte cpu, byte moduleUnit, byte moduleSlot, int dataPosition, byte[] data) {
        byte[] buffer = new byte[10 + data.length];
        buffer[0] = 50;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = moduleUnit;
        buffer[5] = moduleSlot;
        buffer[6] = Utilities.getBytes(dataPosition)[1];
        buffer[7] = Utilities.getBytes(dataPosition)[0];
        buffer[8] = Utilities.getBytes(data.length / 2)[1];
        buffer[9] = Utilities.getBytes(data.length / 2)[0];
        System.arraycopy(data, 0, buffer, 10, data.length);
        return buffer;
    }

    public static OperateResultExOne<byte[]> BuildWriteSpecialModule(byte cpu, String address, byte[] data) {
        OperateResultExOne<ArrayList<byte[]>> analysis = YokogawaLinkTcp.BuildReadSpecialModule(cpu, address, (short)0);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = new byte[10 + data.length];
        buffer[0] = 50;
        buffer[1] = ((byte[])((ArrayList)analysis.Content).get(0))[1];
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = ((byte[])((ArrayList)analysis.Content).get(0))[4];
        buffer[5] = ((byte[])((ArrayList)analysis.Content).get(0))[5];
        buffer[6] = ((byte[])((ArrayList)analysis.Content).get(0))[6];
        buffer[7] = ((byte[])((ArrayList)analysis.Content).get(0))[7];
        buffer[8] = Utilities.getBytes(data.length / 2)[1];
        buffer[9] = Utilities.getBytes(data.length / 2)[0];
        System.arraycopy(data, 0, buffer, 10, data.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    @Override
    public String toString() {
        return "YokogawaLinkTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

