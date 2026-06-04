/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus;

import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;

public interface IModbus
extends IReadWriteDevice {
    public boolean getAddressStartWithZero();

    public void setAddressStartWithZero(boolean var1);

    public byte getStation();

    public void setStation(byte var1);

    public void setDataFormat(DataFormat var1);

    public DataFormat getDataFormat();

    public boolean isStringReverse();

    public void setStringReverse(boolean var1);

    public OperateResultExOne<String> TranslateToModbusAddress(String var1, byte var2);

    public boolean getEnableWriteMaskCode();

    public void setEnableWriteMaskCode(boolean var1);

    public int getBroadcastStation();

    public void setBroadcastStation(int var1);

    public OperateResultExOne<byte[]> ReadWrite(String var1, short var2, String var3, byte[] var4);

    public OperateResultExOne<byte[]> ReadFile(int var1, int var2, int var3);

    public OperateResult WriteFile(int var1, int var2, byte[] var3);
}

