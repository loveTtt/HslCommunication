/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

public class S7AddressData
extends DeviceAddressDataBase {
    private int DataCode = 0;
    private int DbBlock = 0;

    public S7AddressData() {
    }

    public S7AddressData(int dataCode, int dbBlock, int length) {
        this.DataCode = dataCode;
        this.DbBlock = dbBlock;
        this.setLength(length);
    }

    public S7AddressData(int address, int dataCode, int dbBlock, int length) {
        this(dataCode, dbBlock, length);
        this.setAddressStart(address);
    }

    public S7AddressData(S7AddressData s7Address) {
        this.setAddressStart(s7Address.getAddressStart());
        this.setLength(s7Address.getLength());
        this.DbBlock = s7Address.DbBlock;
        this.DataCode = s7Address.DataCode;
    }

    public int getDataCode() {
        return this.DataCode;
    }

    public void setDataCode(int dataCode) {
        this.DataCode = dataCode;
    }

    public int getDbBlock() {
        return this.DbBlock;
    }

    public void setDbBlock(int dbBlock) {
        this.DbBlock = dbBlock;
    }

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<S7AddressData> addressData = S7AddressData.ParseFrom(address, length);
        if (addressData.IsSuccess) {
            this.setAddressStart(((S7AddressData)addressData.Content).getAddressStart());
            this.setLength(((S7AddressData)addressData.Content).getLength());
            this.DataCode = ((S7AddressData)addressData.Content).getDataCode();
            this.DbBlock = ((S7AddressData)addressData.Content).getDbBlock();
        }
    }

    public static int CalculateAddressStarted(String address, boolean isCT) {
        if (address.indexOf(46) < 0) {
            if (isCT) {
                return Integer.parseInt(address);
            }
            return Integer.parseInt(address) * 8;
        }
        String[] temp = address.split("\\.");
        return Integer.parseInt(temp[0]) * 8 + Integer.parseInt(temp[1]);
    }

    public static OperateResultExOne<S7AddressData> ParseFrom(String address) {
        return S7AddressData.ParseFrom(address, 0);
    }

    public static OperateResultExOne<S7AddressData> ParseFrom(String address, int length) {
        S7AddressData addressData;
        block32: {
            addressData = new S7AddressData();
            try {
                addressData.setLength(length);
                addressData.DbBlock = 0;
                if (address.startsWith("SM")) {
                    addressData.DataCode = 5;
                    if (address.startsWith("SMX") || address.startsWith("SMB") || address.startsWith("SMW") || address.startsWith("SMD")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(3), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    }
                    break block32;
                }
                if (address.startsWith("AI") || address.startsWith("ai")) {
                    addressData.DataCode = 6;
                    if (address.startsWith("AIX") || address.startsWith("AIB") || address.startsWith("AIW") || address.startsWith("AID")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(3), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    }
                    break block32;
                }
                if (address.startsWith("AQ") || address.startsWith("aq")) {
                    addressData.DataCode = 7;
                    if (address.startsWith("AQX") || address.startsWith("AQB") || address.startsWith("AQW") || address.startsWith("AQD")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(3), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    }
                    break block32;
                }
                if (address.charAt(0) == 'P') {
                    addressData.DataCode = 128;
                    if (address.startsWith("PIX") || address.startsWith("PIB") || address.startsWith("PIW") || address.startsWith("PID") || address.startsWith("PQX") || address.startsWith("PQB") || address.startsWith("PQW") || address.startsWith("PQD")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(3), false));
                    } else if (address.startsWith("PI") || address.startsWith("PQ")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), false));
                    }
                    break block32;
                }
                if (address.charAt(0) == 'I') {
                    addressData.DataCode = 129;
                    if (address.startsWith("IX") || address.startsWith("IB") || address.startsWith("IW") || address.startsWith("ID")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), false));
                    }
                    break block32;
                }
                if (address.charAt(0) == 'Q') {
                    addressData.DataCode = 130;
                    if (address.startsWith("QX") || address.startsWith("QB") || address.startsWith("QW") || address.startsWith("QD")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), false));
                    }
                    break block32;
                }
                if (address.charAt(0) == 'M') {
                    addressData.DataCode = 131;
                    if (address.startsWith("MX") || address.startsWith("MB") || address.startsWith("MW") || address.startsWith("MD")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), false));
                    }
                    break block32;
                }
                if (address.charAt(0) == 'D' || address.substring(0, 2).equals("DB")) {
                    addressData.DataCode = 132;
                    String[] adds = address.split("\\.");
                    addressData.DbBlock = address.charAt(1) == 'B' ? Integer.parseInt(adds[0].substring(2)) : Integer.parseInt(adds[0].substring(1));
                    String addTemp = address.substring(address.indexOf(46) + 1);
                    if (addTemp.startsWith("DBX") || addTemp.startsWith("DBB") || addTemp.startsWith("DBW") || addTemp.startsWith("DBD")) {
                        addTemp = addTemp.substring(3);
                    }
                    addressData.setAddressStart(S7AddressData.CalculateAddressStarted(addTemp, false));
                    break block32;
                }
                if (address.charAt(0) == 'T') {
                    addressData.DataCode = 31;
                    addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), true));
                    break block32;
                }
                if (address.charAt(0) == 'C') {
                    addressData.DataCode = 30;
                    addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), true));
                    break block32;
                }
                if (address.charAt(0) == 'V') {
                    addressData.DataCode = 132;
                    addressData.DbBlock = 1;
                    if (address.startsWith("VB") || address.startsWith("VW") || address.startsWith("VD") || address.startsWith("VX")) {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(2), false));
                    } else {
                        addressData.setAddressStart(S7AddressData.CalculateAddressStarted(address.substring(1), false));
                    }
                    break block32;
                }
                return new OperateResultExOne<S7AddressData>(StringResources.Language.NotSupportedDataType());
            }
            catch (Exception ex) {
                return new OperateResultExOne<S7AddressData>(ex.getMessage());
            }
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    private static String GetActualStringAddress(int addressStart) {
        if (addressStart % 8 == 0) {
            return String.valueOf(addressStart / 8);
        }
        return addressStart / 8 + "." + addressStart % 8;
    }

    @Override
    public String toString() {
        if (this.DataCode == 31) {
            return "T" + this.getAddressStart();
        }
        if (this.DataCode == 30) {
            return "C" + this.getAddressStart();
        }
        if (this.DataCode == 5) {
            return "SM" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 6) {
            return "AI" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 7) {
            return "AQ" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 128) {
            return "P" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 129) {
            return "I" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 130) {
            return "Q" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 131) {
            return "M" + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        if (this.DataCode == 132) {
            return "DB" + this.DbBlock + "." + S7AddressData.GetActualStringAddress(this.getAddressStart());
        }
        return String.valueOf(this.getAddressStart());
    }
}

