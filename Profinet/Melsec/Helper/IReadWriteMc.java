/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.McType;

public interface IReadWriteMc
extends IReadWriteDevice {
    public byte getNetworkNumber();

    public void setNetworkNumber(byte var1);

    public byte getNetworkStationNumber();

    public void setNetworkStationNumber(byte var1);

    public OperateResultExOne<McAddressData> McAnalysisAddress(String var1, short var2, boolean var3);

    public IByteTransform getByteTransform();

    public McType getMcType();

    public byte[] ExtractActualData(byte[] var1, boolean var2);

    public OperateResult RemoteRun();

    public OperateResult RemoteStop();

    public OperateResult RemoteReset();

    public OperateResultExOne<String> ReadPlcType();

    public OperateResult ErrorStateReset();
}

