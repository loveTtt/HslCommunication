/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;
import java.nio.charset.Charset;
import java.util.Date;

public class FanucAlarm {
    public short AlarmID = 0;
    public short AlarmNumber = 0;
    public short CauseAlarmID = 0;
    public short CauseAlarmNumber = 0;
    public short Severity = 0;
    public Date Time = new Date();
    public String AlarmMessage = "";
    public String CauseAlarmMessage = "";
    public String SeverityMessage = "";

    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        this.AlarmID = Utilities.getShort(content, index);
        this.AlarmNumber = Utilities.getShort(content, index + 2);
        this.CauseAlarmID = Utilities.getShort(content, index + 4);
        this.CauseAlarmNumber = Utilities.getShort(content, index + 6);
        this.Severity = Utilities.getShort(content, index + 8);
        if (Utilities.getShort(content, index + 10) > 0) {
            this.Time = new Date(Utilities.getShort(content, index + 10), Utilities.getShort(content, index + 12), Utilities.getShort(content, index + 14), Utilities.getShort(content, index + 16), Utilities.getShort(content, index + 18), Utilities.getShort(content, index + 20));
        }
        this.AlarmMessage = new String(content, index + 22, 80, encoding).trim();
        this.CauseAlarmMessage = new String(content, index + 102, 80, encoding).trim();
        this.SeverityMessage = new String(content, index + 182, 18, encoding).trim();
    }

    public String toString() {
        return "FanucAlarm ID[" + this.AlarmID + "," + this.AlarmNumber + "," + this.CauseAlarmID + "," + this.CauseAlarmNumber + "," + this.Severity + "]" + System.lineSeparator() + this.AlarmMessage + System.lineSeparator() + this.CauseAlarmMessage + System.lineSeparator() + this.SeverityMessage;
    }

    public static FanucAlarm PraseFrom(IByteTransform byteTransform, byte[] content, int index, Charset encoding) {
        FanucAlarm fanucAlarm = new FanucAlarm();
        fanucAlarm.LoadByContent(byteTransform, content, index, encoding);
        return fanucAlarm;
    }
}

