/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.Profinet.AllenBradley.AllenBradleyHelper;
import HslCommunication.Profinet.AllenBradley.AllenBradleyNet;

public class AllenBradleyMicroCip
extends AllenBradleyNet {
    public AllenBradleyMicroCip() {
    }

    public AllenBradleyMicroCip(String ipAddress, int port) {
        super(ipAddress, port);
    }

    @Override
    protected byte[] PackCommandService(byte[] portSlot, byte[] ... cips) {
        return AllenBradleyHelper.PackCleanCommandService(portSlot, cips);
    }

    @Override
    public String toString() {
        return "AllenBradleyMicroCip[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

