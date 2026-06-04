/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.Core.Transfer.IByteTransform;
import java.nio.charset.Charset;

public class FanucOperatorMessage {
    public short Number;
    public short Type;
    public String Data;

    public static FanucOperatorMessage CreateMessage(IByteTransform byteTransform, byte[] buffer, Charset encoding) {
        FanucOperatorMessage fanucOperator = new FanucOperatorMessage();
        fanucOperator.Number = byteTransform.TransInt16(buffer, 2);
        fanucOperator.Type = byteTransform.TransInt16(buffer, 6);
        short len = byteTransform.TransInt16(buffer, 10);
        fanucOperator.Data = len + 12 <= buffer.length ? new String(buffer, 12, (int)len, encoding) : new String(buffer, 12, buffer.length - 12, encoding).trim();
        return fanucOperator;
    }
}

