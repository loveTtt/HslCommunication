/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Fuji.FujiSPBHelper;
import HslCommunication.StringResources;

public class FujiSPBAddress
extends DeviceAddressDataBase {
    private String TypeCode = "";
    private int BitIndex = 0;

    public String getTypeCode() {
        return this.TypeCode;
    }

    public void setTypeCode(String typeCode) {
        this.TypeCode = typeCode;
    }

    public int getBitIndex() {
        return this.BitIndex;
    }

    public void setBitIndex(int bitIndex) {
        this.BitIndex = bitIndex;
    }

    public String GetWordAddress() {
        return this.TypeCode + FujiSPBHelper.AnalysisIntegerAddress(this.getAddressStart());
    }

    public String GetWriteBoolAddress() {
        int byteIndex = this.getAddressStart() * 2;
        int bitIndex = this.BitIndex;
        if (bitIndex >= 8) {
            ++byteIndex;
            bitIndex -= 8;
        }
        return this.TypeCode + FujiSPBHelper.AnalysisIntegerAddress(byteIndex) + String.format("%02X", bitIndex);
    }

    public int GetBitIndex() {
        return this.getAddressStart() * 16 + this.BitIndex;
    }

    public static OperateResultExOne<FujiSPBAddress> ParseFrom(String address) {
        return FujiSPBAddress.ParseFrom(address, (short)0);
    }

    public static OperateResultExOne<FujiSPBAddress> ParseFrom(String address, short length) {
        FujiSPBAddress addressData = new FujiSPBAddress();
        try {
            OperateResultExTwo<Integer, String> analysit = HslHelper.GetBitIndexInformation(address);
            addressData.BitIndex = (Integer)analysit.Content1;
            address = (String)analysit.Content2;
            switch (address.charAt(0)) {
                case 'X': 
                case 'x': {
                    addressData.TypeCode = "01";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'Y': 
                case 'y': {
                    addressData.TypeCode = "00";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'M': 
                case 'm': {
                    addressData.TypeCode = "02";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'L': 
                case 'l': {
                    addressData.TypeCode = "03";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.TypeCode = "0A";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.TypeCode = "04";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.TypeCode = "0B";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.TypeCode = "05";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'D': 
                case 'd': {
                    addressData.TypeCode = "0C";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'R': 
                case 'r': {
                    addressData.TypeCode = "0D";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'W': 
                case 'w': {
                    addressData.TypeCode = "0E";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<FujiSPBAddress>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

