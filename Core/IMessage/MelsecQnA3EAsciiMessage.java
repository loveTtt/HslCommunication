/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import java.nio.charset.StandardCharsets;

public class MelsecQnA3EAsciiMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 18;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        if (HeadBytes == null) {
            return 0;
        }
        byte[] buffer = new byte[]{HeadBytes[14], HeadBytes[15], HeadBytes[16], HeadBytes[17]};
        return Integer.parseInt(new String(buffer, StandardCharsets.US_ASCII), 16);
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = this.getHeadBytes();
        if (HeadBytes == null) {
            return false;
        }
        return HeadBytes[0] == 68 && HeadBytes[1] == 48 && HeadBytes[2] == 48 && HeadBytes[3] == 48;
    }
}

