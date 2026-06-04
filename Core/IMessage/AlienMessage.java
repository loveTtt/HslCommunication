/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class AlienMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 5;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        return HeadBytes[4] & 0xFFFF;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        return true;
    }
}

