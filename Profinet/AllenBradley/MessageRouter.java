/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import java.nio.charset.StandardCharsets;

public class MessageRouter {
    private byte[] _router = new byte[6];

    public MessageRouter() {
        this._router[0] = 1;
        System.arraycopy(new byte[]{15, 2, 18, 1}, 0, this._router, 1, 4);
        this._router[5] = 12;
    }

    public MessageRouter(String router) {
        String[] splits = router.split("\\.");
        if (splits.length <= 6) {
            if (splits.length > 0) {
                this._router[0] = (byte)Integer.parseInt(splits[0]);
            }
            if (splits.length > 1) {
                this._router[1] = (byte)Integer.parseInt(splits[1]);
            }
            if (splits.length > 2) {
                this._router[2] = (byte)Integer.parseInt(splits[2]);
            }
            if (splits.length > 3) {
                this._router[3] = (byte)Integer.parseInt(splits[3]);
            }
            if (splits.length > 4) {
                this._router[4] = (byte)Integer.parseInt(splits[4]);
            }
            if (splits.length > 5) {
                this._router[5] = (byte)Integer.parseInt(splits[5]);
            }
        } else if (splits.length == 9) {
            String ip = splits[3] + "." + splits[4] + "." + splits[5] + "." + splits[6];
            this._router = new byte[6 + ip.length()];
            this._router[0] = (byte)Integer.parseInt(splits[0]);
            this._router[1] = (byte)Integer.parseInt(splits[1]);
            this._router[2] = (byte)(16 + Integer.parseInt(splits[2]));
            this._router[3] = (byte)ip.length();
            byte[] bufferIp = ip.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(bufferIp, 0, this._router, 4, bufferIp.length);
            this._router[this._router.length - 2] = (byte)Integer.parseInt(splits[7]);
            this._router[this._router.length - 1] = (byte)Integer.parseInt(splits[8]);
        }
    }

    public MessageRouter(byte[] router) {
        this._router = router;
    }

    public byte[] GetRouter() {
        return this._router;
    }

    public byte[] GetRouterCIP() {
        byte[] router = this.GetRouter();
        if (router.length % 2 == 1) {
            router = SoftBasic.SpliceArray(router, new byte[]{0});
        }
        byte[] routerCip = new byte[46 + router.length];
        byte[] buffer1 = SoftBasic.HexStringToBytes("54022006240105f70200 00800100fe8002001b05 28a7fd03020000008084 1e00f44380841e00f443 a305");
        System.arraycopy(buffer1, 0, routerCip, 0, buffer1.length);
        System.arraycopy(router, 0, routerCip, 42, router.length);
        System.arraycopy(new byte[]{32, 2, 36, 1}, 0, routerCip, 42 + router.length, 4);
        routerCip[41] = (byte)(router.length / 2);
        return routerCip;
    }

    public byte getBackplane() {
        return this._router[0];
    }

    public void setBackplane(byte value) {
        this._router[0] = value;
    }

    public byte getSlot() {
        return this._router[5];
    }

    public void setSlot(byte value) {
        this._router[5] = value;
    }
}

