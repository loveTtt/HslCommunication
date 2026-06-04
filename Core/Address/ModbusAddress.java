/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.Core.Address.DeviceAddressBase;

public class ModbusAddress
extends DeviceAddressBase {
    private int Station = -1;
    private int Function = 0;
    private int WriteFunction = -1;

    public ModbusAddress() {
        this.Function = -1;
        this.setAddress(0);
    }

    public ModbusAddress(String address) {
        this.Function = -1;
        this.setAddress(0);
        this.Parse(address);
    }

    public ModbusAddress(String address, byte function) {
        this.Function = function;
        this.setAddress(0);
        this.Parse(address);
    }

    public ModbusAddress(String address, byte station, byte function) {
        this.Function = function;
        this.Station = station;
        this.setAddress(0);
        this.Parse(address);
    }

    public int getStation() {
        return this.Station;
    }

    public void setStation(int station) {
        this.Station = station;
    }

    public int getFunction() {
        return this.Function;
    }

    public void setFunction(int function) {
        this.Function = function;
    }

    public void setWriteFunction(int value) {
        this.WriteFunction = value;
    }

    public int getWriteFunction() {
        return this.WriteFunction;
    }

    @Override
    public void Parse(String address) {
        if (address.indexOf(59) < 0) {
            this.setAddress(Integer.parseInt(address));
        } else {
            String[] list = address.split(";");
            for (int i = 0; i < list.length; ++i) {
                if (list[i].charAt(0) == 's' || list[i].charAt(0) == 'S') {
                    this.Station = Integer.parseInt(list[i].substring(2));
                    continue;
                }
                if (list[i].charAt(0) == 'x' || list[i].charAt(0) == 'X') {
                    this.Function = Integer.parseInt(list[i].substring(2));
                    continue;
                }
                if (list[i].startsWith("w=") || list[i].startsWith("W=")) {
                    this.WriteFunction = Integer.parseInt(list[i].substring(2));
                    continue;
                }
                this.setAddress(Integer.parseInt(list[i]));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.Station >= 0) {
            sb.append("s=" + this.Station + ";");
        }
        if (this.Function >= 1) {
            sb.append("x=" + this.Function + ";");
        }
        sb.append(String.valueOf(this.getAddress()));
        return sb.toString();
    }

    public ModbusAddress AddressAdd(int value) {
        ModbusAddress address = new ModbusAddress();
        address.setStation(this.getStation());
        address.setFunction(this.getFunction());
        address.setAddress(this.getAddress() + value);
        return address;
    }

    public ModbusAddress AddressAdd() {
        return this.AddressAdd(1);
    }
}

