/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.YASKAWA.Helper;

import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Transfer.IByteTransform;

public interface IMemobus
extends IReadWriteDevice {
    public IByteTransform getByteTransform();

    public byte getCpuTo();

    public void setCpuTo(byte var1);

    public byte getCpuFrom();

    public void setCpuFrom(byte var1);
}

