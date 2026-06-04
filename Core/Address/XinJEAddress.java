/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;

public class XinJEAddress
extends DeviceAddressDataBase {
    public byte DataCode = 0;
    public byte Station = 0;
    public int CriticalAddress = 0;

    public XinJEAddress() {
    }

    public XinJEAddress(int dataCode, int address, int criticalAddress, int station) {
        this.DataCode = (byte) dataCode;
        this.setAddressStart(address);
        this.CriticalAddress = criticalAddress;
        this.Station = (byte) station;
    }

    @Override
    public String toString() {
        return String.valueOf(this.getAddressStart());
    }

    public static OperateResultExOne<XinJEAddress> ParseFrom(String address, short length, byte defaultStation) {
        OperateResultExOne<XinJEAddress> analysis = XinJEAddress.ParseFrom(address, defaultStation);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        ((XinJEAddress)analysis.Content).setLength(length);
        return OperateResultExOne.CreateSuccessResult(analysis.Content);
    }

    public static OperateResultExOne<XinJEAddress> ParseFrom(String address, byte defaultStation) {
        try {
            byte stat = defaultStation;
            OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
            if (extraPara.IsSuccess) {
                stat = ((Integer)extraPara.Content1).byteValue();
                address = (String)extraPara.Content2;
            }
            if (address.startsWith("HSCD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-117, Integer.parseInt(address.substring(4)), Integer.MAX_VALUE, stat));
            }
            if (address.startsWith("ETD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-123, Integer.parseInt(address.substring(3)), 0, stat));
            }
            if (address.startsWith("HSD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-116, Integer.parseInt(address.substring(3)), 1024, stat));
            }
            if (address.startsWith("HTD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-119, Integer.parseInt(address.substring(3)), 1024, stat));
            }
            if (address.startsWith("HCD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-118, Integer.parseInt(address.substring(3)), 1024, stat));
            }
            if (address.startsWith("SFD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-114, Integer.parseInt(address.substring(3)), 4096, stat));
            }
            if (address.startsWith("HSC")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(12, Integer.parseInt(address.substring(3)), Integer.MAX_VALUE, stat));
            }
            if (address.startsWith("SD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-125, Integer.parseInt(address.substring(2)), 4096, stat));
            }
            if (address.startsWith("TD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-127, Integer.parseInt(address.substring(2)), 4096, stat));
            }
            if (address.startsWith("CD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-126, Integer.parseInt(address.substring(2)), 4096, stat));
            }
            if (address.startsWith("HD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-120, Integer.parseInt(address.substring(2)), 6144, stat));
            }
            if (address.startsWith("FD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-115, Integer.parseInt(address.substring(2)), 8192, stat));
            }
            if (address.startsWith("ID")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-122, Integer.parseInt(address.substring(2)), 0, stat));
            }
            if (address.startsWith("QD")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-121, Integer.parseInt(address.substring(2)), 0, stat));
            }
            if (address.startsWith("SM")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(13, Integer.parseInt(address.substring(2)), 4096, stat));
            }
            if (address.startsWith("ET")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(7, Integer.parseInt(address.substring(2)), 0, stat));
            }
            if (address.startsWith("HM")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(8, Integer.parseInt(address.substring(2)), 6144, stat));
            }
            if (address.startsWith("HS")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(9, Integer.parseInt(address.substring(2)), Integer.MAX_VALUE, stat));
            }
            if (address.startsWith("HT")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(10, Integer.parseInt(address.substring(2)), 1024, stat));
            }
            if (address.startsWith("HC")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(11, Integer.parseInt(address.substring(2)), 1024, stat));
            }
            if (address.startsWith("D")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(-128, Integer.parseInt(address.substring(1)), 20480, stat));
            }
            if (address.startsWith("M")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(3, Integer.parseInt(address.substring(1)), 20480, stat));
            }
            if (address.startsWith("T")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(5, Integer.parseInt(address.substring(1)), 4096, stat));
            }
            if (address.startsWith("C")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(6, Integer.parseInt(address.substring(1)), 4096, stat));
            }
            if (address.startsWith("Y")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(2, Convert.ToInt32(address.substring(1), 8), Integer.MAX_VALUE, stat));
            }
            if (address.startsWith("X")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(1, Convert.ToInt32(address.substring(1), 8), Integer.MAX_VALUE, stat));
            }
            if (address.startsWith("S")) {
                return OperateResultExOne.CreateSuccessResult(new XinJEAddress(4, Integer.parseInt(address.substring(1)), 8000, stat));
            }
            return new OperateResultExOne<XinJEAddress>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<XinJEAddress>("ParseFrom failed: " + ex.getMessage());
        }
    }
}

