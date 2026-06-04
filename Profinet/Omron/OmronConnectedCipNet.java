/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.NetworkBase.NetworkConnectedCip;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.AllenBradley.AllenBradleyHelper;
import HslCommunication.Profinet.AllenBradley.IReadWriteCip;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class OmronConnectedCipNet
extends NetworkConnectedCip
implements IReadWriteCip {
    public String ProductName = "";
    public byte ConnectionTimeoutMultiplier = (byte)2;

    public OmronConnectedCipNet() {
        this.WordLength = (short)2;
        this.setByteTransform(new RegularByteTransform());
    }

    public OmronConnectedCipNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected byte[] GetLargeForwardOpen(short connectionID) {
        int tOConnectionId;
        this.TOConnectionId = tOConnectionId = (int)(2164129793L + (long)connectionID);
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 34 00 5b 02 20 06 24 01 0e 9c 02 00 00 80 01 00 fe 80 02 00 1b 05 30 a7 2b 03 02 00 00 00 80 84 1e 00 cc 07 00 42 80 84 1e 00 cc 07 00 42 a3 03 20 02 24 01 2c 01");
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((int)(0x80000002L + (long)connectionID)), buffer, 24);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(tOConnectionId), buffer, 28);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)(2 + connectionID)), buffer, 32);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)4105), buffer, 34);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(HslHelper.HslRandom.nextInt()), buffer, 36);
        buffer[40] = this.ConnectionTimeoutMultiplier;
        return buffer;
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResult ini = super.InitializationOnConnect(socket);
        if (!ini.IsSuccess) {
            return ini;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(111, this.SessionHandle, this.GetAttributeAll()), true, false);
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content).length > 59 && ((byte[])read.Content).length >= (59 + ((byte[])read.Content)[58] & 0xFF)) {
            this.ProductName = new String((byte[])read.Content, 59, ((byte[])read.Content)[58] & 0xFF, StandardCharsets.UTF_8);
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, AllenBradleyHelper.UnRegisterSessionHandle(this.SessionHandle), true, false);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    private OperateResultExOne<byte[]> BuildReadCommand(String[] address, short[] length) {
        try {
            ArrayList<byte[]> cips = new ArrayList<byte[]>();
            for (int i = 0; i < address.length; ++i) {
                cips.add(AllenBradleyHelper.PackRequsetRead(address[i], length[i], true));
            }
            return OperateResultExOne.CreateSuccessResult(this.PackCommandService(Utilities.ToArray(cips)));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResultExOne<byte[]> BuildWriteCommand(String address, short typeCode, byte[] data, int length) {
        try {
            return OperateResultExOne.CreateSuccessResult(this.PackCommandService(new byte[][]{AllenBradleyHelper.PackRequestWrite(address, typeCode, data, length, true)}));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResultExThree<byte[], Short, Boolean> ReadWithType(String[] address, short[] length) {
        OperateResultExOne<byte[]> command = this.BuildReadCommand(address, length);
        if (!command.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(read);
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(check);
        }
        return OmronConnectedCipNet.ExtractActualData((byte[])read.Content, true);
    }

    public OperateResultExOne<byte[]> ReadCipFromServer(byte[] ... cips) {
        byte[] command = this.PackCommandService(cips);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(command);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(read.Content);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExThree<byte[], Short, Boolean> read = this.ReadWithType(new String[]{address}, new short[]{length});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(read.Content1);
    }

    public OperateResultExOne<byte[]> Read(String[] address, short[] length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExThree<byte[], Short, Boolean> read = this.ReadWithType(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(read.Content1);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (length == 1 && !Pattern.matches("\\[[0-9]+\\]$", address)) {
            OperateResultExOne<byte[]> read = this.Read(address, length);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray((byte[])read.Content));
        }
        OperateResultExOne<byte[]> read = this.Read(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        boolean[] values = new boolean[length];
        for (int i = 0; i < values.length; ++i) {
            if (((byte[])read.Content)[i] == 0) continue;
            values[i] = true;
        }
        return OperateResultExOne.CreateSuccessResult(values);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((byte[])read.Content)[0]);
    }

    @Override
    public OperateResultExTwo<Short, byte[]> ReadTag(String address, int length) {
        OperateResultExThree<byte[], Short, Boolean> read = this.ReadWithType(new String[]{address}, new short[]{(short)length});
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(read.Content2, read.Content1);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return this.WriteTag(address, (short)209, value, HslHelper.IsAddressEndWithIndex(address) ? value.length : 1);
    }

    public OperateResult WriteTag(String address, short typeCode, byte[] value) {
        return this.WriteTag(address, typeCode, value, 1);
    }

    @Override
    public OperateResult WriteTag(String address, short typeCode, byte[] value, int length) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "type");
        if (extra.IsSuccess) {
            typeCode = ((Integer)extra.Content1).shortValue();
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = this.BuildWriteCommand(address, typeCode, value, length);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return AllenBradleyHelper.ExtractActualData((byte[])read.Content, false);
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], short[]>(){

            @Override
            public short[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], float[]>(){

            @Override
            public float[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], double[]>(){

            @Override
            public double[] Action(byte[] content) {
                return OmronConnectedCipNet.this.getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    public OperateResultExOne<String> ReadString(String address) {
        return this.ReadString(address, (short)1, StandardCharsets.UTF_8);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length) {
        return this.ReadString(address, length, StandardCharsets.UTF_8);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        OperateResultExOne<byte[]> read = this.Read(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (((byte[])read.Content).length >= 2) {
            int strLength = this.getByteTransform().TransUInt16((byte[])read.Content, 0);
            return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, 2, strLength, encoding));
        }
        return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, encoding));
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return this.WriteTag(address, (short)195, this.getByteTransform().TransByte(values), values.length);
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return this.WriteTag(address, (short)196, this.getByteTransform().TransByte(values), values.length);
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return this.WriteTag(address, (short)202, this.getByteTransform().TransByte(values), values.length);
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return this.WriteTag(address, (short)197, this.getByteTransform().TransByte(values), values.length);
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return this.WriteTag(address, (short)203, this.getByteTransform().TransByte(values), values.length);
    }

    @Override
    public OperateResult Write(String address, String value) {
        return this.Write(address, value, StandardCharsets.UTF_8);
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] buffer = Utilities.IsStringNullOrEmpty(value) ? new byte[]{} : value.getBytes(encoding);
        byte[] result = new byte[buffer.length + 2];
        System.arraycopy(Utilities.getBytes((short)buffer.length), 0, result, 0, 2);
        if (buffer.length > 0) {
            System.arraycopy(buffer, 0, result, 2, buffer.length);
        }
        return this.WriteTag(address, (short)208, result);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        byte[] byArray;
        if (value) {
            byte[] byArray2 = new byte[2];
            byArray2[0] = -1;
            byArray = byArray2;
            byArray2[1] = -1;
        } else {
            byte[] byArray3 = new byte[2];
            byArray3[0] = 0;
            byArray = byArray3;
            byArray3[1] = 0;
        }
        return this.WriteTag(address, (short)193, byArray);
    }

    public OperateResult Write(String address, byte value) {
        return this.WriteTag(address, (short)194, new byte[]{value});
    }

    private byte[] GetAttributeAll() {
        return SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 06 00 01 02 20 01 24 01");
    }

    @Override
    public OperateResultExOne<String> ReadPlcType() {
        return OperateResultExOne.CreateSuccessResult(this.ProductName);
    }

    @Override
    public OperateResultExOne<Date> ReadDate(String address) {
        return AllenBradleyHelper.ReadDate(this, address);
    }

    @Override
    public OperateResult WriteDate(String address, Date date) {
        return AllenBradleyHelper.WriteDate(this, address, date);
    }

    @Override
    public OperateResult WriteTimeAndDate(String address, Date date) {
        return AllenBradleyHelper.WriteTimeAndDate(this, address, date);
    }

    @Override
    public OperateResultExOne<Duration> ReadTime(String address) {
        return AllenBradleyHelper.ReadTime(this, address);
    }

    @Override
    public OperateResult WriteTime(String address, Duration time) {
        return AllenBradleyHelper.WriteTime(this, address, time);
    }

    @Override
    public OperateResult WriteTimeOfDate(String address, Duration timeOfDate) {
        return AllenBradleyHelper.WriteTimeOfDate(this, address, timeOfDate);
    }

    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead) {
        short dataType;
        boolean hasMoreData;
        ByteArrayOutputStream data;
        block28: {
            int count;
            int offset;
            block29: {
                block27: {
                    data = new ByteArrayOutputStream();
                    offset = 42;
                    hasMoreData = false;
                    dataType = 0;
                    count = Utilities.getUShort(response, offset);
                    if (Utilities.getInt(response, 46) != 138) break block27;
                    offset = 50;
                    int dataCount = Utilities.getUShort(response, offset);
                    for (int i = 0; i < dataCount; ++i) {
                        int offsetStart = Utilities.getUShort(response, offset + 2 + i * 2) + offset;
                        int offsetEnd = i == dataCount - 1 ? response.length : Utilities.getUShort(response, offset + 4 + i * 2) + offset;
                        int err = Utilities.getUShort(response, offsetStart + 2);
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
                        for (int j = offsetStart + 6; j < offsetEnd; ++j) {
                            data.write(response[j]);
                        }
                    }
                    break block28;
                }
                byte err = response[offset + 6];
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
                if (response[offset + 4] == -51 || response[offset + 4] == -45) {
                    return OperateResultExThree.CreateSuccessResult(data.toByteArray(), dataType, hasMoreData);
                }
                if (response[offset + 4] != -52 && response[offset + 4] != -46) break block29;
                for (int i = offset + 10; i < offset + 2 + count; ++i) {
                    data.write(response[i]);
                }
                dataType = (short)Utilities.getUShort(response, offset + 8);
                break block28;
            }
            if (response[offset + 4] != -43) break block28;
            for (int i = offset + 8; i < offset + 2 + count; ++i) {
                data.write(response[i]);
            }
        }
        return OperateResultExThree.CreateSuccessResult(data.toByteArray(), dataType, hasMoreData);
    }
}

