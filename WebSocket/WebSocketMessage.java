/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.WebSocket;

import HslCommunication.Utilities;

public class WebSocketMessage {
    public boolean HasMask = false;
    public int OpCode = 0;
    public byte[] Payload = null;

    public String toString() {
        return "WebSocketMessage{HasMask=" + this.HasMask + ", OpCode=" + this.OpCode + ", Payload=" + Utilities.getString(this.Payload, "UTF-8") + '}';
    }
}

