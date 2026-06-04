/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Keyence;

import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.McHelper;
import HslCommunication.Profinet.Melsec.MelsecMcNet;

public class KeyenceMcNet
extends MelsecMcNet {
    public KeyenceMcNet() {
    }

    public KeyenceMcNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    @Override
    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length, boolean isBit) {
        return McAddressData.ParseKeyenceFrom(address, length, isBit);
    }

    static boolean CheckKeyenceBoolAddress(String address) {
        return address.startsWith("MR") || address.startsWith("mr") || address.startsWith("CR") || address.startsWith("cr") || address.startsWith("LR") || address.startsWith("lr") || address.startsWith("R") || address.startsWith("r");
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (KeyenceMcNet.CheckKeyenceBoolAddress(address)) {
            return McHelper.ReadBool(this, address, length, false);
        }
        return super.ReadBool(address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        if (KeyenceMcNet.CheckKeyenceBoolAddress(address)) {
            return McHelper.Write(this, address, values, false);
        }
        return super.Write(address, values);
    }

    @Override
    public String toString() {
        return String.format("KeyenceMcNet[%s:%d]", this.getIpAddress(), this.getPort());
    }
}

