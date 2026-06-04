/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Inovance;

import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusTcpNet;
import HslCommunication.Profinet.Inovance.InovanceHelper;
import HslCommunication.Profinet.Inovance.InovanceSeries;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InovanceTcpNet
extends ModbusTcpNet {
    private InovanceSeries Series = InovanceSeries.AM;

    public InovanceTcpNet() {
        this.Series = InovanceSeries.AM;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public InovanceTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.Series = InovanceSeries.AM;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public InovanceTcpNet(InovanceSeries series, String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.Series = series;
    }

    public InovanceSeries getSeries() {
        return this.Series;
    }

    public void setSeries(InovanceSeries series) {
        this.Series = series;
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return InovanceHelper.ReadByte(this, address);
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return InovanceHelper.PraseInovanceAddress(this.Series, address, modbusCode);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        Pattern pattern;
        Matcher matcher;
        if (this.getSeries() == InovanceSeries.AM && (matcher = (pattern = Pattern.compile("MB[0-9]*[13579]$", 2)).matcher(address)).find()) {
            return InovanceHelper.ReadAMString(this, address, length, encoding);
        }
        return super.ReadString(address, length, encoding);
    }

    @Override
    public String toString() {
        return "InovanceTcpNet<" + (Object)((Object)this.Series) + ">[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

