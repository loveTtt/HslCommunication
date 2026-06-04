/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Utilities;

public class FinsMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 8;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        byte[] buffer = new byte[]{HeadBytes[7], HeadBytes[6], HeadBytes[5], HeadBytes[4]};
        int length = Utilities.getInt(buffer, 0);
        if (length > 10000) {
            length = 10000;
        }
        if (length < 0) {
            length = 0;
        }
        return length;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return false;
        }
        return HeadBytes[0] == 70 && HeadBytes[1] == 73 && HeadBytes[2] == 78 && HeadBytes[3] == 83;
    }
}

