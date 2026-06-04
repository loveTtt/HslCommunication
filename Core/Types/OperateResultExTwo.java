/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.FunctionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExFour;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;

public class OperateResultExTwo<T1, T2>
extends OperateResult {
    public T1 Content1 = null;
    public T2 Content2 = null;

    public OperateResultExTwo() {
    }

    public OperateResultExTwo(String msg) {
        super(msg);
    }

    public OperateResultExTwo(int err, String msg) {
        super(err, msg);
    }

    public static <T1, T2> OperateResultExTwo<T1, T2> CreateSuccessResult(T1 content1, T2 content2) {
        OperateResultExTwo<T1, T2> result = new OperateResultExTwo<T1, T2>();
        result.IsSuccess = true;
        result.Content1 = content1;
        result.Content2 = content2;
        result.Message = "success";
        return result;
    }

    public static <T1, T2> OperateResultExTwo<T1, T2> CreateFailedResult(OperateResult result) {
        OperateResultExTwo<T1, T2> resultExTwo = new OperateResultExTwo<T1, T2>();
        resultExTwo.CopyErrorFromOther(result);
        return resultExTwo;
    }

    public OperateResultExTwo<T1, T2> Check(FunctionOperateExTwo<T1, T2, Boolean> check, String message) {
        if (!this.IsSuccess) {
            return this;
        }
        if (check.Action(this.Content1, this.Content2).booleanValue()) {
            return this;
        }
        return new OperateResultExTwo<T1, T2>(message);
    }

    public OperateResultExTwo<T1, T2> Check(FunctionOperateExTwo<T1, T2, OperateResult> check) {
        if (!this.IsSuccess) {
            return this;
        }
        OperateResult checkResult = check.Action(this.Content1, this.Content2);
        if (!checkResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(checkResult);
        }
        return this;
    }

    public OperateResult Then(FunctionOperateExTwo<T1, T2, OperateResult> func) {
        if (this.IsSuccess) {
            return func.Action(this.Content1, this.Content2);
        }
        return this;
    }

    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExTwo<T1, T2, OperateResultExOne<TResult>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2) : OperateResultExOne.CreateFailedResult(this);
    }

    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExTwo<T1, T2, OperateResultExTwo<TResult1, TResult2>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2) : OperateResultExTwo.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExTwo<T1, T2, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2) : OperateResultExThree.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExTwo<T1, T2, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2) : OperateResultExFour.CreateFailedResult(this);
    }
}

