/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftZipped;
import HslCommunication.Core.Net.NetHandle;
import HslCommunication.Core.Security.HslSecurity;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;
import java.util.UUID;

public class HslProtocol {
    public static final int HeadByteLength = 32;
    public static final int ProtocolBufferSize = 1024;
    public static final int ProtocolCheckSecends = 1;
    public static final int ProtocolClientQuit = 2;
    public static final int ProtocolClientRefuseLogin = 3;
    public static final int ProtocolClientAllowLogin = 4;
    public static final int ProtocolAccountLogin = 5;
    public static final int ProtocolAccountRejectLogin = 6;
    public static final int ProtocolUserString = 1001;
    public static final int ProtocolUserBytes = 1002;
    public static final int ProtocolUserBitmap = 1003;
    public static final int ProtocolUserException = 1004;
    public static final int ProtocolUserStringArray = 1005;
    public static final int ProtocolFileDownload = 2001;
    public static final int ProtocolFileUpload = 2002;
    public static final int ProtocolFileDelete = 2003;
    public static final int ProtocolFileCheckRight = 2004;
    public static final int ProtocolFileCheckError = 2005;
    public static final int ProtocolFileSaveError = 2006;
    public static final int ProtocolFileDirectoryFiles = 2007;
    public static final int ProtocolFileDirectories = 2008;
    public static final int ProtocolProgressReport = 2009;
    public static final int ProtocolErrorMsg = 2010;
    public static final int ProtocolNoZipped = 3001;
    public static final int ProtocolZipped = 3002;

    public static byte[] CommandBytes(int command, int customer, UUID token, byte[] data) {
        byte[] _temp = null;
        int _zipped = 3001;
        int _sendLength = 0;
        if (data == null) {
            _temp = new byte[32];
        } else {
            if ((data = HslSecurity.ByteEncrypt(data)).length > 10240) {
                data = SoftZipped.CompressBytes(data);
                _zipped = 3002;
            }
            _temp = new byte[32 + data.length];
            _sendLength = data.length;
        }
        System.arraycopy(Utilities.getBytes(command), 0, _temp, 0, 4);
        System.arraycopy(Utilities.getBytes(customer), 0, _temp, 4, 4);
        System.arraycopy(Utilities.getBytes(_zipped), 0, _temp, 8, 4);
        System.arraycopy(Utilities.UUID2Byte(token), 0, _temp, 12, 16);
        System.arraycopy(Utilities.getBytes(_sendLength), 0, _temp, 28, 4);
        if (_sendLength > 0) {
            System.arraycopy(data, 0, _temp, 32, _sendLength);
        }
        return _temp;
    }

    public static byte[] CommandAnalysis(byte[] head, byte[] content) {
        if (content != null) {
            int _zipped = Utilities.getInt(head, 8);
            if (_zipped == 3002) {
                content = SoftZipped.Decompress(content);
            }
            return HslSecurity.ByteDecrypt(content);
        }
        return null;
    }

    public static byte[] CommandBytes(int customer, UUID token, byte[] data) {
        return HslProtocol.CommandBytes(1002, customer, token, data);
    }

    public static byte[] CommandBytes(int customer, UUID token, String data) {
        if (data == null) {
            return HslProtocol.CommandBytes(1001, customer, token, null);
        }
        return HslProtocol.CommandBytes(1001, customer, token, Utilities.csharpString2Byte(data));
    }

    public static byte[] CommandBytes(int customer, UUID token, String[] data) {
        return HslProtocol.CommandBytes(1005, customer, token, HslProtocol.PackStringArrayToByte(data));
    }

    public static byte[] PackStringArrayToByte(String[] data) {
        if (data == null) {
            data = new String[]{};
        }
        byte[] buffer = Utilities.getBytes(data.length);
        for (int i = 0; i < data.length; ++i) {
            if (!Utilities.IsStringNullOrEmpty(data[i])) {
                byte[] tmp = Utilities.csharpString2Byte(data[i]);
                buffer = SoftBasic.SpliceTwoByteArray(buffer, Utilities.getBytes(tmp.length));
                buffer = SoftBasic.SpliceTwoByteArray(buffer, tmp);
                continue;
            }
            buffer = SoftBasic.SpliceTwoByteArray(buffer, Utilities.getBytes(0));
        }
        return buffer;
    }

    public static String[] UnPackStringArrayFromByte(byte[] content) {
        if (content == null) {
            return null;
        }
        if (content.length < 4) {
            return null;
        }
        int index = 0;
        int count = Utilities.getInt(content, index);
        String[] result = new String[count];
        index += 4;
        for (int i = 0; i < count; ++i) {
            int length = Utilities.getInt(content, index);
            result[i] = length > 0 ? Utilities.byte2CSharpString(content, index += 4, length) : "";
            index += length;
        }
        return result;
    }

    public static OperateResultExTwo<NetHandle, byte[]> ExtractHslData(byte[] content) {
        if (content.length == 0) {
            return OperateResultExTwo.CreateSuccessResult(new NetHandle(0), new byte[0]);
        }
        byte[] headBytes = new byte[32];
        byte[] contentBytes = new byte[content.length - 32];
        System.arraycopy(content, 0, headBytes, 0, 32);
        if (contentBytes.length > 0) {
            System.arraycopy(content, 32, contentBytes, 0, content.length - 32);
        }
        if (Utilities.getInt(headBytes, 0) == 2010) {
            return new OperateResultExTwo<NetHandle, byte[]>(Utilities.byte2CSharpString(contentBytes));
        }
        int code = Utilities.getInt(headBytes, 0);
        int customer = Utilities.getInt(headBytes, 4);
        contentBytes = HslProtocol.CommandAnalysis(headBytes, contentBytes);
        if (code == 6) {
            return new OperateResultExTwo<NetHandle, byte[]>(Utilities.byte2CSharpString(contentBytes));
        }
        return OperateResultExTwo.CreateSuccessResult(new NetHandle(customer), contentBytes);
    }
}

