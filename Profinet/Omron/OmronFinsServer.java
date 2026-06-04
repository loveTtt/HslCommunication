/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftBuffer;
import HslCommunication.Core.Address.OmronFinsAddress;
import HslCommunication.Core.IMessage.FinsMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDataServerBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Omron.OmronFinsDataType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class OmronFinsServer
extends NetworkDataServerBase {
    protected SoftBuffer dBuffer = new SoftBuffer(131072);
    protected SoftBuffer cioBuffer = new SoftBuffer(131072);
    protected SoftBuffer wBuffer = new SoftBuffer(131072);
    protected SoftBuffer hBuffer = new SoftBuffer(131072);
    protected SoftBuffer arBuffer = new SoftBuffer(131072);
    protected SoftBuffer emBuffer = new SoftBuffer(131072);
    protected SoftBuffer cfBuffer = new SoftBuffer(131072);
    protected SoftBuffer irBuffer = new SoftBuffer(131072);
    protected SoftBuffer drBuffer = new SoftBuffer(131072);
    protected boolean connectionInitialization = true;
    private final int DataPoolLength = 65536;

    public OmronFinsServer() {
        this.dBuffer.setIsBoolReverseByWord(true);
        this.cioBuffer.setIsBoolReverseByWord(true);
        this.wBuffer.setIsBoolReverseByWord(true);
        this.hBuffer.setIsBoolReverseByWord(true);
        this.arBuffer.setIsBoolReverseByWord(true);
        this.emBuffer.setIsBoolReverseByWord(true);
        this.cfBuffer.setIsBoolReverseByWord(true);
        this.irBuffer.setIsBoolReverseByWord(true);
        this.drBuffer.setIsBoolReverseByWord(true);
        this.WordLength = 1;
        this.setByteTransform(new ReverseWordTransform(DataFormat.CDAB));
    }

    public DataFormat getDataFormat() {
        return this.getByteTransform().getDataFormat();
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.getByteTransform().setDataFormat(dataFormat);
    }

    private OperateResultExTwo<SoftBuffer, OmronFinsAddress> GetWordAddressBuffer(String address) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(analysis);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == OmronFinsDataType.DM.getWordCode()) {
            return OperateResultExTwo.CreateSuccessResult(this.dBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == OmronFinsDataType.CIO.getWordCode()) {
            return OperateResultExTwo.CreateSuccessResult(this.cioBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == OmronFinsDataType.WR.getWordCode()) {
            return OperateResultExTwo.CreateSuccessResult(this.wBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == OmronFinsDataType.HR.getWordCode()) {
            return OperateResultExTwo.CreateSuccessResult(this.hBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == OmronFinsDataType.AR.getWordCode()) {
            return OperateResultExTwo.CreateSuccessResult(this.arBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == -68) {
            return OperateResultExTwo.CreateSuccessResult(this.drBuffer, analysis.Content);
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == -36) {
            return OperateResultExTwo.CreateSuccessResult(this.irBuffer, analysis.Content);
        }
        if (address.startsWith("E")) {
            return OperateResultExTwo.CreateSuccessResult(this.emBuffer, analysis.Content);
        }
        return new OperateResultExTwo<SoftBuffer, OmronFinsAddress>(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<SoftBuffer, OmronFinsAddress> analysis = this.GetWordAddressBuffer(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        return OperateResultExOne.CreateSuccessResult(((SoftBuffer)analysis.Content1).GetBytes(((OmronFinsAddress)analysis.Content2).getAddressStart() / 16 * 2, length * 2));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExTwo<SoftBuffer, OmronFinsAddress> analysis = this.GetWordAddressBuffer(address);
        if (!analysis.IsSuccess) {
            return analysis;
        }
        ((SoftBuffer)analysis.Content1).SetBytes(value, ((OmronFinsAddress)analysis.Content2).getAddressStart() / 16 * 2);
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.DM.getBitCode()) {
            return OperateResultExOne.CreateSuccessResult(this.dBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.CIO.getBitCode()) {
            return OperateResultExOne.CreateSuccessResult(this.cioBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.WR.getBitCode()) {
            return OperateResultExOne.CreateSuccessResult(this.wBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.HR.getBitCode()) {
            return OperateResultExOne.CreateSuccessResult(this.hBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.AR.getBitCode()) {
            return OperateResultExOne.CreateSuccessResult(this.arBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == 7) {
            return OperateResultExOne.CreateSuccessResult(this.cfBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == -68) {
            return OperateResultExOne.CreateSuccessResult(this.drBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        if (((OmronFinsAddress)analysis.Content).getWordCode() == -36) {
            return OperateResultExOne.CreateSuccessResult(this.irBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
        }
        return OperateResultExOne.CreateSuccessResult(this.emBuffer.GetBool(((OmronFinsAddress)analysis.Content).getAddressStart(), length));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.DM.getBitCode()) {
            this.dBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.CIO.getBitCode()) {
            this.cioBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.WR.getBitCode()) {
            this.wBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.HR.getBitCode()) {
            this.hBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getBitCode() == OmronFinsDataType.AR.getBitCode()) {
            this.arBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getBitCode() == 7) {
            this.cfBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getWordCode() == -68) {
            this.drBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else if (((OmronFinsAddress)analysis.Content).getWordCode() == -36) {
            this.irBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        } else {
            this.emBuffer.SetBool(value, ((OmronFinsAddress)analysis.Content).getAddressStart());
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FinsMessage();
    }

    @Override
    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        if (this.connectionInitialization) {
            OperateResultExOne<byte[]> read1 = this.ReceiveByMessage(socket, 5000, this.GetNewNetMessage());
            if (!read1.IsSuccess) {
                return;
            }
            byte[] sendBuffer1 = SoftBasic.HexStringToBytes("46 49 4E 53 00 00 00 10 00 00 00 01 00 00 00 00 00 00 00 01 00 00 00 02");
            OperateResult send1 = this.Send(socket, sendBuffer1);
            if (!send1.IsSuccess) {
                return;
            }
        }
        super.ThreadPoolLogin(socket, endPoint);
    }

    @Override
    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        try {
            byte[] read = this.ReadFromFinsCore(SoftBasic.BytesArrayRemoveBegin(receive, 26));
            if (receive != null && receive.length > 25) {
                read[20] = receive[23];
                read[23] = receive[20];
                read[25] = receive[25];
            }
            return OperateResultExOne.CreateSuccessResult(read);
        }
        catch (Exception e) {
            return new OperateResultExOne<byte[]>(e.getMessage());
        }
    }

    protected byte[] ReadFromFinsCore(byte[] finsCore) throws Exception {
        if (finsCore.length == 0) {
            return null;
        }
        if (finsCore[0] == 1 && finsCore[1] == 1) {
            byte[] read = this.ReadByCommand(finsCore);
            return this.PackCommand(read == null ? 2 : 0, finsCore, read);
        }
        if (finsCore[0] == 1 && finsCore[1] == 2) {
            if (!this.EnableWrite) {
                return this.PackCommand(3, finsCore, null);
            }
            return this.PackCommand(0, finsCore, this.WriteByMessage(finsCore));
        }
        if (finsCore[0] == 1 && finsCore[1] == 4) {
            byte[] read = this.ReadByMultiCommand(finsCore);
            return this.PackCommand(read == null ? 2 : 0, finsCore, read);
        }
        if (finsCore[0] == 4 && finsCore[1] == 1) {
            return this.PackCommand(0, finsCore, null);
        }
        if (finsCore[0] == 4 && finsCore[1] == 2) {
            return this.PackCommand(0, finsCore, null);
        }
        if (finsCore[0] == 5 && finsCore[1] == 1) {
            return this.PackCommand(0, finsCore, SoftBasic.HexStringToBytes("43 4A 32 4D 2D 43 50 55 33 31 20 20 20 20 20 20 20 20 20 20 30 32 2E 30 31 00 00 00 00 00 30 32 2E 31 30 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 80 01 80 01 80 01 80 00 00 00 00 00 00 00 00 02 01 00 00 0A 17 80 00 08 01 00 00 00 00 00"));
        }
        if (finsCore[0] == 6 && finsCore[1] == 1) {
            return this.PackCommand(0, finsCore, SoftBasic.HexStringToBytes("05 02 00 00 00 00 00 00 00 00 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20"));
        }
        if (finsCore[0] == 7 && finsCore[1] == 1) {
            return this.PackCommand(0, finsCore, SoftBasic.HexStringToBytes(this.getDateString()));
        }
        return this.PackCommand(3, finsCore, null);
    }

    private String getDateString() {
        Date date = new Date();
        String str = Utilities.getStringDateShort(date, "yy-MM-dd-HH-mm-ss");
        str = str.replace("-", "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(7);
        return str + String.format("%02d", dayOfWeek);
    }

    protected byte[] PackCommand(int status, byte[] finsCore, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        byte[] back = new byte[30 + data.length];
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("46 49 4E 53 00 00 00 0000 00 00 02 00 00 00 00C0 00 02 00 EF 00 00 33 00 00 00 00 00 00"), back, 0);
        if (data.length > 0) {
            Utilities.ByteArrayCopyTo(data, back, 30);
        }
        back[26] = finsCore[0];
        back[27] = finsCore[1];
        Utilities.ByteArrayCopyTo(Utilities.ReverseNew(BitConverter.GetBytes(back.length - 8)), back, 4);
        Utilities.ByteArrayCopyTo(Utilities.ReverseNew(BitConverter.GetBytes(status)), back, 12);
        return back;
    }

    private SoftBuffer GetSoftBuffer(byte code, int startIndex) throws Exception {
        if (code == OmronFinsDataType.DM.getBitCode()) {
            return this.dBuffer;
        }
        if (code == OmronFinsDataType.DM.getWordCode()) {
            return this.dBuffer;
        }
        if (code == OmronFinsDataType.CIO.getBitCode()) {
            return this.cioBuffer;
        }
        if (code == OmronFinsDataType.CIO.getWordCode()) {
            return this.cioBuffer;
        }
        if (code == OmronFinsDataType.WR.getBitCode()) {
            return this.wBuffer;
        }
        if (code == OmronFinsDataType.WR.getWordCode()) {
            return this.wBuffer;
        }
        if (code == OmronFinsDataType.HR.getBitCode()) {
            return this.hBuffer;
        }
        if (code == OmronFinsDataType.HR.getWordCode()) {
            return this.hBuffer;
        }
        if (code == OmronFinsDataType.AR.getBitCode()) {
            return this.arBuffer;
        }
        if (code == OmronFinsDataType.AR.getWordCode()) {
            return this.arBuffer;
        }
        if (code == 7) {
            return this.cfBuffer;
        }
        if (code == -68) {
            return this.drBuffer;
        }
        if (code == -36) {
            return this.irBuffer;
        }
        if (code == 0) {
            return startIndex >= 45056 ? this.arBuffer : this.cioBuffer;
        }
        if (code == -128) {
            return startIndex >= 45056 ? this.arBuffer : this.cioBuffer;
        }
        if (32 <= code && code < 48 || 208 <= (code & 0xFF) && (code & 0xFF) < 224) {
            return this.emBuffer;
        }
        if (160 <= (code & 0xFF) && (code & 0xFF) < 176 || 80 <= code && code < 96) {
            return this.emBuffer;
        }
        if (144 <= (code & 0xFF) && (code & 0xFF) < 153) {
            return this.emBuffer;
        }
        if (code == 10) {
            return this.emBuffer;
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }

    private byte[] ReadByCommand(byte[] command) throws Exception {
        if (command[2] == OmronFinsDataType.DM.getBitCode() || command[2] == OmronFinsDataType.CIO.getBitCode() || command[2] == OmronFinsDataType.WR.getBitCode() || command[2] == OmronFinsDataType.HR.getBitCode() || command[2] == OmronFinsDataType.AR.getBitCode() || command[2] == 0 || command[2] == 7 || command[2] == 10 || 32 <= command[2] && command[2] < 48 || 208 <= (command[2] & 0xFF) && (command[2] & 0xFF) < 224) {
            int length = (command[6] & 0xFF) * 256 + (command[7] & 0xFF);
            int startIndex = ((command[3] & 0xFF) * 256 + (command[4] & 0xFF)) * 16 + command[5];
            SoftBuffer softBuffer = this.GetSoftBuffer(command[2], startIndex);
            if ((command[2] == 0 || (command[2] & 0xFF) == 128) && startIndex >= 45056) {
                startIndex -= 45056;
            }
            return Utilities.ToByteArray(softBuffer.GetBool(startIndex, length), (byte)1, (byte)0);
        }
        if (command[2] == OmronFinsDataType.DM.getWordCode() || command[2] == OmronFinsDataType.CIO.getWordCode() || command[2] == OmronFinsDataType.WR.getWordCode() || command[2] == OmronFinsDataType.HR.getWordCode() || command[2] == OmronFinsDataType.AR.getWordCode() || command[2] == -128 || command[2] == -68 || command[2] == -36 || command[2] == -104 || 160 <= (command[2] & 0xFF) && (command[2] & 0xFF) < 176 || 80 <= command[2] && command[2] < 96) {
            int length = (command[6] & 0xFF) * 256 + (command[7] & 0xFF);
            int startIndex = (command[3] & 0xFF) * 256 + (command[4] & 0xFF);
            if (length > 999) {
                return null;
            }
            SoftBuffer softBuffer = this.GetSoftBuffer(command[2], startIndex);
            if ((command[2] == 0 || command[2] == -128) && startIndex >= 45056) {
                startIndex -= 45056;
            }
            return softBuffer.GetBytes(startIndex * 2, length * 2);
        }
        return new byte[0];
    }

    private byte[] ReadByMultiCommand(byte[] command) throws Exception {
        MemoryStream ms = new MemoryStream();
        for (int index = 2; index < command.length; index += 4) {
            int startIndex = (command[index + 1] & 0xFF) * 256 + (command[index + 2] & 0xFF);
            if (command[index] != OmronFinsDataType.DM.getWordCode() && command[index] != OmronFinsDataType.CIO.getWordCode() && command[index] != OmronFinsDataType.WR.getWordCode() && command[index] != OmronFinsDataType.HR.getWordCode() && command[index] != OmronFinsDataType.AR.getWordCode() && command[index] != -128 && command[index] != -68 && command[index] != -36 && command[index] != -104 && (160 > (command[index] & 0xFF) || (command[index] & 0xFF) >= 176) && (80 > command[index] || command[index] >= 96)) continue;
            ms.WriteByte(command[index]);
            SoftBuffer softBuffer = this.GetSoftBuffer(command[index], startIndex);
            if ((command[2] == 0 || command[2] == -128) && startIndex >= 45056) {
                startIndex -= 45056;
            }
            ms.Write(softBuffer.GetBytes(startIndex * 2, 2));
        }
        return ms.ToArray();
    }

    private byte[] WriteByMessage(byte[] command) throws Exception {
        if (command[2] == OmronFinsDataType.DM.getBitCode() || command[2] == OmronFinsDataType.CIO.getBitCode() || command[2] == OmronFinsDataType.WR.getBitCode() || command[2] == OmronFinsDataType.HR.getBitCode() || command[2] == OmronFinsDataType.AR.getBitCode() || command[2] == 0 || command[2] == 7 || 32 <= command[2] && command[2] < 48 || 208 <= (command[2] & 0xFF) && (command[2] & 0xFF) < 224) {
            int length = (command[6] & 0xFF) * 256 + (command[7] & 0xFF);
            int startIndex = ((command[3] & 0xFF) * 256 + (command[4] & 0xFF)) * 16 + (command[5] & 0xFF);
            boolean[] buffer = Utilities.getBoolArray(SoftBasic.BytesArrayRemoveBegin(command, 8), (byte)0);
            SoftBuffer softBuffer = this.GetSoftBuffer(command[2], startIndex);
            if ((command[2] == 0 || (command[2] & 0xFF) == 128) && startIndex >= 45056) {
                startIndex -= 45056;
            }
            softBuffer.SetBool(buffer, startIndex);
            return new byte[0];
        }
        int length = (command[6] & 0xFF) * 256 + (command[7] & 0xFF);
        int startIndex = (command[3] & 0xFF) * 256 + (command[4] & 0xFF);
        byte[] buffer = SoftBasic.BytesArrayRemoveBegin(command, 8);
        SoftBuffer softBuffer = this.GetSoftBuffer(command[2], startIndex);
        if ((command[2] == 0 || command[2] == -128) && startIndex >= 45056) {
            startIndex -= 45056;
        }
        softBuffer.SetBytes(buffer, startIndex * 2);
        return new byte[0];
    }

    @Override
    protected void LoadFromBytes(byte[] content) {
        this.dBuffer.SetBytes(content, 0, 0, 131072);
        this.cioBuffer.SetBytes(content, 131072, 0, 131072);
        this.wBuffer.SetBytes(content, 262144, 0, 131072);
        this.hBuffer.SetBytes(content, 393216, 0, 131072);
        this.arBuffer.SetBytes(content, 524288, 0, 131072);
        this.emBuffer.SetBytes(content, 655360, 0, 131072);
        if (content.length >= 917504) {
            this.cfBuffer.SetBytes(content, 786432, 0, 131072);
        }
    }

    @Override
    protected byte[] SaveToBytes() {
        byte[] buffer = new byte[917504];
        Array.Copy(this.dBuffer.GetBytes(), 0, buffer, 0, 131072);
        Array.Copy(this.cioBuffer.GetBytes(), 0, buffer, 131072, 131072);
        Array.Copy(this.wBuffer.GetBytes(), 0, buffer, 262144, 131072);
        Array.Copy(this.hBuffer.GetBytes(), 0, buffer, 393216, 131072);
        Array.Copy(this.arBuffer.GetBytes(), 0, buffer, 524288, 131072);
        Array.Copy(this.emBuffer.GetBytes(), 0, buffer, 655360, 131072);
        Array.Copy(this.cfBuffer.GetBytes(), 0, buffer, 786432, 131072);
        return buffer;
    }

    @Override
    protected void Dispose(boolean disposing) {
        if (disposing) {
            // empty if block
        }
        super.Dispose(disposing);
    }

    @Override
    public String toString() {
        return "OmronFinsServer[" + this.getPort() + "]";
    }
}

