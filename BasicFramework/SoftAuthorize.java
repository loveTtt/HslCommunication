/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.BasicFramework;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoftAuthorize {
    public static String GetInfo() {
        try {
            String diskSN;
            ArrayList<String> hardwareInfos = new ArrayList<String>();
            hardwareInfos.addAll(SoftAuthorize.getMacAddresses());
            String motherboardSN = SoftAuthorize.getMotherboardSerialNumber();
            if (motherboardSN != null && !motherboardSN.isEmpty()) {
                hardwareInfos.add(motherboardSN);
            }
            if ((diskSN = SoftAuthorize.getDiskSerialNumber()) != null && !diskSN.isEmpty()) {
                hardwareInfos.add(diskSN);
            }
            if (hardwareInfos.isEmpty()) {
                return "DEFAULT_MACHINE_CODE";
            }
            Collections.sort(hardwareInfos);
            StringBuilder sb = new StringBuilder();
            for (String info : hardwareInfos) {
                sb.append(info);
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "ERROR_MACHINE_CODE";
        }
    }

    private static List<String> getMacAddresses() {
        ArrayList<String> macs = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(System.getProperty("os.name").toLowerCase().contains("windows") ? "ipconfig /all" : "ifconfig -a");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));){
                String line;
                while ((line = reader.readLine()) != null) {
                    String mac;
                    if (!(line = line.toLowerCase()).contains("\u7269\u7406\u5730\u5740") && !line.contains("mac address") || (mac = line.split(":")[1].trim().replaceAll("-", "").replaceAll(":", "")).length() != 12) continue;
                    macs.add(mac);
                }
            }
            process.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return macs;
    }

    private static String getMotherboardSerialNumber() {
        String result = "";
        try {
            Process process;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
            } else if (os.contains("mac")) {
                process = Runtime.getRuntime().exec("ioreg -l | grep IOPlatformSerialNumber");
            } else if (os.contains("linux")) {
                process = Runtime.getRuntime().exec("dmidecode -s baseboard-serial-number");
            } else {
                return result;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));){
                String line;
                while ((line = reader.readLine()) != null) {
                    if ((line = line.trim()).isEmpty() || line.equalsIgnoreCase("serialnumber")) continue;
                    result = line;
                    break;
                }
            }
            process.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getDiskSerialNumber() {
        String result = "";
        try {
            Process process;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                process = Runtime.getRuntime().exec("wmic diskdrive get serialnumber");
            } else if (os.contains("mac")) {
                process = Runtime.getRuntime().exec("diskutil info / | grep 'Volume Serial Number'");
            } else if (os.contains("linux")) {
                process = Runtime.getRuntime().exec("hdparm -I /dev/sda | grep 'Serial Number'");
            } else {
                return result;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));){
                String line;
                while ((line = reader.readLine()) != null) {
                    if ((line = line.trim()).isEmpty() || line.equalsIgnoreCase("serialnumber")) continue;
                    result = line.replaceAll("^.*: ", "").trim();
                    break;
                }
            }
            process.waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

