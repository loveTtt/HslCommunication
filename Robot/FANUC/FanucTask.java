/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;
import java.nio.charset.Charset;

public class FanucTask {
    public String ProgramName = "";
    public short LineNumber = 0;
    public short State = 0;
    public String ParentProgramName = "";

    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        this.ProgramName = new String(content, index, 16, encoding).trim();
        this.LineNumber = Utilities.getShort(content, index + 16);
        this.State = Utilities.getShort(content, index + 18);
        this.ParentProgramName = new String(content, index + 20, 16, encoding).trim();
    }

    public String toString() {
        return "ProgramName[" + this.ProgramName + "] LineNumber[" + this.LineNumber + "] State[" + this.State + "] ParentProgramName[" + this.ParentProgramName + "]";
    }

    public static FanucTask ParseFrom(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        FanucTask fanucTask = new FanucTask();
        fanucTask.LoadByContent(byteTransform, content, index, encoding);
        return fanucTask;
    }
}

