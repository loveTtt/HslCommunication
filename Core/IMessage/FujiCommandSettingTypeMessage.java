/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class FujiCommandSettingTypeMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 5;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        if (this.getHeadBytes() == null) {
            return 0;
        }
        return this.getHeadBytes()[4] & 0xFF;
    }
}

