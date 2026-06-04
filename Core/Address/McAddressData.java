/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressDataBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.Profinet.Melsec.MelsecMcRNet;
import HslCommunication.Profinet.Panasonic.PanasonicHelper;
import HslCommunication.StringResources;

public class McAddressData
extends DeviceAddressDataBase {
    private MelsecMcDataType McDataType = null;

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<McAddressData> addressData = McAddressData.ParseMelsecFrom(address, length);
        if (addressData.IsSuccess) {
            this.setAddressStart(((McAddressData)addressData.Content).getAddressStart());
            this.setLength(((McAddressData)addressData.Content).getLength());
            this.McDataType = ((McAddressData)addressData.Content).McDataType;
        }
    }

    public MelsecMcDataType getMcDataType() {
        return this.McDataType;
    }

    public void setMcDataType(MelsecMcDataType mcDataType) {
        this.McDataType = mcDataType;
    }

    public static OperateResultExOne<McAddressData> ParseMelsecFrom(String address, int length) {
        McAddressData addressData = new McAddressData();
        addressData.setLength(length);
        try {
            switch (address.charAt(0)) {
                case 'M': 
                case 'm': {
                    addressData.McDataType = MelsecMcDataType.M;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.M.getFromBase()));
                    break;
                }
                case 'X': 
                case 'x': {
                    addressData.McDataType = MelsecMcDataType.X;
                    address = address.substring(1);
                    if (address.startsWith("0")) {
                        addressData.setAddressStart(Integer.parseInt(address, 8));
                        break;
                    }
                    addressData.setAddressStart(Integer.parseInt(address, MelsecMcDataType.X.getFromBase()));
                    break;
                }
                case 'Y': 
                case 'y': {
                    addressData.McDataType = MelsecMcDataType.Y;
                    address = address.substring(1);
                    if (address.startsWith("0")) {
                        addressData.setAddressStart(Integer.parseInt(address, 8));
                        break;
                    }
                    addressData.setAddressStart(Integer.parseInt(address, MelsecMcDataType.Y.getFromBase()));
                    break;
                }
                case 'D': 
                case 'd': {
                    if (address.charAt(1) == 'X' || address.charAt(1) == 'x') {
                        addressData.McDataType = MelsecMcDataType.DX;
                        if ((address = address.substring(2)).startsWith("0")) {
                            addressData.setAddressStart(Integer.parseInt(address, 8));
                            break;
                        }
                        addressData.setAddressStart(Integer.parseInt(address, MelsecMcDataType.DX.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'Y' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.DY;
                        if ((address = address.substring(2)).startsWith("0")) {
                            addressData.setAddressStart(Integer.parseInt(address, 8));
                            break;
                        }
                        addressData.setAddressStart(Integer.parseInt(address, MelsecMcDataType.DY.getFromBase()));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.D;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.D.getFromBase()));
                    break;
                }
                case 'W': 
                case 'w': {
                    addressData.McDataType = MelsecMcDataType.W;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.W.getFromBase()));
                    break;
                }
                case 'L': 
                case 'l': {
                    addressData.McDataType = MelsecMcDataType.L;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.L.getFromBase()));
                    break;
                }
                case 'F': 
                case 'f': {
                    addressData.McDataType = MelsecMcDataType.F;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.F.getFromBase()));
                    break;
                }
                case 'V': 
                case 'v': {
                    addressData.McDataType = MelsecMcDataType.V;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.V.getFromBase()));
                    break;
                }
                case 'B': 
                case 'b': {
                    addressData.McDataType = MelsecMcDataType.B;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.B.getFromBase()));
                    break;
                }
                case 'R': 
                case 'r': {
                    addressData.McDataType = MelsecMcDataType.R;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.R.getFromBase()));
                    break;
                }
                case 'S': 
                case 's': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.SN;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SN.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.SS;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SS.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.McDataType = MelsecMcDataType.SC;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SC.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.SM;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SM.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'D' || address.charAt(1) == 'd') {
                        addressData.McDataType = MelsecMcDataType.SD;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SD.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'B' || address.charAt(1) == 'b') {
                        addressData.McDataType = MelsecMcDataType.SB;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SB.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'W' || address.charAt(1) == 'w') {
                        addressData.McDataType = MelsecMcDataType.SW;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.SW.getFromBase()));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.S;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.S.getFromBase()));
                    break;
                }
                case 'Z': 
                case 'z': {
                    if (address.startsWith("ZR") || address.startsWith("zr")) {
                        addressData.McDataType = MelsecMcDataType.ZR;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.ZR.getFromBase()));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Z;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), MelsecMcDataType.Z.getFromBase()));
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.TN;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.TN.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.TS;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.TS.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.McDataType = MelsecMcDataType.TC;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.TC.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.CN;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.CN.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.CS;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.CS.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.McDataType = MelsecMcDataType.CC;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), MelsecMcDataType.CC.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<McAddressData>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    public static OperateResultExOne<McAddressData> ParseMelsecFrom(String address, int length, boolean isBit) {
        return McAddressData.ParseMelsecFrom(address, length);
    }

    public static OperateResultExOne<McAddressData> ParseMelsecRFrom(String address, short length) {
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = MelsecMcRNet.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        McAddressData addressData = new McAddressData();
        addressData.McDataType = (MelsecMcDataType)analysis.Content1;
        addressData.setAddressStart((Integer)analysis.Content2);
        addressData.setLength(length);
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    private static int CalculateComplexAddress(String address) {
        int add = 0;
        if (address.indexOf(46) < 0) {
            add = address.length() <= 2 ? Convert.ToInt32(address) : Convert.ToInt32(address.substring(0, address.length() - 2)) * 16 + Convert.ToInt32(address.substring(address.length() - 2), 10);
        } else {
            add = Convert.ToInt32(address.substring(0, address.indexOf(46))) * 16;
            String bit = address.substring(address.indexOf(46) + 1);
            add += HslHelper.CalculateBitStartIndex(bit);
        }
        return add;
    }

    public static OperateResultExOne<McAddressData> ParseKeyenceFrom(String address, int length, boolean isBit) {
        McAddressData addressData = new McAddressData();
        addressData.setLength(length);
        try {
            switch (address.charAt(0)) {
                case 'M': 
                case 'm': {
                    if (address.charAt(1) == 'R' || address.charAt(1) == 'r') {
                        addressData.McDataType = MelsecMcDataType.Keyence_M;
                        addressData.setAddressStart(McAddressData.CalculateComplexAddress(address.substring(2)));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Keyence_M;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_M.getFromBase()));
                    break;
                }
                case 'X': 
                case 'x': {
                    addressData.McDataType = MelsecMcDataType.Keyence_X;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_X.getFromBase()));
                    break;
                }
                case 'Y': 
                case 'y': {
                    addressData.McDataType = MelsecMcDataType.Keyence_Y;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_Y.getFromBase()));
                    break;
                }
                case 'B': 
                case 'b': {
                    addressData.McDataType = MelsecMcDataType.Keyence_B;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_B.getFromBase()));
                    break;
                }
                case 'L': 
                case 'l': {
                    if (address.charAt(1) == 'R' || address.charAt(1) == 'r') {
                        addressData.McDataType = MelsecMcDataType.Keyence_L;
                        addressData.setAddressStart(McAddressData.CalculateComplexAddress(address.substring(2)));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Keyence_L;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_L.getFromBase()));
                    break;
                }
                case 'S': 
                case 's': {
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.Keyence_SM;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_SM.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'D' || address.charAt(1) == 'd') {
                        addressData.McDataType = MelsecMcDataType.Keyence_SD;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_SD.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'D': 
                case 'd': {
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.Keyence_D;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_D.getFromBase()));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Keyence_D;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_D.getFromBase()));
                    break;
                }
                case 'E': 
                case 'e': {
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.Keyence_D;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_D.getFromBase()) + 100000);
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'F': 
                case 'f': {
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.Keyence_R;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_R.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'R': 
                case 'r': {
                    if (isBit) {
                        addressData.McDataType = MelsecMcDataType.Keyence_Y;
                        addressData.setAddressStart(McAddressData.CalculateComplexAddress(address.substring(1)));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Keyence_R;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_R.getFromBase()));
                    break;
                }
                case 'Z': 
                case 'z': {
                    if (address.charAt(1) == 'R' || address.charAt(1) == 'r') {
                        addressData.McDataType = MelsecMcDataType.Keyence_ZR;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_ZR.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'F' || address.charAt(1) == 'f') {
                        addressData.McDataType = MelsecMcDataType.Keyence_ZR;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), 10));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'W': 
                case 'w': {
                    addressData.McDataType = MelsecMcDataType.Keyence_W;
                    addressData.setAddressStart(Convert.ToInt32(address.substring(1), MelsecMcDataType.Keyence_W.getFromBase()));
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.Keyence_TN;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_TN.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.Keyence_TS;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_TS.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.McDataType = MelsecMcDataType.Keyence_TC;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_TC.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.Keyence_CN;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_CN.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.Keyence_CS;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_CS.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.McDataType = MelsecMcDataType.Keyence_CC;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_CC.getFromBase()));
                        break;
                    }
                    if (address.charAt(1) == 'R' || address.charAt(1) == 'r') {
                        addressData.McDataType = MelsecMcDataType.Keyence_SM;
                        addressData.setAddressStart(McAddressData.CalculateComplexAddress(address.substring(2)));
                        break;
                    }
                    if (address.charAt(1) == 'M' || address.charAt(1) == 'm') {
                        addressData.McDataType = MelsecMcDataType.Keyence_SD;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2), MelsecMcDataType.Keyence_SD.getFromBase()));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<McAddressData>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    public static OperateResultExOne<McAddressData> ParsePanasonicFrom(String address, int length, boolean isBit) {
        McAddressData addressData = new McAddressData();
        addressData.setLength(length);
        try {
            switch (address.charAt(0)) {
                case 'R': 
                case 'r': {
                    int add = PanasonicHelper.CalculateComplexAddress(address.substring(1));
                    if (add < 14400) {
                        addressData.McDataType = MelsecMcDataType.Panasonic_R;
                        addressData.setAddressStart(add);
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Panasonic_SM;
                    addressData.setAddressStart(add - 14400);
                    break;
                }
                case 'X': 
                case 'x': {
                    addressData.McDataType = MelsecMcDataType.Panasonic_X;
                    addressData.setAddressStart(PanasonicHelper.CalculateComplexAddress(address.substring(1)));
                    break;
                }
                case 'Y': 
                case 'y': {
                    addressData.McDataType = MelsecMcDataType.Panasonic_Y;
                    addressData.setAddressStart(PanasonicHelper.CalculateComplexAddress(address.substring(1)));
                    break;
                }
                case 'L': 
                case 'l': {
                    if (address.charAt(1) == 'D' || address.charAt(1) == 'd') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_LD;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2)));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Panasonic_L;
                    addressData.setAddressStart(PanasonicHelper.CalculateComplexAddress(address.substring(1)));
                    break;
                }
                case 'D': 
                case 'd': {
                    int add = Integer.parseInt(address.substring(1));
                    if (add < 90000) {
                        addressData.McDataType = MelsecMcDataType.Panasonic_DT;
                        addressData.setAddressStart(Integer.parseInt(address.substring(1)));
                        break;
                    }
                    addressData.McDataType = MelsecMcDataType.Panasonic_SD;
                    addressData.setAddressStart(Integer.parseInt(address.substring(1)) - 90000);
                    break;
                }
                case 'T': 
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_TN;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2)));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_TS;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2)));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'C': 
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_CN;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2)));
                        break;
                    }
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_CS;
                        addressData.setAddressStart(Integer.parseInt(address.substring(2)));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                case 'S': 
                case 's': {
                    if (address.charAt(1) == 'D' || address.charAt(1) == 'd') {
                        addressData.McDataType = MelsecMcDataType.Panasonic_SD;
                        addressData.setAddressStart(Convert.ToInt32(address.substring(2)));
                        break;
                    }
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<McAddressData>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }
}

