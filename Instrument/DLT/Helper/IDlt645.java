/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.DLT.Helper;

import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Instrument.DLT.Helper.DLT645Type;
import java.util.Date;

public interface IDlt645
extends IReadWriteDevice {
    public String getStation();

    public void setStation(String var1);

    public boolean getEnableCodeFE();

    public void setEnableCodeFE(boolean var1);

    public DLT645Type getDLTType();

    public void setDLTType(DLT645Type var1);

    public String getPassword();

    public void setPassword(String var1);

    public String getOpCode();

    public void setOpCode(String var1);

    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] var1, boolean var2, boolean var3);

    public OperateResult ActiveDevice();

    public OperateResult BroadcastTime(Date var1);

    public OperateResult WriteAddress(String var1);

    public OperateResultExOne<String> ReadAddress();

    public OperateResultExOne<String[]> ReadStringArray(String var1);

    public OperateResult Trip(Date var1);

    public OperateResult Trip(String var1, Date var2);

    public OperateResult SwitchingOn(Date var1);

    public OperateResult SwitchingOn(String var1, Date var2);
}

