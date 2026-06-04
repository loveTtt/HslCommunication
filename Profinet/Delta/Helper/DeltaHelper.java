/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Delta.Helper;

import HslCommunication.Core.Types.FunctionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Delta.Helper.DeltaASHelper;
import HslCommunication.Profinet.Delta.Helper.DeltaDvpHelper;
import HslCommunication.Profinet.Delta.IDelta;
import HslCommunication.StringResources;

public class DeltaHelper {
    public static OperateResultExOne<String> TranslateToModbusAddress(IDelta delta, String address, byte modbusCode) {
        switch (delta.GetSeries()) {
            case Dvp: {
                return DeltaDvpHelper.ParseDeltaDvpAddress(address, modbusCode);
            }
            case AS: {
                return DeltaASHelper.ParseDeltaASAddress(address, modbusCode);
            }
        }
        return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
    }

    public static OperateResultExOne<boolean[]> ReadBool(IDelta delta, FunctionOperateExTwo<String, Short, OperateResultExOne<boolean[]>> readBoolFunc, String address, short length) {
        switch (delta.GetSeries()) {
            case Dvp: {
                return DeltaDvpHelper.ReadBool(readBoolFunc, address, length);
            }
            case AS: {
                return readBoolFunc.Action(address, length);
            }
        }
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
    }

    public static OperateResult Write(IDelta delta, FunctionOperateExTwo<String, boolean[], OperateResult> writeBoolFunc, String address, boolean[] values) {
        switch (delta.GetSeries()) {
            case Dvp: {
                return DeltaDvpHelper.Write(writeBoolFunc, address, values);
            }
            case AS: {
                return writeBoolFunc.Action(address, values);
            }
        }
        return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    public static OperateResultExOne<byte[]> Read(IDelta delta, FunctionOperateExTwo<String, Short, OperateResultExOne<byte[]>> readFunc, String address, short length) {
        switch (delta.GetSeries()) {
            case Dvp: {
                return DeltaDvpHelper.Read(readFunc, address, length);
            }
            case AS: {
                return readFunc.Action(address, length);
            }
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    public static OperateResult Write(IDelta delta, FunctionOperateExTwo<String, byte[], OperateResult> writeFunc, String address, byte[] value) {
        switch (delta.GetSeries()) {
            case Dvp: {
                return DeltaDvpHelper.Write(writeFunc, address, value);
            }
            case AS: {
                return writeFunc.Action(address, value);
            }
        }
        return new OperateResult(StringResources.Language.NotSupportedDataType());
    }
}

