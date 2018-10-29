package com.radcheb.sysdis.utils.shuffle;

public class CopyTask {

    private final String Key;
    private final String umFile;
    private final String dstHost;
    private final String srcHost;

    public CopyTask(String key, String dstHost, String srcHost, String umFile) {
        Key = key;
        this.dstHost = dstHost;
        this.srcHost = srcHost;
        this.umFile = umFile;
    }

    public String getKey() {
        return Key;
    }

    public String getDstHost() {
        return dstHost;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public String getUmFile() {
        return umFile;
    }

    @Override
    public int hashCode() {
        return (umFile + dstHost).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        boolean result;
        if ((other == null) || (getClass() != other.getClass())) {
            result = false;
        } // end if
        else {
            CopyTask otherTask = (CopyTask) other;
            result = this.umFile.equals(otherTask.getUmFile()) && this.dstHost.equals(otherTask.dstHost);
        } // end else

        return result;
    }

}
