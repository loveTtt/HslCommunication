/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.MelsecA3CNetHelper;

public class MelsecA3CNetOverTcp
extends NetworkDeviceBase {
    public byte Station = 0;
    public boolean SumCheck = true;
    public int Format = 1;
    public boolean EnableWriteBitToWordRegister = false;

    public MelsecA3CNetOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    public MelsecA3CNetOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return MelsecA3CNetHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return MelsecA3CNetHelper.Write(this, address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return MelsecA3CNetHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return MelsecA3CNetHelper.Write(this, address, value);
    }

    public OperateResult RemoteRun() {
        return MelsecA3CNetHelper.RemoteRun(this);
    }

    public OperateResult RemoteStop() {
        return MelsecA3CNetHelper.RemoteStop(this);
    }

    public OperateResultExOne<String> ReadPlcType() {
        return MelsecA3CNetHelper.ReadPlcType(this);
    }

    @Override
    public String toString() {
        return "MelsecA3CNetOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

