/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;

public class OpenProtocolMessage
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
        try {
            if (HeadBytes.length >= 4) {
                int length = Convert.ToInt32(Encoding.ASCII.GetString(HeadBytes, 0, 4)) - 4 + 1;
                return Math.max(length, 0);
            }
            return 0;
        }
        catch (Exception ex) {
            return 17;
        }
    }
}

