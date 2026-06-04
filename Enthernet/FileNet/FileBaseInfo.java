/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Enthernet.FileNet;

public class FileBaseInfo {
    private String Name;
    private long Size;
    private String Tag;
    private String Upload;

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public long getSize() {
        return this.Size;
    }

    public void setSize(long size) {
        this.Size = size;
    }

    public String getTag() {
        return this.Tag;
    }

    public void setTag(String tag) {
        this.Tag = tag;
    }

    public String getUpload() {
        return this.Upload;
    }

    public void setUpload(String upload) {
        this.Upload = upload;
    }
}

