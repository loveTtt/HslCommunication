/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftBuffer;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.ModbusTcpMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDataServerBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.ModBus.Helper.ModbusDataDict;
import HslCommunication.ModBus.ModbusInfo;
import HslCommunication.Serial.SoftCRC16;

public class ModbusTcpServer
extends NetworkDataServerBase {
    public boolean UseModbusRtuOverTcp = false;
    public int RequestDelayTime = 0;
    private ModbusDataDict dictModbusDataPool;
    private byte station = 1;
    private boolean stationDataIsolation = false;
    private SoftBuffer fileBuffer = new SoftBuffer(65535);

    public ModbusTcpServer() {
        this.dictModbusDataPool = new ModbusDataDict();
        this.setByteTransform(new ReverseWordTransform());
        this.WordLength = 1;
    }

    public DataFormat getDataFormat() {
        return this.getByteTransform().getDataFormat();
    }

    public void setDataFormat(DataFormat value) {
        this.getByteTransform().setDataFormat(value);
    }

    public boolean getIsStringReverse() {
        return this.getByteTransform().getIsStringReverse();
    }

    public void setIsStringReverse(boolean value) {
        this.getByteTransform().setIsStringReverse(value);
    }

    public byte getStation() {
        return this.station;
    }

    public void setStation(byte value) {
        this.station = value;
    }

    public boolean getStationDataIsolation() {
        return this.stationDataIsolation;
    }

    public void setStationDataIsolation(boolean value) {
        this.stationDataIsolation = value;
        this.dictModbusDataPool.Set(value);
    }

    @Override
    protected byte[] SaveToBytes() {
        return this.dictModbusDataPool.GetModbusPool(this.station).SaveToBytes();
    }

    @Override
    protected void LoadFromBytes(byte[] content) {
        this.dictModbusDataPool.GetModbusPool(this.station).LoadFromBytes(content, 0);
    }

    public boolean ReadCoil(String address) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).ReadCoil(address);
    }

    public boolean[] ReadCoil(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).ReadCoil(address, length);
    }

    public void WriteCoil(String address, boolean data) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).WriteCoil(address, data);
    }

    public void WriteCoil(String address, boolean[] data) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).WriteCoil(address, data);
    }

    public boolean ReadDiscrete(String address) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).ReadDiscrete(address);
    }

    public boolean[] ReadDiscrete(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).ReadDiscrete(address, length);
    }

    public void WriteDiscrete(String address, boolean data) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).WriteDiscrete(address, data);
    }

    public void WriteDiscrete(String address, boolean[] data) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).WriteDiscrete(address, data);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        if (address.startsWith("file=")) {
            int file = 0;
            OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "file");
            if (extra.IsSuccess) {
                file = (Integer)extra.Content1;
                address = (String)extra.Content2;
            }
            int add = Integer.parseInt(address);
            return OperateResultExOne.CreateSuccessResult(this.fileBuffer.GetBytes(add * 2, length * 2));
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).Read(address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        if (address.startsWith("file=")) {
            int file = 0;
            OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "file");
            if (extra.IsSuccess) {
                file = (Integer)extra.Content1;
                address = (String)extra.Content2;
            }
            int add = Integer.parseInt(address);
            this.fileBuffer.SetBytes(value, add * 2);
            return OperateResult.CreateSuccessResult();
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).Write(address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).ReadBool(address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExTwo<Integer, String> opStation = HslHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) {
            address = (String)opStation.Content2;
        }
        return this.dictModbusDataPool.GetModbusPool(((Integer)opStation.Content1).byteValue()).Write(address, value);
    }

    public void Write(String address, byte high, byte low) {
        this.Write(address, new byte[]{high, low});
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return this.UseModbusRtuOverTcp ? null : new ModbusTcpMessage();
    }

    @Override
    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        if (this.RequestDelayTime > 0) {
            try {
                Thread.sleep(this.RequestDelayTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.UseModbusRtuOverTcp) {
            if (!SoftCRC16.CheckCRC16(receive)) {
                return new OperateResultExOne<byte[]>("CRC Check Failed ");
            }
            byte[] modbusCore = SoftBasic.BytesArrayRemoveLast(receive, 2);
            if (!this.CheckModbusMessageLegal(modbusCore)) {
                return new OperateResultExOne<byte[]>("Modbus rtu message check failed ");
            }
            if (!this.getStationDataIsolation() && this.station != modbusCore[0]) {
                return new OperateResultExOne<byte[]>("Station not match Modbus-rtu ");
            }
            return OperateResultExOne.CreateSuccessResult(ModbusInfo.PackCommandToRtu(this.ReadFromModbusCore(modbusCore)));
        }
        if (!this.CheckModbusMessageLegal(SoftBasic.BytesArrayRemoveBegin(receive, 6))) {
            return new OperateResultExOne<byte[]>("Modbus message check failed");
        }
        int id = (receive[0] & 0xFF) * 256 + (receive[1] & 0xFF);
        if (!this.getStationDataIsolation() && this.station != receive[6]) {
            return new OperateResultExOne<byte[]>("Station not match Modbus-tcp ");
        }
        byte[] back = ModbusInfo.PackCommandToTcp(this.ReadFromModbusCore(SoftBasic.BytesArrayRemoveBegin(receive, 6)), id);
        return OperateResultExOne.CreateSuccessResult(back);
    }

    private byte[] CreateExceptionBack(byte[] modbusCore, byte error) {
        return new byte[]{modbusCore[0], (byte)(modbusCore[1] + 128), error};
    }

    private byte[] CreateReadBack(byte[] modbusCore, byte[] content) {
        return SoftBasic.SpliceArray(new byte[]{modbusCore[0], modbusCore[1], (byte)content.length}, content);
    }

    private byte[] CreateWriteBack(byte[] modbus) {
        return SoftBasic.BytesArraySelectBegin(modbus, 6);
    }

    private byte[] ReadCoilBack(byte[] modbus, String addressHead) {
        try {
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            int length = this.getByteTransform().TransUInt16(modbus, 4);
            if (address + length > 65536) {
                return this.CreateExceptionBack(modbus, (byte)2);
            }
            if (length > 2040) {
                return this.CreateExceptionBack(modbus, (byte)3);
            }
            boolean[] read = (boolean[])this.dictModbusDataPool.GetModbusPool((byte)modbus[0]).ReadBool((String)new StringBuilder().append((String)addressHead).append((int)address).toString(), (short)((short)length)).Content;
            return this.CreateReadBack(modbus, SoftBasic.BoolArrayToByte(read));
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] ReadRegisterBack(byte[] modbus, String addressHead) {
        try {
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            int length = this.getByteTransform().TransUInt16(modbus, 4);
            if (address + length > 65536) {
                return this.CreateExceptionBack(modbus, (byte)2);
            }
            if (length > 127) {
                return this.CreateExceptionBack(modbus, (byte)3);
            }
            byte[] buffer = (byte[])this.dictModbusDataPool.GetModbusPool((byte)modbus[0]).Read((String)new StringBuilder().append((String)addressHead).append((String)String.valueOf((int)address)).toString(), (short)((short)length)).Content;
            return this.CreateReadBack(modbus, buffer);
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteOneCoilBack(byte[] modbus) {
        try {
            if (!this.EnableWrite) {
                return this.CreateExceptionBack(modbus, (byte)4);
            }
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            if (modbus[4] == -1 && modbus[5] == 0) {
                this.dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new boolean[]{true});
            } else if (modbus[4] == 0 && modbus[5] == 0) {
                this.dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new boolean[]{false});
            }
            return this.CreateWriteBack(modbus);
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteOneRegisterBack(byte[] modbus) {
        try {
            if (!this.EnableWrite) {
                return this.CreateExceptionBack(modbus, (byte)4);
            }
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            short ValueOld = (Short)this.ReadInt16((String)String.valueOf((int)address)).Content;
            this.dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new byte[]{modbus[4], modbus[5]});
            short ValueNew = (Short)this.ReadInt16((String)String.valueOf((int)address)).Content;
            return this.CreateWriteBack(modbus);
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteCoilsBack(byte[] modbus) {
        try {
            int length;
            if (!this.EnableWrite) {
                return this.CreateExceptionBack(modbus, (byte)4);
            }
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            if (address + (length = this.getByteTransform().TransUInt16(modbus, 4)) > 65536) {
                return this.CreateExceptionBack(modbus, (byte)2);
            }
            if (length > 2040) {
                return this.CreateExceptionBack(modbus, (byte)3);
            }
            this.dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(modbus, 7), length));
            return this.CreateWriteBack(modbus);
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteRegisterBack(byte[] modbus) {
        try {
            int length;
            if (!this.EnableWrite) {
                return this.CreateExceptionBack(modbus, (byte)4);
            }
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            if (address + (length = this.getByteTransform().TransUInt16(modbus, 4)) > 65536) {
                return this.CreateExceptionBack(modbus, (byte)2);
            }
            if (length > 127) {
                return this.CreateExceptionBack(modbus, (byte)3);
            }
            byte[] oldValue = (byte[])this.dictModbusDataPool.GetModbusPool((byte)modbus[0]).Read((String)String.valueOf((int)address), (short)((short)length)).Content;
            this.dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), SoftBasic.BytesArrayRemoveBegin(modbus, 7));
            return this.CreateWriteBack(modbus);
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] ReadFileRecordBack(byte[] modbus) {
        try {
            int count = (modbus.length - 3) / 7;
            MemoryStream memoryStream = new MemoryStream();
            for (int i = 0; i < count; ++i) {
                int fileNumber = this.getByteTransform().TransUInt16(modbus, 4 + 7 * i);
                int startAdd = this.getByteTransform().TransUInt16(modbus, 6 + 7 * i);
                int length = this.getByteTransform().TransUInt16(modbus, 8 + 7 * i);
                if (length * 2 + 1 > 255) {
                    return this.CreateExceptionBack(modbus, (byte)3);
                }
                memoryStream.WriteByte((byte)(length * 2 + 1));
                memoryStream.WriteByte(6);
                memoryStream.Write(this.fileBuffer.GetBytes(startAdd * 2, length * 2));
                if (memoryStream.getLength() <= 255) continue;
                return this.CreateExceptionBack(modbus, (byte)3);
            }
            return this.CreateReadBack(modbus, memoryStream.ToArray());
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteFileRecordBack(byte[] modbus) {
        try {
            int writeLength;
            MemoryStream memoryStream = new MemoryStream();
            for (int index = 3; index < modbus.length; index += 7 + writeLength * 2) {
                int fileNumber = this.getByteTransform().TransUInt16(modbus, index + 1);
                int startAdd = this.getByteTransform().TransUInt16(modbus, index + 3);
                writeLength = this.getByteTransform().TransUInt16(modbus, index + 5);
                OperateResult write = this.Write("file=" + fileNumber + ";" + startAdd, SoftBasic.BytesArraySelectMiddle(modbus, index + 7, writeLength * 2));
                if (!write.IsSuccess) {
                    return this.CreateExceptionBack(modbus, (byte)4);
                }
                memoryStream.Write(SoftBasic.BytesArraySelectMiddle(modbus, index, 7));
            }
            return this.CreateReadBack(modbus, memoryStream.ToArray());
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private byte[] WriteMaskRegisterBack(byte[] modbus) {
        try {
            if (!this.EnableWrite) {
                return this.CreateExceptionBack(modbus, (byte)4);
            }
            int address = this.getByteTransform().TransUInt16(modbus, 2);
            int and_Mask = this.getByteTransform().TransUInt16(modbus, 4);
            int or_Mask = this.getByteTransform().TransUInt16(modbus, 6);
            short ValueOld = (Short)this.ReadInt16((String)new StringBuilder().append((String)"s=").append((int)modbus[0]).append((String)"};").append((int)address).toString()).Content;
            short ValueNew = (short)(ValueOld & and_Mask | or_Mask);
            this.Write("s=" + modbus[0] + "};" + address, ValueNew);
            return modbus;
        }
        catch (Exception ex) {
            return this.CreateExceptionBack(modbus, (byte)4);
        }
    }

    private boolean CheckModbusMessageLegal(byte[] buffer) {
        boolean check = false;
        switch (buffer[1]) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                check = buffer.length == 6;
                break;
            }
            case 15: 
            case 16: {
                check = buffer.length > 6 && (buffer[6] & 0xFF) == buffer.length - 7;
                break;
            }
            case 22: {
                check = buffer.length == 8;
                break;
            }
            case 20: 
            case 21: {
                check = (buffer[2] & 0xFF) + 3 == buffer.length;
                break;
            }
            default: {
                check = true;
            }
        }
        return check;
    }

    protected byte[] ReadFromModbusCore(byte[] modbusCore) {
        byte[] buffer;
        switch (modbusCore[1]) {
            case 1: {
                buffer = this.ReadCoilBack(modbusCore, "");
                break;
            }
            case 2: {
                buffer = this.ReadCoilBack(modbusCore, "x=2;");
                break;
            }
            case 3: {
                buffer = this.ReadRegisterBack(modbusCore, "");
                break;
            }
            case 4: {
                buffer = this.ReadRegisterBack(modbusCore, "x=4;");
                break;
            }
            case 5: {
                buffer = this.WriteOneCoilBack(modbusCore);
                break;
            }
            case 6: {
                buffer = this.WriteOneRegisterBack(modbusCore);
                break;
            }
            case 15: {
                buffer = this.WriteCoilsBack(modbusCore);
                break;
            }
            case 16: {
                buffer = this.WriteRegisterBack(modbusCore);
                break;
            }
            case 22: {
                buffer = this.WriteMaskRegisterBack(modbusCore);
                break;
            }
            case 20: {
                buffer = this.ReadFileRecordBack(modbusCore);
                break;
            }
            case 21: {
                buffer = this.WriteFileRecordBack(modbusCore);
                break;
            }
            default: {
                buffer = this.CreateExceptionBack(modbusCore, (byte)1);
            }
        }
        return buffer;
    }

    @Override
    public String toString() {
        return "ModbusTcpServer[" + this.getPort() + "]";
    }
}

