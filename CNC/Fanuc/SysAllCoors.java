/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.BasicFramework.SoftBasic;

public class SysAllCoors {
    public double[] Absolute = null;
    public double[] Machine = null;
    public double[] Relative = null;

    public String toString() {
        return "{\r\n  Absolute: " + SoftBasic.ArrayFormat(this.Absolute) + "\r\n  Machine: " + SoftBasic.ArrayFormat(this.Machine) + "\r\n  Relative: " + SoftBasic.ArrayFormat(this.Relative) + "\r\n}";
    }
}

