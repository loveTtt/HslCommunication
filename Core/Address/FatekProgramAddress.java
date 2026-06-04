/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

public class FatekProgramAddress
extends DeviceAddressDataBase {
    private String DataCode;

    public String getDataCode() {
        return this.DataCode;
    }

    public void setDataCode(String dataCode) {
        this.DataCode = dataCode;
    }

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<FatekProgramAddress> addressData = FatekProgramAddress.ParseFrom(address, length);
        if (addressData.IsSuccess) {
            this.setAddressStart(((FatekProgramAddress)addressData.Content).getAddressStart());
            this.setLength(((FatekProgramAddress)addressData.Content).getLength());
            this.setDataCode(((FatekProgramAddress)addressData.Content).getDataCode());
        }
    }

    @Override
    public String toString() {
        if (this.getDataCode().equals("X") || this.getDataCode().equals("Y") || this.getDataCode().equals("M") || this.getDataCode().equals("S") || this.getDataCode().equals("T") || this.getDataCode().equals("C") || this.getDataCode().equals("RT") || this.getDataCode().equals("RC")) {
            return this.getDataCode() + String.format("%04d", this.getAddressStart());
        }
        return this.getDataCode() + String.format("%05d", this.getAddressStart());
    }

    public static OperateResultExOne<FatekProgramAddress> ParseFrom(String address, int length) {
        try {
            FatekProgramAddress programAddress = new FatekProgramAddress();
            switch (address.charAt(0)) {
                case 'X': 
                case 'x': {
                    programAddress.DataCode = "X";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'Y': 
                case 'y': {
                    programAddress.DataCode = "Y";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'M': 
                case 'm': {
                    programAddress.DataCode = "M";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'S': 
                case 's': {
                    programAddress.DataCode = "S";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'T': 
                case 't': {
                    programAddress.DataCode = "T";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'C': 
                case 'c': {
                    programAddress.DataCode = "C";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'D': 
                case 'd': {
                    programAddress.DataCode = "D";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'R': 
                case 'r': {
                    if (address.charAt(1) == 'T' || address.charAt(1) == 't') {
                        programAddress.DataCode = "RT";
                        programAddress.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        programAddress.DataCode = "RC";
                        programAddress.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    }
                    programAddress.DataCode = "R";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
            return OperateResultExOne.CreateSuccessResult(programAddress);
        }
        catch (Exception ex) {
            return new OperateResultExOne<FatekProgramAddress>(ex.getMessage());
        }
    }
}

