/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

public class ValueLimit {
    public double MaxValue = 0.0;
    public double MinValue = 0.0;
    public double Average = 0.0;
    public double StartValue = 0.0;
    public double Current = 0.0;
    public int Count = 0;

    public ValueLimit SetNewValue(double value) {
        if (!Double.isNaN(value)) {
            if (this.Count == 0) {
                this.MaxValue = value;
                this.MinValue = value;
                this.Count = 1;
                this.Current = value;
                this.Average = value;
                this.StartValue = value;
            } else {
                if (value < this.MinValue) {
                    this.MinValue = value;
                }
                if (value > this.MaxValue) {
                    this.MaxValue = value;
                }
                this.Current = value;
                this.Average = ((double)this.Count * this.Average + value) / (double)(this.Count + 1);
                ++this.Count;
            }
        }
        return this;
    }

    public String toString() {
        return "Avg[" + this.Current + "]";
    }
}

