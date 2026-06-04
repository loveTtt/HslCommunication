/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Panasonic;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.MelsecMcNet;
import HslCommunication.Profinet.Panasonic.PanasonicHelper;
import HslCommunication.Utilities;

public class PanasonicMcNet
extends MelsecMcNet {
    public PanasonicMcNet() {
    }

    public PanasonicMcNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    @Override
    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length, boolean isBit) {
        return McAddressData.ParsePanasonicFrom(address, length, isBit);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        int errorCode = Utilities.getUShort(response, 9);
        if (errorCode != 0) {
            return new OperateResultExOne<byte[]>(errorCode, PanasonicHelper.GetMcErrorDescription(errorCode));
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 11));
    }

    @Override
    public String toString() {
        return "PanasonicMcNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

