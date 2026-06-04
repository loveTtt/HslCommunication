/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.OpenProtocol;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.OpenProtocolMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class OpenProtocolNet
extends NetworkDoubleBase {
    public int RevisonOnConnected = 1;
    public boolean AutoAckControllerMessage = false;
    private Timer timer;
    private int revisonOnConnected = 0;

    public OpenProtocolNet() {
        this.revisonOnConnected = 1;
        this.timer = new Timer();
        TimerTask task = new TimerTask(){

            @Override
            public void run() {
                if (!OpenProtocolNet.this.GetPipeSocket().IsConnectitonError()) {
                    OperateResultExOne<byte[]> command = OpenProtocolNet.BuildReadCommand(9999, 1, -1, -1, null);
                    if (!command.IsSuccess) {
                        return;
                    }
                    OpenProtocolNet.this.Send(OpenProtocolNet.this.GetPipeSocket().getSocket(), (byte[])command.Content);
                }
            }
        };
        this.LogMsgFormatBinary = false;
        this.setUseServerActivePush(true);
    }

    public OpenProtocolNet(String ipAddress) {
        this(ipAddress, 4545);
    }

    public OpenProtocolNet(String ipAddress, int port) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new OpenProtocolMessage();
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        if (this.revisonOnConnected >= 0) {
            OperateResultExOne<byte[]> command = OpenProtocolNet.BuildReadCommand(1, this.revisonOnConnected, -1, -1, null);
            if (!command.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(command);
            }
            OperateResult send = this.Send(socket, (byte[])command.Content);
            if (!send.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(send);
            }
            OperateResultExOne<byte[]> receive = this.ReceiveByMessage(socket, this.getReceiveTimeOut(), this.GetNewNetMessage());
            if (!receive.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(receive);
            }
            String reply = Encoding.ASCII.GetString((byte[])receive.Content);
            if (reply.substring(4, 8).equals("0002")) {
                return super.InitializationOnConnect(socket);
            }
            return new OperateResult("Failed:" + reply.substring(4, 8));
        }
        return super.InitializationOnConnect(socket);
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        OperateResultExOne<byte[]> command = OpenProtocolNet.BuildReadCommand(3, 1, -1, -1, null);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        return this.ReadFromCoreServer(socket, (byte[])command.Content, true, true);
    }

    private int DecideSubscribeData(int mid) {
        if (mid == 15 || mid == 35 || mid == 52 || mid == 61 || mid == 71 || mid == 74 || mid == 76 || mid == 91 || mid == 101) {
            return mid + 1;
        }
        if (mid == 106 || mid == 107) {
            return 108;
        }
        if (mid == 121 || mid == 122 || mid == 123 || mid == 124) {
            return 125;
        }
        if (mid == 152) {
            return 153;
        }
        if (mid == 211) {
            return 212;
        }
        if (mid == 217) {
            return 218;
        }
        if (mid == 221) {
            return 222;
        }
        if (mid == 242) {
            return 243;
        }
        if (mid == 251) {
            return 252;
        }
        if (mid == 401) {
            return 402;
        }
        if (mid == 421) {
            return 422;
        }
        return -1;
    }

    @Override
    protected boolean DecideWhetherQAMessage(Socket socket, OperateResultExOne<byte[]> receive) {
        if (((byte[])receive.Content).length >= 20) {
            boolean ack;
            int mid = Convert.ToInt32(Encoding.ASCII.GetString((byte[])receive.Content, 4, 4));
            boolean bl = ack = ((byte[])receive.Content)[11] == 48;
            if (mid == 9999) {
                return false;
            }
            int id = this.DecideSubscribeData(mid);
            if (id > 0) {
                if (ack || this.AutoAckControllerMessage) {
                    this.Send(socket, (byte[])OpenProtocolNet.BuildReadCommand((int)id, (int)1, (int)-1, (int)-1, null).Content);
                }
                this.OnReceivedOpenMessageMethod(this, Encoding.ASCII.GetString((byte[])receive.Content).trim());
                return false;
            }
        }
        return super.DecideWhetherQAMessage(socket, receive);
    }

    public void OnReceivedOpenMessageMethod(OpenProtocolNet openProtocolNet, String content) {
    }

    public OperateResultExOne<String> ReadCustomer(int mid, int revison, int stationId, int spindleId, String[] parameters) {
        if (parameters == null) {
            parameters = new String[]{};
        }
        OperateResultExOne<byte[]> command = OpenProtocolNet.BuildReadCommand(mid, revison, stationId, spindleId, parameters);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResult check = OpenProtocolNet.CheckRequestReplyMessages((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetString((byte[])read.Content));
    }

    @Override
    public String toString() {
        return "OpenProtocolNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(int mid, int revison, int stationId, int spindleId, String[] parameters) {
        if (mid < 0 || mid > 9999) {
            return new OperateResultExOne<byte[]>("Mid must be between 0 - 9999");
        }
        if (revison < 0 || revison > 999) {
            return new OperateResultExOne<byte[]>("revison must be between 0 - 999");
        }
        if (stationId > 9) {
            return new OperateResultExOne<byte[]>("stationId must be between 0 - 9");
        }
        if (spindleId > 99) {
            return new OperateResultExOne<byte[]>("spindleId must be between 0 - 99");
        }
        int count = 0;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                count += parameters[i].length();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%04d", 20 + count));
        sb.append(String.format("%04d", mid));
        sb.append(String.format("%03d", revison));
        sb.append('0');
        sb.append(stationId < 0 ? "  " : String.format("%02d", stationId));
        sb.append(spindleId < 0 ? "  " : String.format("%02d", spindleId));
        sb.append(' ');
        sb.append(' ');
        sb.append(' ');
        sb.append(' ');
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                sb.append(parameters[i]);
            }
        }
        sb.append('\u0000');
        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetBytes(sb.toString()));
    }

    public static OperateResultExOne<byte[]> BuildOpenProtocolMessage(int mid, int revison, int ack, int stationId, int spindleId, boolean withIndex, String ... parameters) {
        if (mid < 0 || mid > 9999) {
            return new OperateResultExOne<byte[]>("Mid must be between 0 - 9999");
        }
        if (revison < 0 || revison > 999) {
            return new OperateResultExOne<byte[]>("revison must be between 0 - 999");
        }
        if (stationId > 9) {
            return new OperateResultExOne<byte[]>("stationId must be between 0 - 9");
        }
        if (spindleId > 99) {
            return new OperateResultExOne<byte[]>("spindleId must be between 0 - 99");
        }
        int count = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%04d", mid));
        sb.append(String.format("%03d", revison));
        sb.append(ack < 0 ? " " : String.format("%1d", ack));
        sb.append(stationId < 0 ? "  " : String.format("%02d", stationId));
        sb.append(spindleId < 0 ? "  " : String.format("%02d", spindleId));
        sb.append(' ');
        sb.append(' ');
        sb.append(' ');
        sb.append(' ');
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                if (withIndex) {
                    sb.append(String.format("%02d", i + 1));
                    sb.append(parameters[i]);
                    count += 2 + parameters[i].length();
                    continue;
                }
                sb.append(parameters[i]);
                count += parameters[i].length();
            }
        }
        sb.append('\u0000');
        sb.insert(0, String.format("%04d", 20 + count));
        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetBytes(sb.toString()));
    }

    public static String GetErrorText(int code) {
        switch (code) {
            case 1: {
                return "Invalid data";
            }
            case 2: {
                return "Parameter set ID not present";
            }
            case 3: {
                return "Parameter set can not be set.";
            }
            case 4: {
                return "Parameter set not running";
            }
            case 6: {
                return "VIN upload subscription already exists";
            }
            case 7: {
                return "VIN upload subscription does not exists";
            }
            case 8: {
                return "VIN input source not granted";
            }
            case 9: {
                return "Last tightening result subscription already exists";
            }
            case 10: {
                return "Last tightening result subscription does not exist";
            }
            case 11: {
                return "Alarm subscription already exists";
            }
            case 12: {
                return "Alarm subscription does not exist";
            }
            case 13: {
                return "Parameter set selection subscription already exists";
            }
            case 14: {
                return "Parameter set selection subscription does not exist";
            }
            case 15: {
                return "Tightening ID requested not found";
            }
            case 16: {
                return "Connection rejected protocol busy";
            }
            case 17: {
                return "Job ID not present";
            }
            case 18: {
                return "Job info subscription already exists";
            }
            case 19: {
                return "Job info subscription does not exist";
            }
            case 20: {
                return "Job can not be set";
            }
            case 21: {
                return "Job not running";
            }
            case 22: {
                return "Not possible to execute dynamic Job request";
            }
            case 23: {
                return "Job batch decrement failed";
            }
            case 30: {
                return "Controller is not a sync Master/station controller";
            }
            case 31: {
                return "Multi-spindle status subscription already exists";
            }
            case 32: {
                return "Multi-spindle status subscription does not exist";
            }
            case 33: {
                return "Multi-spindle result subscription already exists";
            }
            case 34: {
                return "Multi-spindle result subscription does not exist";
            }
            case 40: {
                return "Job line control info subscription already exists";
            }
            case 41: {
                return "Job line control info subscription does not exist";
            }
            case 42: {
                return "Identifier input source not granted";
            }
            case 43: {
                return "Multiple identifiers work order subscription already exists";
            }
            case 44: {
                return "Multiple identifiers work order subscription does not exist";
            }
            case 50: {
                return "Status external monitored inputs subscription already exists";
            }
            case 51: {
                return "Status external monitored inputs subscription does not exist";
            }
            case 52: {
                return "IO device not connected";
            }
            case 53: {
                return "Faulty IO device ID";
            }
            case 58: {
                return "No alarm present";
            }
            case 59: {
                return "Tool currently in use";
            }
            case 60: {
                return "No histogram available";
            }
            case 70: {
                return "Calibration failed";
            }
            case 79: {
                return "Command failed";
            }
            case 80: {
                return "Audi emergency status subscription exists";
            }
            case 81: {
                return "Audi emergency status subscription does not exist";
            }
            case 82: {
                return "Automatic/Manual mode subscribe already exist";
            }
            case 83: {
                return "Automatic/Manual mode subscribe does not exist";
            }
            case 84: {
                return "The relay function subscription already exists";
            }
            case 85: {
                return "The relay function subscription does not exist";
            }
            case 86: {
                return "The selector socket info subscription already exist";
            }
            case 87: {
                return "The selector socket info subscription does not exist";
            }
            case 88: {
                return "The digin info subscription already exist";
            }
            case 89: {
                return "The digin info subscription does not exist";
            }
            case 90: {
                return "Lock at bach done subscription already exist";
            }
            case 91: {
                return "Lock at bach done subscription does not exist";
            }
            case 92: {
                return "Open protocol commands disabled";
            }
            case 93: {
                return "Open protocol commands disabled subscription already exists";
            }
            case 94: {
                return "Open protocol commands disabled subscription does not exist";
            }
            case 95: {
                return "Reject request, PowerMACS is in manual mode";
            }
            case 96: {
                return "Client already connected";
            }
            case 97: {
                return "MID revision unsupported";
            }
            case 98: {
                return "Controller internal request timeout";
            }
            case 99: {
                return "Unknown MID";
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResult CheckRequestReplyMessages(byte[] reply) {
        try {
            if (Encoding.ASCII.GetString(reply, 4, 4).equals("0004")) {
                String mid = Encoding.ASCII.GetString(reply, 20, 4);
                int code = Convert.ToInt32(Encoding.ASCII.GetString(reply, 24, 2));
                if (code == 0) {
                    return OperateResult.CreateSuccessResult();
                }
                return new OperateResult(code, "The request MID " + mid + " Select parameter set failed: " + OpenProtocolNet.GetErrorText(code));
            }
            return OperateResult.CreateSuccessResult();
        }
        catch (Exception e) {
            return new OperateResult(e.getMessage());
        }
    }
}

