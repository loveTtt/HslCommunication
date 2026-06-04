/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Siemens.Helper.SiemensPPIHelper;
import HslCommunication.StringResources;
import java.net.Socket;
import java.util.Date;

public class SiemensPPIOverTcp
extends NetworkDeviceBase {
    private byte station = (byte)2;
    private Object communicationLock;

    public SiemensPPIOverTcp() {
        this.WordLength = (short)2;
        this.setByteTransform(new ReverseBytesTransform());
        this.communicationLock = new Object();
    }

    public SiemensPPIOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected OperateResultExOne<byte[]> ReceiveByMessage(Socket socket, int timeOut, INetMessage netMessage) {
        MemoryStream ms = new MemoryStream();
        Date startTime = new Date();
        do {
            OperateResultExOne<byte[]> receive = super.ReceiveByMessage(socket, timeOut, netMessage);
            if (!receive.IsSuccess) {
                return receive;
            }
            ms.Write((byte[])receive.Content);
            if (!SiemensPPIHelper.CheckReceiveDataComplete(ms)) continue;
            return OperateResultExOne.CreateSuccessResult(ms.ToArray());
        } while (this.getReceiveTimeOut() <= 0 || new Date().getTime() - startTime.getTime() <= (long)this.getReceiveTimeOut());
        return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout() + this.getReceiveTimeOut());
    }

    public byte getStation() {
        return this.station;
    }

    public void setStation(byte station) {
        this.station = station;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return SiemensPPIHelper.Read(this, address, length, this.station, this.communicationLock);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return SiemensPPIHelper.ReadBool(this, address, length, this.station, this.communicationLock);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return SiemensPPIHelper.Write((IReadWriteDevice)this, address, value, this.station, this.communicationLock);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return SiemensPPIHelper.Write((IReadWriteDevice)this, address, value, this.station, this.communicationLock);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((byte[])read.Content)[0]);
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    public OperateResult Start(String parameter) {
        return SiemensPPIHelper.Start(this, parameter, this.station, this.communicationLock);
    }

    public OperateResult Stop(String parameter) {
        return SiemensPPIHelper.Stop(this, parameter, this.station, this.communicationLock);
    }

    @Override
    public String toString() {
        return "SiemensPPIOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

