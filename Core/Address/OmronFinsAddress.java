/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Omron.OmronFinsDataType;
import HslCommunication.Profinet.Omron.OmronPlcType;
import HslCommunication.StringResources;

public class OmronFinsAddress
extends DeviceAddressDataBase {
    private byte BitCode = 0;
    private byte WordCode = 0;

    public byte getBitCode() {
        return this.BitCode;
    }

    public void setBitCode(byte bitCode) {
        this.BitCode = bitCode;
    }

    public byte getWordCode() {
        return this.WordCode;
    }

    public void setWordCode(byte wordCode) {
        this.WordCode = wordCode;
    }

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<OmronFinsAddress> addressData = OmronFinsAddress.ParseFrom(address, (short)length, OmronPlcType.CSCJ);
        if (addressData.IsSuccess) {
            this.setAddressStart(((OmronFinsAddress)addressData.Content).getAddressStart());
            this.setLength(((OmronFinsAddress)addressData.Content).getLength());
            this.setBitCode(((OmronFinsAddress)addressData.Content).getBitCode());
            this.setWordCode(((OmronFinsAddress)addressData.Content).getWordCode());
        }
    }

    private static int CalculateBitIndex(String address) {
        String[] splits = address.split("\\.");
        int addr = Integer.parseInt(splits[0]) * 16;
        if (splits.length > 1) {
            addr += HslHelper.CalculateBitStartIndex(splits[1]);
        }
        return addr;
    }

    public static OperateResultExOne<OmronFinsAddress> ParseFrom(String address) {
        return OmronFinsAddress.ParseFrom(address, (short)0, OmronPlcType.CSCJ);
    }

    public static OperateResultExOne<OmronFinsAddress> ParseFrom(String address, short length, OmronPlcType plcType) {
        OmronFinsAddress addressData;
        block34: {
            addressData = new OmronFinsAddress();
            try {
                addressData.setLength(length);
                if (address.startsWith("DR") || address.startsWith("dr")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.setWordCode((byte)-100);
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)) + 48);
                    } else {
                        addressData.setWordCode((byte)-68);
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)) + 8192);
                    }
                    break block34;
                }
                if (address.startsWith("IR") || address.startsWith("ir")) {
                    addressData.setWordCode((byte)-36);
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)) + 4096);
                    break block34;
                }
                if (address.startsWith("DM") || address.startsWith("dm")) {
                    addressData.BitCode = OmronFinsDataType.DM.getBitCode();
                    addressData.WordCode = OmronFinsDataType.DM.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)));
                    break block34;
                }
                if (address.startsWith("TIM") || address.startsWith("tim")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 1;
                        addressData.WordCode = (byte)-127;
                    } else {
                        addressData.BitCode = OmronFinsDataType.TIM.getBitCode();
                        addressData.WordCode = OmronFinsDataType.TIM.getWordCode();
                    }
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(3)));
                    break block34;
                }
                if (address.startsWith("CNT") || address.startsWith("cnt")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 1;
                        addressData.WordCode = (byte)-127;
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(3)) + 32768);
                    } else {
                        addressData.BitCode = OmronFinsDataType.TIM.getBitCode();
                        addressData.WordCode = OmronFinsDataType.TIM.getWordCode();
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(3)) + 524288);
                    }
                    break block34;
                }
                if (address.startsWith("CIO") || address.startsWith("cio")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 0;
                        addressData.WordCode = (byte)-128;
                    } else {
                        addressData.BitCode = OmronFinsDataType.CIO.getBitCode();
                        addressData.WordCode = OmronFinsDataType.CIO.getWordCode();
                    }
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(3)));
                    break block34;
                }
                if (address.startsWith("WR") || address.startsWith("wr")) {
                    addressData.BitCode = OmronFinsDataType.WR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.WR.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)));
                    break block34;
                }
                if (address.startsWith("HR") || address.startsWith("hr")) {
                    addressData.BitCode = OmronFinsDataType.HR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.HR.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)));
                    break block34;
                }
                if (address.startsWith("AR") || address.startsWith("ar")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 0;
                        addressData.WordCode = (byte)-128;
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)) + 45056);
                    } else {
                        addressData.BitCode = OmronFinsDataType.AR.getBitCode();
                        addressData.WordCode = OmronFinsDataType.AR.getWordCode();
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)));
                    }
                    break block34;
                }
                if (address.startsWith("CF") || address.startsWith("cf")) {
                    addressData.BitCode = (byte)7;
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(2)));
                    break block34;
                }
                if (address.startsWith("EM") || address.startsWith("em") || address.startsWith("E") || address.startsWith("e")) {
                    String[] splits = address.split("\\.");
                    int block = Integer.parseInt(splits[0].substring(address.charAt(1) == 'M' || address.charAt(1) == 'm' ? 2 : 1), 16);
                    if (block < 16) {
                        addressData.BitCode = (byte)(32 + block);
                        addressData.WordCode = plcType == OmronPlcType.CV ? (byte)(144 + block) : (byte)(160 + block);
                    } else {
                        addressData.BitCode = (byte)(224 + block - 16);
                        addressData.WordCode = (byte)(96 + block - 16);
                    }
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(address.indexOf(46) + 1)));
                    break block34;
                }
                if (address.startsWith("D") || address.startsWith("d")) {
                    addressData.BitCode = OmronFinsDataType.DM.getBitCode();
                    addressData.WordCode = OmronFinsDataType.DM.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)));
                    break block34;
                }
                if (address.startsWith("C") || address.startsWith("c")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 0;
                        addressData.WordCode = (byte)-128;
                    } else {
                        addressData.BitCode = OmronFinsDataType.CIO.getBitCode();
                        addressData.WordCode = OmronFinsDataType.CIO.getWordCode();
                    }
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)));
                    break block34;
                }
                if (address.startsWith("W") || address.startsWith("w")) {
                    addressData.BitCode = OmronFinsDataType.WR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.WR.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)));
                    break block34;
                }
                if (address.startsWith("H") || address.startsWith("h")) {
                    addressData.BitCode = OmronFinsDataType.HR.getBitCode();
                    addressData.WordCode = OmronFinsDataType.HR.getWordCode();
                    addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)));
                    break block34;
                }
                if (address.startsWith("A") || address.startsWith("a")) {
                    if (plcType == OmronPlcType.CV) {
                        addressData.BitCode = 0;
                        addressData.WordCode = (byte)-128;
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)) + 45056);
                    } else {
                        addressData.BitCode = OmronFinsDataType.AR.getBitCode();
                        addressData.WordCode = OmronFinsDataType.AR.getWordCode();
                        addressData.setAddressStart(OmronFinsAddress.CalculateBitIndex(address.substring(1)));
                    }
                    break block34;
                }
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            catch (Exception ex) {
                return new OperateResultExOne<OmronFinsAddress>(ex.getMessage());
            }
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

