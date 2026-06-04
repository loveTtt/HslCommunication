/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.Types.MemoryStream;

public interface INetMessage {
    public int ProtocolHeadBytesLength();

    public int GetContentLengthByHeadBytes();

    public boolean CheckHeadBytesLegal(byte[] var1);

    public int PependedUselesByteLength(byte[] var1);

    public int GetHeadBytesIdentity();

    public void setHeadBytes(byte[] var1);

    public byte[] getHeadBytes();

    public byte[] getContentBytes();

    public void setContentBytes(byte[] var1);

    public byte[] getSendBytes();

    public void setSendBytes(byte[] var1);

    public boolean CheckReceiveDataComplete(byte[] var1, MemoryStream var2);

    public int CheckMessageMatch(byte[] var1, byte[] var2);
}

