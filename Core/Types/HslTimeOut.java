/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import java.net.Socket;
import java.util.Date;

public class HslTimeOut {
    public Date StartTime = new Date();
    public boolean IsSuccessful = false;
    public int DelayTime = 5000;
    public Socket WorkSocket = null;
    public boolean IsTimeout = false;

    public String toString() {
        return "HslTimeOut[" + this.DelayTime + "]";
    }
}

