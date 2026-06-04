/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class MelsecFxLinksAddress
extends DeviceAddressDataBase {
    public String TypeCode = "";

    @Override
    public void Parse(String address, int length) {
        super.Parse(address, length);
    }

    @Override
    public String toString() {
        switch (this.TypeCode) {
            case "X": 
            case "Y": {
                String add = Convert.ToString(this.getAddressStart(), 8);
                return this.TypeCode + Utilities.PadLeft(add, this.getAddressStart() >= 10000 ? 6 : 4, '0');
            }
        }
        String add = Convert.ToString(this.getAddressStart(), 10);
        return this.TypeCode + Utilities.PadLeft(add, (this.getAddressStart() >= 10000 ? 7 : 5) - this.TypeCode.length());
    }

    public static OperateResultExOne<MelsecFxLinksAddress> ParseFrom(String address) {
        return MelsecFxLinksAddress.ParseFrom(address, 0);
    }

    public static OperateResultExOne<MelsecFxLinksAddress> ParseFrom(String address, int length) {
        MelsecFxLinksAddress melsecFxLinks = new MelsecFxLinksAddress();
        melsecFxLinks.setLength(length);
        try {
            switch (address.charAt(0)) {
                case 'X': 
                case 'x': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 8));
                    melsecFxLinks.TypeCode = "X";
                    break;
                }
                case 'Y': 
                case 'y': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 8));
                    melsecFxLinks.TypeCode = "Y";
                    break;
                }
                case 'M': 
                case 'm': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "M";
                    break;
                }
                case 'S': 
                case 's': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "S";
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "TS";
                        break;
                    }
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "TN";
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "CS";
                        break;
                    }
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        melsecFxLinks.TypeCode = "CN";
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'D': 
                case 'd': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "D";
                    break;
                }
                case 'R': 
                case 'r': {
                    melsecFxLinks.setAddressStart(Convert.ToInt32(address.substring(1), 10));
                    melsecFxLinks.TypeCode = "R";
                    break;
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
            return OperateResultExOne.CreateSuccessResult(melsecFxLinks);
        }
        catch (Exception ex) {
            return new OperateResultExOne<MelsecFxLinksAddress>("Address Create failed: " + ex.getMessage());
        }
    }
}

