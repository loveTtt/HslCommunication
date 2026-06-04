/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Omron.IOmronFins;
import HslCommunication.Profinet.Omron.OmronCpuUnitData;
import HslCommunication.Profinet.Omron.OmronCpuUnitStatus;
import HslCommunication.Profinet.Omron.OmronFinsNetHelper;
import HslCommunication.Profinet.Omron.OmronPlcType;
import java.util.Date;

public class OmronFinsUdp
extends NetworkUdpDeviceBase
implements IOmronFins {
    public byte ICF = (byte)-128;
    public byte RSV = 0;
    public byte GCT = (byte)2;
    public byte DNA = 0;
    public byte DA1 = (byte)19;
    public byte DA2 = 0;
    public byte SNA = 0;
    public byte SA1 = (byte)11;
    public byte SA2 = 0;
    public byte SID = 0;
    public int ReadSplits = 500;
    private OmronPlcType plcType = OmronPlcType.CSCJ;

    public OmronFinsUdp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        this.setByteTransform(transform);
    }

    public OmronFinsUdp() {
        this.WordLength = 1;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        this.setByteTransform(transform);
    }

    @Override
    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        this.DA1 = (byte)Integer.parseInt(ipAddress.substring(ipAddress.lastIndexOf(".") + 1));
    }

    @Override
    public OmronPlcType getPlcType() {
        return this.plcType;
    }

    @Override
    public void setPlcType(OmronPlcType plcType) {
        this.plcType = plcType;
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return this.PackCommand(command);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OmronFinsNetHelper.UdpResponseValidAnalysis(response);
    }

    private byte[] PackCommand(byte[] cmd) {
        byte[] buffer = new byte[10 + cmd.length];
        buffer[0] = this.ICF;
        buffer[1] = this.RSV;
        buffer[2] = this.GCT;
        buffer[3] = this.DNA;
        buffer[4] = this.DA1;
        buffer[5] = this.DA2;
        buffer[6] = this.SNA;
        buffer[7] = this.SA1;
        buffer[8] = this.SA2;
        buffer[9] = this.SID;
        System.arraycopy(cmd, 0, buffer, 10, cmd.length);
        return buffer;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return OmronFinsNetHelper.Read(this, address, length, this.ReadSplits);
    }

    public OperateResultExOne<byte[]> Read(String[] address) {
        return OmronFinsNetHelper.Read(this, address);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return OmronFinsNetHelper.Write((IOmronFins)this, address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return OmronFinsNetHelper.ReadBool(this, address, length, this.ReadSplits);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return OmronFinsNetHelper.Write((IOmronFins)this, address, values);
    }

    @Override
    public OperateResult Run() {
        return OmronFinsNetHelper.Run(this);
    }

    @Override
    public OperateResult Stop() {
        return OmronFinsNetHelper.Stop(this);
    }

    @Override
    public OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData() {
        return OmronFinsNetHelper.ReadCpuUnitData(this);
    }

    @Override
    public OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus() {
        return OmronFinsNetHelper.ReadCpuUnitStatus(this);
    }

    @Override
    public OperateResultExOne<Date> ReadCpuTime() {
        return OmronFinsNetHelper.ReadCpuTime(this);
    }

    @Override
    public String toString() {
        return "OmronFinsUdp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

