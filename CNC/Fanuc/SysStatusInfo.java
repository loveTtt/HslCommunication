/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.CNC.Fanuc.CNCRunStatus;
import HslCommunication.CNC.Fanuc.CNCWorkMode;

public class SysStatusInfo {
    public short Dummy = 0;
    public short TMMode = 0;
    public CNCWorkMode WorkMode = null;
    public CNCRunStatus RunStatus = null;
    public short Motion = 0;
    public short MSTB = 0;
    public short Emergency = 0;
    public short Alarm = 0;
    public short Edit = 0;

    public String toString() {
        return "Dummy: " + this.Dummy + ", TMMode:" + this.TMMode + ", WorkMode:" + (Object)((Object)this.WorkMode) + ", RunStatus:" + (Object)((Object)this.RunStatus) + ", Motion:" + this.Motion + ", MSTB:" + this.MSTB + ", Emergency:" + this.Emergency + ", Alarm:" + this.Alarm + ", Edit:" + this.Edit;
    }
}

