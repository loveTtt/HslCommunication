/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.AllenBradleyMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.AllenBradley.AllenBradleyHelper;
import HslCommunication.Profinet.AllenBradley.IReadWriteCip;
import HslCommunication.Profinet.AllenBradley.MessageRouter;
import HslCommunication.Utilities;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

public class AllenBradleyNet
extends NetworkDeviceBase
implements IReadWriteCip {
    private MessageRouter MessageRouter = null;
    private int SessionHandle = 0;
    private byte Slot = 0;
    public int ArraySegment = 100;
    public byte[] PortSlot = null;

    public AllenBradleyNet() {
        this.WordLength = (short)2;
        this.setByteTransform(new RegularByteTransform());
    }

    public AllenBradleyNet(String ipAddress) {
        this(ipAddress, 44818);
    }

    public AllenBradleyNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AllenBradleyMessage();
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return AllenBradleyHelper.PackRequestHeader(111, this.SessionHandle, command);
    }

    public int getSessionHandle() {
        return this.SessionHandle;
    }

    public byte getSlot() {
        return this.Slot;
    }

    public void setSlot(byte value) {
        this.Slot = value;
    }

    public int getArraySegment() {
        return this.ArraySegment;
    }

    public void setArraySegment(int value) {
        this.ArraySegment = value;
    }

    public byte[] getPortSlot() {
        return this.PortSlot;
    }

    public void setPortSlot(byte[] value) {
        this.PortSlot = value;
    }

    public MessageRouter getMessageRouter() {
        return this.MessageRouter;
    }

    public void setMessageRouter(MessageRouter value) {
        this.MessageRouter = value;
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, AllenBradleyHelper.RegisterSessionHandle(), true, false);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return check;
        }
        this.SessionHandle = this.getByteTransform().TransInt32((byte[])read.Content, 4);
        if (this.MessageRouter != null) {
            byte[] cip = this.MessageRouter.GetRouterCIP();
            OperateResultExOne<byte[]> messageRouter = this.ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(111, this.SessionHandle, AllenBradleyHelper.PackCommandSpecificData(new byte[]{0, 0, 0, 0}, AllenBradleyHelper.PackCommandSingleService(cip, 178, false))), true, false);
            if (!messageRouter.IsSuccess) {
                return messageRouter;
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        if (socket != null) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, AllenBradleyHelper.UnRegisterSessionHandle(this.SessionHandle), true, false);
            if (!read.IsSuccess) {
                return read;
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<byte[]> BuildReadCommand(String[] address, int[] length) {
        if (address == null || length == null) {
            return new OperateResultExOne<byte[]>("address or length is null");
        }
        if (address.length != length.length) {
            return new OperateResultExOne<byte[]>("address and length is not same array");
        }
        try {
            byte[] byArray;
            ArrayList<byte[]> cips = new ArrayList<byte[]>();
            for (int i = 0; i < address.length; ++i) {
                cips.add(AllenBradleyHelper.PackRequsetRead(address[i], length[i]));
            }
            byte[][] byArrayArray = new byte[2][];
            byArrayArray[0] = new byte[4];
            if (this.PortSlot == null) {
                byte[] byArray2 = new byte[2];
                byArray2[0] = 1;
                byArray = byArray2;
                byArray2[1] = this.Slot;
            } else {
                byArray = this.PortSlot;
            }
            byArrayArray[1] = AllenBradleyHelper.PackCommandService(byArray, Utilities.ToArray(cips));
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(byArrayArray);
            return OperateResultExOne.CreateSuccessResult(commandSpecificData);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    public OperateResultExOne<byte[]> BuildReadCommand(String[] address) {
        if (address == null) {
            return new OperateResultExOne<byte[]>("address or length is null");
        }
        int[] length = new int[address.length];
        for (int i = 0; i < address.length; ++i) {
            length[i] = 1;
        }
        return this.BuildReadCommand(address, length);
    }

    public OperateResultExOne<byte[]> BuildWriteCommand(String address, short typeCode, byte[] data) {
        OperateResultExOne<ArrayList<byte[]>> build = this.BuildWriteCommand(address, typeCode, data, 1);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        return OperateResultExOne.CreateSuccessResult((byte[])((ArrayList)build.Content).get(0));
    }

    public OperateResultExOne<ArrayList<byte[]>> BuildWriteCommand(String address, short typeCode, byte[] data, int length) {
        try {
            byte[] byArray;
            byte slotTmp = this.Slot;
            OperateResultExTwo<Integer, String> extra_slot = HslHelper.ExtractParameter(address, "slot");
            if (extra_slot.IsSuccess) {
                slotTmp = ((Integer)extra_slot.Content1).byteValue();
                address = (String)extra_slot.Content2;
            }
            int writeSegment = -1;
            OperateResultExTwo<Integer, String> extra_seg = HslHelper.ExtractParameter(address, "x");
            if (extra_seg.IsSuccess) {
                writeSegment = (Integer)extra_seg.Content1;
                address = (String)extra_seg.Content2;
            }
            if (writeSegment == 83 || writeSegment == 82) {
                int startOffset = 0;
                ArrayList<byte[]> list = SoftBasic.ArraySplitByLength(data, 474);
                for (int i = 0; i < list.size(); ++i) {
                    byte[] byArray2;
                    byte[] cip = AllenBradleyHelper.PackRequestWriteSegment(address, typeCode, list.get(i), startOffset, length, false);
                    startOffset += list.get(i).length;
                    byte[][] byArrayArray = new byte[2][];
                    byArrayArray[0] = new byte[4];
                    if (this.PortSlot != null) {
                        byArray2 = this.PortSlot;
                    } else {
                        byte[] byArray3 = new byte[2];
                        byArray3[0] = 1;
                        byArray2 = byArray3;
                        byArray3[1] = slotTmp;
                    }
                    byArrayArray[1] = this.PackCommandService(byArray2, new byte[][]{cip});
                    byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(byArrayArray);
                    list.set(i, commandSpecificData);
                }
                return OperateResultExOne.CreateSuccessResult(list);
            }
            byte[] cip = AllenBradleyHelper.PackRequestWrite(address, typeCode, data, length);
            byte[][] byArrayArray = new byte[2][];
            byArrayArray[0] = new byte[4];
            if (this.PortSlot != null) {
                byArray = this.PortSlot;
            } else {
                byte[] byArray4 = new byte[2];
                byArray4[0] = 1;
                byArray = byArray4;
                byArray4[1] = slotTmp;
            }
            byArrayArray[1] = this.PackCommandService(byArray, new byte[][]{cip});
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(byArrayArray);
            ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
            arrayList.add(commandSpecificData);
            return OperateResultExOne.CreateSuccessResult(arrayList);
        }
        catch (Exception ex) {
            return new OperateResultExOne<ArrayList<byte[]>>("Address Wrong:" + ex.getMessage());
        }
    }

    public OperateResultExOne<byte[]> BuildWriteCommand(String address, Boolean data) {
        try {
            byte[] byArray;
            byte[] cip = AllenBradleyHelper.PackRequestWrite(address, data);
            byte[][] byArrayArray = new byte[2][];
            byArrayArray[0] = new byte[4];
            if (this.PortSlot != null) {
                byArray = this.PortSlot;
            } else {
                byte[] byArray2 = new byte[2];
                byArray2[0] = 1;
                byArray = byArray2;
                byArray2[1] = this.Slot;
            }
            byArrayArray[1] = this.PackCommandService(byArray, new byte[][]{cip});
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(byArrayArray);
            return OperateResultExOne.CreateSuccessResult(commandSpecificData);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResult CheckResponse(byte[] response) {
        OperateResult check = AllenBradleyHelper.CheckResponse(response);
        if (!check.IsSuccess && check.ErrorCode == 100) {
            this.GetPipeSocket().setIsSocketError(true);
        }
        return check;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<Integer, String> extra1 = HslHelper.ExtractParameter(address, "type");
        if (extra1.IsSuccess) {
            address = (String)extra1.Content2;
        }
        int x = -1;
        OperateResultExTwo<Integer, String> extra2 = HslHelper.ExtractParameter(address, "x");
        if (extra2.IsSuccess) {
            x = (Integer)extra2.Content1;
            address = (String)extra2.Content2;
        }
        if (x == 82 || x == 83) {
            return this.ReadSegment(address, 0, length);
        }
        if (length > 1) {
            return this.ReadSegment(address, 0, length);
        }
        return this.Read(new String[]{address}, new int[]{length});
    }

    public OperateResultExOne<byte[]> Read(String[] address) {
        if (address == null) {
            return new OperateResultExOne<byte[]>("address can not be null");
        }
        int[] length = new int[address.length];
        for (int i = 0; i < length.length; ++i) {
            length[i] = 1;
        }
        return this.Read(address, length);
    }

    public OperateResultExOne<byte[]> Read(String[] address, int[] length) {
        OperateResultExOne<byte[]> command = this.BuildReadCommand(address, length);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = this.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        OperateResultExThree<byte[], Short, Boolean> analysis = AllenBradleyHelper.ExtractActualData((byte[])read.Content, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return OperateResultExOne.CreateSuccessResult(analysis.Content1);
    }

    public OperateResultExOne<byte[]> ReadSegment(String address, int startIndex, int length) {
        try {
            OperateResultExThree<byte[], Short, Boolean> analysis;
            ArrayList<Byte> bytesContent = new ArrayList<Byte>();
            do {
                OperateResultExOne<byte[]> read = this.ReadCipFromServer(new byte[][]{AllenBradleyHelper.PackRequestReadSegment(address, startIndex, length)});
                if (!read.IsSuccess) {
                    return read;
                }
                analysis = AllenBradleyHelper.ExtractActualData((byte[])read.Content, true);
                if (!analysis.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(analysis);
                }
                startIndex += ((byte[])analysis.Content1).length;
                for (int i = 0; i < ((byte[])analysis.Content1).length; ++i) {
                    bytesContent.add(((byte[])analysis.Content1)[i]);
                }
            } while (((Boolean)analysis.Content3).booleanValue());
            byte[] buffer = new byte[bytesContent.size()];
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = (Byte)bytesContent.get(i);
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResultExOne<byte[]> ReadByCips(byte[] ... cips) {
        OperateResultExOne<byte[]> read = this.ReadCipFromServer(cips);
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExThree<byte[], Short, Boolean> analysis = AllenBradleyHelper.ExtractActualData((byte[])read.Content, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return OperateResultExOne.CreateSuccessResult(analysis.Content1);
    }

    public OperateResultExOne<byte[]> ReadCipFromServer(byte[] ... cips) {
        try {
            byte[] byArray;
            byte[][] byArrayArray = new byte[2][];
            byArrayArray[0] = new byte[4];
            if (this.PortSlot == null) {
                byte[] byArray2 = new byte[2];
                byArray2[0] = 1;
                byArray = byArray2;
                byArray2[1] = this.Slot;
            } else {
                byArray = this.PortSlot;
            }
            byArrayArray[1] = AllenBradleyHelper.PackCommandService(byArray, cips);
            byte[] commandSpecificData = AllenBradleyHelper.PackCommandSpecificData(byArrayArray);
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(commandSpecificData);
            if (!read.IsSuccess) {
                return read;
            }
            OperateResult check = this.CheckResponse((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            return OperateResultExOne.CreateSuccessResult(read.Content);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        if (address.startsWith("i=")) {
            OperateResultExOne<boolean[]> read = this.ReadBool(address, (short)1);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            return OperateResultExOne.CreateSuccessResult(((boolean[])read.Content)[0]);
        }
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransBool((byte[])read.Content, 0));
    }

    public OperateResultExOne<boolean[]> ReadBoolArray(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransBool((byte[])read.Content, 0, ((byte[])read.Content).length * 8));
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (address.startsWith("i=")) {
            address = address.substring(2);
            OperateResultExTwo<String, Integer> analysis = AllenBradleyHelper.AnalysisArrayIndex(address);
            address = (String)analysis.Content1;
            int bitIndex = (Integer)analysis.Content2;
            String uintIndex = bitIndex / 32 == 0 ? "" : "[" + bitIndex / 32 + "]";
            short len = (short)HslHelper.CalculateOccupyLength(bitIndex, length, 32);
            OperateResultExOne<byte[]> read = this.Read(address + uintIndex, len);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), bitIndex % 32, length));
        }
        OperateResultExOne<byte[]> read = this.Read(address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray((byte[])read.Content, length));
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransByte((byte[])read.Content, 0));
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], short[]>(){

            @Override
            public short[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], float[]>(){

            @Override
            public float[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], double[]>(){

            @Override
            public double[] Action(byte[] content) {
                return AllenBradleyNet.this.getByteTransform().TransDouble(content, 0, length);
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
        try {
            OperateResultExOne<byte[]> read = this.Read(address, length);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content).length >= 6) {
                int strLength = Utilities.getInt((byte[])read.Content, 2);
                return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, 6, strLength, encoding));
            }
            return OperateResultExOne.CreateSuccessResult(new String((byte[])read.Content, encoding));
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    @Override
    public OperateResultExOne<String> ReadPlcType() {
        return AllenBradleyHelper.ReadPlcType(this);
    }

    @Override
    public OperateResultExTwo<Short, byte[]> ReadTag(String address, int length) {
        OperateResultExThree<byte[], Short, Boolean> read = this.ReadWithType(new String[]{address}, new int[]{length});
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(read.Content2, read.Content1);
    }

    private OperateResultExThree<byte[], Short, Boolean> ReadWithType(String[] address, int[] length) {
        OperateResultExOne<byte[]> command = this.BuildReadCommand(address, length);
        if (!command.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(read);
        }
        OperateResult check = this.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(check);
        }
        return AllenBradleyHelper.ExtractActualData((byte[])read.Content, true);
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
        OperateResultExOne<ArrayList<byte[]>> command = this.BuildWriteCommand(address, typeCode, value, length);
        if (!command.IsSuccess) {
            return command;
        }
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return read;
            }
            OperateResult check = this.CheckResponse((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            OperateResultExThree<byte[], Short, Boolean> actual = AllenBradleyHelper.ExtractActualData((byte[])read.Content, false);
            if (actual.IsSuccess) continue;
            return actual;
        }
        return OperateResult.CreateSuccessResult();
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
        if (Utilities.IsStringNullOrEmpty(value)) {
            value = "";
        }
        try {
            byte[] data = value.getBytes(encoding);
            OperateResult write = this.Write(address + ".LEN", data.length);
            if (!write.IsSuccess) {
                return write;
            }
            byte[] buffer = SoftBasic.ArrayExpandToLengthEven(data);
            return this.WriteTag(address + ".DATA[0]", (short)194, buffer, data.length);
        }
        catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        byte[] byArray;
        if (address.startsWith("i=")) {
            OperateResultExOne<byte[]> command = this.BuildWriteCommand(address.substring(2), value);
            if (!command.IsSuccess) {
                return command;
            }
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
            if (!read.IsSuccess) {
                return read;
            }
            OperateResult check = this.CheckResponse((byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            return AllenBradleyHelper.ExtractActualData((byte[])read.Content, false);
        }
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
        return this.WriteTag(address, (short)194, new byte[]{value, 0});
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

    protected byte[] PackCommandService(byte[] portSlot, byte[] ... cips) throws IOException {
        return AllenBradleyHelper.PackCommandService(portSlot, cips);
    }

    @Override
    public String toString() {
        return "AllenBradleyNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

