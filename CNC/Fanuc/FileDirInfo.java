/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileDirInfo {
    public boolean IsDirectory = false;
    public String Name = "";
    public Date LastModified = new Date();
    public int Size = 0;

    public FileDirInfo() {
    }

    public FileDirInfo(IByteTransform byteTransform, byte[] buffer, int index) {
        this.IsDirectory = byteTransform.TransInt16(buffer, index) == 0;
        this.Name = Utilities.GetStringOrEndChar(buffer, index + 28, 36, StandardCharsets.US_ASCII);
        if (!this.IsDirectory) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(byteTransform.TransInt16(buffer, index + 2), byteTransform.TransInt16(buffer, index + 4) - 1, byteTransform.TransInt16(buffer, index + 6), byteTransform.TransInt16(buffer, index + 8), byteTransform.TransInt16(buffer, index + 10), byteTransform.TransInt16(buffer, index + 12));
            this.LastModified = calendar.getTime();
            this.Size = byteTransform.TransInt32(buffer, index + 20);
        }
    }

    public String toFileString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.IsDirectory ? "[PATH]   " : "[FILE]   ");
        stringBuilder.append(String.format("%-40s", this.Name));
        if (!this.IsDirectory) {
            stringBuilder.append("     ");
            stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this.LastModified));
            stringBuilder.append("         ");
            stringBuilder.append(SoftBasic.GetSizeDescription(this.Size));
        }
        return stringBuilder.toString();
    }

    public String toString() {
        return this.Name;
    }
}

