/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class MelsecA1EBinaryMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 2;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes[1] == 91) {
            return 2;
        }
        if (HeadBytes[1] == 0) {
            switch (HeadBytes[0]) {
                case -128: {
                    return SendBytes[10] != 0 ? ((SendBytes[10] & 0xFF) + 1) / 2 : 128;
                }
                case -127: {
                    return (SendBytes[10] & 0xFF) * 2;
                }
                case -126: 
                case -125: {
                    return 0;
                }
            }
            return 0;
        }
        return 0;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        return HeadBytes != null && (HeadBytes[0] & 0xFF) - (SendBytes[0] & 0xFF) == 128;
    }
}

