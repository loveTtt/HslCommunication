/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import java.nio.charset.StandardCharsets;

public class MelsecA1EAsciiMessage
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
        if (HeadBytes[2] == 53 && HeadBytes[3] == 66) {
            return 4;
        }
        if (HeadBytes[2] == 48 && HeadBytes[3] == 48) {
            int length = Integer.parseInt(new String(SendBytes, 20, 2, StandardCharsets.US_ASCII), 16);
            if (length == 0) {
                length = 256;
            }
            switch (HeadBytes[1]) {
                case 48: {
                    return length % 2 == 1 ? length + 1 : length;
                }
                case 49: {
                    return length * 4;
                }
                case 50: 
                case 51: {
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
        if (HeadBytes != null) {
            return (HeadBytes[0] & 255 - SendBytes[0] & 0xFF) == 8;
        }
        return false;
    }
}

