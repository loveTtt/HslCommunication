/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.MegMeet;

import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusRtuOverTcp;
import HslCommunication.Profinet.MegMeet.MegMeetHelper;

public class MegMeetSerialOverTcp
extends ModbusRtuOverTcp {
    public MegMeetSerialOverTcp() {
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public MegMeetSerialOverTcp(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return MegMeetHelper.PraseMegMeetAddress(address, modbusCode);
    }

    @Override
    public String toString() {
        return "MegMeetSerialOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

