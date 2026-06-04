/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FujiCommandSettingTypeAddress
extends DeviceAddressDataBase {
    public byte DataCode = 0;
    public String AddressHeader = "";

    @Override
    public void Parse(String address, int length) {
        super.Parse(address, length);
    }

    @Override
    public String toString() {
        return this.AddressHeader + this.getAddressStart();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static OperateResultExOne<FujiCommandSettingTypeAddress> ParseFrom(String address, int length) {
        try {
            FujiCommandSettingTypeAddress fujiAddress = new FujiCommandSettingTypeAddress();
            String addType = "";
            String addOffset = "";
            if (address.indexOf(46) < 0) {
                Matcher matcher = Pattern.compile("^[A-Z]+").matcher(address);
                if (!matcher.find()) {
                    return new OperateResultExOne<FujiCommandSettingTypeAddress>(StringResources.Language.NotSupportedDataType());
                }
                addType = matcher.group();
                addOffset = address.substring(addType.length());
            } else {
                String[] splits = Utilities.SplitDot(address);
                if (splits[0].charAt(0) != 'W') {
                    return new OperateResultExOne<FujiCommandSettingTypeAddress>(StringResources.Language.NotSupportedDataType());
                }
                addType = splits[0];
                addOffset = splits[1];
            }
            fujiAddress.AddressHeader = addType;
            fujiAddress.setAddressStart(Convert.ToInt32(addOffset));
            fujiAddress.setLength(length);
            if (addType.equals("TS")) {
                fujiAddress.DataCode = (byte)10;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("TR")) {
                fujiAddress.DataCode = (byte)11;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("CS")) {
                fujiAddress.DataCode = (byte)12;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("CR")) {
                fujiAddress.DataCode = (byte)13;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("BD")) {
                fujiAddress.DataCode = (byte)14;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("WL")) {
                fujiAddress.DataCode = (byte)20;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("B")) {
                fujiAddress.DataCode = 0;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("M")) {
                fujiAddress.DataCode = 1;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("K")) {
                fujiAddress.DataCode = (byte)2;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("F")) {
                fujiAddress.DataCode = (byte)3;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("A")) {
                fujiAddress.DataCode = (byte)4;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("D")) {
                fujiAddress.DataCode = (byte)5;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else if (addType.equals("S")) {
                fujiAddress.DataCode = (byte)8;
                return OperateResultExOne.CreateSuccessResult(fujiAddress);
            } else {
                if (!addType.startsWith("W")) throw new Exception(StringResources.Language.NotSupportedDataType());
                int add = Convert.ToInt32(addType.substring(1));
                if (add == 9) {
                    fujiAddress.DataCode = (byte)9;
                    return OperateResultExOne.CreateSuccessResult(fujiAddress);
                } else if (add >= 21 && add <= 26) {
                    fujiAddress.DataCode = (byte)add;
                    return OperateResultExOne.CreateSuccessResult(fujiAddress);
                } else if (add >= 30 && add <= 109) {
                    fujiAddress.DataCode = (byte)add;
                    return OperateResultExOne.CreateSuccessResult(fujiAddress);
                } else if (add >= 120 && add <= 123) {
                    fujiAddress.DataCode = (byte)add;
                    return OperateResultExOne.CreateSuccessResult(fujiAddress);
                } else {
                    if (add != 125) throw new Exception(StringResources.Language.NotSupportedDataType());
                    fujiAddress.DataCode = (byte)add;
                }
            }
            return OperateResultExOne.CreateSuccessResult(fujiAddress);
        }
        catch (Exception ex) {
            return new OperateResultExOne<FujiCommandSettingTypeAddress>(FujiCommandSettingTypeAddress.GetUnsupportedAddressInfo(address, ex));
        }
    }
}

