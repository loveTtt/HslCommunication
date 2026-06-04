/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

public class SystemVersion {
    private int m_MainVersion = 2;
    private int m_SecondaryVersion = 0;
    private int m_EditVersion = 0;
    private int m_InnerVersion = 0;

    public SystemVersion(String VersionString) {
        String[] temp = VersionString.split("\\.");
        if (temp.length >= 3) {
            this.m_MainVersion = Integer.parseInt(temp[0]);
            this.m_SecondaryVersion = Integer.parseInt(temp[1]);
            this.m_EditVersion = Integer.parseInt(temp[2]);
            if (temp.length >= 4) {
                this.m_InnerVersion = Integer.parseInt(temp[3]);
            }
        }
    }

    public SystemVersion(int main, int sec, int edit) {
        this.m_MainVersion = main;
        this.m_SecondaryVersion = sec;
        this.m_EditVersion = edit;
    }

    public SystemVersion(int main, int sec, int edit, int inner) {
        this.m_MainVersion = main;
        this.m_SecondaryVersion = sec;
        this.m_EditVersion = edit;
        this.m_InnerVersion = inner;
    }

    public int MainVersion() {
        return this.m_MainVersion;
    }

    public int SecondaryVersion() {
        return this.m_SecondaryVersion;
    }

    public int EditVersion() {
        return this.m_EditVersion;
    }

    public int InnerVersion() {
        return this.m_InnerVersion;
    }

    public String toString(String format) {
        if (format == "C") {
            return this.MainVersion() + "." + this.SecondaryVersion() + "." + this.EditVersion() + "." + this.InnerVersion();
        }
        if (format == "N") {
            return this.MainVersion() + "." + this.SecondaryVersion() + "." + this.EditVersion();
        }
        if (format == "S") {
            return this.MainVersion() + "." + this.SecondaryVersion();
        }
        return this.toString();
    }

    public String toString() {
        if (this.InnerVersion() == 0) {
            return this.MainVersion() + "." + this.SecondaryVersion() + "." + this.EditVersion();
        }
        return this.MainVersion() + "." + this.SecondaryVersion() + "." + this.EditVersion() + "." + this.InnerVersion();
    }

    public boolean IsSameVersion(SystemVersion sv) {
        if (this.m_MainVersion != sv.m_MainVersion) {
            return false;
        }
        if (this.m_SecondaryVersion != sv.m_SecondaryVersion) {
            return false;
        }
        if (this.m_EditVersion != sv.m_EditVersion) {
            return false;
        }
        return this.m_InnerVersion == sv.m_InnerVersion;
    }

    public boolean IsSmallerThan(SystemVersion sv) {
        if (this.m_MainVersion < sv.m_MainVersion) {
            return true;
        }
        if (this.m_MainVersion > sv.m_MainVersion) {
            return false;
        }
        if (this.m_SecondaryVersion < sv.m_SecondaryVersion) {
            return true;
        }
        if (this.m_SecondaryVersion > sv.m_SecondaryVersion) {
            return false;
        }
        if (this.m_EditVersion < sv.m_EditVersion) {
            return true;
        }
        if (this.m_EditVersion > sv.m_EditVersion) {
            return false;
        }
        if (this.m_InnerVersion < sv.m_InnerVersion) {
            return true;
        }
        if (this.m_InnerVersion > sv.m_InnerVersion) {
            return false;
        }
        return false;
    }
}

