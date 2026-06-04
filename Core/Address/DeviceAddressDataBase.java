/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Address;

import HslCommunication.StringResources;

public class DeviceAddressDataBase {
    private int AddressStart = 0;
    private int Length = 0;

    public int getAddressStart() {
        return this.AddressStart;
    }

    public void setAddressStart(int addressStart) {
        this.AddressStart = addressStart;
    }

    public void setAddressOffset(int offset) {
        this.AddressStart += offset;
    }

    public int getLength() {
        return this.Length;
    }

    public void setLength(int length) {
        this.Length = length;
    }

    public void Parse(String address, int length) {
        this.AddressStart = Integer.parseInt(address);
        this.Length = length;
    }

    public String toString() {
        return String.valueOf(this.AddressStart);
    }

    public static String GetUnsupportedAddressInfo(String address, Exception ex) {
        if (ex == null) {
            return "Address[" + address + "]: " + StringResources.Language.NotSupportedDataType();
        }
        return "Parse [" + address + "] failed: " + ex.getMessage();
    }
}

