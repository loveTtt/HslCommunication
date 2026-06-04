/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import java.util.ArrayList;

public class List<T>
extends ArrayList<T> {
    public void Add(T[] items) {
        for (int i = 0; i < items.length; ++i) {
            this.add(items[i]);
        }
    }

    public void Add(ArrayList<T> items) {
        for (int i = 0; i < items.size(); ++i) {
            this.add(items.get(i));
        }
    }

    public void Add(T item) {
        this.add(item);
    }

    public double[] toDoubleArray() {
        double[] values = new double[this.size()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = (Double)this.get(i);
        }
        return values;
    }

    public String[] toStringArray() {
        String[] values = new String[this.size()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = (String)this.get(i);
        }
        return values;
    }

    public int[] toInt32Array() {
        int[] values = new int[this.size()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = (Integer)this.get(i);
        }
        return values;
    }

    public int getCount() {
        return this.size();
    }
}

