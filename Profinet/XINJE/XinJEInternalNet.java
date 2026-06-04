/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.XINJE;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.ModbusTcpMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusInfo;
import HslCommunication.Profinet.XINJE.XinJEHelper;
import java.util.ArrayList;

public class XinJEInternalNet
extends NetworkDeviceBase {
    private SoftIncrementCount softIncrementCount = new SoftIncrementCount(65535L);
    public byte Station = 1;

    public XinJEInternalNet() {
        this.WordLength = 1;
        this.setByteTransform(new ReverseWordTransform());
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public XinJEInternalNet(String ipAddress, int port, byte station) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.Station = station;
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new ModbusTcpMessage();
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

    public SoftIncrementCount MessageId() {
        return this.softIncrementCount;
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        return ModbusInfo.PackCommandToTcp(command, (short)this.softIncrementCount.GetCurrentValue());
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(response));
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = XinJEHelper.BuildReadCommand(this.Station, address, length, false);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return this.ReadFromCoreServer((ArrayList)command.Content);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<ArrayList<byte[]>> command = XinJEHelper.BuildReadCommand(this.Station, address, length, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((ArrayList)command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), 0, length));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = XinJEHelper.BuildWriteWordCommand(this.Station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        return this.ReadFromCoreServer((byte[])command.Content);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<byte[]> command = XinJEHelper.BuildWriteBoolCommand(this.Station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        return this.ReadFromCoreServer((byte[])command.Content);
    }

    @Override
    public String toString() {
        return "XinJEInternalNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

