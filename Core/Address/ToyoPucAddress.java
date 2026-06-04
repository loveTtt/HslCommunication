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

public class ToyoPucAddress
extends DeviceAddressDataBase {
    public int PRG = -1;

    @Override
    public String toString() {
        return String.valueOf(this.getAddressStart());
    }

    public static OperateResultExOne<ToyoPucAddress> ParseFrom(String address, short length, boolean isBit) {
        ToyoPucAddress addressData;
        block38: {
            addressData = new ToyoPucAddress();
            addressData.setLength(length);
            OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "prg", -1);
            if (extra.IsSuccess) {
                addressData.PRG = (Integer)extra.Content1;
                address = (String)extra.Content2;
            }
            try {
                if (address.charAt(0) == 'K' || address.charAt(0) == 'k') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 512 : 32));
                    break block38;
                }
                if (address.charAt(0) == 'V' || address.charAt(0) == 'v') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 1280 : 80));
                    break block38;
                }
                if (address.charAt(0) == 'T' || address.charAt(0) == 't') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 1536 : 96));
                    break block38;
                }
                if (address.charAt(0) == 'C' || address.charAt(0) == 'c') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 1536 : 96));
                    break block38;
                }
                if (address.charAt(0) == 'L' || address.charAt(0) == 'l') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 2048 : 128));
                    break block38;
                }
                if (address.charAt(0) == 'X' || address.charAt(0) == 'x') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 4096 : 256));
                    break block38;
                }
                if (address.charAt(0) == 'Y' || address.charAt(0) == 'y') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 4096 : 256));
                    break block38;
                }
                if (address.charAt(0) == 'M' || address.charAt(0) == 'm') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + (isBit ? 6144 : 384));
                    break block38;
                }
                if (address.charAt(0) == 'S' || address.charAt(0) == 's') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 512);
                    break block38;
                }
                if (address.charAt(0) == 'N' || address.charAt(0) == 'n') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 1536);
                    break block38;
                }
                if (address.charAt(0) == 'R' || address.charAt(0) == 'r') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 2048);
                    break block38;
                }
                if (address.charAt(0) == 'D' || address.charAt(0) == 'd') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 4096);
                    break block38;
                }
                if (address.charAt(0) == 'B' || address.charAt(0) == 'b') {
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 24576);
                    break block38;
                }
                if (address.charAt(0) == 'E' || address.charAt(0) == 'e') {
                    if (address.charAt(1) == 'K' || address.charAt(1) == 'k') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 4096 : 256));
                        break block38;
                    }
                    if (address.charAt(1) == 'V' || address.charAt(1) == 'v') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 8192 : 512));
                        break block38;
                    }
                    if (address.charAt(1) == 'T' || address.charAt(1) == 't') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 12288 : 768));
                        break block38;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 12288 : 768));
                        break block38;
                    }
                    if (address.charAt(1) == 'L' || address.charAt(1) == 'l') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 14336 : 896));
                        break block38;
                    }
                    if (address.charAt(1) == 'X' || address.charAt(1) == 'x') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 22528 : 1408));
                        break block38;
                    }
                    if (address.charAt(1) == 'Y' || address.charAt(1) == 'y') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 22528 : 1408));
                        break block38;
                    }
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + (isBit ? 24576 : 1536));
                        break block38;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + 2048);
                        break block38;
                    }
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.PRG = 0;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + 4096);
                        break block38;
                    }
                    if (address.charAt(1) == 'B' || address.charAt(1) == 'b') {
                        int add = Convert.ToInt32(address.substring(2), 16);
                        if (add < 32768) {
                            addressData.PRG = 9;
                            addressData.setAddressStart(add);
                        } else if (add < 65536) {
                            addressData.PRG = 10;
                            addressData.setAddressStart(add - 32768);
                        } else {
                            addressData.PRG = 11;
                            addressData.setAddressStart(add - 65536);
                        }
                        break block38;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                if (address.charAt(0) == 'H' || address.charAt(0) == 'h') {
                    addressData.PRG = 0;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16) + 6144);
                    break block38;
                }
                if (address.charAt(0) == 'U' || address.charAt(0) == 'u') {
                    addressData.PRG = 8;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), 16));
                    break block38;
                }
                if (address.charAt(0) == 'G' || address.charAt(0) == 'g') {
                    if (address.charAt(1) == 'X' || address.charAt(1) == 'x') {
                        addressData.PRG = 7;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16));
                        break block38;
                    }
                    if (address.charAt(1) == 'Y' || address.charAt(1) == 'y') {
                        addressData.PRG = 7;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16));
                        break block38;
                    }
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.PRG = 7;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 16) + 4096);
                        break block38;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            catch (Exception ex) {
                return new OperateResultExOne<ToyoPucAddress>(ex.getMessage());
            }
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

