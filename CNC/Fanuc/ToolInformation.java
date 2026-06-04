/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.Core.Transfer.IByteTransform;

public class ToolInformation {
    public int Life = 0;
    public int Use = 0;

    public ToolInformation() {
    }

    public ToolInformation(byte[] content, IByteTransform byteTransform) {
        this.Life = byteTransform.TransInt32(content, 26);
        this.Use = byteTransform.TransInt32(content, 34);
    }

    public String toString() {
        return "\u5f53\u524d\u5200\u5177\u5bff\u547d: " + String.valueOf(this.Life) + "\r\n\u4f7f\u7528\u6b21\u6570: " + String.valueOf(this.Use);
    }
}

