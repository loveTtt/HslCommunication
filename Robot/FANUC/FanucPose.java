/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Utilities;

public class FanucPose {
    public float[] Xyzwpr = null;
    public String[] Config = null;
    public float[] Joint = null;
    public short UF = 0;
    public short UT = 0;
    public short ValidC = 0;
    public short ValidJ = 0;

    public void LoadByContent(IByteTransform byteTransform, byte[] content, int index) {
        int i;
        this.Xyzwpr = new float[9];
        for (i = 0; i < this.Xyzwpr.length; ++i) {
            this.Xyzwpr[i] = Utilities.getFloat(content, index + 4 * i);
        }
        this.Config = FanucPose.TransConfigStringArray(byteTransform.TransInt16(content, index + 36, 7));
        this.Joint = new float[9];
        for (i = 0; i < this.Joint.length; ++i) {
            this.Joint[i] = Utilities.getFloat(content, index + 52 + 4 * i);
        }
        this.ValidC = Utilities.getShort(content, index + 50);
        this.ValidJ = Utilities.getShort(content, index + 88);
        this.UF = Utilities.getShort(content, index + 90);
        this.UT = Utilities.getShort(content, index + 92);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FanucPose UF=" + this.UF + " UT=" + this.UT);
        if (this.ValidC != 0) {
            sb.append(System.lineSeparator()).append("Xyzwpr=").append(SoftBasic.ArrayFormat(this.Xyzwpr)).append(System.lineSeparator()).append("Config=").append(SoftBasic.ArrayFormat(this.Config));
        }
        if (this.ValidJ != 0) {
            sb.append(System.lineSeparator()).append("JOINT=").append(SoftBasic.ArrayFormat(this.Joint));
        }
        return sb.toString();
    }

    public static FanucPose ParseFrom(IByteTransform byteTransform, byte[] content, int index) {
        FanucPose fanucPose = new FanucPose();
        fanucPose.LoadByContent(byteTransform, content, index);
        return fanucPose;
    }

    public static String[] TransConfigStringArray(short[] value) {
        String[] array = new String[]{value[0] != 0 ? "F" : "N", value[1] != 0 ? "L" : "R", value[2] != 0 ? "U" : "D", value[3] != 0 ? "T" : "B", String.valueOf(value[4]), String.valueOf(value[5]), String.valueOf(value[6])};
        return array;
    }
}

