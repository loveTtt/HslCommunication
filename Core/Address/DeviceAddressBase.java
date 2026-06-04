/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

public class DeviceAddressBase {
    private int Address = 0;

    public int getAddress() {
        return this.Address;
    }

    public void setAddress(int address) {
        this.Address = address;
    }

    public void Parse(String address) {
        this.Address = Integer.parseInt(address);
    }

    public String toString() {
        return String.valueOf(this.Address);
    }
}

