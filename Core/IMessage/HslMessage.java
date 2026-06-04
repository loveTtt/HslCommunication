/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Utilities;

public class HslMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 32;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        if (HeadBytes == null) {
            return 0;
        }
        if (HeadBytes.length != 32) {
            return 0;
        }
        return Utilities.getInt(HeadBytes, 28);
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        return SoftBasic.IsTwoBytesEquel(HeadBytes, 12, token, 0, 16);
    }
}

