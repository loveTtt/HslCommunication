/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftBuffer;
import HslCommunication.Core.Address.S7AddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.S7Message;
import HslCommunication.Core.Net.NetworkBase.NetworkDataServerBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Siemens.Helper.SiemensS7Helper;
import HslCommunication.Profinet.Siemens.SiemensPLCS;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SiemensS7Server
extends NetworkDataServerBase {
    private SoftBuffer systemBuffer;
    private SoftBuffer inputBuffer = new SoftBuffer(65536);
    private SoftBuffer outputBuffer = new SoftBuffer(65536);
    private SoftBuffer memeryBuffer = new SoftBuffer(65536);
    private SoftBuffer countBuffer = new SoftBuffer(131072);
    private SoftBuffer timerBuffer = new SoftBuffer(131072);
    private SoftBuffer aiBuffer = new SoftBuffer(65536);
    private SoftBuffer aqBuffer = new SoftBuffer(65536);
    private Map<Integer, SoftBuffer> dbBlockBuffer;
    private final int DataPoolLength = 65536;

    public SiemensS7Server() {
        this.systemBuffer = new SoftBuffer(65536);
        this.systemBuffer.SetBytes(SoftBasic.HexStringToBytes("43 50 55 20 32 32 36 20 43 4E 20 20 20 20 20 20 30 32 30 31"), 0);
        this.WordLength = (short)2;
        this.setByteTransform(new ReverseBytesTransform());
        this.dbBlockBuffer = new HashMap<Integer, SoftBuffer>();
        this.dbBlockBuffer.put(1, new SoftBuffer(65536));
        this.dbBlockBuffer.put(2, new SoftBuffer(65536));
        this.dbBlockBuffer.put(3, new SoftBuffer(65536));
    }

    private OperateResultExOne<SoftBuffer> GetDataAreaFromS7Address(S7AddressData s7Address) {
        switch (s7Address.getDataCode()) {
            case 3: {
                return OperateResultExOne.CreateSuccessResult(this.systemBuffer);
            }
            case 129: {
                return OperateResultExOne.CreateSuccessResult(this.inputBuffer);
            }
            case 130: {
                return OperateResultExOne.CreateSuccessResult(this.outputBuffer);
            }
            case 131: {
                return OperateResultExOne.CreateSuccessResult(this.memeryBuffer);
            }
            case 132: {
                if (this.dbBlockBuffer.containsKey(s7Address.getDbBlock())) {
                    return OperateResultExOne.CreateSuccessResult(this.dbBlockBuffer.get(s7Address.getDbBlock()));
                }
                return new OperateResultExOne<SoftBuffer>(10, StringResources.Language.SiemensError000A());
            }
            case 30: {
                return OperateResultExOne.CreateSuccessResult(this.countBuffer);
            }
            case 31: {
                return OperateResultExOne.CreateSuccessResult(this.timerBuffer);
            }
            case 6: {
                return OperateResultExOne.CreateSuccessResult(this.aiBuffer);
            }
            case 7: {
                return OperateResultExOne.CreateSuccessResult(this.aqBuffer);
            }
        }
        return new OperateResultExOne<SoftBuffer>(6, StringResources.Language.SiemensError0006());
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address, length);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address((S7AddressData)analysis.Content);
        if (!buffer.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(buffer);
        }
        if (((S7AddressData)analysis.Content).getDataCode() == 30 || ((S7AddressData)analysis.Content).getDataCode() == 31) {
            return OperateResultExOne.CreateSuccessResult(((SoftBuffer)buffer.Content).GetBytes(((S7AddressData)analysis.Content).getAddressStart() * 2, length * 2));
        }
        return OperateResultExOne.CreateSuccessResult(((SoftBuffer)buffer.Content).GetBytes(((S7AddressData)analysis.Content).getAddressStart() / 8, length));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address((S7AddressData)analysis.Content);
        if (!buffer.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(buffer);
        }
        if (((S7AddressData)analysis.Content).getDataCode() == 30 || ((S7AddressData)analysis.Content).getDataCode() == 31) {
            ((SoftBuffer)buffer.Content).SetBytes(value, ((S7AddressData)analysis.Content).getAddressStart() * 2);
        } else {
            ((SoftBuffer)buffer.Content).SetBytes(value, ((S7AddressData)analysis.Content).getAddressStart() / 8);
        }
        return OperateResultExOne.CreateSuccessResult();
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetByteResultFromBytes(this.Read(address, (short)1), this.getByteTransform());
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address((S7AddressData)analysis.Content);
        if (!buffer.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(((SoftBuffer)buffer.Content).GetBool(((S7AddressData)analysis.Content).getAddressStart(), length));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return analysis;
        }
        OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address((S7AddressData)analysis.Content);
        if (!buffer.IsSuccess) {
            return buffer;
        }
        ((SoftBuffer)buffer.Content).SetBool(value, ((S7AddressData)analysis.Content).getAddressStart());
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        return SiemensS7Helper.Write(this, SiemensPLCS.S1200, address, value, encoding);
    }

    public OperateResult WriteWString(String address, String value) {
        return SiemensS7Helper.WriteWString(this, SiemensPLCS.S1200, address, value);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        return length == 0 ? this.ReadString(address, encoding) : super.ReadString(address, length, encoding);
    }

    public OperateResultExOne<String> ReadString(String address) {
        return this.ReadString(address, StandardCharsets.US_ASCII);
    }

    public OperateResultExOne<String> ReadString(String address, Charset encoding) {
        return SiemensS7Helper.ReadString(this, SiemensPLCS.S1200, address, encoding);
    }

    public OperateResultExOne<String> ReadWString(String address) {
        return SiemensS7Helper.ReadWString(this, SiemensPLCS.S1200, address);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new S7Message();
    }

    @Override
    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        if (this.IsNeedShakeHands()) {
            OperateResultExOne<byte[]> read1 = this.ReceiveByMessage(socket, 5000, new S7Message());
            if (!read1.IsSuccess) {
                return;
            }
            if (read1.Content == null || ((byte[])read1.Content).length < 10) {
                this.CloseSocket(socket);
                return;
            }
            ((byte[])read1.Content)[5] = -48;
            ((byte[])read1.Content)[6] = ((byte[])read1.Content)[8];
            ((byte[])read1.Content)[7] = ((byte[])read1.Content)[9];
            ((byte[])read1.Content)[8] = 0;
            ((byte[])read1.Content)[9] = 12;
            OperateResult send1 = this.Send(socket, (byte[])read1.Content);
            if (!send1.IsSuccess) {
                return;
            }
            OperateResultExOne<byte[]> read2 = this.ReceiveByMessage(socket, 5000, new S7Message());
            if (!read2.IsSuccess) {
                return;
            }
            OperateResult send2 = this.Send(socket, SoftBasic.HexStringToBytes("03 00 00 1B 02 f0 80 32 03 00 00 00 00 00 08 00 00 00 00 f0 01 00 01 00 f0 00 f0"));
            if (!send2.IsSuccess) {
                return;
            }
        }
        super.ThreadPoolLogin(socket, endPoint);
    }

    protected boolean IsNeedShakeHands() {
        return true;
    }

    @Override
    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        byte[] back = null;
        try {
            if (receive[17] == 4) {
                back = this.ReadByMessage(receive);
            } else if (receive[17] == 5) {
                back = this.WriteByMessage(receive);
            } else if (receive[17] == 0) {
                back = this.ReadPlcType();
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
            }
            Utilities.ByteArrayCopyTo(SoftBasic.BytesArraySelectMiddle(receive, 11, 2), back, 11);
            return OperateResultExOne.CreateSuccessResult(back);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    private byte[] ReadPlcType() {
        return SoftBasic.HexStringToBytes("03 00 00 7D 02 F0 80 32 07 00 00 00 01 00 0C 00 60 00 01 12 08 12 84 01 01 00 00 00 00 FF 09 00 5C 00 11 00 00 00 1C 00 03 00 01 36 45 53 37 20 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 00 06 20 20 00 06 36 45 53 37 20 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 00 06 20 20 00 07 36 45 53 37 20 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 56 04 02 01");
    }

    protected byte[] PackReadBack(byte[] command, ArrayList<Byte> content) {
        if (content.size() > 226) {
            content = new ArrayList();
            Utilities.ArrayListAddArray(content, this.PackReadWordCommandBack((short)5, null));
        }
        byte[] back = new byte[21 + content.size()];
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("03 00 00 1A 02 F0 80 32 03 00 00 00 01 00 02 00 05 00 00 04 01"), back, 0);
        back[2] = (byte)(back.length / 256);
        back[3] = (byte)(back.length % 256);
        back[15] = (byte)(content.size() / 256);
        back[16] = (byte)(content.size() % 256);
        back[20] = command[18];
        Utilities.ByteArrayCopyTo(Utilities.getBytes(content), back, 21);
        return back;
    }

    private byte[] ReadByMessage(byte[] packCommand) throws Exception {
        ArrayList<Byte> content = new ArrayList<Byte>();
        int count = packCommand[18] & 0xFF;
        int index = 19;
        for (int i = 0; i < count; ++i) {
            int length = packCommand[index + 1] & 0xFF;
            byte[] command = SoftBasic.BytesArraySelectMiddle(packCommand, index, length + 2);
            index += length + 2;
            byte[] read = this.ReadByCommand(command);
            Utilities.ArrayListAddArray(content, read);
        }
        return this.PackReadBack(packCommand, content);
    }

    private byte[] ReadByCommand(byte[] command) throws Exception {
        if (command[3] == 1) {
            int startIndex = (command[9] & 0xFF) * 65536 + (command[10] & 0xFF) * 256 + (command[11] & 0xFF);
            int dbBlock = this.getByteTransform().TransUInt16(command, 6);
            int length = this.getByteTransform().TransUInt16(command, 4);
            OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address(new S7AddressData(startIndex, command[8] & 0xFF, dbBlock, 1));
            if (!buffer.IsSuccess) {
                throw new Exception(buffer.Message);
            }
            return this.PackReadBitCommandBack(((SoftBuffer)buffer.Content).GetBool(startIndex));
        }
        if (command[3] == 30 || command[3] == 31) {
            int length = this.getByteTransform().TransUInt16(command, 4);
            int startIndex = (command[9] & 0xFF) * 65536 + (command[10] & 0xFF) * 256 + (command[11] & 0xFF);
            OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address(new S7AddressData(startIndex, command[8] & 0xFF, 0, length));
            if (!buffer.IsSuccess) {
                throw new Exception(buffer.Message);
            }
            return this.PackReadCTCommandBack(((SoftBuffer)buffer.Content).GetBytes(startIndex * 2, length * 2), command[3] == 30 ? 3 : 5);
        }
        int length = this.getByteTransform().TransUInt16(command, 4);
        if (command[3] == 4) {
            length *= 2;
        }
        int dbBlock = this.getByteTransform().TransUInt16(command, 6);
        int startIndex = ((command[9] & 0xFF) * 65536 + (command[10] & 0xFF) * 256 + (command[11] & 0xFF)) / 8;
        OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address(new S7AddressData(startIndex, command[8] & 0xFF, dbBlock, length));
        if (!buffer.IsSuccess) {
            return this.PackReadWordCommandBack((short)buffer.ErrorCode, null);
        }
        return this.PackReadWordCommandBack((short)0, ((SoftBuffer)buffer.Content).GetBytes(startIndex, length));
    }

    private byte[] PackReadWordCommandBack(short err, byte[] result) {
        if (err > 0) {
            byte[] back = new byte[4];
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(err), back, 0);
            return back;
        }
        byte[] back = new byte[4 + result.length];
        back[0] = -1;
        back[1] = 4;
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)(result.length * 8)), back, 2);
        Utilities.ByteArrayCopyTo(result, back, 4);
        return back;
    }

    private byte[] PackReadCTCommandBack(byte[] result, int dataLength) {
        byte[] back = new byte[4 + result.length * dataLength / 2];
        back[0] = -1;
        back[1] = 9;
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)(back.length - 4)), back, 2);
        for (int i = 0; i < result.length / 2; ++i) {
            Utilities.ByteArrayCopyTo(SoftBasic.BytesArraySelectMiddle(result, i * 2, 2), back, 4 + dataLength - 2 + i * dataLength);
        }
        return back;
    }

    private byte[] PackReadBitCommandBack(boolean value) {
        byte[] back = new byte[]{-1, 3, 0, 1, (byte)(value ? 1 : 0)};
        return back;
    }

    protected byte[] PackWriteBack(byte[] packCommand, byte status) {
        return this.PackWriteBack(packCommand, new byte[]{status});
    }

    private byte[] PackWriteBack(byte[] packCommand, byte[] status) {
        byte[] buffer = new byte[21 + status.length];
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("03 00 00 16 02 F0 80 32 03 00 00 00 01 00 02 00 01 00 00 05 01"), buffer, 0);
        buffer[20] = (byte)status.length;
        Utilities.ByteArrayCopyTo(status, buffer, 21);
        buffer[2] = BitConverter.GetBytes(buffer.length)[1];
        buffer[3] = BitConverter.GetBytes(buffer.length)[0];
        return buffer;
    }

    private byte[] WriteByMessage(byte[] packCommand) {
        if (!this.EnableWrite) {
            return this.PackWriteBack(packCommand, (byte)4);
        }
        int itemCount = packCommand[18] & 0xFF;
        byte[] result = new byte[itemCount];
        int dataIndex = 19 + itemCount * 12;
        for (int i = 0; i < itemCount; ++i) {
            int dbBlock;
            if (packCommand[22 + 12 * i] == 2 || packCommand[22 + 12 * i] == 4) {
                dbBlock = this.getByteTransform().TransUInt16(packCommand, 25 + 12 * i);
                int count = this.getByteTransform().TransInt16(packCommand, 23 + 12 * i);
                if (packCommand[22 + 12 * i] == 4) {
                    count *= 2;
                }
                int startIndex = 0;
                startIndex = (packCommand[27 + 12 * i] & 0xFF) >= 28 && (packCommand[27 + 12 * i] & 0xFF) <= 31 ? ((packCommand[28 + 12 * i] & 0xFF) * 65536 + (packCommand[29 + 12 * i] & 0xFF) * 256 + (packCommand[30 + 12 * i] & 0xFF)) * 2 : ((packCommand[28 + 12 * i] & 0xFF) * 65536 + (packCommand[29 + 12 * i] & 0xFF) * 256 + (packCommand[30 + 12 * i] & 0xFF)) / 8;
                byte[] data = this.getByteTransform().TransByte(packCommand, dataIndex + 4, count);
                dataIndex += 4 + count;
                if (i < itemCount - 1 && count % 2 == 1) {
                    ++dataIndex;
                }
                OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address(new S7AddressData(packCommand[27 + 12 * i] & 0xFF, dbBlock, 1));
                if (!buffer.IsSuccess) {
                    result[i] = (byte)buffer.ErrorCode;
                    continue;
                }
                ((SoftBuffer)buffer.Content).SetBytes(data, startIndex);
                result[i] = -1;
                continue;
            }
            dbBlock = this.getByteTransform().TransUInt16(packCommand, 25 + 12 * i);
            int startIndex = (packCommand[28 + 12 * i] & 0xFF) * 65536 + (packCommand[29 + 12 * i] & 0xFF) * 256 + (packCommand[30 + 12 * i] & 0xFF);
            boolean value = packCommand[dataIndex + 4] != 0;
            dataIndex += 5;
            if (i < itemCount - 1) {
                ++dataIndex;
            }
            OperateResultExOne<SoftBuffer> buffer = this.GetDataAreaFromS7Address(new S7AddressData(packCommand[27 + 12 * i] & 0xFF, dbBlock, 1));
            if (!buffer.IsSuccess) {
                result[i] = (byte)buffer.ErrorCode;
                continue;
            }
            ((SoftBuffer)buffer.Content).SetBool(value, startIndex);
            result[i] = -1;
        }
        return this.PackWriteBack(packCommand, result);
    }

    public void AddDbBlock(int db) {
        if (this.dbBlockBuffer.get(db) == null) {
            this.dbBlockBuffer.put(db, new SoftBuffer(65536));
        }
    }

    public void RemoveDbBlock(int db) {
        if (db == 1 || db == 2 || db == 3) {
            return;
        }
        if (this.dbBlockBuffer.get(db) != null) {
            this.dbBlockBuffer.remove(db);
        }
    }

    @Override
    protected void LoadFromBytes(byte[] content) {
        if (content.length < 458752) {
            return;
        }
        this.inputBuffer.SetBytes(content, 0, 0, 65536);
        this.outputBuffer.SetBytes(content, 65536, 0, 65536);
        this.memeryBuffer.SetBytes(content, 131072, 0, 65536);
        this.dbBlockBuffer.get(1).SetBytes(content, 196608, 0, 65536);
        this.dbBlockBuffer.get(2).SetBytes(content, 262144, 0, 65536);
        this.dbBlockBuffer.get(3).SetBytes(content, 327680, 0, 65536);
        if (content.length < 720896) {
            return;
        }
        this.countBuffer.SetBytes(content, 458752, 0, 131072);
        this.timerBuffer.SetBytes(content, 589824, 0, 131072);
    }

    @Override
    protected byte[] SaveToBytes() {
        byte[] buffer = new byte[720896];
        Array.Copy(this.inputBuffer.GetBytes(), 0, buffer, 0, 65536);
        Array.Copy(this.outputBuffer.GetBytes(), 0, buffer, 65536, 65536);
        Array.Copy(this.memeryBuffer.GetBytes(), 0, buffer, 131072, 65536);
        Array.Copy(this.dbBlockBuffer.get(1).GetBytes(), 0, buffer, 196608, 65536);
        Array.Copy(this.dbBlockBuffer.get(2).GetBytes(), 0, buffer, 262144, 65536);
        Array.Copy(this.dbBlockBuffer.get(3).GetBytes(), 0, buffer, 327680, 65536);
        Array.Copy(this.countBuffer.GetBytes(), 0, buffer, 458752, 131072);
        Array.Copy(this.timerBuffer.GetBytes(), 0, buffer, 589824, 131072);
        return buffer;
    }

    @Override
    public String toString() {
        return "SiemensS7Server[" + this.getPort() + "]";
    }
}

