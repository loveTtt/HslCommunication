/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

public class FujiSPHAddress
extends DeviceAddressDataBase {
    private byte TypeCode = 0;
    private int BitIndex = 0;

    public byte getTypeCode() {
        return this.TypeCode;
    }

    public void setTypeCode(byte typeCode) {
        this.TypeCode = typeCode;
    }

    public int getBitIndex() {
        return this.BitIndex;
    }

    public void setBitIndex(int bitIndex) {
        this.BitIndex = bitIndex;
    }

    public static OperateResultExOne<FujiSPHAddress> ParseFrom(String address) {
        return FujiSPHAddress.ParseFrom(address, (short)0);
    }

    public static OperateResultExOne<FujiSPHAddress> ParseFrom(String address, short length) {
        FujiSPHAddress addressData = new FujiSPHAddress();
        try {
            switch (address.charAt(0)) {
                case 'M': 
                case 'm': {
                    String[] splits = address.split("\\.");
                    int datablock = Integer.parseInt(splits[0].substring(1));
                    if (datablock == 1) {
                        addressData.setTypeCode((byte)2);
                    } else if (datablock == 3) {
                        addressData.setTypeCode((byte)4);
                    } else if (datablock == 10) {
                        addressData.setTypeCode((byte)8);
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                    addressData.setAddressStart(Integer.parseInt(splits[1]));
                    if (splits.length > 2) {
                        addressData.BitIndex = HslHelper.CalculateBitStartIndex(splits[2]);
                    }
                    break;
                }
                case 'I': 
                case 'Q': 
                case 'i': 
                case 'q': {
                    String[] splits = address.split("\\.");
                    addressData.TypeCode = 1;
                    addressData.setAddressStart(Integer.parseInt(splits[0].substring(1)));
                    if (splits.length > 1) {
                        addressData.BitIndex = HslHelper.CalculateBitStartIndex(splits[1]);
                    }
                    break;
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<FujiSPHAddress>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

