/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Fuji;

import HslCommunication.Core.IMessage.FujiSPBMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Fuji.FujiSPBHelper;

public class FujiSPBOverTcp
extends NetworkDeviceBase {
    private byte station = 1;

    public FujiSPBOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    public FujiSPBOverTcp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FujiSPBMessage();
    }

    public byte getStation() {
        return this.station;
    }

    public void setStation(byte station) {
        this.station = station;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return FujiSPBHelper.Read(this, this.station, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return FujiSPBHelper.Write((IReadWriteDevice)this, this.station, address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return FujiSPBHelper.ReadBool(this, this.station, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return FujiSPBHelper.Write((IReadWriteDevice)this, this.station, address, value);
    }

    @Override
    public String toString() {
        return "FujiSPBOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

