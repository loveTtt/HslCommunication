/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class YokogawaLinkAddress
extends DeviceAddressDataBase {
    private int DataCode = 0;

    public int getDataCode() {
        return this.DataCode;
    }

    public void setDataCode(int dataCode) {
        this.DataCode = dataCode;
    }

    public byte[] GetAddressBinaryContent() {
        byte[] buffer = new byte[]{Utilities.getBytes(this.DataCode)[1], Utilities.getBytes(this.DataCode)[0], Utilities.getBytes(this.getAddressStart())[3], Utilities.getBytes(this.getAddressStart())[2], Utilities.getBytes(this.getAddressStart())[1], Utilities.getBytes(this.getAddressStart())[0]};
        return buffer;
    }

    public void Parse(String address, short length) {
        OperateResultExOne<YokogawaLinkAddress> addressData = YokogawaLinkAddress.ParseFrom(address, length);
        if (addressData.IsSuccess) {
            this.setAddressStart(((YokogawaLinkAddress)addressData.Content).getAddressStart());
            this.setLength(((YokogawaLinkAddress)addressData.Content).getLength());
            this.DataCode = ((YokogawaLinkAddress)addressData.Content).DataCode;
        }
    }

    @Override
    public String toString() {
        switch (this.DataCode) {
            case 49: {
                return "CN" + this.getAddressStart();
            }
            case 33: {
                return "TN" + this.getAddressStart();
            }
            case 24: {
                return "X" + this.getAddressStart();
            }
            case 25: {
                return "Y" + this.getAddressStart();
            }
            case 9: {
                return "I" + this.getAddressStart();
            }
            case 5: {
                return "E" + this.getAddressStart();
            }
            case 13: {
                return "M" + this.getAddressStart();
            }
            case 20: {
                return "T" + this.getAddressStart();
            }
            case 3: {
                return "C" + this.getAddressStart();
            }
            case 12: {
                return "L" + this.getAddressStart();
            }
            case 4: {
                return "D" + this.getAddressStart();
            }
            case 2: {
                return "B" + this.getAddressStart();
            }
            case 6: {
                return "F" + this.getAddressStart();
            }
            case 18: {
                return "R" + this.getAddressStart();
            }
            case 22: {
                return "V" + this.getAddressStart();
            }
            case 26: {
                return "Z" + this.getAddressStart();
            }
            case 23: {
                return "W" + this.getAddressStart();
            }
        }
        return String.valueOf(this.getAddressStart());
    }

    public static OperateResultExOne<YokogawaLinkAddress> ParseFrom(String address, short length) {
        try {
            int type = 0;
            int offset = 0;
            if (address.startsWith("CN") || address.startsWith("cn")) {
                type = 49;
                offset = Integer.parseInt(address.substring(2));
            } else if (address.startsWith("TN") || address.startsWith("tn")) {
                type = 33;
                offset = Integer.parseInt(address.substring(2));
            } else if (address.startsWith("X") || address.startsWith("x")) {
                type = 24;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("Y") || address.startsWith("y")) {
                type = 25;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("I") || address.startsWith("i")) {
                type = 9;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("E") || address.startsWith("e")) {
                type = 5;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("M") || address.startsWith("m")) {
                type = 13;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("T") || address.startsWith("t")) {
                type = 20;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("C") || address.startsWith("c")) {
                type = 3;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("L") || address.startsWith("l")) {
                type = 12;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("D") || address.startsWith("d")) {
                type = 4;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("B") || address.startsWith("b")) {
                type = 2;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("F") || address.startsWith("f")) {
                type = 6;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("R") || address.startsWith("r")) {
                type = 18;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("V") || address.startsWith("v")) {
                type = 22;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("Z") || address.startsWith("z")) {
                type = 26;
                offset = Integer.parseInt(address.substring(1));
            } else if (address.startsWith("W") || address.startsWith("w")) {
                type = 23;
                offset = Integer.parseInt(address.substring(1));
            } else {
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            YokogawaLinkAddress yokogawaLinkAddress = new YokogawaLinkAddress();
            yokogawaLinkAddress.setAddressStart(offset);
            yokogawaLinkAddress.setLength(length);
            yokogawaLinkAddress.setDataCode(type);
            return OperateResultExOne.CreateSuccessResult(yokogawaLinkAddress);
        }
        catch (Exception ex) {
            return new OperateResultExOne<YokogawaLinkAddress>(ex.getMessage());
        }
    }
}

