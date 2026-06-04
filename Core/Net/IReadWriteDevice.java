/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Types.OperateResultExOne;
import java.util.ArrayList;

public interface IReadWriteDevice
extends IReadWriteNet {
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] var1);

    public OperateResultExOne<byte[]> ReadFromCoreServer(ArrayList<byte[]> var1);
}

