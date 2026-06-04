/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.NetworkBase.NetworkUdpDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.IReadWriteMc;
import HslCommunication.Profinet.Melsec.Helper.McBinaryHelper;
import HslCommunication.Profinet.Melsec.Helper.McHelper;
import HslCommunication.Profinet.Melsec.Helper.McType;

public class MelsecMcUdp
extends NetworkUdpDeviceBase
implements IReadWriteMc {
    private byte NetworkNumber = 0;
    private byte NetworkStationNumber = 0;

    public MelsecMcUdp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public MelsecMcUdp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new RegularByteTransform());
    }

    @Override
    public byte getNetworkNumber() {
        return this.NetworkNumber;
    }

    @Override
    public void setNetworkNumber(byte networkNumber) {
        this.NetworkNumber = networkNumber;
    }

    @Override
    public byte getNetworkStationNumber() {
        return this.NetworkStationNumber;
    }

    @Override
    public void setNetworkStationNumber(byte networkStationNumber) {
        this.NetworkStationNumber = networkStationNumber;
    }

    @Override
    public McType getMcType() {
        return McType.McBinary;
    }

    @Override
    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length, boolean isBit) {
        return McAddressData.ParseMelsecFrom(address, length, isBit);
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return McBinaryHelper.PackMcCommand(command, this.NetworkNumber, this.NetworkStationNumber);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        OperateResult check = McBinaryHelper.CheckResponseContentHelper(response);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 11));
    }

    @Override
    public byte[] ExtractActualData(byte[] response, boolean isBit) {
        return McBinaryHelper.ExtractActualDataHelper(response, isBit);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return McHelper.Read(this, address, length);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return McHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return McHelper.Write((IReadWriteMc)this, address, value);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return McHelper.Write((IReadWriteMc)this, address, values);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        return McHelper.ReadRandom(this, address);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address, short[] length) {
        return McHelper.ReadRandom(this, address, length);
    }

    public OperateResultExOne<short[]> ReadRandomInt16(String[] address) {
        return McHelper.ReadRandomInt16(this, address);
    }

    public OperateResultExOne<byte[]> ReadTags(String tag, short length) {
        return this.ReadTags(new String[]{tag}, new short[]{length});
    }

    public OperateResultExOne<byte[]> ReadTags(String[] tags, short[] length) {
        return McBinaryHelper.ReadTags(this, tags, length);
    }

    public OperateResultExOne<byte[]> ReadExtend(short extend, String address, short length) {
        return McHelper.ReadExtend(this, extend, address, length);
    }

    public OperateResultExOne<byte[]> ReadMemory(String address, short length) {
        return McHelper.ReadMemory(this, address, length);
    }

    @Override
    public OperateResult RemoteRun() {
        return McHelper.RemoteRun(this);
    }

    @Override
    public OperateResult RemoteStop() {
        return McHelper.RemoteStop(this);
    }

    @Override
    public OperateResult RemoteReset() {
        return McHelper.RemoteReset(this);
    }

    @Override
    public OperateResultExOne<String> ReadPlcType() {
        return McHelper.ReadPlcType(this);
    }

    @Override
    public OperateResult ErrorStateReset() {
        return McHelper.ErrorStateReset(this);
    }

    @Override
    public String toString() {
        return "MelsecMcUdp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

