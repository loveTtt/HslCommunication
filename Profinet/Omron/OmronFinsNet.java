/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Core.IMessage.FinsMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Omron.IOmronFins;
import HslCommunication.Profinet.Omron.OmronCpuUnitData;
import HslCommunication.Profinet.Omron.OmronCpuUnitStatus;
import HslCommunication.Profinet.Omron.OmronFinsNetHelper;
import HslCommunication.Profinet.Omron.OmronPlcType;
import HslCommunication.Utilities;
import java.net.Socket;
import java.util.Date;

public class OmronFinsNet
extends NetworkDeviceBase
implements IOmronFins {
    public byte ICF = (byte)-128;
    public byte RSV = 0;
    public byte GCT = (byte)2;
    public byte DNA = 0;
    public byte DA1 = (byte)19;
    public byte DA2 = 0;
    public byte SNA = 0;
    public byte SA1 = 1;
    public byte SA2 = 0;
    public byte SID = 0;
    public int ReadSplits = 500;
    private final byte[] handSingle = new byte[]{70, 73, 78, 83, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private OmronPlcType plcType = OmronPlcType.CSCJ;

    public OmronFinsNet() {
        this.WordLength = 1;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        this.setByteTransform(transform);
    }

    public OmronFinsNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FinsMessage();
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

    public DataFormat getDataFormat() {
        return this.getByteTransform().getDataFormat();
    }

    public boolean isStringReverse() {
        return this.getByteTransform().getIsStringReverse();
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.getByteTransform().setDataFormat(dataFormat);
    }

    public void setStringReverse(boolean stringReverse) {
        this.getByteTransform().setIsStringReverse(stringReverse);
    }

    private byte[] PackCommand(byte[] cmd) {
        byte[] buffer = new byte[26 + cmd.length];
        System.arraycopy(this.handSingle, 0, buffer, 0, 4);
        byte[] tmp = Utilities.getBytes(buffer.length - 8);
        Utilities.bytesReverse(tmp);
        System.arraycopy(tmp, 0, buffer, 4, tmp.length);
        buffer[11] = 2;
        buffer[16] = this.ICF;
        buffer[17] = this.RSV;
        buffer[18] = this.GCT;
        buffer[19] = this.DNA;
        buffer[20] = this.DA1;
        buffer[21] = this.DA2;
        buffer[22] = this.SNA;
        buffer[23] = this.SA1;
        buffer[24] = this.SA2;
        buffer[25] = this.SID;
        System.arraycopy(cmd, 0, buffer, 26, cmd.length);
        return buffer;
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return this.PackCommand(command);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OmronFinsNetHelper.ResponseValidAnalysis(response);
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, this.handSingle, true, false);
        if (!read.IsSuccess) {
            return read;
        }
        byte[] buffer = new byte[]{((byte[])read.Content)[15], ((byte[])read.Content)[14], ((byte[])read.Content)[13], ((byte[])read.Content)[12]};
        int status = Utilities.getInt(buffer, 0);
        if (status != 0) {
            return new OperateResult(status, OmronFinsNetHelper.GetStatusDescription(status));
        }
        if (((byte[])read.Content).length >= 20) {
            this.SA1 = ((byte[])read.Content)[19];
        }
        if (((byte[])read.Content).length >= 24) {
            this.DA1 = ((byte[])read.Content)[23];
        }
        return OperateResult.CreateSuccessResult();
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
        return "OmronFinsNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

