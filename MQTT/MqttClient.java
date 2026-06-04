/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Net.NetSupport;
import HslCommunication.Core.Net.NetworkBase.NetworkXBase;
import HslCommunication.Core.Security.AesCryptography;
import HslCommunication.Core.Security.HslSecurity;
import HslCommunication.Core.Security.RSACryptoServiceProvider;
import HslCommunication.Core.Types.ActionOperateExOne;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.MQTT.MqttApplicationMessage;
import HslCommunication.MQTT.MqttConnectionOptions;
import HslCommunication.MQTT.MqttHelper;
import HslCommunication.MQTT.MqttPublishMessage;
import HslCommunication.MQTT.MqttQualityOfServiceLevel;
import HslCommunication.MQTT.MqttSubscribeMessage;
import HslCommunication.Utilities;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MqttClient
extends NetworkXBase {
    public ActionOperateExTwo<MqttClient, MqttApplicationMessage> OnMqttMessageReceived;
    public ActionOperateExOne<MqttClient> OnNetworkError;
    public ActionOperateExOne<MqttClient> OnClientConnected = null;
    private Date activeTime;
    private int isReConnectServer = 0;
    private List<MqttPublishMessage> publishMessages;
    private Object listLock;
    private final Object connectLock;
    private final Object subscribeLock;
    private SoftIncrementCount incrementCount;
    private boolean closed = false;
    private MqttConnectionOptions connectionOptions;
    private Timer timerCheck;
    private boolean disposedValue;
    private RSACryptoServiceProvider cryptoServiceProvider = null;
    private AesCryptography aesCryptography = null;
    private List<String> subscribeTopics = new List();
    private boolean isConnected = false;
    public boolean UseTimerCheckDropped = true;
    public Object Tag = null;

    public MqttClient(MqttConnectionOptions options) {
        this.connectionOptions = options;
        this.incrementCount = new SoftIncrementCount(65535L, 1L);
        this.listLock = new Object();
        this.publishMessages = new List();
        this.activeTime = new Date();
        this.subscribeLock = new Object();
        this.connectLock = new Object();
    }

    public OperateResult ConnectServer() {
        if (this.connectionOptions == null) {
            return new OperateResult("Options is null");
        }
        OperateResultExOne<Socket> connect = this.CreateSocketAndConnect(this.connectionOptions.IpAddress, this.connectionOptions.Port, this.connectionOptions.ConnectTimeout);
        if (!connect.IsSuccess) {
            return connect;
        }
        RSACryptoServiceProvider rsa = null;
        if (this.connectionOptions.UseRSAProvider) {
            this.cryptoServiceProvider = new RSACryptoServiceProvider();
            OperateResult sendKey = this.Send((Socket)connect.Content, (byte[])MqttHelper.BuildMqttCommand((byte)-1, null, (byte[])HslSecurity.ByteEncrypt((byte[])this.cryptoServiceProvider.GetPEMPublicKey()), null).Content);
            if (!sendKey.IsSuccess) {
                return sendKey;
            }
            OperateResultExTwo<Byte, byte[]> key = this.ReceiveMqttMessage((Socket)connect.Content, 10000, null);
            if (!key.IsSuccess) {
                return key;
            }
            try {
                byte[] serverPublicToken = this.cryptoServiceProvider.DecryptLargeData(HslSecurity.ByteDecrypt((byte[])key.Content2));
                rsa = new RSACryptoServiceProvider(null, serverPublicToken);
            }
            catch (Exception ex) {
                NetSupport.CloseSocket((Socket)connect.Content);
                return new OperateResult("RSA check failed: " + ex.getMessage());
            }
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildConnectMqttCommand(this.connectionOptions, "MQTT", rsa);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResult send = this.Send((Socket)connect.Content, (byte[])command.Content);
        if (!send.IsSuccess) {
            return send;
        }
        OperateResultExTwo<Byte, byte[]> receive = this.ReceiveMqttMessage((Socket)connect.Content, 30000, null);
        if (!receive.IsSuccess) {
            return receive;
        }
        OperateResult check = MqttHelper.CheckConnectBack((Byte)receive.Content1, (byte[])receive.Content2);
        if (!check.IsSuccess) {
            NetSupport.CloseSocket((Socket)connect.Content);
            return check;
        }
        if (this.connectionOptions.UseRSAProvider) {
            try {
                String key = Encoding.UTF8.GetString(this.cryptoServiceProvider.DecryptLargeData(SoftBasic.BytesArrayRemoveBegin((byte[])receive.Content2, 2)));
                this.aesCryptography = new AesCryptography(key);
            }
            catch (Exception ex) {
                NetSupport.CloseSocket((Socket)connect.Content);
                return new OperateResult("RSA check failed: " + ex.getMessage());
            }
        }
        this.incrementCount.ResetCurrentValue();
        this.closed = false;
        NetSupport.CloseSocket(this.CoreSocket);
        this.CoreSocket = (Socket)connect.Content;
        this.isConnected = true;
        if (connect.Content != null) {
            new Thread(new MultiThreadServer((Socket)connect.Content)).start();
        }
        if (this.OnClientConnected != null) {
            this.OnClientConnected.Action(this);
        }
        if (this.timerCheck != null) {
            this.timerCheck.cancel();
        }
        this.activeTime = new Date();
        if (this.UseTimerCheckDropped && this.connectionOptions.KeepAliveSendInterval > 0) {
            this.timerCheck = new Timer();
            TimerTask task = new TimerTask(){

                @Override
                public void run() {
                    if (MqttClient.this.CoreSocket != null) {
                        if (Utilities.calculateDifferenceInSeconds(new Date(), MqttClient.this.activeTime) > (long)((MqttClient)MqttClient.this).connectionOptions.KeepAliveSendInterval * 3L) {
                            if (MqttClient.this.LogNet != null) {
                                MqttClient.this.LogNet.WriteError("Mqtt client check time 90s failed");
                            }
                            MqttClient.this.OnMqttNetworkError();
                        } else if (!((MqttClient)MqttClient.this).SendMqttBytes((byte[])((byte[])MqttHelper.BuildMqttCommand((byte)12, (byte)0, (byte[])new byte[0], (byte[])new byte[0]).Content)).IsSuccess) {
                            MqttClient.this.OnMqttNetworkError();
                        }
                    }
                }
            };
            this.timerCheck.scheduleAtFixedRate(task, 2000L, (long)this.connectionOptions.KeepAliveSendInterval * 1000L);
        }
        return OperateResult.CreateSuccessResult();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ConnectClose() {
        Object object = this.connectLock;
        synchronized (object) {
            this.closed = true;
            this.isConnected = false;
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildMqttCommand((byte)14, (byte)0, null, null);
        if (command.IsSuccess) {
            this.SendMqttBytes((byte[])command.Content);
        }
        if (this.timerCheck != null) {
            this.timerCheck.cancel();
        }
        HslHelper.ThreadSleep(20);
        NetSupport.CloseSocket(this.CoreSocket);
    }

    public OperateResult PublishMessage(MqttApplicationMessage message) {
        MqttPublishMessage publishMessage = new MqttPublishMessage();
        publishMessage.Identifier = message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtMostOnce ? 0 : (int)this.incrementCount.GetCurrentValue();
        publishMessage.Message = message;
        OperateResultExOne<byte[]> command = MqttHelper.BuildPublishMqttCommand(publishMessage, this.aesCryptography);
        if (!command.IsSuccess) {
            return command;
        }
        if (message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtMostOnce) {
            return this.SendMqttBytes((byte[])command.Content);
        }
        this.AddPublishMessage(publishMessage);
        return this.SendMqttBytes((byte[])command.Content);
    }

    public OperateResult SubscribeMessage(String topic) {
        return this.SubscribeMessage(new String[]{topic});
    }

    public OperateResult SubscribeMessage(String[] topics) {
        MqttSubscribeMessage subcribeMessage = new MqttSubscribeMessage();
        subcribeMessage.Identifier = (int)this.incrementCount.GetCurrentValue();
        subcribeMessage.Topics = topics;
        return this.SubscribeMessage(subcribeMessage);
    }

    public OperateResult SubscribeMessage(MqttSubscribeMessage subcribeMessage) {
        if (subcribeMessage.Topics == null) {
            return OperateResult.CreateSuccessResult();
        }
        if (subcribeMessage.Topics.length == 0) {
            return OperateResult.CreateSuccessResult();
        }
        OperateResultExOne<byte[]> command = MqttHelper.BuildSubscribeMqttCommand(subcribeMessage);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResult send = this.SendMqttBytes((byte[])command.Content);
        if (!send.IsSuccess) {
            return send;
        }
        this.AddSubTopics(subcribeMessage.Topics);
        return OperateResult.CreateSuccessResult();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void AddSubTopics(String[] topics) {
        Object object = this.subscribeLock;
        synchronized (object) {
            for (int i = 0; i < topics.length; ++i) {
                if (this.subscribeTopics.contains(topics[i])) continue;
                this.subscribeTopics.Add(topics[i]);
            }
        }
    }

    public OperateResult UnSubscribeMessage(String[] topics) {
        MqttSubscribeMessage subcribeMessage = new MqttSubscribeMessage();
        subcribeMessage.Identifier = (int)this.incrementCount.GetCurrentValue();
        subcribeMessage.Topics = topics;
        OperateResultExOne<byte[]> command = MqttHelper.BuildUnSubscribeMqttCommand(subcribeMessage);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResult send = this.SendMqttBytes((byte[])command.Content);
        if (!send.IsSuccess) {
            return send;
        }
        this.RemoveSubTopics(topics);
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult UnSubscribeMessage(String topic) {
        return this.UnSubscribeMessage(new String[]{topic});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean RemoveSubTopics(String[] topics) {
        boolean remove = true;
        Object object = this.subscribeLock;
        synchronized (object) {
            for (int i = 0; i < topics.length; ++i) {
                this.subscribeTopics.remove(topics[i]);
            }
        }
        return remove;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void OnMqttNetworkError() {
        if (this.closed) {
            if (this.LogNet != null) {
                this.LogNet.WriteDebug(this.toString(), "Closed");
            }
            return;
        }
        if (this.isReConnectServer == 0) {
            this.isReConnectServer = 1;
            try {
                block23: {
                    this.isConnected = false;
                    if (this.timerCheck != null) {
                        this.timerCheck.cancel();
                    }
                    this.timerCheck = null;
                    if (this.OnNetworkError == null) {
                        if (this.LogNet != null) {
                            this.LogNet.WriteInfo(this.toString(), "The network is abnormal, and the system is ready to automatically reconnect after 10 seconds.");
                        }
                        while (true) {
                            for (int i = 0; i < 10; ++i) {
                                HslHelper.ThreadSleep(1000);
                                if (this.LogNet != null) {
                                    this.LogNet.WriteInfo(this.toString(), "Wait for " + (10 - i) + " second to connect to the server ...");
                                }
                                if (!this.closed) continue;
                                if (this.LogNet != null) {
                                    this.LogNet.WriteDebug(this.toString(), "Closed");
                                }
                                this.isReConnectServer = 0;
                                return;
                            }
                            Object i = this.connectLock;
                            synchronized (i) {
                                if (this.closed) {
                                    if (this.LogNet != null) {
                                        this.LogNet.WriteDebug(this.toString(), "Closed");
                                    }
                                    this.isReConnectServer = 0;
                                    return;
                                }
                                OperateResult connect = this.ConnectServer();
                                if (connect.IsSuccess) {
                                    if (this.LogNet != null) {
                                        this.LogNet.WriteInfo(this.toString(), "Successfully connected to the server!");
                                    }
                                    break block23;
                                }
                                if (this.LogNet != null) {
                                    this.LogNet.WriteInfo(this.toString(), "The connection failed. Prepare to reconnect after 10 seconds.");
                                }
                                if (this.closed) {
                                    if (this.LogNet != null) {
                                        this.LogNet.WriteDebug(this.toString(), "Closed");
                                    }
                                    this.isReConnectServer = 0;
                                    return;
                                }
                            }
                        }
                    }
                    if (this.OnNetworkError != null) {
                        this.OnNetworkError.Action(this);
                    }
                }
                this.isReConnectServer = 0;
            }
            catch (Exception e) {
                this.isReConnectServer = 0;
                throw e;
            }
        }
    }

    private void LogDebug(int mqttCode, String message, byte[] data) {
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), "Code[" + String.format("%02X", mqttCode) + "] " + message + ": " + SoftBasic.ByteToHexString(data, ' '));
        }
    }

    private void ExtraPublishData(byte mqttCode, byte[] data) {
        this.activeTime = new Date();
        OperateResultExTwo<String, byte[]> extra = MqttHelper.ExtraMqttReceiveData(mqttCode, data, this.aesCryptography);
        if (!extra.IsSuccess) {
            if (this.LogNet != null) {
                this.LogNet.WriteDebug(this.toString(), extra.Message);
            }
            return;
        }
        int qos = MqttHelper.ExtraQosFromMqttCode(mqttCode);
        MqttApplicationMessage message = new MqttApplicationMessage();
        message.Topic = (String)extra.Content1;
        message.Retain = (mqttCode & 1) == 1;
        message.QualityOfServiceLevel = MqttHelper.GetFromQos(qos);
        message.Payload = (byte[])extra.Content2;
        if (this.OnMqttMessageReceived != null) {
            this.OnMqttMessageReceived.Action(this, message);
        }
    }

    private void AddPublishMessage(MqttPublishMessage publishMessage) {
    }

    protected void Dispose(boolean disposing) {
        if (!this.disposedValue) {
            if (disposing) {
                if (this.timerCheck != null) {
                    this.timerCheck.cancel();
                }
                this.OnClientConnected = null;
                this.OnMqttMessageReceived = null;
                this.OnNetworkError = null;
            }
            this.disposedValue = true;
        }
    }

    private OperateResult SendMqttBytes(byte[] data) {
        return this.Send(this.CoreSocket, data);
    }

    public MqttConnectionOptions ConnectionOptions() {
        return this.connectionOptions;
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] SubcribeTopics() {
        Object object = this.subscribeLock;
        synchronized (object) {
            return this.subscribeTopics.toStringArray();
        }
    }

    @Override
    public String toString() {
        return "MqttClient[" + this.connectionOptions.IpAddress + ":" + this.connectionOptions.Port + "]";
    }

    private class MultiThreadServer
    implements Runnable {
        private Socket socket;

        MultiThreadServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                OperateResultExTwo read = MqttClient.this.ReceiveMqttMessage(this.socket, -1, null);
                if (!read.IsSuccess) {
                    if (MqttClient.this.LogNet != null) {
                        MqttClient.this.LogNet.WriteDebug(this.toString(), "ReceiveMqttMessage Failed:" + read.Message);
                    }
                    MqttClient.this.OnMqttNetworkError();
                    return;
                }
                byte mqttCode = (Byte)read.Content1;
                byte[] data = (byte[])read.Content2;
                int code = (mqttCode & 0xFF) >> 4;
                if (code == 4) {
                    MqttClient.this.LogDebug(code, "Publish Ack", data);
                    continue;
                }
                if (code == 5) {
                    MqttClient.this.SendMqttBytes((byte[])MqttHelper.BuildMqttCommand((byte)6, (byte)2, (byte[])data, (byte[])new byte[0]).Content);
                    MqttClient.this.LogDebug(code, "Publish Rec", data);
                    continue;
                }
                if (code == 7) {
                    MqttClient.this.LogDebug(code, "Publish Complete", data);
                    continue;
                }
                if (code == 13) {
                    MqttClient.this.activeTime = new Date();
                    MqttClient.this.LogDebug(code, "Heart Code Check!", null);
                    continue;
                }
                if (code == 9) {
                    MqttClient.this.LogDebug(code, "Subscribe Ack", data);
                    continue;
                }
                if (code == 11) {
                    MqttClient.this.LogDebug(code, "UnSubscribe Ack", data);
                    continue;
                }
                if (code == 3) {
                    MqttClient.this.ExtraPublishData(mqttCode, data);
                    continue;
                }
                MqttClient.this.LogDebug(code, "", data);
            }
        }
    }
}

