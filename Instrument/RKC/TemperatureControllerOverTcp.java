/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.RKC;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.RkcTemperatureMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Instrument.RKC.Helper.TemperatureControllerHelper;

public class TemperatureControllerOverTcp
extends NetworkDeviceBase {
    private byte station = 1;

    public TemperatureControllerOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    public TemperatureControllerOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new RkcTemperatureMessage();
    }

    public byte getStation() {
        return this.station;
    }

    public void setStation(byte value) {
        this.station = value;
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, short length) {
        OperateResultExOne<Double> read = TemperatureControllerHelper.ReadDouble(this, this.station, address);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(new double[]{(Double)read.Content});
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        if (values == null || values.length == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return TemperatureControllerHelper.Write(this, this.station, address, values[0]);
    }

    @Override
    public String toString() {
        return "RkcTemperatureControllerOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

