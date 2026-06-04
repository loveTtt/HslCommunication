/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.FunctionOperateExFour;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;

public class OperateResultExFour<T1, T2, T3, T4>
extends OperateResult {
    public T1 Content1 = null;
    public T2 Content2 = null;
    public T3 Content3 = null;
    public T4 Content4 = null;

    public OperateResultExFour() {
    }

    public OperateResultExFour(String msg) {
        super(msg);
    }

    public OperateResultExFour(int err, String msg) {
        super(err, msg);
    }

    public static <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> CreateSuccessResult(T1 content1, T2 content2, T3 content3, T4 content4) {
        OperateResultExFour<T1, T2, T3, T4> result = new OperateResultExFour<T1, T2, T3, T4>();
        result.IsSuccess = true;
        result.Content1 = content1;
        result.Content2 = content2;
        result.Content3 = content3;
        result.Content4 = content4;
        result.Message = "success";
        return result;
    }

    public static <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> CreateFailedResult(OperateResult result) {
        OperateResultExFour<T1, T2, T3, T4> resultExFour = new OperateResultExFour<T1, T2, T3, T4>();
        resultExFour.CopyErrorFromOther(result);
        return resultExFour;
    }

    public OperateResultExFour<T1, T2, T3, T4> Check(FunctionOperateExFour<T1, T2, T3, T4, Boolean> check, String message) {
        if (!this.IsSuccess) {
            return this;
        }
        if (check.Action(this.Content1, this.Content2, this.Content3, this.Content4).booleanValue()) {
            return this;
        }
        return new OperateResultExFour<T1, T2, T3, T4>(message);
    }

    public OperateResultExFour<T1, T2, T3, T4> Check(FunctionOperateExFour<T1, T2, T3, T4, OperateResult> check) {
        if (!this.IsSuccess) {
            return this;
        }
        OperateResult checkResult = check.Action(this.Content1, this.Content2, this.Content3, this.Content4);
        if (!checkResult.IsSuccess) {
            return OperateResultExFour.CreateFailedResult(checkResult);
        }
        return this;
    }

    public OperateResult Then(FunctionOperateExFour<T1, T2, T3, T4, OperateResult> func) {
        if (this.IsSuccess) {
            return func.Action(this.Content1, this.Content2, this.Content3, this.Content4);
        }
        return this;
    }

    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExOne<TResult>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3, this.Content4) : OperateResultExOne.CreateFailedResult(this);
    }

    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExTwo<TResult1, TResult2>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3, this.Content4) : OperateResultExTwo.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3, this.Content4) : OperateResultExThree.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExFour<T1, T2, T3, T4, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3, this.Content4) : OperateResultExFour.CreateFailedResult(this);
    }
}

