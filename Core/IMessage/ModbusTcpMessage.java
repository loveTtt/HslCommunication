/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class ModbusTcpMessage
extends NetMessageBase
implements INetMessage {
    public boolean IsCheckMessageId = true;
    public boolean StationCheckMatch = true;

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
        if (HeadBytes.length >= this.ProtocolHeadBytesLength()) {
            int length = (HeadBytes[4] & 0xFF) * 256 + (HeadBytes[5] & 0xFF);
            int remainLength = 0;
            if (length == 0) {
                HeadBytes = SoftBasic.BytesArrayRemoveBegin(HeadBytes, 1);
                this.setHeadBytes(HeadBytes);
                remainLength = (HeadBytes[4] & 0xFF) * 256 + (HeadBytes[5] & 0xFF) - 1;
            } else {
                remainLength = Math.min(length - 2, 300);
            }
            if (SendBytes != null && SendBytes.length == 12) {
                int expectLength;
                int readLength;
                if (SendBytes[7] == 3 || SendBytes[7] == 4) {
                    int expectLength2;
                    int readLength2 = (SendBytes[10] & 0xFF) * 256 + (SendBytes[11] & 0xFF);
                    if (readLength2 > 0 && readLength2 <= 127 && (expectLength2 = readLength2 * 2 + 1) != remainLength && length == 6) {
                        remainLength = expectLength2;
                    }
                } else if ((SendBytes[7] == 1 || SendBytes[7] == 2) && (readLength = (SendBytes[10] & 0xFF) * 256 + (SendBytes[11] & 0xFF)) > 0 && readLength <= 2040 && (expectLength = (readLength - 1) / 8 + 1 + 1) != remainLength && length == 6) {
                    remainLength = expectLength;
                }
            }
            return remainLength;
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
        return HeadBytes[2] == 0 && HeadBytes[3] == 0;
    }

    @Override
    public int CheckMessageMatch(byte[] send, byte[] receive) {
        if (send == null) {
            return 1;
        }
        if (receive == null) {
            return 1;
        }
        if (send.length < 8 || receive.length < 8) {
            return 1;
        }
        if (this.IsCheckMessageId && (send[0] != receive[0] || send[1] != receive[1])) {
            return -1;
        }
        if (this.StationCheckMatch && send[6] != receive[6]) {
            return -1;
        }
        return 1;
    }
}

