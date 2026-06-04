/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.OperateResultExOne;

public class AllenBradleySLCAddress
extends DeviceAddressDataBase {
    public byte DataCode = 0;
    public short DbBlock = 0;

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<AllenBradleySLCAddress> addressData = AllenBradleySLCAddress.ParseFrom(address, (short)length);
        if (addressData.IsSuccess) {
            this.setAddressOffset(((AllenBradleySLCAddress)addressData.Content).getAddressStart());
            this.setLength(((AllenBradleySLCAddress)addressData.Content).getLength());
            this.DataCode = ((AllenBradleySLCAddress)addressData.Content).DataCode;
            this.DbBlock = ((AllenBradleySLCAddress)addressData.Content).DbBlock;
        }
    }

    @Override
    public String toString() {
        switch (this.DataCode) {
            case -114: {
                return "A" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -123: {
                return "B" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -119: {
                return "N" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -118: {
                return "F" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -115: {
                return "ST" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -124: {
                return "S" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -121: {
                return "C" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -125: {
                return "I" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -126: {
                return "O" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -120: {
                return "R" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -122: {
                return "T" + this.DbBlock + ":" + this.getAddressStart();
            }
            case -111: {
                return "L" + this.DbBlock + ":" + this.getAddressStart();
            }
        }
        return String.valueOf(this.getAddressStart());
    }

    public static OperateResultExOne<AllenBradleySLCAddress> ParseFrom(String address) {
        return AllenBradleySLCAddress.ParseFrom(address, (short)0);
    }

    public static OperateResultExOne<AllenBradleySLCAddress> ParseFrom(String address, short length) {
        if (!address.contains(":")) {
            return new OperateResultExOne<AllenBradleySLCAddress>("Address can't find ':', example : A9:0");
        }
        String[] adds = address.split(":");
        try {
            AllenBradleySLCAddress allenBradleySLC = new AllenBradleySLCAddress();
            switch (adds[0].charAt(0)) {
                case 'A': {
                    allenBradleySLC.DataCode = (byte)-114;
                    break;
                }
                case 'B': {
                    allenBradleySLC.DataCode = (byte)-123;
                    break;
                }
                case 'N': {
                    allenBradleySLC.DataCode = (byte)-119;
                    break;
                }
                case 'F': {
                    allenBradleySLC.DataCode = (byte)-118;
                    break;
                }
                case 'S': {
                    if (adds[0].length() > 1 && adds[0].charAt(1) == 'T') {
                        allenBradleySLC.DataCode = (byte)-115;
                        break;
                    }
                    allenBradleySLC.DataCode = (byte)-124;
                    break;
                }
                case 'C': {
                    allenBradleySLC.DataCode = (byte)-121;
                    break;
                }
                case 'I': {
                    allenBradleySLC.DataCode = (byte)-125;
                    break;
                }
                case 'O': {
                    allenBradleySLC.DataCode = (byte)-126;
                    break;
                }
                case 'R': {
                    allenBradleySLC.DataCode = (byte)-120;
                    break;
                }
                case 'T': {
                    allenBradleySLC.DataCode = (byte)-122;
                    break;
                }
                case 'L': {
                    allenBradleySLC.DataCode = (byte)-111;
                    break;
                }
                default: {
                    throw new Exception("Address code wrong, must be A,B,N,F,S,C,I,O,R,T,ST,L");
                }
            }
            switch (allenBradleySLC.DataCode) {
                case -124: {
                    allenBradleySLC.DbBlock = (short)(adds[0].length() == 1 ? 2 : (int)Short.parseShort(adds[0].substring(1)));
                    break;
                }
                case -126: {
                    allenBradleySLC.DbBlock = adds[0].length() == 1 ? (short)0 : Short.parseShort(adds[0].substring(1));
                    break;
                }
                case -125: {
                    allenBradleySLC.DbBlock = adds[0].length() == 1 ? (short)1 : Short.parseShort(adds[0].substring(1));
                    break;
                }
                case -115: {
                    allenBradleySLC.DbBlock = adds[0].length() == 2 ? (short)1 : Short.parseShort(adds[0].substring(2));
                    break;
                }
                default: {
                    allenBradleySLC.DbBlock = Short.parseShort(adds[0].substring(1));
                }
            }
            allenBradleySLC.setAddressStart(Short.parseShort(adds[1]));
            return OperateResultExOne.CreateSuccessResult(allenBradleySLC);
        }
        catch (Exception ex) {
            return new OperateResultExOne<AllenBradleySLCAddress>("Wrong Address format: " + ex.getMessage());
        }
    }
}

