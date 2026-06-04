/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.FATEK;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.SpecifiedCharacterMessage;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.FATEK.FatekProgramHelper;

public class FatekProgramOverTcp
extends NetworkDeviceBase {
    private byte Station = 0;
    private byte station = 1;

    public FatekProgramOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public FatekProgramOverTcp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(3);
    }

    public byte getStation() {
        return this.Station;
    }

    public void setStation(byte station) {
        this.Station = station;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return FatekProgramHelper.Read(this, this.getStation(), address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return FatekProgramHelper.Write((IReadWriteDevice)this, this.getStation(), address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return FatekProgramHelper.ReadBool(this, this.getStation(), address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return FatekProgramHelper.Write((IReadWriteDevice)this, this.getStation(), address, value);
    }

    public OperateResult Run() {
        return FatekProgramHelper.Run(this, this.getStation());
    }

    public OperateResult Stop() {
        return FatekProgramHelper.Stop(this, this.getStation());
    }

    public OperateResultExOne<boolean[]> ReadStatus() {
        return FatekProgramHelper.ReadStatus(this, this.getStation());
    }

    @Override
    public String toString() {
        return "FatekProgramOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

