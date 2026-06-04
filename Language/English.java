/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Language;

import HslCommunication.Language.DefaultLanguage;

public class English
extends DefaultLanguage {
    @Override
    public String TimeDescriptionSecond() {
        return " Second";
    }

    @Override
    public String TimeDescriptionMinute() {
        return " Minute";
    }

    @Override
    public String TimeDescriptionHour() {
        return " Hour";
    }

    @Override
    public String TimeDescriptionDay() {
        return " Day";
    }

    @Override
    public String AuthorizationFailed() {
        return "System authorization failed, need to use activation code authorization, thank you for your support.";
    }

    @Override
    public String InsufficientPrivileges() {
        return "The current method interface or class is only open to commercial authorized users, and the permissions are insufficient. Thank you for your support. If you need commercial authorization, please contact QQ200962190, WeChat: 13516702732, Email: hsl200909@163.com";
    }

    @Override
    public String ConnectedFailed() {
        return "Connected Failed: ";
    }

    @Override
    public String ConnectedSuccess() {
        return "Connect Success!";
    }

    @Override
    public String UnknownError() {
        return "Unknown Error";
    }

    @Override
    public String ErrorCode() {
        return "Error Code: ";
    }

    @Override
    public String TextDescription() {
        return "Description: ";
    }

    @Override
    public String ExceptionMessage() {
        return "Exception Info: ";
    }

    @Override
    public String ExceptionSource() {
        return "Exception Source\uff1a";
    }

    @Override
    public String ExceptionType() {
        return "Exception Type\uff1a";
    }

    @Override
    public String ExceptionStackTrace() {
        return "Exception Stack: ";
    }

    @Override
    public String ExceptionTargetSite() {
        return "Exception Method: ";
    }

    @Override
    public String ExceptionCustomer() {
        return "Error in user-defined method: ";
    }

    @Override
    public String SuccessText() {
        return "Success";
    }

    @Override
    public String TwoParametersLengthIsNotSame() {
        return "Two Parameter Length is not same";
    }

    @Override
    public String NotSupportedDataType() {
        return "Unsupported DataType, input again";
    }

    @Override
    public String NotSupportedFunction() {
        return "The current feature logic does not support";
    }

    @Override
    public String DataLengthIsNotEnough() {
        return "Receive length is not enough\uff0cShould:{0},Actual:{1}";
    }

    @Override
    public String ReceiveDataTimeout() {
        return "Receive timeout: ";
    }

    @Override
    public String ReceiveDataLengthTooShort() {
        return "Receive length is too short: ";
    }

    @Override
    public String MessageTip() {
        return "Message prompt:";
    }

    @Override
    public String Close() {
        return "Close";
    }

    @Override
    public String Time() {
        return "Time:";
    }

    @Override
    public String SoftWare() {
        return "Software:";
    }

    @Override
    public String BugSubmit() {
        return "Bug submit";
    }

    @Override
    public String MailServerCenter() {
        return "Mail Center System";
    }

    @Override
    public String MailSendTail() {
        return "Mail Service system issued automatically, do not reply";
    }

    @Override
    public String IpAddressError() {
        return "IP address input exception, format is incorrect";
    }

    @Override
    public String Send() {
        return "Send";
    }

    @Override
    public String Receive() {
        return "Recv";
    }

    @Override
    public String AddressOffsetEven() {
        return "The address offset must be even";
    }

    @Override
    public String SystemInstallOperater() {
        return "Install new software: ip address is";
    }

    @Override
    public String SystemUpdateOperater() {
        return "Update software: ip address is";
    }

    @Override
    public String SocketIOException() {
        return "Socket transport error: ";
    }

    @Override
    public String SocketSendException() {
        return "Synchronous Data Send exception: ";
    }

    @Override
    public String SocketHeadReceiveException() {
        return "Command header receive exception: ";
    }

    @Override
    public String SocketContentReceiveException() {
        return "Content Data Receive exception: ";
    }

    @Override
    public String SocketContentRemoteReceiveException() {
        return "Recipient content Data Receive exception: ";
    }

    @Override
    public String SocketAcceptCallbackException() {
        return "Asynchronously accepts an incoming connection attempt: ";
    }

    @Override
    public String SocketReAcceptCallbackException() {
        return "To re-accept incoming connection attempts asynchronously";
    }

    @Override
    public String SocketSendAsyncException() {
        return "Asynchronous Data send Error: ";
    }

    @Override
    public String SocketEndSendException() {
        return "Asynchronous data end callback send Error";
    }

    @Override
    public String SocketReceiveException() {
        return "Asynchronous Data send Error: ";
    }

    @Override
    public String SocketEndReceiveException() {
        return "Asynchronous data end receive instruction header error";
    }

    @Override
    public String SocketRemoteCloseException() {
        return "An existing connection was forcibly closed by the remote host";
    }

    @Override
    public String FileDownloadSuccess() {
        return "File Download Successful";
    }

    @Override
    public String FileDownloadFailed() {
        return "File Download exception";
    }

    @Override
    public String FileUploadFailed() {
        return "File Upload exception";
    }

    @Override
    public String FileUploadSuccess() {
        return "File Upload Successful";
    }

    @Override
    public String FileDeleteFailed() {
        return "File Delete exception";
    }

    @Override
    public String FileDeleteSuccess() {
        return "File deletion succeeded";
    }

    @Override
    public String FileReceiveFailed() {
        return "Confirm File Receive exception";
    }

    @Override
    public String FileNotExist() {
        return "File does not exist";
    }

    @Override
    public String FileSaveFailed() {
        return "File Store failed";
    }

    @Override
    public String FileLoadFailed() {
        return "File load failed";
    }

    @Override
    public String FileSendClientFailed() {
        return "An exception occurred when the file was sent";
    }

    @Override
    public String FileWriteToNetFailed() {
        return "File Write Network exception";
    }

    @Override
    public String FileReadFromNetFailed() {
        return "Read file exceptions from the network";
    }

    @Override
    public String FilePathCreateFailed() {
        return "Folder path creation failed: ";
    }

    @Override
    public String FileRemoteNotExist() {
        return "The other file does not exist, cannot receive!";
    }

    @Override
    public String TokenCheckFailed() {
        return "Receive authentication token inconsistency";
    }

    @Override
    public String TokenCheckTimeout() {
        return "Receive authentication timeout: ";
    }

    @Override
    public String CommandHeadCodeCheckFailed() {
        return "Command header check failed";
    }

    @Override
    public String CommandLengthCheckFailed() {
        return "Command length check failed";
    }

    @Override
    public String NetClientAliasFailed() {
        return "Client's alias receive failed: ";
    }

    @Override
    public String NetClientAccountTimeout() {
        return "Wait for account check timeout\uff1a";
    }

    @Override
    public String NetEngineStart() {
        return "Start engine";
    }

    @Override
    public String NetEngineClose() {
        return "Shutting down the engine";
    }

    @Override
    public String NetClientOnline() {
        return "Online";
    }

    @Override
    public String NetClientOffline() {
        return "Offline";
    }

    @Override
    public String NetClientBreak() {
        return "Abnormal offline";
    }

    @Override
    public String NetClientFull() {
        return "The server hosts the upper limit and receives an exceeded request connection.";
    }

    @Override
    public String NetClientLoginFailed() {
        return "Error in Client logon: ";
    }

    @Override
    public String NetHeartCheckFailed() {
        return "Heartbeat Validation exception: ";
    }

    @Override
    public String NetHeartCheckTimeout() {
        return "Heartbeat verification timeout, force offline: ";
    }

    @Override
    public String DataSourceFormatError() {
        return "Data source format is incorrect";
    }

    @Override
    public String ServerFileCheckFailed() {
        return "Server confirmed file failed, please re-upload";
    }

    @Override
    public String ClientOnlineInfo() {
        return "Client [ {0} ] Online";
    }

    @Override
    public String ClientOfflineInfo() {
        return "Client [ {0} ] Offline";
    }

    @Override
    public String ClientDisableLogin() {
        return "Client [ {0} ] is not trusted, login forbidden";
    }

    @Override
    public String ReConnectServerSuccess() {
        return "Re-connect server succeeded";
    }

    @Override
    public String ReConnectServerAfterTenSeconds() {
        return "Reconnect the server after 10 seconds";
    }

    @Override
    public String KeyIsNotAllowedNull() {
        return "The keyword is not allowed to be empty";
    }

    @Override
    public String KeyIsExistAlready() {
        return "The current keyword already exists";
    }

    @Override
    public String KeyIsNotExist() {
        return "The keyword for the current subscription does not exist";
    }

    @Override
    public String ConnectingServer() {
        return "Connecting to Server...";
    }

    @Override
    public String ConnectFailedAndWait() {
        return "Connection disconnected, wait {0} seconds to reconnect";
    }

    @Override
    public String AttemptConnectServer() {
        return "Attempting to connect server {0} times";
    }

    @Override
    public String ConnectServerSuccess() {
        return "Connection Server succeeded";
    }

    @Override
    public String GetClientIpAddressFailed() {
        return "Client IP Address acquisition failed";
    }

    @Override
    public String ConnectionIsNotAvailable() {
        return "The current connection is not available";
    }

    @Override
    public String DeviceCurrentIsLoginRepeat() {
        return "ID of the current device duplicate login";
    }

    @Override
    public String DeviceCurrentIsLoginForbidden() {
        return "The ID of the current device prohibits login";
    }

    @Override
    public String PasswordCheckFailed() {
        return "Password validation failed";
    }

    @Override
    public String DataTransformError() {
        return "Data conversion failed, source data: ";
    }

    @Override
    public String RemoteClosedConnection() {
        return "Remote shutdown of connection";
    }

    @Override
    public String LogNetDebug() {
        return "Debug";
    }

    @Override
    public String LogNetInfo() {
        return "Info";
    }

    @Override
    public String LogNetWarn() {
        return "Warn";
    }

    @Override
    public String LogNetError() {
        return "Error";
    }

    @Override
    public String LogNetFatal() {
        return "Fatal";
    }

    @Override
    public String LogNetAbandon() {
        return "Abandon";
    }

    @Override
    public String LogNetAll() {
        return "All";
    }

    @Override
    public String ModbusTcpFunctionCodeNotSupport() {
        return "Unsupported function code";
    }

    @Override
    public String ModbusTcpFunctionCodeOverBound() {
        return "Data read out of bounds";
    }

    @Override
    public String ModbusTcpFunctionCodeQuantityOver() {
        return "Read length exceeds maximum value";
    }

    @Override
    public String ModbusTcpFunctionCodeReadWriteException() {
        return "Read and Write exceptions";
    }

    @Override
    public String ModbusTcpReadCoilException() {
        return "Read Coil anomalies";
    }

    @Override
    public String ModbusTcpWriteCoilException() {
        return "Write Coil exception";
    }

    @Override
    public String ModbusTcpReadRegisterException() {
        return "Read Register exception";
    }

    @Override
    public String ModbusTcpWriteRegisterException() {
        return "Write Register exception";
    }

    @Override
    public String ModbusAddressMustMoreThanOne() {
        return "The address value must be greater than 1 in the case where the start address is 1";
    }

    @Override
    public String ModbusAsciiFormatCheckFailed() {
        return "Modbus ASCII command check failed, not MODBUS-ASCII message";
    }

    @Override
    public String ModbusCRCCheckFailed() {
        return "The CRC checksum check failed for Modbus";
    }

    @Override
    public String ModbusLRCCheckFailed() {
        return "The LRC checksum check failed for Modbus";
    }

    @Override
    public String ModbusMatchFailed() {
        return "Not the standard Modbus protocol";
    }

    @Override
    public String ModbusBitIndexOverstep() {
        return "The index of the bit access is out of range, it should be between 0-15";
    }

    @Override
    public String MelsecPleaseReferToManualDocument() {
        return "Please check Mitsubishi's communication manual for details of the alarm.";
    }

    @Override
    public String MelsecReadBitInfo() {
        return "The read bit variable array can only be used for bit soft elements, if you read the word soft component, call the Read method";
    }

    @Override
    public String MelsecCurrentTypeNotSupportedWordOperate() {
        return "The current type does not support word read and write";
    }

    @Override
    public String MelsecCurrentTypeNotSupportedBitOperate() {
        return "The current type does not support bit read and write";
    }

    @Override
    public String MelsecFxReceiveZero() {
        return "The received data length is 0";
    }

    @Override
    public String MelsecFxAckNagative() {
        return "Invalid data from PLC feedback";
    }

    @Override
    public String MelsecFxAckWrong() {
        return "PLC Feedback Signal Error: ";
    }

    @Override
    public String MelsecFxCrcCheckFailed() {
        return "PLC Feedback message and check failed!";
    }

    @Override
    public String MelsecError02() {
        return "The specified range of the \"read/write\" (in/out) device is incorrect.";
    }

    @Override
    public String MelsecError51() {
        return "When using random access buffer memory for communication, the start address specified by the external device is set outside the range of 0-6143. Solution: Check and correct the specified start address.";
    }

    @Override
    public String MelsecError52() {
        return "1. When using random access buffer memory for communication, the start address + data word count specified by the external device (depending on the setting when reading) is outside the range of 0-6143. \r\n2. Data of the specified word count (text) cannot be sent in one frame. (The data length value and the total text of the communication are not within the allowed range.)";
    }

    @Override
    public String MelsecError54() {
        return "When \"ASCII Communication\" is selected in [Operation Settings]-[Communication Data Code] via GX Developer, ASCII codes from external devices that cannot be converted to binary codes are received.";
    }

    @Override
    public String MelsecError55() {
        return "When [Operation Settings]-[Cannot Write in Run Time] cannot be set by GX Developer (No check mark), if the PLCCPU is in the running state, the external device requests to write data. ";
    }

    @Override
    public String MelsecError56() {
        return "The device specified from the outside is incorrect.";
    }

    @Override
    public String MelsecError58() {
        return "1. The command start address (start device number and start step number) specified by the external device can be set outside the specified range.\r\n2. The block number specified for the extended file register does not exist.\r\n3. File register (R) cannot be specified.\r\n4. Specify the word device for the bit device command.\r\n5. The start number of the bit device is specified by a certain value. This value is not a multiple of 16 in the word device command.";
    }

    @Override
    public String MelsecError59() {
        return "The register of the extension file cannot be specified";
    }

    @Override
    public String MelsecError430D() {
        return "The request data is abnormal. Therefore, operations cannot be performed on the CPU module in which user authentication is enabled. Set the user authentication function to disabled.";
    }

    @Override
    public String MelsecErrorC04D() {
        return "In the information received by the Ethernet module through automatic open UDP port communication or out-of-order fixed buffer communication, the data length specified in the application domain is incorrect.";
    }

    @Override
    public String MelsecErrorC050() {
        return "When the operation setting of ASCII code communication is performed in the Ethernet module, ASCII code data that cannot be converted into binary code is received.";
    }

    @Override
    public String MelsecErrorC051_54() {
        return "The number of read/write points is outside the allowable range.";
    }

    @Override
    public String MelsecErrorC055() {
        return "The number of file data read/write points is outside the allowable range.";
    }

    @Override
    public String MelsecErrorC056() {
        return "The read/write request exceeded the maximum address.";
    }

    @Override
    public String MelsecErrorC057() {
        return "The length of the requested data does not match the data count of the character area (partial text).";
    }

    @Override
    public String MelsecErrorC058() {
        return "After the ASCII binary conversion, the length of the requested data does not match the data count of the character area (partial text).";
    }

    @Override
    public String MelsecErrorC059() {
        return "The designation of commands and subcommands is incorrect.";
    }

    @Override
    public String MelsecErrorC05A_B() {
        return "The Ethernet module cannot read and write to the specified device.";
    }

    @Override
    public String MelsecErrorC05C() {
        return "The requested content is incorrect. (Request to read/write to word device in bits.)";
    }

    @Override
    public String MelsecErrorC05D() {
        return "Monitoring registration is not performed.";
    }

    @Override
    public String MelsecErrorC05E() {
        return "The communication time between the Ethernet module and the PLC CPU exceeds the time of the CPU watchdog timer.";
    }

    @Override
    public String MelsecErrorC05F() {
        return "The request cannot be executed on the target PLC.";
    }

    @Override
    public String MelsecErrorC060() {
        return "The requested content is incorrect. (Incorrect data is specified for the bit device, etc.)";
    }

    @Override
    public String MelsecErrorC061() {
        return "The length of the requested data does not match the number of data in the character area (partial text).";
    }

    @Override
    public String MelsecErrorC062() {
        return "When the online correction is prohibited, the remote protocol I/O station (QnA compatible 3E frame or 4E frame) write operation is performed by the MC protocol.";
    }

    @Override
    public String MelsecErrorC070() {
        return "Cannot specify the range of device memory for the target station";
    }

    @Override
    public String MelsecErrorC072() {
        return "The requested content is incorrect. (Request to write to word device in bit units.) ";
    }

    @Override
    public String MelsecErrorC074() {
        return "The target PLC does not execute the request. The network number and PC number need to be corrected.";
    }

    @Override
    public String MelsecFxLinksError02() {
        return "Sum check error, the sum check code in the received data is inconsistent with the sum check generated from the received data";
    }

    @Override
    public String MelsecFxLinksError03() {
        return "he communication protocol is abnormal, and the control sequence used for communication is different from the control sequence set with the parameters. Or part of it is different from the specified control order. Or the command specified in the control sequence does not exist.";
    }

    @Override
    public String MelsecFxLinksError06() {
        return "Error in character A, B, C area\r\n1. The control sequence set by the parameter is different\r\n2.A device number that does not exist in the target PLC is specified. \r\n3. The device number is not specified in the specified number of characters (5 characters, or 7 characters).";
    }

    @Override
    public String MelsecFxLinksError07() {
        return "\u5728\u8f6f\u5143\u4ef6\u4e2d\u5199\u5165\u7684\u6570\u636e\u4e0d\u662f16\u8fdb\u5236\u7684ASCII\u7801\u3002";
    }

    @Override
    public String MelsecFxLinksError0A() {
        return "\u4e0d\u5b58\u5728\u8be5PC\u53f7\u7684\u7ad9\u70b9\u3002";
    }

    @Override
    public String MelsecFxLinksError10() {
        return "\u4e0d\u5b58\u5728\u8be5PC\u53f7\u7684\u7ad9\u70b9\u3002";
    }

    @Override
    public String MelsecFxLinksError18() {
        return "\u4e0d\u80fd\u6267\u884c\u8fdc\u7a0bRUN/SOP\u3002\u5728\u53ef\u7f16\u7a0b\u63a7\u5236\u5668\u7684\u786c\u4ef6\u4e2d\u51b3\u5b9a\u4e86 RUN \u6216\u662f STOP\u3002 (\u6bd4\u5982\u4f7f\u7528\u4e86RUN/STOP\u5f00\u5173\u7b49)";
    }

    @Override
    public String SiemensDBAddressNotAllowedLargerThan255() {
        return "DB block data cannot be greater than 255";
    }

    @Override
    public String SiemensReadLengthMustBeEvenNumber() {
        return "The length of the data read must be an even number";
    }

    @Override
    public String SiemensWriteError() {
        return "Writes the data exception, the code name is: ";
    }

    @Override
    public String SiemensReadLengthCannotLargerThan19() {
        return "The number of arrays read does not allow greater than 19";
    }

    @Override
    public String SiemensDataLengthCheckFailed() {
        return "Block length checksum failed, please check if Put/get is turned on and DB block optimization is turned off";
    }

    @Override
    public String SiemensFWError() {
        return "An exception occurred, the specific information to find the Fetch/write protocol document";
    }

    @Override
    public String SiemensReadLengthOverPlcAssign() {
        return "The range of data read exceeds the setting of the PLC";
    }

    @Override
    public String SiemensError000A() {
        return "Object does not exist:  Occurs when trying to request a Data Block that does not exist.";
    }

    @Override
    public String SiemensError0006() {
        return "The data type of the current operation is not supported";
    }

    @Override
    public String OmronAddressMustBeZeroToFifteen() {
        return "The bit address entered can only be between 0-15";
    }

    @Override
    public String OmronReceiveDataError() {
        return "Data Receive exception";
    }

    @Override
    public String OmronStatus0() {
        return "Communication is normal.";
    }

    @Override
    public String OmronStatus1() {
        return "The message header is not fins";
    }

    @Override
    public String OmronStatus2() {
        return "Data length too long";
    }

    @Override
    public String OmronStatus3() {
        return "This command does not support";
    }

    @Override
    public String OmronStatus20() {
        return "Exceeding connection limit";
    }

    @Override
    public String OmronStatus21() {
        return "The specified node is already in the connection";
    }

    @Override
    public String OmronStatus22() {
        return "Attempt to connect to a protected network node that is not yet configured in the PLC";
    }

    @Override
    public String OmronStatus23() {
        return "The current client's network node exceeds the normal range";
    }

    @Override
    public String OmronStatus24() {
        return "The current client's network node is already in use";
    }

    @Override
    public String OmronStatus25() {
        return "All network nodes are already in use";
    }

    @Override
    public String AllenBradley04() {
        return "The IOI could not be deciphered. Either it was not formed correctly or the match tag does not exist.";
    }

    @Override
    public String AllenBradley05() {
        return "The particular item referenced (usually instance) could not be found.";
    }

    @Override
    public String AllenBradley06() {
        return "The amount of data requested would not fit into the response buffer. Partial data transfer has occurred.";
    }

    @Override
    public String AllenBradley0A() {
        return "An error has occurred trying to process one of the attributes.";
    }

    @Override
    public String AllenBradley0C() {
        return "An error occurred while attempting a read and write operation, which triggers an error during program loading.";
    }

    @Override
    public String AllenBradley13() {
        return "Not enough command data / parameters were supplied in the command to execute the service requested.";
    }

    @Override
    public String AllenBradley1C() {
        return "An insufficient number of attributes were provided compared to the attribute count.";
    }

    @Override
    public String AllenBradley1E() {
        return "A service request in this service went wrong.";
    }

    @Override
    public String AllenBradley26() {
        return "The IOI word length did not match the amount of IOI which was processed.";
    }

    @Override
    public String AllenBradleySessionStatus00() {
        return "success";
    }

    @Override
    public String AllenBradleySessionStatus01() {
        return "The sender issued an invalid or unsupported encapsulation command.";
    }

    @Override
    public String AllenBradleySessionStatus02() {
        return "Insufficient memory resources in the receiver to handle the command. This is not an application error. Instead, it only results if the encapsulation layer cannot obtain memory resources that it need.";
    }

    @Override
    public String AllenBradleySessionStatus03() {
        return "Poorly formed or incorrect data in the data portion of the encapsulation message.";
    }

    @Override
    public String AllenBradleySessionStatus64() {
        return "An originator used an invalid session handle when sending an encapsulation message.";
    }

    @Override
    public String AllenBradleySessionStatus65() {
        return "The target received a message of invalid length.";
    }

    @Override
    public String AllenBradleySessionStatus69() {
        return "Unsupported encapsulation protocol revision.";
    }

    @Override
    public String PanasonicReceiveLengthMustLargerThan9() {
        return "The received data length must be greater than 9";
    }

    @Override
    public String PanasonicAddressParameterCannotBeNull() {
        return "Address parameter is not allowed to be empty";
    }

    @Override
    public String PanasonicAddressBitStartMulti16() {
        return "The starting address for bit writing needs to be a multiple of 16, for example: R0.0, R2.0, L3.0, Y4.0";
    }

    @Override
    public String PanasonicBoolLengthMulti16() {
        return "The data length written in batch bool needs to be a multiple of 16, otherwise it cannot be written";
    }

    @Override
    public String PanasonicMewStatus20() {
        return "Error unknown";
    }

    @Override
    public String PanasonicMewStatus21() {
        return "Nack error, the remote unit could not be correctly identified, or a data error occurred.";
    }

    @Override
    public String PanasonicMewStatus22() {
        return "WACK Error: The receive buffer for the remote unit is full.";
    }

    @Override
    public String PanasonicMewStatus23() {
        return "Multiple port error: The remote unit number (01 to 16) is set to repeat with the local unit.";
    }

    @Override
    public String PanasonicMewStatus24() {
        return "Transport format error: An attempt was made to send data that does not conform to the transport format, or a frame data overflow or a data error occurred.";
    }

    @Override
    public String PanasonicMewStatus25() {
        return "Hardware error: Transport system hardware stopped operation.";
    }

    @Override
    public String PanasonicMewStatus26() {
        return "Unit Number error: The remote unit's numbering setting exceeds the range of 01 to 63.";
    }

    @Override
    public String PanasonicMewStatus27() {
        return "Error not supported: Receiver data frame overflow. An attempt was made to send data of different frame lengths between different modules.";
    }

    @Override
    public String PanasonicMewStatus28() {
        return "No answer error: The remote unit does not exist. (timeout).";
    }

    @Override
    public String PanasonicMewStatus29() {
        return "Buffer Close error: An attempt was made to send or receive a buffer that is in a closed state.";
    }

    @Override
    public String PanasonicMewStatus30() {
        return "Timeout error: Persisted in transport forbidden State.";
    }

    @Override
    public String PanasonicMewStatus40() {
        return "BCC Error: A transmission error occurred in the instruction data.";
    }

    @Override
    public String PanasonicMewStatus41() {
        return "Malformed: The sent instruction information does not conform to the transmission format.";
    }

    @Override
    public String PanasonicMewStatus42() {
        return "Error not supported: An unsupported instruction was sent. An instruction was sent to a target station that was not supported.";
    }

    @Override
    public String PanasonicMewStatus43() {
        return "Processing Step Error: Additional instructions were sent when the transfer request information was suspended.";
    }

    @Override
    public String PanasonicMewStatus50() {
        return "Link Settings Error: A link number that does not actually exist is set.";
    }

    @Override
    public String PanasonicMewStatus51() {
        return "Simultaneous operation error: When issuing instructions to other units, the transmit buffer for the local unit is full.";
    }

    @Override
    public String PanasonicMewStatus52() {
        return "Transport suppression Error: Unable to transfer to other units.";
    }

    @Override
    public String PanasonicMewStatus53() {
        return "Busy error: Other instructions are being processed when the command is received.";
    }

    @Override
    public String PanasonicMewStatus60() {
        return "Parameter error: Contains code that cannot be used in the directive, or the code does not have a zone specified parameter (X, Y, D), and so on.";
    }

    @Override
    public String PanasonicMewStatus61() {
        return "Data error: Contact number, area number, Data code format (BCD,HEX, etc.) overflow, overflow, and area specified error.";
    }

    @Override
    public String PanasonicMewStatus62() {
        return "Register ERROR: Excessive logging of data in an unregistered state of operations (Monitoring records, tracking records, etc.). )\u3002";
    }

    @Override
    public String PanasonicMewStatus63() {
        return "PLC mode error: When an instruction is issued, the run mode is not able to process the instruction.";
    }

    @Override
    public String PanasonicMewStatus65() {
        return "Protection Error: Performs a write operation to the program area or system register in the storage protection state.";
    }

    @Override
    public String PanasonicMewStatus66() {
        return "Address Error: Address (program address, absolute address, etc.) Data encoding form (BCD, hex, etc.), overflow, underflow, or specified range error.";
    }

    @Override
    public String PanasonicMewStatus67() {
        return "Missing data error: The data to be read does not exist. (reads data that is not written to the comment register.)";
    }

    @Override
    public String PanasonicMc4031() {
        return "Address out of range (starting device + number of writing points)";
    }

    @Override
    public String PanasonicMcC051() {
        return "Outside the specified range of equipment points";
    }

    @Override
    public String PanasonicMcC056() {
        return "Outside the specified range of the starting device";
    }

    @Override
    public String PanasonicMcC059() {
        return "Command search When there is no command consistent with the received data command in the MC protocol command table";
    }

    @Override
    public String PanasonicMcC05B() {
        return "Outside the specified range of the equipment code";
    }

    @Override
    public String PanasonicMcC05C() {
        return "When the slave command is a bit unit (0001) and the device code is a word device";
    }

    @Override
    public String PanasonicMcC05F() {
        return "1. \"Network number\" check \r\n2. \"PC number\" check \r\n3. \"Request target unit IO number\" check \r\n4. The number of received write data is abnormal";
    }

    @Override
    public String PanasonicMcC060() {
        return "Write contact data abnormal (other than 0/1)";
    }

    @Override
    public String PanasonicMcC061() {
        return "1. The number of received data has not reached the minimum number of bytes received for the start character content check \r\n 2. The number of received data has not reached the minimum number of bytes received";
    }

    @Override
    public String FatekStatus02() {
        return "Illegal value";
    }

    @Override
    public String FatekStatus03() {
        return "Write disabled";
    }

    @Override
    public String FatekStatus04() {
        return "Invalid command code";
    }

    @Override
    public String FatekStatus05() {
        return "Cannot be activated (down RUN command but Ladder Checksum does not match)";
    }

    @Override
    public String FatekStatus06() {
        return "Cannot be activated (down RUN command but PLC ID \u2260 Ladder ID)";
    }

    @Override
    public String FatekStatus07() {
        return "Cannot be activated (down RUN command but program syntax error)";
    }

    @Override
    public String FatekStatus09() {
        return "Cannot be activated (down RUN command, but the ladder program command PLC cannot be executed)";
    }

    @Override
    public String FatekStatus10() {
        return "Illegal address";
    }

    @Override
    public String FujiSpbStatus01() {
        return "Write to the ROM";
    }

    @Override
    public String FujiSpbStatus02() {
        return "Received undefined commands or commands that could not be processed";
    }

    @Override
    public String FujiSpbStatus03() {
        return "There is a contradiction in the data part (parameter exception)";
    }

    @Override
    public String FujiSpbStatus04() {
        return "Unable to process due to transfer interlocks from other programmers";
    }

    @Override
    public String FujiSpbStatus05() {
        return "The module number is incorrect";
    }

    @Override
    public String FujiSpbStatus06() {
        return "Search item not found";
    }

    @Override
    public String FujiSpbStatus07() {
        return "An address that exceeds the module range (when writing) is specified";
    }

    @Override
    public String FujiSpbStatus09() {
        return "Unable to execute due to faulty program (RUN)";
    }

    @Override
    public String FujiSpbStatus0C() {
        return "Inconsistent password";
    }

    @Override
    public String MQTTDataTooLong() {
        return "The current data length exceeds the limit of the agreement";
    }

    @Override
    public String MQTTStatus01() {
        return "unacceptable protocol version";
    }

    @Override
    public String MQTTStatus02() {
        return "identifier rejected";
    }

    @Override
    public String MQTTStatus03() {
        return "server unavailable";
    }

    @Override
    public String MQTTStatus04() {
        return "bad user name or password";
    }

    @Override
    public String MQTTStatus05() {
        return "not authorized";
    }

    @Override
    public String SAMReceiveLengthMustLargerThan8() {
        return "Received data length is less than 8, must be greater than 8";
    }

    @Override
    public String SAMHeadCheckFailed() {
        return "Data frame header check failed for SAM\u3002";
    }

    @Override
    public String SAMLengthCheckFailed() {
        return "Data length header check failed for SAM\u3002";
    }

    @Override
    public String SAMSumCheckFailed() {
        return "SAM's data checksum check failed.";
    }

    @Override
    public String SAMAddressStartWrong() {
        return "SAM string address identification error.";
    }

    @Override
    public String SAMStatus90() {
        return "Successful operation";
    }

    @Override
    public String SAMStatus91() {
        return "No content in the card";
    }

    @Override
    public String SAMStatus9F() {
        return "Find card success";
    }

    @Override
    public String SAMStatus10() {
        return "Received data checksum error";
    }

    @Override
    public String SAMStatus11() {
        return "Received data length error";
    }

    @Override
    public String SAMStatus21() {
        return "Receive data command error";
    }

    @Override
    public String SAMStatus23() {
        return "Unauthorized operation";
    }

    @Override
    public String SAMStatus24() {
        return "Unrecognized error";
    }

    @Override
    public String SAMStatus31() {
        return "Card authentication SAM failed";
    }

    @Override
    public String SAMStatus32() {
        return "SAM certificate / card failed";
    }

    @Override
    public String SAMStatus33() {
        return "Information validation error";
    }

    @Override
    public String SAMStatus40() {
        return "Unrecognized card type";
    }

    @Override
    public String SAMStatus41() {
        return "ID / card operation failed";
    }

    @Override
    public String SAMStatus47() {
        return "Random number failed";
    }

    @Override
    public String SAMStatus60() {
        return "SAM Self-test failed";
    }

    @Override
    public String SAMStatus66() {
        return "SAM unauthorized";
    }

    @Override
    public String SAMStatus80() {
        return "Failed to find card";
    }

    @Override
    public String SAMStatus81() {
        return "\u9009\u53d6\u8bc1/\u5361\u5931\u8d25";
    }

    @Override
    public String DLTAddressCannotNull() {
        return "Address information cannot be empty or have a length of 0";
    }

    @Override
    public String DLTAddressCannotMoreThan12() {
        return "Address information length cannot be greater than 12";
    }

    @Override
    public String DLTAddressMatchFailed() {
        return "Address format failed to match, please check whether it is less than 12 words, and are all addresses composed of 0-9 or A digits";
    }

    @Override
    public String DLTErrorInfoBit0() {
        return "Other errors";
    }

    @Override
    public String DLTErrorInfoBit1() {
        return "No data requested";
    }

    @Override
    public String DLTErrorInfoBit2() {
        return "Incorrect password / unauthorized";
    }

    @Override
    public String DLTErrorInfoBit3() {
        return "The communication rate cannot be changed";
    }

    @Override
    public String DLTErrorInfoBit4() {
        return "Annual time zone exceeded";
    }

    @Override
    public String DLTErrorInfoBit5() {
        return "Day time slot exceeded";
    }

    @Override
    public String DLTErrorInfoBit6() {
        return "Rates exceeded";
    }

    @Override
    public String DLTErrorInfoBit7() {
        return "Reserve";
    }

    @Override
    public String DLTErrorWriteReadCheckFailed() {
        return "Verify that the data after writing is consistent with the previous data fails";
    }

    @Override
    public String DLT1997ErrorInfoBit0() {
        return "Illegal data";
    }

    @Override
    public String DLT1997ErrorInfoBit1() {
        return "Data identification error";
    }

    @Override
    public String DLT1997ErrorInfoBit2() {
        return "The password is wrong";
    }

    @Override
    public String DLT1997ErrorInfoBit4() {
        return "The number of annual time zones is out of range";
    }

    @Override
    public String DLT1997ErrorInfoBit5() {
        return "The number of daily hours is out of range";
    }

    @Override
    public String DLT1997ErrorInfoBit6() {
        return "The number of rates is out of range";
    }

    @Override
    public String DLT698Error01() {
        return "hardware failure";
    }

    @Override
    public String DLT698Error02() {
        return "temporarily invalid";
    }

    @Override
    public String DLT698Error03() {
        return "refuse to read and write";
    }

    @Override
    public String DLT698Error04() {
        return "object is undefined";
    }

    @Override
    public String DLT698Error05() {
        return "Object interface class does not conform to";
    }

    @Override
    public String DLT698Error06() {
        return "object does not exist";
    }

    @Override
    public String DLT698Error07() {
        return "Type mismatch";
    }

    @Override
    public String DLT698Error08() {
        return "out of bounds";
    }

    @Override
    public String DLT698Error09() {
        return "data block not available";
    }

    @Override
    public String DLT698Error10() {
        return "Framing transfer canceled";
    }

    @Override
    public String DLT698Error11() {
        return "Not in framed transmission state";
    }

    @Override
    public String DLT698Error12() {
        return "Block write cancel";
    }

    @Override
    public String DLT698Error13() {
        return "No block write state exists";
    }

    @Override
    public String DLT698Error14() {
        return "Invalid data block sequence number";
    }

    @Override
    public String DLT698Error15() {
        return "wrong password/unauthorized";
    }

    @Override
    public String DLT698Error16() {
        return "Communication rate cannot be changed";
    }

    @Override
    public String DLT698Error17() {
        return "Year time zone exceeded";
    }

    @Override
    public String DLT698Error18() {
        return "The number of time slots exceeds";
    }

    @Override
    public String DLT698Error19() {
        return "Exceeded rate";
    }

    @Override
    public String DLT698Error20() {
        return "Security authentication mismatch";
    }

    @Override
    public String DLT698Error21() {
        return "Repeat recharge";
    }

    @Override
    public String DLT698Error22() {
        return "ESAM verification failed";
    }

    @Override
    public String DLT698Error23() {
        return "Security authentication failed";
    }

    @Override
    public String DLT698Error24() {
        return "Customer ID does not match";
    }

    @Override
    public String DLT698Error25() {
        return "Wrong number of recharges";
    }

    @Override
    public String DLT698Error26() {
        return "Super hoarding of electricity";
    }

    @Override
    public String DLT698Error27() {
        return "abnormal address";
    }

    @Override
    public String DLT698Error28() {
        return "Symmetric decryption error";
    }

    @Override
    public String DLT698Error29() {
        return "Asymmetric decryption error";
    }

    @Override
    public String DLT698Error30() {
        return "Signature error";
    }

    @Override
    public String DLT698Error31() {
        return "Energy meter hangs";
    }

    @Override
    public String DLT698Error32() {
        return "Invalid time tag";
    }

    @Override
    public String DLT698Error33() {
        return "Request timed out";
    }

    @Override
    public String DLT698Error34() {
        return "Incorrect P1P2 for ESAM";
    }

    @Override
    public String DLT698Error35() {
        return "LC errors for ESAM";
    }

    @Override
    public String KeyenceNanoE0() {
        return "1. The specified device number, bank number, unit number, and address are out of range. {Environment.NewLine} 2. Specify the numbers of timers, counters, CTH and CTC that are not used by the program. {Environment.NewLine} 3. The monitor is not logged in, but the monitor needs to be read.";
    }

    @Override
    public String KeyenceNanoE1() {
        return "1. A command not supported by the CPU Unit was sent. {Environment.NewLine} 2. The method of the specified instruction is wrong. {Environment.NewLine} 3. Before communication was established, a command other than CR was sent.";
    }

    @Override
    public String KeyenceNanoE2() {
        return "1. The \"M1 (switch to RUN mode)\" command was sent when the CPU unit did not store a program. {Environment.NewLine} 2. When the RUN/PROG switch of the CPU unit is in the PROG state, the \"M1 (switch to RUN mode)\" command is sent.";
    }

    @Override
    public String KeyenceNanoE4() {
        return "Want to change the set values of timers, counters, and CTCs written in the disable program.";
    }

    @Override
    public String KeyenceNanoE5() {
        return "When the CPU unit error has not been eliminated, the \"M1 (switch to RUN mode)\" command was sent.";
    }

    @Override
    public String KeyenceNanoE6() {
        return "Read from the device selected by the \"RDC\" instruction.";
    }

    @Override
    public String YokogawaLinkError01() {
        return " The CPU number is outside the range of 1 to 4";
    }

    @Override
    public String YokogawaLinkError02() {
        return "The command does not exist or the command is not executable.";
    }

    @Override
    public String YokogawaLinkError03() {
        return "The device name does not exist or A relay device is incorrectly specified for read/write access in word units.";
    }

    @Override
    public String YokogawaLinkError04() {
        return "Value outside the setting range: 1. Characters other than 0 and 1 are used for bit setting. 2. Word setting is out of the valid range of 0000 to FFFF. 3. The specified starting position in a command, such as Load/Save, is out of the valid address range.";
    }

    @Override
    public String YokogawaLinkError05() {
        return "Data count out of range: 1. The specified bit count, word count, etc. exceeded the specifications range. 2. The specified data count and the device parameter count, etc. do not match.";
    }

    @Override
    public String YokogawaLinkError06() {
        return "Attempted to execute monitoring without having specified a monitor command( BRS, WRS)";
    }

    @Override
    public String YokogawaLinkError07() {
        return "Not a BASIC CPU";
    }

    @Override
    public String YokogawaLinkError08() {
        return "A parameter is invalid for a reason other than those given above.";
    }

    @Override
    public String YokogawaLinkError41() {
        return "An error has occurred during communication";
    }

    @Override
    public String YokogawaLinkError42() {
        return "Value of checksum differs. (Bit omitted or changed characters)";
    }

    @Override
    public String YokogawaLinkError43() {
        return "The amount of data received exceeded stipulated value.";
    }

    @Override
    public String YokogawaLinkError44() {
        return "Timeout while receiving characters: 1. No End character or ETX was received. 2. Timeout duration is 5 seconds";
    }

    @Override
    public String YokogawaLinkError51() {
        return "Timeout error: 1. No end-of-process response is returned from the CPU for reasons such as CPU power failure.(timeout) 2. Sequence CPU hardware failure. 3. Sequence CPU is not accepting commands. 4. Insufficient sequence CPU service time";
    }

    @Override
    public String YokogawaLinkError52() {
        return "The CPU has detected an error during processing. ";
    }

    @Override
    public String YokogawaLinkErrorF1() {
        return "Internal error: 1. A Cancel (PLC) command was issued during execution of a command other than a Load( PLD) or Save( PSV) command. 2. An internal error was detected.";
    }

    @Override
    public String GeSRTPNotSupportBitReadWrite() {
        return "The current address data does not support read and write operations in bit units";
    }

    @Override
    public String GeSRTPAddressCannotBeZero() {
        return "The starting address of the current address cannot be 0, it needs to start from 1";
    }

    @Override
    public String GeSRTPNotSupportByteReadWrite() {
        return "The current address data does not support read and write operations in byte units, and can only be read and written in word units";
    }

    @Override
    public String GeSRTPWriteLengthMustBeEven() {
        return "The length of the data written to the current address must be an even number";
    }

    @Override
    public String Memobus01() {
        return "SFC exception";
    }

    @Override
    public String Memobus02() {
        return "The reference number is abnormal";
    }

    @Override
    public String Memobus03() {
        return "The number of data is abnormal";
    }

    @Override
    public String Memobus40() {
        return "The register type is incorrect";
    }

    @Override
    public String Memobus41() {
        return "The data type is wrong";
    }

    @Override
    public String Memobus42() {
        return "The register type of this site is incorrect";
    }

    @Override
    public String ToyoPuc11() {
        return "Because it is a hardware exception of the CPU module, it cannot be handled";
    }

    @Override
    public String ToyoPuc20() {
        return "The fixed data (ENQ) in the relay command is not 05";
    }

    @Override
    public String ToyoPuc21() {
        return "The number of transfers is abnormal and there is an error in the number of bytes transferred";
    }

    @Override
    public String ToyoPuc23() {
        return "The command code is illegal";
    }

    @Override
    public String ToyoPuc24() {
        return "The subcommand code is illegal";
    }

    @Override
    public String ToyoPuc25() {
        return "The data bytes in command format are illegal";
    }

    @Override
    public String ToyoPuc34() {
        return "Access is prohibited by setting the access prohibition";
    }

    @Override
    public String ToyoPuc3E() {
        return "The command could not be executed because a reset was in progress";
    }

    @Override
    public String ToyoPuc3F() {
        return "The command cannot be executed because it is in a stopped state";
    }

    @Override
    public String ToyoPuc40() {
        return "The address is not in the range due to reading or writing the command, or the address + data quantity of the command deviates from the address range";
    }

    @Override
    public String ToyoPuc41() {
        return "The number of words or bytes is out of range";
    }
}

