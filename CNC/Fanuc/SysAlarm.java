/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

public class SysAlarm {
    public int AlarmId = 0;
    public short Type = 0;
    public short Axis = 0;
    public String Message = "";

    public String toString() {
        return "AlarmId:[" + this.AlarmId + "] Type:[" + this.Type + "] Axis:[" + this.Axis + "] Message: " + this.Message;
    }

    public static String GetArrayString(SysAlarm[] alarms) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < alarms.length; ++i) {
            stringBuilder.append("\r\n  ");
            stringBuilder.append(alarms[i].toString());
        }
        stringBuilder.append("\r\n]");
        return stringBuilder.toString();
    }
}

