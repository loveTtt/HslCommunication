/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

public class CutterInfo {
    public double LengthSharpOffset = 0.0;
    public double LengthWearOffset = 0.0;
    public double RadiusSharpOffset = 0.0;
    public double RadiusWearOffset = 0.0;

    public String toString() {
        return "LengthSharpOffset:" + this.LengthSharpOffset + " LengthWearOffset:" + this.LengthWearOffset + " RadiusSharpOffset:" + this.RadiusSharpOffset + " RadiusWearOffset:" + this.RadiusWearOffset;
    }
}

