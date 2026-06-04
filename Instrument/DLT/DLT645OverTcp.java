/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.DLT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.DLT645Message;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Instrument.DLT.Helper.DLT645Helper;
import HslCommunication.Instrument.DLT.Helper.DLT645Type;
import HslCommunication.Instrument.DLT.Helper.IDlt645;
import HslCommunication.Utilities;
import java.util.Date;

public class DLT645OverTcp
extends NetworkDeviceBase
implements IDlt645 {
    private String station = "1";
    private String password = "00000000";
    private String opCode = "00000000";
    private boolean enableCodeFE = false;
    private DLT645Type dlt645Type = DLT645Type.DLT2007;

    public DLT645OverTcp() {
        this.setByteTransform(new RegularByteTransform());
        this.password = "00000000";
        this.opCode = "00000000";
        this.WordLength = 1;
    }

    public DLT645OverTcp(String ipAddress, int port, String station) {
        this(ipAddress, port, station, "", "");
    }

    public DLT645OverTcp(String ipAddress, int port, String station, String password, String opCode) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.station = station;
        this.password = Utilities.IsStringNullOrEmpty(password) ? "00000000" : password;
        this.opCode = Utilities.IsStringNullOrEmpty(opCode) ? "00000000" : opCode;
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new DLT645Message();
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        if (this.getEnableCodeFE()) {
            return SoftBasic.SpliceArray(new byte[]{-2, -2, -2, -2}, command);
        }
        return super.PackCommandWithHeader(command);
    }

    @Override
    public OperateResult ActiveDevice() {
        return this.ReadFromCoreServer(new byte[]{-2, -2, -2, -2}, false, false);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return DLT645Helper.Read(this, address, length);
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, short length) {
        return DLT645Helper.ReadDouble(this, address, length);
    }

    public OperateResultExOne<String> ReadString(String address, short length, Encoding encoding) {
        return ByteTransformHelper.GetResultFromArray(this.ReadStringArray(address));
    }

    @Override
    public OperateResultExOne<String[]> ReadStringArray(String address) {
        return DLT645Helper.ReadStringArray(this, address);
    }

    @Override
    public OperateResult Trip(Date validTime) {
        return this.Trip(this.getStation(), validTime);
    }

    @Override
    public OperateResult Trip(String station, Date validTime) {
        return DLT645Helper.Function1C(this, this.password, this.opCode, station, (byte)26, validTime);
    }

    @Override
    public OperateResult SwitchingOn(Date validTime) {
        return this.SwitchingOn(this.getStation(), validTime);
    }

    @Override
    public OperateResult SwitchingOn(String station, Date validTime) {
        return DLT645Helper.Function1C(this, this.password, this.opCode, station, (byte)27, validTime);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, value);
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strings[i] = String.valueOf(values[i]);
        }
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, strings);
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strings[i] = String.valueOf(values[i]);
        }
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, strings);
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strings[i] = String.valueOf(values[i]);
        }
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, strings);
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strings[i] = String.valueOf(values[i]);
        }
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, strings);
    }

    public OperateResult Write(String address, String value, Encoding encoding) {
        return DLT645Helper.Write((IDlt645)this, this.password, this.opCode, address, new String[]{value});
    }

    @Override
    public OperateResultExOne<String> ReadAddress() {
        return DLT645Helper.ReadAddress(this);
    }

    @Override
    public OperateResult WriteAddress(String address) {
        return DLT645Helper.WriteAddress(this, address);
    }

    @Override
    public OperateResult BroadcastTime(Date dateTime) {
        return DLT645Helper.BroadcastTime(this, dateTime);
    }

    public OperateResult FreezeCommand(String dataArea) {
        return DLT645Helper.FreezeCommand(this, dataArea);
    }

    public OperateResult ChangeBaudRate(String baudRate) {
        return DLT645Helper.ChangeBaudRate(this, baudRate);
    }

    @Override
    public String getStation() {
        return this.station;
    }

    @Override
    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public boolean getEnableCodeFE() {
        return this.enableCodeFE;
    }

    @Override
    public void setEnableCodeFE(boolean enableCodeFE) {
        this.enableCodeFE = enableCodeFE;
    }

    @Override
    public DLT645Type getDLTType() {
        return this.dlt645Type;
    }

    @Override
    public void setDLTType(DLT645Type dLTType) {
        this.dlt645Type = dLTType;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getOpCode() {
        return this.opCode;
    }

    @Override
    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    @Override
    public String toString() {
        return "DLT645OverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

