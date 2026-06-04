/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Beckhoff.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class AdsHelper {
    public static byte[] BuildAmsHeaderCommand(int commandId, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        byte[] buffer = new byte[32 + data.length];
        buffer[16] = BitConverter.GetBytes(commandId)[0];
        buffer[17] = BitConverter.GetBytes(commandId)[1];
        buffer[18] = 4;
        buffer[19] = 0;
        buffer[20] = BitConverter.GetBytes(data.length)[0];
        buffer[21] = BitConverter.GetBytes(data.length)[1];
        buffer[22] = BitConverter.GetBytes(data.length)[2];
        buffer[23] = BitConverter.GetBytes(data.length)[3];
        buffer[24] = 0;
        buffer[25] = 0;
        buffer[26] = 0;
        buffer[27] = 0;
        Utilities.ByteArrayCopyTo(data, buffer, 32);
        return AdsHelper.PackAmsTcpHelper(0, buffer);
    }

    public static OperateResultExOne<byte[]> BuildReadDeviceInfoCommand() {
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(1, null));
    }

    public static OperateResultExOne<byte[]> BuildReadStateCommand() {
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(4, null));
    }

    public static OperateResultExOne<byte[]> BuildWriteControlCommand(short state, short deviceState, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        byte[] buffer = new byte[8 + data.length];
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(5, SoftBasic.SpliceArray(BitConverter.GetBytes(state), BitConverter.GetBytes(deviceState), BitConverter.GetBytes(data.length), data)));
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, int length, boolean isBit) {
        OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] data = new byte[12];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), data, 8);
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(2, data));
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String[] address, short[] length) {
        byte[] data = new byte[12 * address.length];
        int lenCount = 0;
        for (int i = 0; i < address.length; ++i) {
            OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address[i], false);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content1), data, 12 * i + 0);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content2), data, 12 * i + 4);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((int)length[i]), data, 12 * i + 8);
            lenCount += length[i];
        }
        return AdsHelper.BuildReadWriteCommand("ig=0xF080;0", lenCount, false, data);
    }

    public static OperateResultExOne<byte[]> BuildReadWriteCommand(String address, int length, boolean isBit, byte[] value) {
        OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] data = new byte[16 + value.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), data, 8);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), data, 12);
        Utilities.ByteArrayCopyTo(value, data, 16);
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(9, data));
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String[] address, ArrayList<byte[]> value) {
        MemoryStream ms = new MemoryStream();
        int lenCount = 0;
        for (int i = 0; i < address.length; ++i) {
            OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address[i], false);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            ms.Write(BitConverter.GetBytes((Integer)analysis.Content1));
            ms.Write(BitConverter.GetBytes((Integer)analysis.Content2));
            ms.Write(BitConverter.GetBytes(value.get(i).length));
            ms.Write(value.get(i));
            lenCount += value.get(i).length;
        }
        return AdsHelper.BuildReadWriteCommand("ig=0xF081;0", lenCount, false, ms.ToArray());
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value, boolean isBit) {
        OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] data = new byte[12 + value.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), data, 8);
        Utilities.ByteArrayCopyTo(value, data, 12);
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(3, data));
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean[] value, boolean isBit) {
        OperateResultExTwo<Integer, Integer> analysis = AdsHelper.AnalysisAddress(address, isBit);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        byte[] buffer = Array.GetByteFromBoolArray(value);
        byte[] data = new byte[12 + buffer.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content1), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((Integer)analysis.Content2), data, 4);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(buffer.length), data, 8);
        Utilities.ByteArrayCopyTo(buffer, data, 12);
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(3, data));
    }

    public static OperateResultExOne<byte[]> BuildReleaseSystemHandle(int handle) {
        byte[] data = new byte[16];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(61446), data, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(4), data, 8);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(handle), data, 12);
        return OperateResultExOne.CreateSuccessResult(AdsHelper.BuildAmsHeaderCommand(3, data));
    }

    public static OperateResultExOne<Integer> CheckResponse(byte[] response) {
        try {
            int status;
            int ams = BitConverter.ToInt32(response, 30);
            if (ams > 0) {
                return new OperateResultExOne<Integer>(ams, AdsHelper.GetErrorCodeText(ams) + "\r\nSource:" + SoftBasic.ByteToHexString(response, ' '));
            }
            if (response.length >= 42 && (status = BitConverter.ToInt32(response, 38)) != 0) {
                return new OperateResultExOne<Integer>(status, AdsHelper.GetErrorCodeText(status) + "\r\nSource:" + SoftBasic.ByteToHexString(response, ' '));
            }
        }
        catch (Exception ex) {
            return new OperateResultExOne<Integer>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(response, ' '));
        }
        return OperateResultExOne.CreateSuccessResult(0);
    }

    public static byte[] PackAmsTcpHelper(int headerFlags, byte[] command) {
        byte[] buffer = new byte[6 + command.length];
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)headerFlags), buffer, 0);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(command.length), buffer, 2);
        Utilities.ByteArrayCopyTo(command, buffer, 6);
        return buffer;
    }

    private static int CalculateAddressStarted(String address) {
        if (address.indexOf(46) < 0) {
            return Convert.ToInt32(address);
        }
        String[] temp = Utilities.SplitDot(address);
        return Convert.ToInt32(temp[0]) * 8 + HslHelper.CalculateBitStartIndex(temp[1]);
    }

    public static OperateResultExTwo<Integer, Integer> AnalysisAddress(String address, boolean isBit) {
        OperateResultExTwo<Integer, Integer> result = new OperateResultExTwo<Integer, Integer>();
        try {
            if (address.startsWith("i=") || address.startsWith("I=")) {
                result.Content1 = 61445;
                result.Content2 = (int)Long.parseLong(address.substring(2));
            } else if (address.startsWith("s=") || address.startsWith("S=")) {
                result.Content1 = 61443;
                result.Content2 = 0;
            } else if (address.startsWith("ig=") || address.startsWith("IG=")) {
                OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "ig", 0);
                if (extra.IsSuccess) {
                    address = (String)extra.Content2;
                    result.Content1 = extra.Content1;
                }
                result.Content2 = (int)Long.parseLong(address);
            } else {
                switch (address.charAt(0)) {
                    case 'M': 
                    case 'm': {
                        if (isBit) {
                            result.Content1 = 16417;
                            result.Content2 = AdsHelper.CalculateAddressStarted(address.substring(1));
                            break;
                        }
                        result.Content1 = 16416;
                        result.Content2 = (int)Long.parseLong(address.substring(1));
                        break;
                    }
                    case 'I': 
                    case 'i': {
                        if (isBit) {
                            result.Content1 = 61473;
                            result.Content2 = AdsHelper.CalculateAddressStarted(address.substring(1)) + 1024000;
                            break;
                        }
                        result.Content1 = 61472;
                        result.Content2 = (int)Long.parseLong(address.substring(1)) + 128000;
                        break;
                    }
                    case 'Q': 
                    case 'q': {
                        if (isBit) {
                            result.Content1 = 61489;
                            result.Content2 = AdsHelper.CalculateAddressStarted(address.substring(1)) + 2048000;
                            break;
                        }
                        result.Content1 = 61488;
                        result.Content2 = (int)(Long.parseLong(address.substring(1)) + 256000L);
                        break;
                    }
                    default: {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                }
            }
        }
        catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }
        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();
        return result;
    }

    public static byte[] StrToAdsBytes(String value) {
        return SoftBasic.SpliceArray(Encoding.ASCII.GetBytes(value), new byte[1]);
    }

    public static byte[] StrToAMSNetId(String amsNetId) {
        byte[] buffer;
        String ip = amsNetId;
        if (amsNetId.indexOf(58) > 0) {
            buffer = new byte[8];
            String[] ipPort = amsNetId.split(":");
            ip = ipPort[0];
            buffer[6] = BitConverter.GetBytes(Integer.parseInt(ipPort[1]))[0];
            buffer[7] = BitConverter.GetBytes(Integer.parseInt(ipPort[1]))[1];
        } else {
            buffer = new byte[6];
        }
        String[] ips = Utilities.SplitDot(ip);
        for (int i = 0; i < ips.length; ++i) {
            buffer[i] = (byte)Integer.parseInt(ips[i]);
        }
        return buffer;
    }

    public static String GetAmsNetIdString(byte[] data, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append(data[index] & 0xFF);
        sb.append(".");
        sb.append(data[index + 1] & 0xFF);
        sb.append(".");
        sb.append(data[index + 2] & 0xFF);
        sb.append(".");
        sb.append(data[index + 3] & 0xFF);
        sb.append(".");
        sb.append(data[index + 4] & 0xFF);
        sb.append(".");
        sb.append(data[index + 5] & 0xFF);
        sb.append(":");
        sb.append(BitConverter.ToUInt16(data, index + 6));
        return sb.toString();
    }

    public static String GetErrorCodeText(int error) {
        switch (error) {
            case 0: {
                return "NO ERROR";
            }
            case 1: {
                return "InternalError";
            }
            case 2: {
                return "NO RTIME";
            }
            case 3: {
                return "Allocation locked \u2013 memory error.";
            }
            case 4: {
                return "Mailbox full \u2013 the ADS message could not be sent. Reducing the number of ADS messages per cycle will help.";
            }
            case 5: {
                return "WRONG RECEIVEH MSG";
            }
            case 6: {
                return "Target port not found \u2013 ADS server is not started or is not reachable.";
            }
            case 7: {
                return "Target computer not found \u2013 AMS route was not found.";
            }
            case 8: {
                return "Unknown command ID.";
            }
            case 9: {
                return "Invalid task ID.";
            }
            case 10: {
                return "No IO.";
            }
            case 11: {
                return "Unknown AMS command.";
            }
            case 12: {
                return "Win32 error.";
            }
            case 13: {
                return "Port not connected.";
            }
            case 14: {
                return "Invalid AMS length.";
            }
            case 15: {
                return "Invalid AMS Net ID.";
            }
            case 16: {
                return "Installation level is too low \u2013TwinCAT 2 license error.";
            }
            case 17: {
                return "No debugging available.";
            }
            case 18: {
                return "Port disabled \u2013 TwinCAT system service not started.";
            }
            case 19: {
                return "Port already connected.";
            }
            case 20: {
                return "AMS Sync Win32 error.";
            }
            case 21: {
                return "AMS Sync Timeout.";
            }
            case 22: {
                return "AMS Sync error.";
            }
            case 23: {
                return "No index map for AMS Sync available.";
            }
            case 24: {
                return "Invalid AMS port.";
            }
            case 25: {
                return "No memory.";
            }
            case 26: {
                return "TCP send error.";
            }
            case 27: {
                return "Host unreachable.";
            }
            case 28: {
                return "Invalid AMS fragment.";
            }
            case 29: {
                return "TLS send error \u2013 secure ADS connection failed.";
            }
            case 30: {
                return "Access denied \u2013 secure ADS access denied.";
            }
            case 1280: {
                return "Locked memory cannot be allocated.";
            }
            case 1281: {
                return "The router memory size could not be changed.";
            }
            case 1282: {
                return "The mailbox has reached the maximum number of possible messages.";
            }
            case 1283: {
                return "The Debug mailbox has reached the maximum number of possible messages.";
            }
            case 1284: {
                return "The port type is unknown.";
            }
            case 1285: {
                return "The router is not initialized.";
            }
            case 1286: {
                return "The port number is already assigned.";
            }
            case 1287: {
                return "The port is not registered.";
            }
            case 1288: {
                return "The maximum number of ports has been reached.";
            }
            case 1289: {
                return "The port is invalid.";
            }
            case 1290: {
                return "The router is not active.";
            }
            case 1291: {
                return "The mailbox has reached the maximum number for fragmented messages.";
            }
            case 1292: {
                return "A fragment timeout has occurred.";
            }
            case 1293: {
                return "The port is removed.";
            }
            case 1792: {
                return "General device error.";
            }
            case 1793: {
                return "Service is not supported by the server.";
            }
            case 1794: {
                return "Invalid index group.";
            }
            case 1795: {
                return "Invalid index offset.";
            }
            case 1796: {
                return "Reading or writing not permitted.";
            }
            case 1797: {
                return "Parameter size not correct. Commonly found in batch processing, check the calculation command length";
            }
            case 1798: {
                return "Invalid data values.";
            }
            case 1799: {
                return "Device is not ready to operate. It is possible that the TSM configuration is incorrect, reactivate the configuration";
            }
            case 1800: {
                return "Device Busy";
            }
            case 1801: {
                return "Invalid operating system context. This can result from use of ADS blocks in different tasks. It may be possible to resolve this through multitasking synchronization in the PLC.";
            }
            case 1802: {
                return "Insufficient memory.";
            }
            case 1803: {
                return "Invalid parameter values.";
            }
            case 1804: {
                return "Device Not Found";
            }
            case 1805: {
                return "Device Syntax Error";
            }
            case 1806: {
                return "Objects do not match.";
            }
            case 1807: {
                return "Object already exists.";
            }
            case 1808: {
                return "Symbol not found. Check whether the variable name is correct, Note: the global variables in some PLC equipment are: .[Variable Name]";
            }
            case 1809: {
                return "Invalid symbol version. This can occur due to an online change. Create a new handle.";
            }
            case 1810: {
                return "Device (server) is in invalid state.";
            }
            case 1811: {
                return "AdsTransMode not supported.";
            }
            case 1812: {
                return "Device Notify Handle Invalid";
            }
            case 1813: {
                return "Notification client not registered.";
            }
            case 1814: {
                return "Device No More Handles";
            }
            case 1815: {
                return "Device Invalid Watch size";
            }
            case 1816: {
                return "Device Not Initialized";
            }
            case 1817: {
                return "Device TimeOut";
            }
            case 1818: {
                return "Device No Interface";
            }
            case 1819: {
                return "Device Invalid Interface";
            }
            case 1820: {
                return "Device Invalid CLSID";
            }
            case 1821: {
                return "Device Invalid Object ID";
            }
            case 1822: {
                return "Device Request Is Pending";
            }
            case 1823: {
                return "Device Request Is Aborted";
            }
            case 1824: {
                return "Device Signal Warning";
            }
            case 1825: {
                return "Device Invalid Array Index";
            }
            case 1826: {
                return "Device Symbol Not Active";
            }
            case 1827: {
                return "Device Access Denied";
            }
            case 1828: {
                return "Device Missing License";
            }
            case 1829: {
                return "Device License Expired";
            }
            case 1830: {
                return "Device License Exceeded";
            }
            case 1831: {
                return "Device License Invalid";
            }
            case 1832: {
                return "Device License System Id";
            }
            case 1833: {
                return "Device License No Time Limit";
            }
            case 1834: {
                return "Device License Future Issue";
            }
            case 1835: {
                return "Device License Time To Long";
            }
            case 1836: {
                return "Device Exception During Startup";
            }
            case 1837: {
                return "Device License Duplicated";
            }
            case 1838: {
                return "Device Signature Invalid";
            }
            case 1839: {
                return "Device Certificate Invalid";
            }
            case 1840: {
                return "Device License Oem Not Found";
            }
            case 1841: {
                return "Device License Restricted";
            }
            case 1842: {
                return "Device License Demo Denied";
            }
            case 1843: {
                return "Device Invalid Function Id";
            }
            case 1844: {
                return "Device Out Of Range";
            }
            case 1845: {
                return "Device Invalid Alignment";
            }
            case 1846: {
                return "Device License Platform";
            }
            case 1847: {
                return "Device Context Forward Passive Level";
            }
            case 1848: {
                return "Device Context Forward Dispatch Level";
            }
            case 1849: {
                return "Device Context Forward RealTime";
            }
            case 1850: {
                return "Device Certificate Entrust";
            }
            case 1856: {
                return "ClientError";
            }
            case 1857: {
                return "Client Invalid Parameter";
            }
            case 1858: {
                return "Client List Empty";
            }
            case 1859: {
                return "Client Variable In Use";
            }
            case 1860: {
                return "Client Duplicate InvokeID";
            }
            case 1861: {
                return "Timeout has occurred \u2013 the remote terminal is not responding in the specified ADS timeout. The route setting of the remote terminal may be configured incorrectly.";
            }
            case 1862: {
                return "ClientW32OR";
            }
            case 1863: {
                return "Client Timeout Invalid";
            }
            case 1864: {
                return "Client Port Not Open";
            }
            case 1865: {
                return "Client No Ams Addr";
            }
            case 1872: {
                return "Client Sync Internal";
            }
            case 1873: {
                return "Client Add Hash";
            }
            case 1874: {
                return "Client Remove Hash";
            }
            case 1875: {
                return "Client No More Symbols";
            }
            case 1876: {
                return "Client Response Invalid";
            }
            case 1877: {
                return "Client Port Locked";
            }
            case 32768: {
                return "ClientQueueFull";
            }
            case 10060: {
                return "A connection timeout has occurred - error while establishing the connection, because the remote terminal did not respond properly after a certain period of time";
            }
            case 10061: {
                return "WSA_ConnRefused";
            }
            case 10065: {
                return "No route to host - a socket operation referred to an unavailable host.";
            }
        }
        return StringResources.Language.UnknownError();
    }
}

