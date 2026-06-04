//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.Authorization;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.ReadWriteNetHelper;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.MelsecMcRNet;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class McHelper {
    public McHelper() {
        super();
    }

    public static int GetReadWordLength(McType type) {
        return type == McType.McBinary ? 950 : 460;
    }

    public static int GetReadBoolLength(McType type) {
        return type == McType.McBinary ? 7168 : 3584;
    }

    public static OperateResultExOne<byte[]> Read(IReadWriteMc mc, String address, short length) {
        if ((mc.getMcType() != McType.McBinary || !address.startsWith("s=")) && !address.startsWith("S=")) {
            if ((mc.getMcType() == McType.McBinary || mc.getMcType() == McType.MCAscii) && Pattern.matches("ext=[0-9]+;", address)) {
                String extStr = Pattern.compile("ext=[0-9]+;").matcher(address).group();
                short ext = Short.parseShort(Pattern.compile("[0-9]+").matcher(extStr).group());
                return ReadExtend(mc, ext, address.substring(extStr.length()), length);
            } else if ((mc.getMcType() == McType.McBinary || mc.getMcType() == McType.MCAscii) && Pattern.matches("mem=", address)) {
                return ReadMemory(mc, address.substring(4), length);
            } else {
                OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, false);
                if (!addressResult.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(addressResult);
                } else {
                    ArrayList<Byte> bytesContent = new ArrayList();
                    int alreadyFinished = 0;

                    while(alreadyFinished < length) {
                        int readLength = Math.min(length - alreadyFinished, GetReadWordLength(mc.getMcType()));
                        ((McAddressData)addressResult.Content).setLength(readLength);
                        byte[] command = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreCommand((McAddressData)addressResult.Content, false) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreCommand((McAddressData)addressResult.Content, false) : (mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildReadMcCoreCommand((McAddressData)addressResult.Content, false) : null));
                        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(command);
                        if (!read.IsSuccess) {
                            return read;
                        }

                        Utilities.ArrayListAddArray(bytesContent, mc.ExtractActualData((byte[])read.Content, false));
                        alreadyFinished += readLength;
                        if (((McAddressData)addressResult.Content).getMcDataType().getDataType() == 0) {
                            ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength);
                        } else {
                            ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength * 16);
                        }
                    }

                    return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bytesContent));
                }
            }
        } else {
            return McBinaryHelper.ReadTags(mc, new String[]{address.substring(2)}, new short[]{length});
        }
    }

    public static OperateResult Write(IReadWriteMc mc, String address, byte[] value) {
        OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, (short)0, false);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        } else {
            byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildWriteWordCoreCommand((McAddressData)addressResult.Content, value) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiWriteWordCoreCommand((McAddressData)addressResult.Content, value) : (mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildWriteWordCoreCommand((McAddressData)addressResult.Content, value) : null));
            OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
            return (OperateResult)(!read.IsSuccess ? read : OperateResult.CreateSuccessResult());
        }
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteMc mc, String address, short length) {
        return ReadBool(mc, address, length, true);
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteMc mc, String address, short length, boolean supportWordAdd) {
        if (supportWordAdd && address.indexOf(46) > 0) {
            return HslHelper.ReadBool(mc, address, length);
        } else {
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, true);
            if (!addressResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(addressResult);
            } else {
                ArrayList<Boolean> boolContent = new ArrayList();
                short alreadyFinished = 0;

                while(alreadyFinished < length) {
                    short readLength = (short)Math.min(length - alreadyFinished, GetReadBoolLength(mc.getMcType()));
                    ((McAddressData)addressResult.Content).setLength(readLength);
                    byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreCommand((McAddressData)addressResult.Content, true) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreCommand((McAddressData)addressResult.Content, true) : (mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildReadMcCoreCommand((McAddressData)addressResult.Content, true) : null));
                    OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
                    if (!read.IsSuccess) {
                        return OperateResultExOne.CreateFailedResult(read);
                    }

                    byte[] extra = mc.ExtractActualData((byte[])read.Content, true);

                    for(int i = 0; i < readLength; ++i) {
                        if (i < extra.length) {
                            boolContent.add(extra[i] == 1);
                        }
                    }

                    alreadyFinished += readLength;
                    ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength);
                }

                return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(boolContent));
            }
        }
    }

    public static OperateResult Write(IReadWriteMc mc, String address, boolean[] values) {
        return Write(mc, address, values, true);
    }

    public static OperateResult Write(IReadWriteMc mc, String address, boolean[] values, boolean supportWordAdd) {
        if (supportWordAdd && address.indexOf(".") > 0) {
            return ReadWriteNetHelper.WriteBoolWithWord(mc, address, values, 16);
        } else {
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, (short)0, true);
            if (!addressResult.IsSuccess) {
                return addressResult;
            } else {
                byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildWriteBitCoreCommand((McAddressData)addressResult.Content, values) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiWriteBitCoreCommand((McAddressData)addressResult.Content, values) : (mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildWriteBitCoreCommand((McAddressData)addressResult.Content, values) : null));
                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
                return (OperateResult)(!read.IsSuccess ? read : OperateResult.CreateSuccessResult());
            }
        }
    }

    public static OperateResultExOne<byte[]> ReadRandom(IReadWriteMc mc, String[] address) {
        McAddressData[] mcAddressDatas = new McAddressData[address.length];

        for(int i = 0; i < address.length; ++i) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], 1);
            if (!addressResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(addressResult);
            }

            mcAddressDatas[i] = (McAddressData)addressResult.Content;
        }

        byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadRandomWordCommand(mcAddressDatas) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadRandomWordCommand(mcAddressDatas) : null);
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        } else {
            return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData((byte[])read.Content, false));
        }
    }

    public static OperateResultExOne<byte[]> ReadRandom(IReadWriteMc mc, String[] address, short[] length) {
        if (length.length != address.length) {
            return new OperateResultExOne(StringResources.Language.TwoParametersLengthIsNotSame());
        } else {
            McAddressData[] mcAddressDatas = new McAddressData[address.length];

            for(int i = 0; i < address.length; ++i) {
                OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], length[i]);
                if (!addressResult.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(addressResult);
                }

                mcAddressDatas[i] = (McAddressData)addressResult.Content;
            }

            byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadRandomCommand(mcAddressDatas) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadRandomCommand(mcAddressDatas) : null);
            OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            } else {
                return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData((byte[])read.Content, false));
            }
        }
    }

    public static OperateResultExOne<short[]> ReadRandomInt16(IReadWriteMc mc, String[] address) {
        OperateResultExOne<byte[]> read = ReadRandom(mc, address);
        return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult(mc.getByteTransform().TransInt16((byte[])read.Content, 0, address.length));
    }

    public static OperateResultExOne<int[]> ReadRandomUInt16(IReadWriteMc mc, String[] address) {
        OperateResultExOne<byte[]> read = ReadRandom(mc, address);
        return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult(mc.getByteTransform().TransUInt16((byte[])read.Content, 0, address.length));
    }

    public static OperateResultExOne<byte[]> ReadMemory(IReadWriteMc mc, String address, short length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        } else {
            OperateResultExOne<byte[]> coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMemoryCommand(address, length) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMemoryCommand(address, length) : null);
            if (!coreResult.IsSuccess) {
                return coreResult;
            } else {
                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer((byte[])coreResult.Content);
                return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult(mc.ExtractActualData((byte[])read.Content, false));
            }
        }
    }

    public static OperateResultExOne<byte[]> ReadSmartModule(IReadWriteMc mc, short module, String address, short length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        } else {
            OperateResultExOne<byte[]> coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadSmartModule(module, address, length) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadSmartModule(module, address, length) : null);
            if (!coreResult.IsSuccess) {
                return coreResult;
            } else {
                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer((byte[])coreResult.Content);
                return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult(mc.ExtractActualData((byte[])read.Content, false));
            }
        }
    }

    public static OperateResultExOne<byte[]> ReadExtend(IReadWriteMc mc, short extend, String address, short length) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne(StringResources.Language.InsufficientPrivileges());
        } else {
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, false);
            if (!addressResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(addressResult);
            } else {
                byte[] coreResult = mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreExtendCommand((McAddressData)addressResult.Content, extend, false) : (mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreExtendCommand((McAddressData)addressResult.Content, extend, false) : null);
                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
                return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult(mc.ExtractActualData((byte[])read.Content, false));
            }
        }
    }

    public static OperateResult RemoteRun(IReadWriteMc mc) {
        return mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{1, 16, 0, 0, 1, 0, 0, 0}) : (mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("1001000000010000".getBytes(StandardCharsets.US_ASCII)) : new OperateResultExOne(StringResources.Language.NotSupportedFunction()));
    }

    public static OperateResult RemoteStop(IReadWriteMc mc) {
        return mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{2, 16, 0, 0, 1, 0}) : (mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("100200000001".getBytes(StandardCharsets.US_ASCII)) : new OperateResultExOne(StringResources.Language.NotSupportedFunction()));
    }

    public static OperateResult RemoteReset(IReadWriteMc mc) {
        return mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{6, 16, 0, 0, 1, 0}) : (mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("100600000001".getBytes(StandardCharsets.US_ASCII)) : new OperateResultExOne(StringResources.Language.NotSupportedFunction()));
    }

    public static OperateResultExOne<String> ReadPlcType(IReadWriteMc mc) {
        OperateResultExOne<byte[]> read = mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{1, 1, 0, 0}) : (mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("01010000".getBytes(StandardCharsets.US_ASCII)) : new OperateResultExOne(StringResources.Language.NotSupportedFunction()));
        return !read.IsSuccess ? OperateResultExOne.CreateFailedResult(read) : OperateResultExOne.CreateSuccessResult((new String((byte[])read.Content, 0, 16, StandardCharsets.US_ASCII)).trim());
    }

    public static OperateResult ErrorStateReset(IReadWriteMc mc) {
        return (OperateResult)(mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{23, 22, 0, 0}) : (mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("16170000".getBytes(StandardCharsets.US_ASCII)) : new OperateResult(StringResources.Language.NotSupportedFunction())));
    }
}
