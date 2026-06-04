/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.ModbusTcpMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.FunctionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.IModbus;
import HslCommunication.ModBus.ModbusHelper;
import HslCommunication.ModBus.ModbusInfo;

public class ModbusTcpNet
extends NetworkDeviceBase
implements IModbus {
    public boolean IsCheckMessageId = true;
    public boolean StationCheckMatch = true;
    private byte station = 1;
    private SoftIncrementCount softIncrementCount = new SoftIncrementCount(65535L, 0L);
    private boolean isAddressStartWithZero = true;
    private boolean enableWriteMaskCode = true;
    private int broadcastStation = -1;
    private FunctionOperateExTwo<String, Byte, OperateResultExOne<String>> addressMapping;

    public ModbusTcpNet() {
        this.WordLength = 1;
        this.station = 1;
        this.setByteTransform(new ReverseWordTransform());
    }

    public ModbusTcpNet(String ipAddress, int port, byte station) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.WordLength = 1;
        this.station = station;
        this.setByteTransform(new ReverseWordTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        ModbusTcpMessage message = new ModbusTcpMessage();
        message.IsCheckMessageId = this.IsCheckMessageId;
        message.StationCheckMatch = this.StationCheckMatch;
        return message;
    }

    @Override
    public boolean getAddressStartWithZero() {
        return this.isAddressStartWithZero;
    }

    @Override
    public void setAddressStartWithZero(boolean addressStartWithZero) {
        this.isAddressStartWithZero = addressStartWithZero;
    }

    @Override
    public byte getStation() {
        return this.station;
    }

    @Override
    public void setStation(byte station) {
        this.station = station;
    }

    @Override
    public void setDataFormat(DataFormat dataFormat) {
        this.getByteTransform().setDataFormat(dataFormat);
    }

    @Override
    public DataFormat getDataFormat() {
        return this.getByteTransform().getDataFormat();
    }

    @Override
    public boolean isStringReverse() {
        return this.getByteTransform().getIsStringReverse();
    }

    @Override
    public void setStringReverse(boolean stringReverse) {
        this.getByteTransform().setIsStringReverse(stringReverse);
    }

    @Override
    public boolean getEnableWriteMaskCode() {
        return this.enableWriteMaskCode;
    }

    @Override
    public void setEnableWriteMaskCode(boolean value) {
        this.enableWriteMaskCode = value;
    }

    @Override
    public int getBroadcastStation() {
        return this.broadcastStation;
    }

    @Override
    public void setBroadcastStation(int broadcastStation) {
        this.broadcastStation = broadcastStation;
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return ModbusInfo.PackCommandToTcp(command, (int)this.softIncrementCount.GetCurrentValue());
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(response));
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        if (this.addressMapping != null) {
            return this.addressMapping.Action(address, modbusCode);
        }
        return OperateResultExOne.CreateSuccessResult(address);
    }

    public void RegisteredAddressMapping(FunctionOperateExTwo<String, Byte, OperateResultExOne<String>> mapping) {
        this.addressMapping = mapping;
    }

    @Override
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send) {
        if (this.getBroadcastStation() >= 0 && send[0] == this.getBroadcastStation()) {
            return super.ReadFromCoreServer(send, false, true);
        }
        return super.ReadFromCoreServer(send);
    }

    public OperateResultExOne<Boolean> ReadCoil(String address) {
        return this.ReadBool(address);
    }

    public OperateResultExOne<boolean[]> ReadCoil(String address, short length) {
        return this.ReadBool(address, length);
    }

    public OperateResultExOne<Boolean> ReadDiscrete(String address) {
        OperateResultExOne<boolean[]> read = this.ReadDiscrete(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((boolean[])read.Content)[0]);
    }

    public OperateResultExOne<boolean[]> ReadDiscrete(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, (byte)2);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return ModbusHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return ModbusHelper.Write((IModbus)this, address, value);
    }

    @Override
    public OperateResult Write(String address, short value) {
        return ModbusHelper.Write((IModbus)this, address, value);
    }

    public OperateResult WriteMask(String address, short andMask, short orMask) {
        return ModbusHelper.WriteMask(this, address, andMask, orMask);
    }

    @Override
    public OperateResultExOne<byte[]> ReadFile(int fileNumber, int address, int length) {
        return ModbusHelper.ReadFile(this, this.getStation(), fileNumber, address, length);
    }

    @Override
    public OperateResult WriteFile(int fileNumber, int address, byte[] data) {
        return ModbusHelper.WriteFile(this, this.getStation(), fileNumber, address, data);
    }

    public OperateResult WriteOneRegister(String address, short value) {
        return this.Write(address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, (byte)1);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return ModbusHelper.Write((IModbus)this, address, values);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return ModbusHelper.Write((IModbus)this, address, value);
    }

    @Override
    public OperateResultExOne<byte[]> ReadWrite(String readAddress, short length, String writeAddress, byte[] value) {
        return ModbusHelper.ReadWrite(this, readAddress, length, writeAddress, value);
    }

    @Override
    public String toString() {
        return "ModbusTcpNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

