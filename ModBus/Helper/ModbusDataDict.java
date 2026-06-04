/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus.Helper;

import HslCommunication.ModBus.Helper.ModbusDataPool;
import java.util.HashMap;
import java.util.Iterator;

public class ModbusDataDict {
    private HashMap<Integer, ModbusDataPool> dictModbusDataPool = new HashMap();
    private boolean stationDataIsolation = false;

    public ModbusDataDict() {
        this.dictModbusDataPool.put(1, new ModbusDataPool(1));
    }

    public void Set(boolean stationDataIsolation) {
        if (this.stationDataIsolation != stationDataIsolation) {
            this.stationDataIsolation = stationDataIsolation;
            if (this.stationDataIsolation) {
                this.dictModbusDataPool = new HashMap();
                for (int i = 0; i < 255; ++i) {
                    this.dictModbusDataPool.put(i, new ModbusDataPool((byte)i));
                }
            } else {
                this.dictModbusDataPool = new HashMap();
                this.dictModbusDataPool.put(0, new ModbusDataPool(0));
            }
        }
    }

    public ModbusDataPool GetModbusPool(byte station) {
        if (this.stationDataIsolation) {
            return this.dictModbusDataPool.get(station);
        }
        Iterator<ModbusDataPool> iterator = this.dictModbusDataPool.values().iterator();
        if (iterator.hasNext()) {
            ModbusDataPool modbusPool = iterator.next();
            return modbusPool;
        }
        return null;
    }

    public void Dispose() {
        this.dictModbusDataPool.clear();
    }
}

