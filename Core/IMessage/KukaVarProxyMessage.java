/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class KukaVarProxyMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 4;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        if (HeadBytes.length < 4) {
            return 0;
        }
        return (HeadBytes[2] & 0xFF) * 256 + HeadBytes[3] & 0xFF;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        return true;
    }

    @Override
    public int GetHeadBytesIdentity() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        return (HeadBytes[0] & 0xFF) * 256 + (HeadBytes[1] & 0xFF);
    }
}

