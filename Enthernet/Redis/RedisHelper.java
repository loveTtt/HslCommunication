/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Enthernet.Redis;

import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class RedisHelper {
    public static byte[] PackStringCommand(String[] commands) {
        StringBuilder sb = new StringBuilder();
        sb.append('*');
        sb.append(commands.length);
        sb.append("\r\n");
        for (int i = 0; i < commands.length; ++i) {
            sb.append('$');
            sb.append(Utilities.getBytes(commands[i], "utf-8").length);
            sb.append("\r\n");
            sb.append(commands[i]);
            sb.append("\r\n");
        }
        return Utilities.getBytes(sb.toString(), "utf-8");
    }

    public static OperateResultExOne<Integer> GetNumberFromCommandLine(byte[] commandLine) {
        try {
            String command = Utilities.getString(commandLine, "utf-8");
            if (command.endsWith("\r\n")) {
                command = command.substring(0, command.length() - 2);
            }
            return OperateResultExOne.CreateSuccessResult(Integer.parseInt(command.substring(1)));
        }
        catch (Exception ex) {
            return new OperateResultExOne<Integer>(ex.getMessage());
        }
    }

    public static OperateResultExOne<Long> GetLongNumberFromCommandLine(byte[] commandLine) {
        try {
            String command = Utilities.getString(commandLine, "utf-8");
            if (command.endsWith("\r\n")) {
                command = command.substring(0, command.length() - 2);
            }
            return OperateResultExOne.CreateSuccessResult(Long.parseLong(command.substring(1)));
        }
        catch (Exception ex) {
            return new OperateResultExOne<Long>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String> GetStringFromCommandLine(byte[] commandLine) {
        try {
            int length;
            if (commandLine[0] != 36) {
                return new OperateResultExOne<String>(Utilities.getString(commandLine, "UTF-8"));
            }
            int index_start = -1;
            int index_end = -1;
            for (int i = 0; i < commandLine.length; ++i) {
                if (commandLine[i] == 13 || commandLine[i] == 10) {
                    index_start = i;
                }
                if (commandLine[i] != 10) continue;
                index_end = i;
                break;
            }
            if ((length = Integer.parseInt(Utilities.getString(commandLine, 1, index_start - 1, "UTF-8"))) < 0) {
                return new OperateResultExOne<String>("(nil) None Value");
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getString(commandLine, index_end + 1, length, "UTF-8"));
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String[]> GetStringsFromCommandLine(byte[] commandLine) {
        try {
            ArrayList<String> lists = new ArrayList<String>();
            if (commandLine[0] != 42) {
                return new OperateResultExOne<String[]>(Utilities.getString(commandLine, "UTF-8"));
            }
            int index = 0;
            for (int i = 0; i < commandLine.length; ++i) {
                if (commandLine[i] != 13 && commandLine[i] != 10) continue;
                index = i;
                break;
            }
            int length = Integer.parseInt(Utilities.getString(commandLine, 1, index - 1, "UTF-8"));
            for (int i = 0; i < length; ++i) {
                int j;
                int index_start;
                int index_end = -1;
                for (int j2 = index; j2 < commandLine.length; ++j2) {
                    if (commandLine[j2] != 10) continue;
                    index_end = j2;
                    break;
                }
                if (commandLine[index = index_end + 1] == 36) {
                    int stringLength;
                    index_start = -1;
                    for (j = index; j < commandLine.length; ++j) {
                        if (commandLine[j] != 13 && commandLine[j] != 10) continue;
                        index_start = j;
                        break;
                    }
                    if ((stringLength = Integer.parseInt(Utilities.getString(commandLine, index + 1, index_start - index - 1, "UTF-8"))) >= 0) {
                        for (int j3 = index; j3 < commandLine.length; ++j3) {
                            if (commandLine[j3] != 10) continue;
                            index_end = j3;
                            break;
                        }
                        index = index_end + 1;
                        lists.add(Utilities.getString(commandLine, index, stringLength, "UTF-8"));
                        index += stringLength;
                        continue;
                    }
                    lists.add(null);
                    continue;
                }
                index_start = -1;
                for (j = index; j < commandLine.length; ++j) {
                    if (commandLine[j] != 13 && commandLine[j] != 10) continue;
                    index_start = j;
                    break;
                }
                lists.add(Utilities.getString(commandLine, index, index_start - index - 1, "UTF-8"));
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getStrings(lists));
        }
        catch (Exception ex) {
            return new OperateResultExOne<String[]>(ex.getMessage());
        }
    }
}

