/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.MegMeet;

import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusTcpNet;
import HslCommunication.Profinet.MegMeet.MegMeetHelper;

public class MegMeetTcpNet
extends ModbusTcpNet {
    public MegMeetTcpNet() {
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public MegMeetTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return MegMeetHelper.PraseMegMeetAddress(address, modbusCode);
    }

    @Override
    public String toString() {
        return "MegMeetTcpNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

