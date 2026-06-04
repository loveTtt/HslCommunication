/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import HslCommunication.Core.Security.AesCryptography;
import HslCommunication.Core.Security.HslSecurity;
import HslCommunication.Core.Security.RSACryptoServiceProvider;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.MQTT.MqttConnectionOptions;
import HslCommunication.MQTT.MqttHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MqttSyncClient
extends NetworkDoubleBase {
    private SoftIncrementCount incrementCount;
    private MqttConnectionOptions connectionOptions;
    private String StringEncoding = "UTF-8";
    private RSACryptoServiceProvider cryptoServiceProvider = null;
    private AesCryptography aesCryptography = null;

    public MqttSyncClient(MqttConnectionOptions options) {
        this.setByteTransform(new RegularByteTransform());
        this.connectionOptions = options;
        this.setIpAddress(options.IpAddress);
        this.setPort(options.Port);
        this.incrementCount = new SoftIncrementCount(65536L, 1L);
        this.setConnectTimeOut(options.ConnectTimeout);
        this.setReceiveTimeOut(60000);
    }

    public MqttSyncClient(String ipAddress, int port) {
        this.connectionOptions = new MqttConnectionOptions();
        this.connectionOptions.IpAddress = ipAddress;
        this.connectionOptions.Port = port;
        this.setByteTransform(new RegularByteTransform());
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.incrementCount = new SoftIncrementCount(65536L, 1L);
        this.setReceiveTimeOut(60000);
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        RSACryptoServiceProvider rsa = null;
        if (this.connectionOptions.UseRSAProvider) {
            this.cryptoServiceProvider = new RSACryptoServiceProvider();
            OperateResult sendKey = this.Send(socket, (byte[])MqttHelper.BuildMqttCommand((byte)-1, null, (byte[])HslSecurity.ByteEncrypt((byte[])this.cryptoServiceProvider.GetPEMPublicKey()), null).Content);
            if (!sendKey.IsSuccess) {
                return sendKey;
            }
            OperateResultExTwo<Byte, byte[]> key = this.ReceiveMqttMessage(socket, this.getReceiveTimeOut(), null);
            if (!key.IsSuccess) {
                return key;
            }
            try {
                byte[] serverPublicToken = this.cryptoServiceProvider.DecryptLargeData(HslSecurity.ByteDecrypt((byte[])key.Content2));
                rsa = new RSACryptoServiceProvider(null, serverPublicToken);
            }
            catch (Exception ex) {
                this.CloseSocket(socket);
                return new OperateResult("RSA check failed: " + ex.getMessage());
            }
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildConnectMqttCommand(this.connectionOptions, "HUSL", rsa);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResult send = this.Send(socket, (byte[])command.Content);
        if (!send.IsSuccess) {
            return send;
        }
        OperateResultExTwo<Byte, byte[]> receive = this.ReceiveMqttMessage(socket, this.getReceiveTimeOut(), null);
        if (!receive.IsSuccess) {
            return receive;
        }
        OperateResult check = MqttHelper.CheckConnectBack((Byte)receive.Content1, (byte[])receive.Content2);
        if (!check.IsSuccess) {
            this.CloseSocket(socket);
            return check;
        }
        if (this.connectionOptions.UseRSAProvider) {
            try {
                String key = new String(this.cryptoServiceProvider.DecryptLargeData(SoftBasic.BytesArrayRemoveBegin((byte[])receive.Content2, 2)), StandardCharsets.UTF_8);
                this.aesCryptography = new AesCryptography(key);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.incrementCount.ResetCurrentValue();
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(Socket socket, byte[] send) {
        OperateResultExTwo<Byte, byte[]> read = this.ReadMqttFromCoreServer(socket, send, null, null, null);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(read.Content2);
    }

    private OperateResultExTwo<Byte, byte[]> ReadMqttFromCoreServer(Socket socket, byte[] send, ActionOperateExTwo<Long, Long> sendProgress, ActionOperateExTwo<String, String> handleProgress, ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExTwo<Byte, byte[]> receive;
        long total;
        long already;
        OperateResult sendResult = this.Send(socket, send);
        if (!sendResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(sendResult);
        }
        do {
            OperateResultExTwo<Byte, byte[]> server_receive = this.ReceiveMqttMessage(socket, this.getReceiveTimeOut(), null);
            if (!server_receive.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(server_receive);
            }
            OperateResultExTwo<String, byte[]> server_back = MqttHelper.ExtraMqttReceiveData((Byte)server_receive.Content1, (byte[])server_receive.Content2);
            if (!server_back.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(server_back);
            }
            if (((byte[])server_back.Content2).length != 16) {
                return new OperateResultExTwo<Byte, byte[]>(StringResources.Language.ReceiveDataLengthTooShort());
            }
            already = Utilities.getLong((byte[])server_back.Content2, 0);
            total = Utilities.getLong((byte[])server_back.Content2, 8);
            if (sendProgress == null) continue;
            sendProgress.Action(already, total);
        } while (already != total);
        while (true) {
            receive = this.ReceiveMqttMessage(socket, this.getReceiveTimeOut(), receiveProgress);
            if (!receive.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(receive);
            }
            if (((Byte)receive.Content1 & 0xF0) >> 4 != 15) break;
            OperateResultExTwo<String, byte[]> extra = MqttHelper.ExtraMqttReceiveData((Byte)receive.Content1, (byte[])receive.Content2);
            if (handleProgress == null) continue;
            handleProgress.Action((String)extra.Content1, Utilities.getString((byte[])extra.Content2, "UTF-8"));
        }
        return OperateResultExTwo.CreateSuccessResult(receive.Content1, receive.Content2);
    }

    private OperateResultExOne<byte[]> ReadMqttFromCoreServer(byte control, byte flags, byte[] variableHeader, byte[] payLoad, ActionOperateExTwo<Long, Long> sendProgress, ActionOperateExTwo<String, String> handleProgress, ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
        this.GetPipeSocket().PipeLockEnter();
        OperateResultExOne<Socket> resultSocket = this.GetAvailableSocket();
        if (!resultSocket.IsSuccess) {
            this.GetPipeSocket().setIsSocketError(true);
            if (this.AlienSession != null) {
                this.AlienSession.Offline();
            }
            this.GetPipeSocket().PipeLockLeave();
            result.CopyErrorFromOther(resultSocket);
            return result;
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildMqttCommand(control, flags, variableHeader, payLoad, this.aesCryptography);
        if (!command.IsSuccess) {
            this.GetPipeSocket().PipeLockLeave();
            result.CopyErrorFromOther(command);
            return result;
        }
        OperateResultExTwo<Byte, byte[]> read = this.ReadMqttFromCoreServer((Socket)resultSocket.Content, (byte[])command.Content, sendProgress, handleProgress, receiveProgress);
        if (read.IsSuccess) {
            this.GetPipeSocket().setIsSocketError(false);
            if (((Byte)read.Content1 & 0xF0) >> 4 == 0) {
                OperateResultExTwo<String, byte[]> extra = MqttHelper.ExtraMqttReceiveData((Byte)read.Content1, (byte[])read.Content2, this.aesCryptography);
                result.IsSuccess = false;
                result.ErrorCode = Integer.parseInt((String)extra.Content1);
                result.Message = new String((byte[])extra.Content2, StandardCharsets.UTF_8);
            } else {
                result.IsSuccess = read.IsSuccess;
                result.Content = read.Content2;
                result.Message = StringResources.Language.SuccessText();
            }
        } else {
            this.GetPipeSocket().setIsSocketError(true);
            if (this.AlienSession != null) {
                this.AlienSession.Offline();
            }
            result.CopyErrorFromOther(read);
        }
        this.ExtraAfterReadFromCoreServer(read);
        this.GetPipeSocket().PipeLockLeave();
        if (!this.isPersistentConn) {
            this.CloseSocket((Socket)resultSocket.Content);
        }
        return result;
    }

    public OperateResultExTwo<String, byte[]> Read(String topic, byte[] payload, ActionOperateExTwo<Long, Long> sendProgress, ActionOperateExTwo<String, String> handleProgress, ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExOne<byte[]> read = this.ReadMqttFromCoreServer((byte)3, (byte)0, MqttHelper.BuildSegCommandByString(topic), payload, sendProgress, handleProgress, receiveProgress);
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return MqttHelper.ExtraMqttReceiveData((byte)3, (byte[])read.Content, this.aesCryptography);
    }

    public OperateResultExTwo<String, byte[]> Read(String topic, byte[] payload) {
        return this.Read(topic, payload, null, null, null);
    }

    public OperateResultExTwo<String, String> ReadString(String topic, String payload, ActionOperateExTwo<Long, Long> sendProgress, ActionOperateExTwo<String, String> handleProgress, ActionOperateExTwo<Long, Long> receiveProgress) {
        OperateResultExTwo<String, byte[]> read = this.Read(topic, Utilities.IsStringNullOrEmpty(payload) ? null : Utilities.getBytes(payload, this.StringEncoding), sendProgress, handleProgress, receiveProgress);
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(read.Content1, Utilities.getString((byte[])read.Content2, this.StringEncoding));
    }

    public OperateResultExOne<String> ReadRpcApis() {
        OperateResultExOne<byte[]> read = this.ReadMqttFromCoreServer((byte)8, (byte)0, MqttHelper.BuildSegCommandByString(""), null, null, null, null);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExTwo<String, byte[]> mqtt = MqttHelper.ExtraMqttReceiveData((byte)3, (byte[])read.Content, this.aesCryptography);
        if (!mqtt.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(mqtt);
        }
        return OperateResultExOne.CreateSuccessResult(new String((byte[])mqtt.Content2, StandardCharsets.UTF_8));
    }

    public OperateResultExOne<String[]> ReadRetainTopics() {
        OperateResultExOne<byte[]> read = this.ReadMqttFromCoreServer((byte)4, (byte)0, MqttHelper.BuildSegCommandByString(""), null, null, null, null);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExTwo<String, byte[]> mqtt = MqttHelper.ExtraMqttReceiveData((byte)3, (byte[])read.Content, this.aesCryptography);
        if (!mqtt.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(mqtt);
        }
        return OperateResultExOne.CreateSuccessResult(HslProtocol.UnPackStringArrayFromByte((byte[])mqtt.Content2));
    }

    public MqttConnectionOptions getConnectionOptions() {
        return this.connectionOptions;
    }

    public void setConnectionOptions(MqttConnectionOptions connectionOptions) {
        this.connectionOptions = connectionOptions;
    }

    public String getStringEncoding() {
        return this.StringEncoding;
    }

    public void setStringEncoding(String stringEncoding) {
        this.StringEncoding = stringEncoding;
    }

    @Override
    public String toString() {
        return "MqttSyncClient[" + this.connectionOptions.IpAddress + ":" + this.connectionOptions.Port + "]";
    }
}

