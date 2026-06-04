/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class SAMMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 7;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        if (HeadBytes.length >= 7) {
            return (HeadBytes[5] & 0xFF) * 256 + (HeadBytes[6] & 0xFF);
        }
        return 0;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return false;
        }
        return HeadBytes[0] == -86 && HeadBytes[1] == -86 && HeadBytes[2] == -86 && HeadBytes[3] == -106 && HeadBytes[4] == 105;
    }
}

