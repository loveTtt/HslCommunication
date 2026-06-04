/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Address.S7AddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.S7Message;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Profinet.Siemens.Helper.SiemensS7Helper;
import HslCommunication.Profinet.Siemens.SiemensDateTime;
import HslCommunication.Profinet.Siemens.SiemensPLCS;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SiemensS7Net
extends NetworkDeviceBase {
    private byte[] plcHead1 = new byte[]{3, 0, 0, 22, 17, -32, 0, 0, 0, 1, 0, -64, 1, 10, -63, 2, 1, 2, -62, 2, 1, 0};
    private byte[] plcHead2 = new byte[]{3, 0, 0, 25, 2, -16, -128, 50, 1, 0, 0, 4, 0, 0, 8, 0, 0, -16, 0, 0, 1, 0, 1, 1, -32};
    private byte[] plcOrderNumber = new byte[]{3, 0, 0, 33, 2, -16, -128, 50, 7, 0, 0, 0, 1, 0, 8, 0, 8, 0, 1, 18, 4, 17, 68, 1, 0, -1, 9, 0, 4, 0, 17, 0, 0};
    private SiemensPLCS CurrentPlc = SiemensPLCS.S1200;
    private byte[] plcHead1_200smart = new byte[]{3, 0, 0, 22, 17, -32, 0, 0, 0, 1, 0, -63, 2, 16, 0, -62, 2, 3, 0, -64, 1, 10};
    private byte[] plcHead2_200smart = new byte[]{3, 0, 0, 25, 2, -16, -128, 50, 1, 0, 0, -52, -63, 0, 8, 0, 0, -16, 0, 0, 1, 0, 1, 3, -64};
    private byte[] plcHead1_200 = new byte[]{3, 0, 0, 22, 17, -32, 0, 0, 0, 1, 0, -63, 2, 77, 87, -62, 2, 77, 87, -64, 1, 9};
    private byte[] plcHead2_200 = new byte[]{3, 0, 0, 25, 2, -16, -128, 50, 1, 0, 0, 0, 0, 0, 8, 0, 0, -16, 0, 0, 1, 0, 1, 3, -64};
    private byte plc_slot = 0;
    private byte plc_rack = 0;
    private int pdu_length = 200;
    private SoftIncrementCount incrementCount = new SoftIncrementCount(65535L, 1L);

    public SiemensS7Net(SiemensPLCS siemens) {
        this.Initialization(siemens, "");
    }

    public SiemensS7Net(SiemensPLCS siemens, String ipAddress) {
        this.Initialization(siemens, ipAddress);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new S7Message();
    }

    private void Initialization(SiemensPLCS siemens, String ipAddress) {
        this.WordLength = (short)2;
        this.setIpAddress(ipAddress);
        this.setPort(102);
        this.CurrentPlc = siemens;
        this.setByteTransform(new ReverseBytesTransform());
        switch (siemens) {
            case S1200: {
                this.plcHead1[21] = 0;
                break;
            }
            case S300: {
                this.plcHead1[21] = 2;
                break;
            }
            case S400: {
                this.plcHead1[21] = 3;
                this.plcHead1[17] = 0;
                break;
            }
            case S1500: {
                this.plcHead1[21] = 0;
                break;
            }
            case S200Smart: {
                this.plcHead1 = this.plcHead1_200smart;
                this.plcHead2 = this.plcHead2_200smart;
                break;
            }
            case S200: {
                this.plcHead1 = this.plcHead1_200;
                this.plcHead2 = this.plcHead2_200;
                break;
            }
            default: {
                this.plcHead1[18] = 0;
            }
        }
    }

    public byte getSlot() {
        return this.plc_slot;
    }

    public void setSlot(byte value) {
        this.plc_slot = value;
        if (this.CurrentPlc != SiemensPLCS.S200 && this.CurrentPlc != SiemensPLCS.S200Smart) {
            this.plcHead1[21] = (byte)(this.plc_rack * 32 + this.plc_slot);
        }
    }

    public byte getRack() {
        return this.plc_rack;
    }

    public void setRack(byte value) {
        this.plc_rack = value;
        if (this.CurrentPlc != SiemensPLCS.S200 && this.CurrentPlc != SiemensPLCS.S200Smart) {
            this.plcHead1[21] = (byte)(this.plc_rack * 32 + this.plc_slot);
        }
    }

    public byte getConnectionType() {
        return this.plcHead1[20];
    }

    public void setConnectionType(byte value) {
        if (this.CurrentPlc != SiemensPLCS.S200 && this.CurrentPlc != SiemensPLCS.S200Smart) {
            this.plcHead1[20] = value;
        }
    }

    public int getLocalTSAP() {
        if (this.CurrentPlc == SiemensPLCS.S200 || this.CurrentPlc == SiemensPLCS.S200Smart) {
            return (this.plcHead1[13] & 0xFF) * 256 + this.plcHead1[14] & 0xFF;
        }
        return (this.plcHead1[16] & 0xFF) * 256 + this.plcHead1[17] & 0xFF;
    }

    public void setLocalTSAP(int value) {
        if (this.CurrentPlc == SiemensPLCS.S200 || this.CurrentPlc == SiemensPLCS.S200Smart) {
            this.plcHead1[13] = Utilities.getBytes(value)[1];
            this.plcHead1[14] = Utilities.getBytes(value)[0];
        } else {
            this.plcHead1[16] = Utilities.getBytes(value)[1];
            this.plcHead1[17] = Utilities.getBytes(value)[0];
        }
    }

    public int getDestTSAP() {
        if (this.CurrentPlc == SiemensPLCS.S200 || this.CurrentPlc == SiemensPLCS.S200Smart) {
            return (this.plcHead1[17] & 0xFF) * 256 + this.plcHead1[18] & 0xFF;
        }
        return (this.plcHead1[20] & 0xFF) * 256 + this.plcHead1[21] & 0xFF;
    }

    public void setDestTSAP(int value) {
        if (this.CurrentPlc == SiemensPLCS.S200 || this.CurrentPlc == SiemensPLCS.S200Smart) {
            this.plcHead1[17] = Utilities.getBytes(value)[1];
            this.plcHead1[18] = Utilities.getBytes(value)[0];
        } else {
            this.plcHead1[20] = Utilities.getBytes(value)[1];
            this.plcHead1[21] = Utilities.getBytes(value)[0];
        }
    }

    public int getPduLength() {
        return this.pdu_length;
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read_first = this.ReadFromCoreServer(socket, this.plcHead1, true, true);
        if (!read_first.IsSuccess) {
            return read_first;
        }
        OperateResultExOne<byte[]> read_second = this.ReadFromCoreServer(socket, this.plcHead2, true, true);
        if (!read_second.IsSuccess) {
            return read_second;
        }
        this.pdu_length = this.getByteTransform().TransUInt16(SoftBasic.BytesArraySelectLast((byte[])read_second.Content, 2), 0) - 28;
        if (this.pdu_length < 200) {
            this.pdu_length = 200;
        }
        this.incrementCount = new SoftIncrementCount(65535L, 1L);
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<String> ReadOrderNumber() {
        OperateResultExOne<String> result = new OperateResultExOne<String>();
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.plcOrderNumber);
        if (read.IsSuccess && ((byte[])read.Content).length > 100) {
            result.IsSuccess = true;
            result.Content = Utilities.getString(Arrays.copyOfRange((byte[])read.Content, 71, 91), "ASCII");
        }
        if (!result.IsSuccess) {
            result.CopyErrorFromOther(read);
        }
        return result;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<S7AddressData> addressResult = S7AddressData.ParseFrom(address, length);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        ArrayList<Byte> bytesContent = new ArrayList<Byte>();
        short alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short)Math.min(length - alreadyFinished, this.pdu_length);
            ((S7AddressData)addressResult.Content).setLength(readLength);
            OperateResultExOne<byte[]> read = this.Read(new S7AddressData[]{(S7AddressData)addressResult.Content});
            if (!read.IsSuccess) {
                return read;
            }
            Utilities.ArrayListAddArray(bytesContent, (byte[])read.Content);
            alreadyFinished = (short)(alreadyFinished + readLength);
            if (((S7AddressData)addressResult.Content).getDataCode() == 31 || ((S7AddressData)addressResult.Content).getDataCode() == 30) {
                ((S7AddressData)addressResult.Content).setAddressStart(((S7AddressData)addressResult.Content).getAddressStart() + readLength / 2);
                continue;
            }
            ((S7AddressData)addressResult.Content).setAddressStart(((S7AddressData)addressResult.Content).getAddressStart() + readLength * 8);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(bytesContent));
    }

    private OperateResultExOne<byte[]> ReadBitFromPLC(String address) {
        OperateResultExOne<byte[]> command = SiemensS7Net.BuildBitReadCommand(address, this.GetMessageId());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return SiemensS7Net.AnalysisReadBit((byte[])read.Content);
    }

    public OperateResultExOne<byte[]> Read(String[] address, short[] length) {
        S7AddressData[] addressResult = new S7AddressData[address.length];
        for (int i = 0; i < address.length; ++i) {
            OperateResultExOne<S7AddressData> tmp = S7AddressData.ParseFrom(address[i], length[i]);
            if (!tmp.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(tmp);
            }
            addressResult[i] = (S7AddressData)tmp.Content;
        }
        return this.Read(addressResult);
    }

    public OperateResultExOne<byte[]> Read(S7AddressData[] s7Addresses) {
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        ArrayList<S7AddressData[]> groups = SiemensS7Helper.ArraySplitByLength(s7Addresses, this.pdu_length);
        for (int i = 0; i < groups.size(); ++i) {
            S7AddressData[] group = groups.get(i);
            if (group.length == 1 && group[0].getLength() > this.pdu_length) {
                S7AddressData[] array = SiemensS7Helper.SplitS7Address(group[0], this.pdu_length);
                for (int j = 0; j < array.length; ++j) {
                    OperateResultExOne<byte[]> read = this.ReadS7AddressData(new S7AddressData[]{array[j]});
                    if (!read.IsSuccess) {
                        return read;
                    }
                    Utilities.ArrayListAddArray(bytes, (byte[])read.Content);
                }
                continue;
            }
            OperateResultExOne<byte[]> read = this.ReadS7AddressData(group);
            if (!read.IsSuccess) {
                return read;
            }
            Utilities.ArrayListAddArray(bytes, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(bytes));
    }

    private OperateResultExOne<byte[]> ReadS7AddressData(S7AddressData[] s7Addresses) {
        OperateResultExOne<byte[]> command = SiemensS7Net.BuildReadCommand(s7Addresses, this.GetMessageId());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return SiemensS7Net.AnalysisReadByte(s7Addresses, (byte[])read.Content);
    }

    public OperateResult Write(String[] address, List<byte[]> data) {
        S7AddressData[] addressResult = new S7AddressData[address.length];
        for (int i = 0; i < address.length; ++i) {
            OperateResultExOne<S7AddressData> tmp = S7AddressData.ParseFrom(address[i], 1);
            if (!tmp.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(tmp);
            }
            addressResult[i] = (S7AddressData)tmp.Content;
        }
        OperateResultExOne<byte[]> command = SiemensS7Net.BuildWriteByteCommand(addressResult, data, this.GetMessageId());
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return SiemensS7Net.AnalysisWrite((byte[])read.Content);
    }

    private static OperateResult AnalysisWrite(byte[] content) {
        try {
            if (content != null && content.length >= 22) {
                int itemCount = content[20];
                for (int i = 0; i < itemCount; ++i) {
                    byte code = content[21 + i];
                    if (code == -1) continue;
                    if (code == 5) {
                        return new OperateResult(code, StringResources.Language.SiemensReadLengthOverPlcAssign());
                    }
                    if (code == 6) {
                        return new OperateResult(code, StringResources.Language.SiemensError0006());
                    }
                    if (code == 10) {
                        return new OperateResult(code, StringResources.Language.SiemensError000A());
                    }
                    return new OperateResult(code, StringResources.Language.SiemensWriteError() + code + " Msg:" + SoftBasic.ByteToHexString(content, ' '));
                }
                return OperateResult.CreateSuccessResult();
            }
            return new OperateResult(StringResources.Language.UnknownError() + " Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
        catch (Exception ex) {
            return new OperateResult("AnalysisWrite failed: " + ex.getMessage() + Environment.NewLine + " Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        return ByteTransformHelper.GetResultFromBytes(this.ReadBitFromPLC(address), new FunctionOperateExOne<byte[], Boolean>(){

            @Override
            public Boolean Action(byte[] content) {
                return content[0] != 0;
            }
        });
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExThree<Integer, Short, Integer> calculate = HslHelper.CalculateStartBitIndexAndLength(((S7AddressData)analysis.Content).getAddressStart(), length);
        ((S7AddressData)analysis.Content).setAddressStart((Integer)calculate.Content1);
        ((S7AddressData)analysis.Content).setLength(((Short)calculate.Content2).shortValue());
        OperateResultExOne<byte[]> read = this.Read(new S7AddressData[]{(S7AddressData)analysis.Content});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), (Integer)calculate.Content3, length));
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)1), new FunctionOperateExOne<byte[], Byte>(){

            @Override
            public Byte Action(byte[] content) {
                return content[0];
            }
        });
    }

    private OperateResult WriteBase(byte[] entireValue) {
        OperateResultExOne<byte[]> write = this.ReadFromCoreServer(entireValue);
        if (!write.IsSuccess) {
            return write;
        }
        return SiemensS7Net.AnalysisWrite((byte[])write.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return this.Write((S7AddressData)analysis.Content, value);
    }

    private OperateResult Write(S7AddressData address, byte[] value) {
        short writeLength;
        int length = value.length;
        for (int alreadyFinished = 0; alreadyFinished < length; alreadyFinished += writeLength) {
            writeLength = (short)Math.min(length - alreadyFinished, 200);
            byte[] buffer = this.getByteTransform().TransByte(value, alreadyFinished, writeLength);
            OperateResultExOne<byte[]> command = SiemensS7Net.BuildWriteByteCommand(address, buffer, this.GetMessageId());
            if (!command.IsSuccess) {
                return command;
            }
            OperateResult write = this.WriteBase((byte[])command.Content);
            if (!write.IsSuccess) {
                return write;
            }
            address.setAddressStart(address.getAddressStart() + writeLength * 8);
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        OperateResultExOne<byte[]> command = SiemensS7Net.BuildWriteBitCommand(address, value, this.GetMessageId());
        if (!command.IsSuccess) {
            return command;
        }
        return this.WriteBase((byte[])command.Content);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExThree<Integer, Short, Integer> calcu = HslHelper.CalculateStartBitIndexAndLength(((S7AddressData)analysis.Content).getAddressStart(), (short)values.length);
        int newStart = (Integer)calcu.Content1;
        short byteLength = (Short)calcu.Content2;
        int offset = (Integer)calcu.Content3;
        ((S7AddressData)analysis.Content).setAddressStart(newStart);
        ((S7AddressData)analysis.Content).setLength(byteLength);
        OperateResultExOne<byte[]> read = this.Read(new S7AddressData[]{(S7AddressData)analysis.Content});
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        boolean[] boolArray = SoftBasic.ByteToBoolArray((byte[])read.Content);
        System.arraycopy(values, 0, boolArray, offset, values.length);
        return this.Write((S7AddressData)analysis.Content, SoftBasic.BoolArrayToByte(boolArray));
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        if (length == 0) {
            return this.ReadString(address, encoding);
        }
        return super.ReadString(address, length, encoding);
    }

    public OperateResultExOne<String> ReadString(String address) {
        return this.ReadString(address, StandardCharsets.US_ASCII);
    }

    public OperateResultExOne<String> ReadString(String address, Charset encoding) {
        if (this.CurrentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = this.Read(address, (short)2);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content)[0] == 0 || ((byte[])read.Content)[0] == -1) {
                return new OperateResultExOne<String>("Value in plc is not string type");
            }
            OperateResultExOne<byte[]> readString = this.Read(address, (short)(2 + ((byte[])read.Content)[1]));
            if (!readString.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(readString);
            }
            try {
                byte[] buffer = new byte[((byte[])read.Content)[1]];
                System.arraycopy(readString.Content, 2, buffer, 0, buffer.length);
                return OperateResultExOne.CreateSuccessResult(new String(buffer, encoding));
            }
            catch (Exception ex) {
                return new OperateResultExOne<String>(ex.getMessage());
            }
        }
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> readString = this.Read(address, (short)(1 + ((byte[])read.Content)[0]));
        if (!readString.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(readString);
        }
        try {
            byte[] buffer = new byte[((byte[])read.Content)[0]];
            System.arraycopy(readString.Content, 1, buffer, 0, buffer.length);
            return OperateResultExOne.CreateSuccessResult(new String(buffer, encoding));
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public OperateResultExOne<String> ReadWString(String address) {
        if (this.CurrentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = this.Read(address, (short)4);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> readString = this.Read(address, (short)(4 + (((byte[])read.Content)[2] * 256 + ((byte[])read.Content)[3]) * 2));
            if (!readString.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(readString);
            }
            byte[] buffer = SoftBasic.BytesArrayRemoveBegin((byte[])readString.Content, 4);
            return OperateResultExOne.CreateSuccessResult(new String(buffer, StandardCharsets.UTF_16));
        }
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> readString = this.Read(address, (short)(1 + ((byte[])read.Content)[0] * 2));
        if (!readString.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(readString);
        }
        byte[] buffer = SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin((byte[])readString.Content, 1));
        return OperateResultExOne.CreateSuccessResult(new String(buffer, StandardCharsets.UTF_16));
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] temp = this.getByteTransform().TransByte(value, encoding);
        if (this.WordLength == 1) {
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
        }
        if (this.CurrentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> readLength = this.Read(address, (short)2);
            if (!readLength.IsSuccess) {
                return readLength;
            }
            if (((byte[])readLength.Content)[0] == -1) {
                return new OperateResultExOne("Value in plc is not string type");
            }
            if (((byte[])readLength.Content)[0] == 0) {
                ((byte[])readLength.Content)[0] = -2;
            }
            if (temp.length > (((byte[])readLength.Content)[0] & 0xFF)) {
                return new OperateResultExOne("String length is too long than plc defined");
            }
            return this.Write(address, SoftBasic.SpliceArray(new byte[]{((byte[])readLength.Content)[0], (byte)temp.length}, temp));
        }
        return this.Write(address, SoftBasic.SpliceArray(new byte[]{(byte)temp.length}, temp));
    }

    public OperateResult WriteWString(String address, String value) {
        if (this.CurrentPlc != SiemensPLCS.S200Smart) {
            byte[] buffer;
            if (value == null) {
                value = "";
            }
            if ((buffer = value.getBytes(StandardCharsets.UTF_16)).length > 2 && buffer[0] == -2 && buffer[1] == -1) {
                buffer = SoftBasic.BytesArrayRemoveBegin(buffer, 2);
            }
            OperateResultExOne<byte[]> readLength = this.Read(address, (short)4);
            if (!readLength.IsSuccess) {
                return readLength;
            }
            int defineLength = ((byte[])readLength.Content)[0] * 256 + ((byte[])readLength.Content)[1];
            if (defineLength == 0) {
                defineLength = 254;
                ((byte[])readLength.Content)[1] = -2;
            }
            if (value.length() > defineLength) {
                return new OperateResultExOne("String length is too long than plc defined");
            }
            byte[] write = new byte[buffer.length + 4];
            write[0] = ((byte[])readLength.Content)[0];
            write[1] = ((byte[])readLength.Content)[1];
            write[2] = Utilities.getBytes(value.length())[1];
            write[3] = Utilities.getBytes(value.length())[0];
            System.arraycopy(buffer, 0, write, 4, buffer.length);
            return this.Write(address, write);
        }
        return this.Write(address, value, StandardCharsets.UTF_16);
    }

    public OperateResultExOne<Date> ReadDateTime(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)8);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        try {
            return OperateResultExOne.CreateSuccessResult(SiemensDateTime.FromByteArray((byte[])read.Content));
        }
        catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage());
        }
    }

    public OperateResultExOne<Date> ReadDTLDataTime(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)12);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SiemensDateTime.GetDTLTime(this.getByteTransform(), (byte[])read.Content, 0));
    }

    public OperateResult Write(String address, Date dateTime) {
        try {
            return this.Write(address, SiemensDateTime.ToByteArray(dateTime));
        }
        catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    public OperateResult WriteDTLTime(String address, Date dateTime) {
        return this.Write(address, SiemensDateTime.GetBytesFromDTLTime(this.getByteTransform(), dateTime));
    }

    public OperateResultExOne<Date> ReadDate(String address) {
        OperateResultExOne<Integer> read = this.ReadUInt16(address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        try {
            Calendar c = Calendar.getInstance();
            c.set(1990, 0, 1);
            c.add(5, (Integer)read.Content);
            return OperateResultExOne.CreateSuccessResult(c.getTime());
        }
        catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage());
        }
    }

    public OperateResult WriteDate(String address, Date dateTime) {
        Calendar c = Calendar.getInstance();
        c.set(1990, 0, 1);
        long tick = dateTime.getTime() - c.getTime().getTime();
        return this.Write(address, (short)(tick / 86400000L));
    }

    private int GetMessageId() {
        return (int)this.incrementCount.GetCurrentValue();
    }

    @Override
    public String toString() {
        return "SiemensS7Net";
    }

    public static int CalculateAddressStarted(String address) {
        if (address.indexOf(46) < 0) {
            return Integer.parseInt(address) * 8;
        }
        String[] temp = address.split("\\.");
        return Integer.parseInt(temp[0]) * 8 + Integer.parseInt(temp[1]);
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(S7AddressData[] s7Addresses, int msgId) {
        if (s7Addresses == null) {
            throw new RuntimeException("s7Addresses");
        }
        if (s7Addresses.length > 19) {
            throw new RuntimeException(StringResources.Language.SiemensReadLengthCannotLargerThan19());
        }
        int readCount = s7Addresses.length;
        byte[] _PLCCommand = new byte[19 + readCount * 12];
        _PLCCommand[0] = 3;
        _PLCCommand[1] = 0;
        _PLCCommand[2] = (byte)(_PLCCommand.length / 256);
        _PLCCommand[3] = (byte)(_PLCCommand.length % 256);
        _PLCCommand[4] = 2;
        _PLCCommand[5] = -16;
        _PLCCommand[6] = -128;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        _PLCCommand[13] = (byte)((_PLCCommand.length - 17) / 256);
        _PLCCommand[14] = (byte)((_PLCCommand.length - 17) % 256);
        _PLCCommand[15] = 0;
        _PLCCommand[16] = 0;
        _PLCCommand[17] = 4;
        _PLCCommand[18] = (byte)readCount;
        for (int ii = 0; ii < readCount; ++ii) {
            _PLCCommand[19 + ii * 12] = 18;
            _PLCCommand[20 + ii * 12] = 10;
            _PLCCommand[21 + ii * 12] = 16;
            if (s7Addresses[ii].getDataCode() == 30 || s7Addresses[ii].getDataCode() == 31) {
                _PLCCommand[22 + ii * 12] = (byte)s7Addresses[ii].getDataCode();
                _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 / 256);
                _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 % 256);
            } else if (s7Addresses[ii].getDataCode() == 6 | s7Addresses[ii].getDataCode() == 7) {
                _PLCCommand[22 + ii * 12] = 4;
                _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 / 256);
                _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 % 256);
            } else {
                _PLCCommand[22 + ii * 12] = 2;
                _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 256);
                _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() % 256);
            }
            _PLCCommand[25 + ii * 12] = (byte)(s7Addresses[ii].getDbBlock() / 256);
            _PLCCommand[26 + ii * 12] = (byte)(s7Addresses[ii].getDbBlock() % 256);
            _PLCCommand[27 + ii * 12] = (byte)s7Addresses[ii].getDataCode();
            _PLCCommand[28 + ii * 12] = (byte)(s7Addresses[ii].getAddressStart() / 256 / 256 % 256);
            _PLCCommand[29 + ii * 12] = (byte)(s7Addresses[ii].getAddressStart() / 256 % 256);
            _PLCCommand[30 + ii * 12] = (byte)(s7Addresses[ii].getAddressStart() % 256);
        }
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildBitReadCommand(String address, int msgId) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] _PLCCommand = new byte[31];
        _PLCCommand[0] = 3;
        _PLCCommand[1] = 0;
        _PLCCommand[2] = (byte)(_PLCCommand.length / 256);
        _PLCCommand[3] = (byte)(_PLCCommand.length % 256);
        _PLCCommand[4] = 2;
        _PLCCommand[5] = (byte)-16;
        _PLCCommand[6] = (byte)-128;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        _PLCCommand[13] = (byte)((_PLCCommand.length - 17) / 256);
        _PLCCommand[14] = (byte)((_PLCCommand.length - 17) % 256);
        _PLCCommand[15] = 0;
        _PLCCommand[16] = 0;
        _PLCCommand[17] = 4;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        _PLCCommand[22] = 1;
        _PLCCommand[23] = 0;
        _PLCCommand[24] = 1;
        _PLCCommand[25] = (byte)(((S7AddressData)analysis.Content).getDbBlock() / 256);
        _PLCCommand[26] = (byte)(((S7AddressData)analysis.Content).getDbBlock() % 256);
        _PLCCommand[27] = (byte)((S7AddressData)analysis.Content).getDataCode();
        _PLCCommand[28] = (byte)(((S7AddressData)analysis.Content).getAddressStart() / 256 / 256 % 256);
        _PLCCommand[29] = (byte)(((S7AddressData)analysis.Content).getAddressStart() / 256 % 256);
        _PLCCommand[30] = (byte)(((S7AddressData)analysis.Content).getAddressStart() % 256);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(String address, byte[] data, int msgId) {
        if (data == null) {
            data = new byte[]{};
        }
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return SiemensS7Net.BuildWriteByteCommand((S7AddressData)analysis.Content, data, msgId);
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(S7AddressData s7Address, byte[] data, int msgId) {
        if (data == null) {
            data = new byte[]{};
        }
        byte[] _PLCCommand = new byte[35 + data.length];
        _PLCCommand[0] = 3;
        _PLCCommand[1] = 0;
        _PLCCommand[2] = (byte)((35 + data.length) / 256);
        _PLCCommand[3] = (byte)((35 + data.length) % 256);
        _PLCCommand[4] = 2;
        _PLCCommand[5] = -16;
        _PLCCommand[6] = -128;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        _PLCCommand[13] = 0;
        _PLCCommand[14] = 14;
        _PLCCommand[15] = (byte)((4 + data.length) / 256);
        _PLCCommand[16] = (byte)((4 + data.length) % 256);
        _PLCCommand[17] = 5;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        if (s7Address.getDataCode() == 6 || s7Address.getDataCode() == 7) {
            _PLCCommand[22] = 4;
            _PLCCommand[23] = (byte)(data.length / 2 / 256);
            _PLCCommand[24] = (byte)(data.length / 2 % 256);
        } else {
            _PLCCommand[22] = 2;
            _PLCCommand[23] = (byte)(data.length / 256);
            _PLCCommand[24] = (byte)(data.length % 256);
        }
        _PLCCommand[25] = (byte)(s7Address.getDbBlock() / 256);
        _PLCCommand[26] = (byte)(s7Address.getDbBlock() % 256);
        _PLCCommand[27] = (byte)s7Address.getDataCode();
        _PLCCommand[28] = (byte)(s7Address.getAddressStart() / 256 / 256 % 256);
        _PLCCommand[29] = (byte)(s7Address.getAddressStart() / 256 % 256);
        _PLCCommand[30] = (byte)(s7Address.getAddressStart() % 256);
        _PLCCommand[31] = 0;
        _PLCCommand[32] = 4;
        _PLCCommand[33] = (byte)(data.length * 8 / 256);
        _PLCCommand[34] = (byte)(data.length * 8 % 256);
        System.arraycopy(data, 0, _PLCCommand, 35, data.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteBitCommand(String address, boolean data, int msgId) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = new byte[]{data ? (byte)1 : 0};
        byte[] _PLCCommand = new byte[35 + buffer.length];
        _PLCCommand[0] = 3;
        _PLCCommand[1] = 0;
        _PLCCommand[2] = (byte)((35 + buffer.length) / 256);
        _PLCCommand[3] = (byte)((35 + buffer.length) % 256);
        _PLCCommand[4] = 2;
        _PLCCommand[5] = -16;
        _PLCCommand[6] = -128;
        _PLCCommand[7] = 50;
        _PLCCommand[8] = 1;
        _PLCCommand[9] = 0;
        _PLCCommand[10] = 0;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        _PLCCommand[13] = 0;
        _PLCCommand[14] = 14;
        _PLCCommand[15] = (byte)((4 + buffer.length) / 256);
        _PLCCommand[16] = (byte)((4 + buffer.length) % 256);
        _PLCCommand[17] = 5;
        _PLCCommand[18] = 1;
        _PLCCommand[19] = 18;
        _PLCCommand[20] = 10;
        _PLCCommand[21] = 16;
        _PLCCommand[22] = 1;
        _PLCCommand[23] = (byte)(buffer.length / 256);
        _PLCCommand[24] = (byte)(buffer.length % 256);
        _PLCCommand[25] = (byte)(((S7AddressData)analysis.Content).getDbBlock() / 256);
        _PLCCommand[26] = (byte)(((S7AddressData)analysis.Content).getDbBlock() % 256);
        _PLCCommand[27] = (byte)((S7AddressData)analysis.Content).getDataCode();
        _PLCCommand[28] = (byte)(((S7AddressData)analysis.Content).getAddressStart() / 256 / 256);
        _PLCCommand[29] = (byte)(((S7AddressData)analysis.Content).getAddressStart() / 256);
        _PLCCommand[30] = (byte)(((S7AddressData)analysis.Content).getAddressStart() % 256);
        if (((S7AddressData)analysis.Content).getDataCode() == 28) {
            _PLCCommand[31] = 0;
            _PLCCommand[32] = 9;
        } else {
            _PLCCommand[31] = 0;
            _PLCCommand[32] = 3;
        }
        _PLCCommand[33] = (byte)(buffer.length / 256);
        _PLCCommand[34] = (byte)(buffer.length % 256);
        System.arraycopy(buffer, 0, _PLCCommand, 35, buffer.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    public static OperateResultExOne<byte[]> BuildWriteByteCommand(S7AddressData[] s7Address, List<byte[]> data, int msgId) {
        MemoryStream ms = new MemoryStream();
        ms.Write(new byte[]{3, 0, 0, 0, 2, -16, -128});
        ms.WriteByte(50);
        ms.WriteByte(1);
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(BitConverter.GetBytes(msgId)[1]);
        ms.WriteByte(BitConverter.GetBytes(msgId)[0]);
        ms.WriteByte(BitConverter.GetBytes(s7Address.length * 12 + 2)[1]);
        ms.WriteByte(BitConverter.GetBytes(s7Address.length * 12 + 2)[0]);
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(5);
        ms.WriteByte((byte)s7Address.length);
        for (int i = 0; i < s7Address.length; ++i) {
            SiemensS7Net.WriteS7AddressToStream(ms, s7Address[i], (byte)2, data.get(i) == null ? 0 : ((byte[])data.get(i)).length);
        }
        int dataLen = ms.getLength();
        for (int i = 0; i < data.size(); ++i) {
            ms.WriteByte(0);
            ms.WriteByte(4);
            if (data.get(i) != null) {
                ms.WriteByte(BitConverter.GetBytes(((byte[])data.get(i)).length * 8)[1]);
                ms.WriteByte(BitConverter.GetBytes(((byte[])data.get(i)).length * 8)[0]);
                ms.Write((byte[])data.get(i));
                if (i >= data.size() - 1 || ((byte[])data.get(i)).length % 2 != 1) continue;
                ms.WriteByte(0);
                continue;
            }
            ms.WriteByte(0);
            ms.WriteByte(0);
        }
        byte[] _PLCCommand = ms.ToArray();
        _PLCCommand[2] = (byte)(_PLCCommand.length / 256);
        _PLCCommand[3] = (byte)(_PLCCommand.length % 256);
        _PLCCommand[15] = BitConverter.GetBytes(_PLCCommand.length - dataLen)[1];
        _PLCCommand[16] = BitConverter.GetBytes(_PLCCommand.length - dataLen)[0];
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    private static void WriteS7AddressToStream(MemoryStream ms, S7AddressData add, byte writeType, int dataLen) {
        ms.WriteByte(18);
        ms.WriteByte(10);
        ms.WriteByte(16);
        if (add.getDataCode() == 6 || add.getDataCode() == 7) {
            ms.WriteByte(4);
            ms.WriteByte(BitConverter.GetBytes(dataLen / 2)[1]);
            ms.WriteByte(BitConverter.GetBytes(dataLen / 2)[0]);
        } else {
            ms.WriteByte(writeType);
            ms.WriteByte(BitConverter.GetBytes(dataLen)[1]);
            ms.WriteByte(BitConverter.GetBytes(dataLen)[0]);
        }
        ms.WriteByte(BitConverter.GetBytes(add.getDbBlock())[1]);
        ms.WriteByte(BitConverter.GetBytes(add.getDbBlock())[0]);
        ms.WriteByte(add.getDataCode());
        ms.WriteByte(BitConverter.GetBytes(add.getAddressStart())[2]);
        ms.WriteByte(BitConverter.GetBytes(add.getAddressStart())[1]);
        ms.WriteByte(BitConverter.GetBytes(add.getAddressStart())[0]);
    }

    private static OperateResultExOne<byte[]> AnalysisReadByte(S7AddressData[] s7Addresses, byte[] content) {
        int receiveCount = 0;
        for (int i = 0; i < s7Addresses.length; ++i) {
            if (s7Addresses[i].getDataCode() == 31 || s7Addresses[i].getDataCode() == 30) {
                receiveCount += s7Addresses[i].getLength() * 2;
                continue;
            }
            receiveCount += s7Addresses[i].getLength();
        }
        if (content.length >= 21 && content[20] == s7Addresses.length) {
            byte[] buffer = new byte[receiveCount];
            int kk = 0;
            int ll = 0;
            for (int ii = 21; ii < content.length; ++ii) {
                if (ii + 1 >= content.length) continue;
                if (content[ii] == -1 && content[ii + 1] == 4) {
                    System.arraycopy(content, ii + 4, buffer, ll, s7Addresses[kk].getLength());
                    ii += s7Addresses[kk].getLength() + 3;
                    ll += s7Addresses[kk].getLength();
                    ++kk;
                    continue;
                }
                if (content[ii] == -1 && content[ii + 1] == 9) {
                    int i;
                    int count = content[ii + 2] * 256 + content[ii + 3];
                    if (count % 3 == 0) {
                        for (i = 0; i < count / 3; ++i) {
                            System.arraycopy(content, ii + 5 + 3 * i, buffer, ll, 2);
                            ll += 2;
                        }
                    } else {
                        for (i = 0; i < count / 5; ++i) {
                            System.arraycopy(content, ii + 7 + 5 * i, buffer, ll, 2);
                            ll += 2;
                        }
                    }
                    ii += count + 4;
                    ++kk;
                    continue;
                }
                if (content[ii] == 5 && content[ii + 1] == 0) {
                    return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensReadLengthOverPlcAssign());
                }
                if (content[ii] == 6 && content[ii + 1] == 0) {
                    return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensError0006());
                }
                if (content[ii] != 10 || content[ii + 1] != 0) continue;
                return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensError000A());
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed() + " Msg:" + SoftBasic.ByteToHexString(content, ' '));
    }

    private static OperateResultExOne<byte[]> AnalysisReadBit(byte[] content) {
        int receiveCount = 1;
        if (content.length >= 21 && content[20] == 1) {
            byte[] buffer = new byte[receiveCount];
            if (22 < content.length) {
                if (content[21] == -1 && content[22] == 3) {
                    buffer[0] = content[25];
                } else {
                    if (content[21] == 5 && content[22] == 0) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensReadLengthOverPlcAssign());
                    }
                    if (content[21] == 6 && content[22] == 0) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError0006());
                    }
                    if (content[21] == 10 && content[22] == 0) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError000A());
                    }
                    return new OperateResultExOne<byte[]>(content[21], StringResources.Language.UnknownError() + " Source: " + SoftBasic.ByteToHexString(content, ' '));
                }
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed());
    }
}

