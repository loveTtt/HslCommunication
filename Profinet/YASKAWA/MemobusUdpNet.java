/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.YASKAWA;

import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MemobusMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.YASKAWA.Helper.IMemobus;
import HslCommunication.Profinet.YASKAWA.Helper.MemobusHelper;

public class MemobusUdpNet
extends NetworkUdpDeviceBase
implements IMemobus {
    private byte cpuTo = (byte)2;
    private byte cpuFrom = 1;
    private SoftIncrementCount softIncrementCount = new SoftIncrementCount(255L);

    public MemobusUdpNet() {
        this.WordLength = 1;
        this.setByteTransform(new ReverseWordTransform(DataFormat.CDAB));
    }

    public MemobusUdpNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage() {
        return new MemobusMessage();
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        return MemobusHelper.PackCommandWithHeader(command, this.softIncrementCount.GetCurrentValue());
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return MemobusHelper.UnpackResponseContent(send, response);
    }

    @Override
    public byte getCpuTo() {
        return this.cpuTo;
    }

    @Override
    public void setCpuTo(byte value) {
        this.cpuTo = value;
    }

    @Override
    public byte getCpuFrom() {
        return this.cpuFrom;
    }

    @Override
    public void setCpuFrom(byte value) {
        this.cpuFrom = value;
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return MemobusHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return MemobusHelper.Write((IMemobus)this, address, value);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return MemobusHelper.Write((IMemobus)this, address, value);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return MemobusHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return MemobusHelper.Write((IMemobus)this, address, value);
    }

    @Override
    public OperateResult Write(String address, short value) {
        if (address.matches("^[0-9]+$")) {
            return MemobusHelper.Write((IMemobus)this, address, value);
        }
        return super.Write(address, value);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        return MemobusHelper.ReadRandom((IMemobus)this, address);
    }

    public OperateResultExOne<byte[]> ReadRandom(short[] address) {
        return MemobusHelper.ReadRandom((IMemobus)this, address);
    }

    public OperateResult WriteRandom(int[] address, byte[] value) {
        return MemobusHelper.WriteRandom(this, address, value);
    }

    @Override
    public String toString() {
        return "MemobusTcpNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

