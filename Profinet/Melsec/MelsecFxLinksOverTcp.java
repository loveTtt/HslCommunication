/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.MelsecFxLinksHelper;

public class MelsecFxLinksOverTcp
extends NetworkDeviceBase {
    private byte station = 0;
    private byte watiingTime = 0;
    private boolean sumCheck = true;
    private int format = 1;

    public MelsecFxLinksOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    public MelsecFxLinksOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        return MelsecFxLinksHelper.PackCommandWithHeader(this, command);
    }

    public byte getStation() {
        return this.station;
    }

    public void setStation(byte value) {
        this.station = value;
    }

    public byte getWaittingTime() {
        return this.watiingTime;
    }

    public void setWaittingTime(byte value) {
        this.watiingTime = value > 15 ? (byte)15 : value;
    }

    public boolean getSumCheck() {
        return this.sumCheck;
    }

    public void setSumCheck(boolean value) {
        this.sumCheck = value;
    }

    public int getFormat() {
        return this.format;
    }

    public void setFormat(int value) {
        this.format = value;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return MelsecFxLinksHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return MelsecFxLinksHelper.Write(this, address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return MelsecFxLinksHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return MelsecFxLinksHelper.Write(this, address, value);
    }

    public OperateResult StartPLC(String parameter) {
        return MelsecFxLinksHelper.StartPLC(this, parameter);
    }

    public OperateResult StopPLC(String parameter) {
        return MelsecFxLinksHelper.StopPLC(this, parameter);
    }

    public OperateResultExOne<String> ReadPlcType(String parameter) {
        return MelsecFxLinksHelper.ReadPlcType(this, parameter);
    }

    @Override
    public String toString() {
        return "MelsecFxLinksOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

