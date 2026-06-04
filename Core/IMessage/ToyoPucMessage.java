/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Core.Types.BitConverter;

public class ToyoPucMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 4;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        try {
            byte[] HeadBytes = this.getHeadBytes();
            if (HeadBytes == null) {
                return 0;
            }
            if (HeadBytes.length >= 4) {
                return BitConverter.ToUInt16(HeadBytes, 2);
            }
            return 0;
        }
        catch (Exception ex) {
            return 0;
        }
    }
}

