/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.XINJE;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusTcpNet;
import HslCommunication.Profinet.XINJE.XinJEHelper;
import HslCommunication.Profinet.XINJE.XinJESeries;

public class XinJETcpNet
extends ModbusTcpNet {
    public XinJESeries Series = XinJESeries.XC;

    public XinJETcpNet() {
        this.Series = XinJESeries.XC;
    }

    public XinJETcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.Series = XinJESeries.XC;
    }

    public XinJETcpNet(XinJESeries series, String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.Series = series;
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return XinJEHelper.PraseXinJEAddress(this.Series, address, modbusCode);
    }

    @Override
    public String toString() {
        return "XinJETcpNet<" + (Object)((Object)this.Series) + ">[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

