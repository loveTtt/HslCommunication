/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Delta;

import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.FunctionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusTcpNet;
import HslCommunication.Profinet.Delta.DeltaSeries;
import HslCommunication.Profinet.Delta.Helper.DeltaHelper;
import HslCommunication.Profinet.Delta.IDelta;

public class DeltaTcpNet
extends ModbusTcpNet
implements IDelta {
    private DeltaSeries deltaSeries = DeltaSeries.Dvp;

    public DeltaTcpNet() {
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    public DeltaTcpNet(String ipAddress, int port, byte station) {
        super(ipAddress, port, station);
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    @Override
    public DeltaSeries GetSeries() {
        return this.deltaSeries;
    }

    @Override
    public void SetSeries(DeltaSeries series) {
        this.deltaSeries = series;
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        return DeltaHelper.TranslateToModbusAddress(this, address, modbusCode);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return DeltaHelper.ReadBool(this, new FunctionOperateExTwo<String, Short, OperateResultExOne<boolean[]>>(){

            @Override
            public OperateResultExOne<boolean[]> Action(String content1, Short content2) {
                return DeltaTcpNet.super.ReadBool(content1, content2);
            }
        }, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return DeltaHelper.Write((IDelta)this, new FunctionOperateExTwo<String, boolean[], OperateResult>(){

            @Override
            public OperateResult Action(String content1, boolean[] content2) {
                return DeltaTcpNet.super.Write(content1, content2);
            }
        }, address, values);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return super.Write(address, value);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return DeltaHelper.Read(this, new FunctionOperateExTwo<String, Short, OperateResultExOne<byte[]>>(){

            @Override
            public OperateResultExOne<byte[]> Action(String content1, Short content2) {
                return DeltaTcpNet.super.Read(content1, content2);
            }
        }, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return DeltaHelper.Write((IDelta)this, new FunctionOperateExTwo<String, byte[], OperateResult>(){

            @Override
            public OperateResult Action(String content1, byte[] content2) {
                return DeltaTcpNet.super.Write(content1, content2);
            }
        }, address, value);
    }

    @Override
    public String toString() {
        return "DeltaTcpNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

