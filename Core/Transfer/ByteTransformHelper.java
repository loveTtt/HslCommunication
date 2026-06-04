/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Transfer;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResultExOne;
import java.nio.charset.StandardCharsets;

public class ByteTransformHelper {
    public static <TResult> OperateResultExOne<TResult> GetResultFromBytes(OperateResultExOne<byte[]> result, FunctionOperateExOne<byte[], TResult> translator) {
        try {
            if (result.IsSuccess) {
                return OperateResultExOne.CreateSuccessResult(translator.Action((byte[])result.Content));
            }
            return OperateResultExOne.CreateFailedResult(result);
        }
        catch (Exception ex) {
            return new OperateResultExOne("\u6570\u636e\u8f6c\u5316\u5931\u8d25\uff0c\u6e90\u6570\u636e\uff1a" + SoftBasic.ByteToHexString((byte[])result.Content) + " \u6d88\u606f\uff1a" + ex.getMessage());
        }
    }

    public static <TResult> OperateResultExOne<TResult> GetResultFromArray(OperateResultExOne<TResult[]> result) {
        if (!result.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(result);
        }
        return OperateResultExOne.CreateSuccessResult(((TResult[])result.Content)[0]);
    }

    public static <TResult1, TResult2> OperateResultExOne<TResult1> GetTResultFromArray(OperateResultExOne<TResult2[]> result) {
        if (!result.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(result);
        }
        return OperateResultExOne.CreateSuccessResult((TResult1)((TResult2[])result.Content)[0]);
    }

    public static OperateResultExOne<Boolean> GetBoolResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Boolean>(){

            @Override
            public Boolean Action(byte[] content) {
                return byteTransform.TransBool(content, 0);
            }
        });
    }

    public static OperateResultExOne<Boolean> GetBoolResultFromArray(OperateResultExOne<boolean[]> result) {
        if (!result.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(result);
        }
        return OperateResultExOne.CreateSuccessResult(((boolean[])result.Content)[0]);
    }

    public static OperateResultExOne<Byte> GetByteResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Byte>(){

            @Override
            public Byte Action(byte[] content) {
                return byteTransform.TransByte(content, 0);
            }
        });
    }

    public static OperateResultExOne<Short> GetInt16ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Short>(){

            @Override
            public Short Action(byte[] content) {
                return byteTransform.TransInt16(content, 0);
            }
        });
    }

    public static OperateResultExOne<Integer> GetInt32ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Integer>(){

            @Override
            public Integer Action(byte[] content) {
                return byteTransform.TransInt32(content, 0);
            }
        });
    }

    public static OperateResultExOne<Long> GetInt64ResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Long>(){

            @Override
            public Long Action(byte[] content) {
                return byteTransform.TransInt64(content, 0);
            }
        });
    }

    public static OperateResultExOne<Float> GetSingleResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Float>(){

            @Override
            public Float Action(byte[] content) {
                return Float.valueOf(byteTransform.TransSingle(content, 0));
            }
        });
    }

    public static OperateResultExOne<Double> GetDoubleResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], Double>(){

            @Override
            public Double Action(byte[] content) {
                return byteTransform.TransDouble(content, 0);
            }
        });
    }

    public static OperateResultExOne<String> GetStringResultFromBytes(OperateResultExOne<byte[]> result, final IByteTransform byteTransform) {
        return ByteTransformHelper.GetResultFromBytes(result, new FunctionOperateExOne<byte[], String>(){

            @Override
            public String Action(byte[] content) {
                return byteTransform.TransString(content, 0, content.length, StandardCharsets.US_ASCII);
            }
        });
    }
}

