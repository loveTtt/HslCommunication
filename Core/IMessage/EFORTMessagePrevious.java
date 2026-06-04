/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Utilities;

public class EFORTMessagePrevious
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 17;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        int length = Utilities.getShort(HeadBytes, 15) - 17;
        if (length < 0) {
            length = 0;
        }
        return length;
    }
}

