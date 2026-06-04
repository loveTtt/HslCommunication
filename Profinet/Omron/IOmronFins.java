/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Omron.OmronCpuUnitData;
import HslCommunication.Profinet.Omron.OmronCpuUnitStatus;
import HslCommunication.Profinet.Omron.OmronPlcType;
import java.util.Date;

public interface IOmronFins
extends IReadWriteDevice {
    public OmronPlcType getPlcType();

    public void setPlcType(OmronPlcType var1);

    public OperateResult Run();

    public OperateResult Stop();

    public OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData();

    public OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus();

    public OperateResultExOne<Date> ReadCpuTime();
}

