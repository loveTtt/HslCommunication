/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Security.AesCryptography;
import HslCommunication.Core.Security.RSACryptoServiceProvider;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.MQTT.MqttConnectionOptions;
import HslCommunication.MQTT.MqttPublishMessage;
import HslCommunication.MQTT.MqttQualityOfServiceLevel;
import HslCommunication.MQTT.MqttSubscribeMessage;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class MqttHelper {
    public static OperateResultExOne<byte[]> CalculateLengthToMqttLength(int length) {
        if (length > 0xFFFFFFF) {
            return new OperateResultExOne<byte[]>(StringResources.Language.MQTTDataTooLong());
        }
        if (length < 128) {
            return OperateResultExOne.CreateSuccessResult(new byte[]{(byte)length});
        }
        if (length < 16384) {
            byte[] buffer = new byte[]{(byte)(length % 128 + 128), (byte)(length / 128)};
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        if (length < 0x200000) {
            byte[] buffer = new byte[]{(byte)(length % 128 + 128), (byte)(length / 128 % 128 + 128), (byte)(length / 128 / 128)};
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        byte[] buffer = new byte[]{(byte)(length % 128 + 128), (byte)(length / 128 % 128 + 128), (byte)(length / 128 / 128 % 128 + 128), (byte)(length / 128 / 128 / 128)};
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildMqttCommand(byte control, byte flags, byte[] variableHeader, byte[] payLoad) {
        return MqttHelper.BuildMqttCommand(control, flags, variableHeader, payLoad, null);
    }

    public static OperateResultExOne<byte[]> BuildMqttCommand(byte control, byte flags, byte[] variableHeader, byte[] payLoad, AesCryptography aesCryptography) {
        if (variableHeader == null) {
            variableHeader = new byte[]{};
        }
        if (payLoad == null) {
            payLoad = new byte[]{};
        }
        control = (byte)(control << 4);
        byte head = (byte)(control | flags);
        return MqttHelper.BuildMqttCommand(head, variableHeader, payLoad, aesCryptography);
    }

    public static OperateResultExOne<byte[]> BuildMqttCommand(byte head, byte[] variableHeader, byte[] payLoad, AesCryptography aesCryptography) {
        if (variableHeader == null) {
            variableHeader = new byte[]{};
        }
        if (payLoad == null) {
            payLoad = new byte[]{};
        }
        if (aesCryptography != null) {
            try {
                payLoad = aesCryptography.Encrypt(payLoad);
            }
            catch (Exception e) {
                return new OperateResultExOne<byte[]>("AES Encrypt failed: " + e.getMessage());
            }
        }
        OperateResultExOne<byte[]> bufferLength = MqttHelper.CalculateLengthToMqttLength(variableHeader.length + payLoad.length);
        if (!bufferLength.IsSuccess) {
            return bufferLength;
        }
        ArrayList<Byte> ms = new ArrayList<Byte>();
        ms.add(head);
        Utilities.ArrayListAddArray(ms, (byte[])bufferLength.Content);
        if (variableHeader.length > 0) {
            Utilities.ArrayListAddArray(ms, variableHeader);
        }
        if (payLoad.length > 0) {
            Utilities.ArrayListAddArray(ms, payLoad);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(ms));
    }

    public static byte[] BuildSegCommandByString(String message) {
        byte[] buffer = Utilities.IsStringNullOrEmpty(message) ? new byte[]{} : Utilities.getBytes(message, "UTF-8");
        byte[] result = new byte[buffer.length + 2];
        System.arraycopy(buffer, 0, result, 2, buffer.length);
        result[0] = (byte)(buffer.length / 256);
        result[1] = (byte)(buffer.length % 256);
        return result;
    }

    public static OperateResultExTwo<String, Integer> ExtraMsgFromBytes(byte[] buffer, int index) {
        int indexTmp = index;
        int length = (buffer[index] & 0xFF) * 256 + (buffer[index + 1] & 0xFF);
        index = index + 2 + length;
        return OperateResultExTwo.CreateSuccessResult(Utilities.getString(buffer, indexTmp + 2, length, "UTF-8"), index);
    }

    public static OperateResultExTwo<Integer, Integer> ExtraIntFromBytes(byte[] buffer, int index) {
        int length = (buffer[index] & 0xFF) * 256 + (buffer[index + 1] & 0xFF);
        return OperateResultExTwo.CreateSuccessResult(length, index += 2);
    }

    public static byte[] BuildIntBytes(int data) {
        return new byte[]{Utilities.getBytes(data)[1], Utilities.getBytes(data)[0]};
    }

    public static OperateResultExOne<byte[]> BuildConnectMqttCommand(MqttConnectionOptions connectionOptions, String protocol) {
        return MqttHelper.BuildConnectMqttCommand(connectionOptions, protocol, null);
    }

    public static OperateResultExOne<byte[]> BuildConnectMqttCommand(MqttConnectionOptions connectionOptions, String protocol, RSACryptoServiceProvider rsa) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(variableHeader, new byte[]{0, 4});
        Utilities.ArrayListAddArray(variableHeader, Utilities.getBytes(protocol, "US-ASCII"));
        variableHeader.add((byte)4);
        byte connectFlags = 0;
        if (connectionOptions.Credentials != null) {
            connectFlags = (byte)(connectFlags | 0x80);
            connectFlags = (byte)(connectFlags | 0x40);
        }
        if (connectionOptions.CleanSession) {
            connectFlags = (byte)(connectFlags | 2);
        }
        variableHeader.add(connectFlags);
        if (connectionOptions.KeepAlivePeriod < 1) {
            connectionOptions.KeepAlivePeriod = 1;
        }
        byte[] keepAlivePeriod = Utilities.getBytes(connectionOptions.KeepAlivePeriod);
        variableHeader.add(keepAlivePeriod[1]);
        variableHeader.add(keepAlivePeriod[0]);
        ArrayList<Byte> payLoad = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(payLoad, MqttHelper.BuildSegCommandByString(connectionOptions.ClientId));
        if (connectionOptions.Credentials != null) {
            Utilities.ArrayListAddArray(payLoad, MqttHelper.BuildSegCommandByString(connectionOptions.Credentials.getUserName()));
            Utilities.ArrayListAddArray(payLoad, MqttHelper.BuildSegCommandByString(connectionOptions.Credentials.getPassword()));
        }
        if (rsa == null) {
            return MqttHelper.BuildMqttCommand((byte)1, (byte)0, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
        }
        return MqttHelper.BuildMqttCommand((byte)1, (byte)0, rsa.EncryptLargeData(Utilities.getBytes(variableHeader)), rsa.EncryptLargeData(Utilities.getBytes(payLoad)));
    }

    public static OperateResult CheckConnectBack(byte code, byte[] data) {
        if (code >> 4 != 2) {
            return new OperateResult("MQTT Connection Back Is Wrong: " + code);
        }
        if (data.length < 2) {
            return new OperateResult("MQTT Connection Data Is Short: " + SoftBasic.ByteToHexString(data, ' '));
        }
        int status = (data[0] & 0xFF) * 256 + (data[1] & 0xFF);
        if (status > 0) {
            return new OperateResult(status, MqttHelper.GetMqttCodeText(status));
        }
        return OperateResult.CreateSuccessResult();
    }

    public static String GetMqttCodeText(int status) {
        switch (status) {
            case 1: {
                return StringResources.Language.MQTTStatus01();
            }
            case 2: {
                return StringResources.Language.MQTTStatus02();
            }
            case 3: {
                return StringResources.Language.MQTTStatus03();
            }
            case 4: {
                return StringResources.Language.MQTTStatus04();
            }
            case 5: {
                return StringResources.Language.MQTTStatus05();
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> BuildPublishMqttCommand(MqttPublishMessage message) {
        byte flag = 0;
        if (!message.IsSendFirstTime) {
            flag = (byte)(flag | 8);
        }
        if (message.Message.Retain) {
            flag = (byte)(flag | 1);
        }
        if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtLeastOnce) {
            flag = (byte)(flag | 2);
        } else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.ExactlyOnce) {
            flag = (byte)(flag | 4);
        } else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.OnlyTransfer) {
            flag = (byte)(flag | 6);
        }
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(variableHeader, MqttHelper.BuildSegCommandByString(message.Message.Topic));
        if (message.Message.QualityOfServiceLevel != MqttQualityOfServiceLevel.AtMostOnce) {
            variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
            variableHeader.add(Utilities.getBytes(message.Identifier)[0]);
        }
        return MqttHelper.BuildMqttCommand((byte)3, flag, Utilities.getBytes(variableHeader), message.Message.Payload);
    }

    public static OperateResultExOne<byte[]> BuildPublishMqttCommand(String topic, byte[] payload) {
        return MqttHelper.BuildMqttCommand((byte)3, (byte)0, MqttHelper.BuildSegCommandByString(topic), payload);
    }

    public static OperateResultExOne<byte[]> BuildSubscribeMqttCommand(MqttSubscribeMessage message) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        ArrayList<Byte> payLoad = new ArrayList<Byte>();
        variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
        variableHeader.add(Utilities.getBytes(message.Identifier)[0]);
        for (int i = 0; i < message.Topics.length; ++i) {
            Utilities.ArrayListAddArray(payLoad, MqttHelper.BuildSegCommandByString(message.Topics[i]));
            if (message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtMostOnce) {
                payLoad.add((byte)0);
                continue;
            }
            if (message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtLeastOnce) {
                payLoad.add((byte)1);
                continue;
            }
            payLoad.add((byte)2);
        }
        return MqttHelper.BuildMqttCommand((byte)8, (byte)2, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
    }

    public static OperateResultExOne<byte[]> BuildPublishMqttCommand(MqttPublishMessage message, AesCryptography aesCryptography) {
        int flag = 0;
        if (!message.IsSendFirstTime) {
            flag |= 8;
        }
        if (message.Message.Retain) {
            flag |= 1;
        }
        if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.AtLeastOnce) {
            flag |= 2;
        } else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.ExactlyOnce) {
            flag = (byte)(flag | 4);
        } else if (message.Message.QualityOfServiceLevel == MqttQualityOfServiceLevel.OnlyTransfer) {
            flag = (byte)(flag | 6);
        }
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        Utilities.ArrayListAddArray(variableHeader, MqttHelper.BuildSegCommandByString(message.Message.Topic));
        if (message.Message.QualityOfServiceLevel != MqttQualityOfServiceLevel.AtMostOnce) {
            variableHeader.add(BitConverter.GetBytes(message.Identifier)[1]);
            variableHeader.add(BitConverter.GetBytes(message.Identifier)[0]);
        }
        return MqttHelper.BuildMqttCommand((byte)3, (byte)flag, Utilities.getBytes(variableHeader), message.Message.Payload, aesCryptography);
    }

    public static OperateResultExOne<byte[]> BuildUnSubscribeMqttCommand(MqttSubscribeMessage message) {
        ArrayList<Byte> variableHeader = new ArrayList<Byte>();
        ArrayList<Byte> payLoad = new ArrayList<Byte>();
        variableHeader.add(Utilities.getBytes(message.Identifier)[1]);
        variableHeader.add(Utilities.getBytes(message.Identifier)[0]);
        for (int i = 0; i < message.Topics.length; ++i) {
            Utilities.ArrayListAddArray(payLoad, MqttHelper.BuildSegCommandByString(message.Topics[i]));
        }
        return MqttHelper.BuildMqttCommand((byte)10, (byte)2, Utilities.getBytes(variableHeader), Utilities.getBytes(payLoad));
    }

    public static int ExtraQosFromMqttCode(byte code) {
        return ((code & 4) == 4 ? 2 : 0) + ((code & 2) == 2 ? 1 : 0);
    }

    public static MqttQualityOfServiceLevel GetFromQos(int qos) {
        MqttQualityOfServiceLevel mqttQuality = MqttQualityOfServiceLevel.AtMostOnce;
        if (qos == 1) {
            mqttQuality = MqttQualityOfServiceLevel.AtLeastOnce;
        } else if (qos == 2) {
            mqttQuality = MqttQualityOfServiceLevel.ExactlyOnce;
        } else if (qos == 3) {
            mqttQuality = MqttQualityOfServiceLevel.OnlyTransfer;
        }
        return mqttQuality;
    }

    public static OperateResultExTwo<String, byte[]> ExtraMqttReceiveData(byte mqttCode, byte[] data) {
        return MqttHelper.ExtraMqttReceiveData(mqttCode, data, null);
    }

    public static OperateResultExTwo<String, byte[]> ExtraMqttReceiveData(byte mqttCode, byte[] data, AesCryptography aesCryptography) {
        if (data.length < 2) {
            return new OperateResultExTwo<String, byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + data.length);
        }
        int topicLength = (data[0] & 0xFF) * 256 + (data[1] & 0xFF);
        if (data.length < 2 + topicLength) {
            return new OperateResultExTwo<String, byte[]>("Code[" + mqttCode + "] Subscribe Error: " + SoftBasic.ByteToHexString(data, ' '));
        }
        String topic = topicLength > 0 ? Utilities.getString(data, 2, topicLength, "UTF-8") : "";
        byte[] payload = new byte[data.length - topicLength - 2];
        System.arraycopy(data, topicLength + 2, payload, 0, payload.length);
        if (aesCryptography != null) {
            try {
                payload = aesCryptography.Decrypt(payload);
            }
            catch (Exception ex) {
                return new OperateResultExTwo<String, byte[]>("AES Decrypt failed: " + ex.getMessage());
            }
        }
        return OperateResultExTwo.CreateSuccessResult(topic, payload);
    }
}

