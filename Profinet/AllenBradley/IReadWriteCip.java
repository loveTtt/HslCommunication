/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import java.time.Duration;
import java.util.Date;

public interface IReadWriteCip
extends IReadWriteNet {
    public IByteTransform getByteTransform();

    public OperateResult WriteTag(String var1, short var2, byte[] var3, int var4);

    public OperateResultExTwo<Short, byte[]> ReadTag(String var1, int var2);

    public OperateResultExOne<String> ReadPlcType();

    public OperateResultExOne<Date> ReadDate(String var1);

    public OperateResult WriteDate(String var1, Date var2);

    public OperateResult WriteTimeAndDate(String var1, Date var2);

    public OperateResultExOne<Duration> ReadTime(String var1);

    public OperateResult WriteTime(String var1, Duration var2);

    public OperateResult WriteTimeOfDate(String var1, Duration var2);
}

