/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Core.Types.BitConverter;

public class MemobusMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 12;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        if (HeadBytes == null) {
            return 0;
        }
        if (HeadBytes.length >= this.ProtocolHeadBytesLength()) {
            int length = BitConverter.ToUInt16(this.getHeadBytes(), 6) - 12;
            if (length < 0) {
                length = 0;
            }
            return length;
        }
        return 0;
    }
}

