/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

public class GeSRTPAddress
extends DeviceAddressDataBase {
    private byte DataCode = 0;

    public byte getDataCode() {
        return this.DataCode;
    }

    public void setDataCode(byte dataCode) {
        this.DataCode = dataCode;
    }

    @Override
    public void Parse(String address, int length) {
        super.Parse(address, length);
    }

    public static OperateResultExOne<GeSRTPAddress> ParseFrom(String address, boolean isBit) {
        return GeSRTPAddress.ParseFrom(address, (short)0, isBit);
    }

    public static OperateResultExOne<GeSRTPAddress> ParseFrom(String address, short length, boolean isBit) {
        GeSRTPAddress addressData = new GeSRTPAddress();
        try {
            addressData.setLength(length);
            if (address.startsWith("AI") || address.startsWith("ai")) {
                if (isBit) {
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                }
                addressData.DataCode = (byte)10;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("AQ") || address.startsWith("aq")) {
                if (isBit) {
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                }
                addressData.DataCode = (byte)12;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("R") || address.startsWith("r")) {
                if (isBit) {
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                }
                addressData.DataCode = (byte)8;
                addressData.setAddressStart(Integer.parseInt(address.substring(1)));
            } else if (address.startsWith("SA") || address.startsWith("sa")) {
                addressData.DataCode = (byte)(isBit ? 78 : 24);
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("SB") || address.startsWith("sb")) {
                addressData.DataCode = (byte)(isBit ? 80 : 26);
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("SC") || address.startsWith("sc")) {
                addressData.DataCode = (byte)(isBit ? 82 : 28);
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else {
                if (address.charAt(0) == 'I' || address.charAt(0) == 'i') {
                    addressData.DataCode = (byte)(isBit ? 70 : 16);
                } else if (address.charAt(0) == 'Q' || address.charAt(0) == 'q') {
                    addressData.DataCode = (byte)(isBit ? 72 : 18);
                } else if (address.charAt(0) == 'M' || address.charAt(0) == 'm') {
                    addressData.DataCode = (byte)(isBit ? 76 : 22);
                } else if (address.charAt(0) == 'T' || address.charAt(0) == 't') {
                    addressData.DataCode = (byte)(isBit ? 74 : 20);
                } else if (address.charAt(0) == 'S' || address.charAt(0) == 's') {
                    addressData.DataCode = (byte)(isBit ? 84 : 30);
                } else if (address.charAt(0) == 'G' || address.charAt(0) == 'g') {
                    addressData.DataCode = (byte)(isBit ? 86 : 56);
                } else {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                addressData.setAddressStart(Integer.parseInt(address.substring(1)));
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<GeSRTPAddress>(ex.getMessage());
        }
        if (addressData.getAddressStart() == 0) {
            return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPAddressCannotBeZero());
        }
        if (addressData.getAddressStart() > 0) {
            addressData.setAddressStart(addressData.getAddressStart() - 1);
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

