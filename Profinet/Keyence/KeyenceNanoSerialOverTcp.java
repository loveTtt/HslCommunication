/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Keyence;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.SpecifiedCharacterMessage;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Keyence.KeyenceNanoHelper;
import HslCommunication.Profinet.Keyence.KeyencePLCS;
import java.net.Socket;
import java.util.Date;

public class KeyenceNanoSerialOverTcp
extends NetworkDeviceBase {
    public byte Station = 0;
    public boolean UseStation = false;

    public KeyenceNanoSerialOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.getByteTransform().setIsStringReverse(true);
        this.LogMsgFormatBinary = false;
    }

    public KeyenceNanoSerialOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(13, 10);
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> result = this.ReadFromCoreServer(socket, KeyenceNanoHelper.GetConnectCmd(this.Station, this.UseStation), true, true);
        if (!result.IsSuccess) {
            return result;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        OperateResultExOne<byte[]> result = this.ReadFromCoreServer(socket, KeyenceNanoHelper.GetDisConnectCmd(this.Station, this.UseStation), true, true);
        if (!result.IsSuccess) {
            return result;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return KeyenceNanoHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return KeyenceNanoHelper.Write((IReadWriteDevice)this, address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return KeyenceNanoHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return KeyenceNanoHelper.Write((IReadWriteDevice)this, address, value);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return KeyenceNanoHelper.Write((IReadWriteDevice)this, address, value);
    }

    public OperateResult ClearError() {
        return KeyenceNanoHelper.ClearError(this);
    }

    public OperateResultExOne<KeyencePLCS> ReadPlcType() {
        return KeyenceNanoHelper.ReadPlcType(this);
    }

    public OperateResultExOne<Integer> ReadPlcMode() {
        return KeyenceNanoHelper.ReadPlcMode(this);
    }

    public OperateResult SetPlcDateTime(Date dateTime) {
        return KeyenceNanoHelper.SetPlcDateTime(this, dateTime);
    }

    public OperateResultExOne<String> ReadAddressAnnotation(String address) {
        return KeyenceNanoHelper.ReadAddressAnnotation(this, address);
    }

    public OperateResultExOne<byte[]> ReadExpansionMemory(byte unit, short address, short length) {
        return KeyenceNanoHelper.ReadExpansionMemory(this, unit, address, length);
    }

    public OperateResult WriteExpansionMemory(byte unit, short address, byte[] value) {
        return KeyenceNanoHelper.WriteExpansionMemory(this, unit, address, value);
    }

    @Override
    public String toString() {
        return "KeyenceNanoSerialOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

