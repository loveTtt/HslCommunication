/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Delta;

import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Profinet.Delta.DeltaSeries;

public interface IDelta
extends IReadWriteDevice {
    public DeltaSeries GetSeries();

    public void SetSeries(DeltaSeries var1);
}

