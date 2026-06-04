/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class FetchWriteMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 16;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes[5] == 5 || HeadBytes[5] == 4) {
            return 0;
        }
        if (HeadBytes[5] == 6) {
            if (SendBytes == null) {
                return 0;
            }
            if (HeadBytes[8] != 0) {
                return 0;
            }
            if (SendBytes[8] == 1 || SendBytes[8] == 6 || SendBytes[8] == 7) {
                return ((SendBytes[12] & 0xFF) * 256 + (SendBytes[13] & 0xFF)) * 2;
            }
            return (SendBytes[12] & 0xFF) * 256 + (SendBytes[13] & 0xFF);
        }
        if (HeadBytes[5] == 3) {
            if (HeadBytes[8] == 1 || HeadBytes[8] == 6 || HeadBytes[8] == 7) {
                return ((HeadBytes[12] & 0xFF) * 256 + (HeadBytes[13] & 0xFF)) * 2;
            }
            return (HeadBytes[12] & 0xFF) * 256 + (HeadBytes[13] & 0xFF);
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
        return HeadBytes[0] == 83 && HeadBytes[1] == 53;
    }

    @Override
    public int GetHeadBytesIdentity() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        return HeadBytes[3];
    }
}

