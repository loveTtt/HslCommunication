/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

public interface IDataTransfer {
    public short getReadCount();

    public void ParseSource(byte[] var1);

    public byte[] ToSource();
}

