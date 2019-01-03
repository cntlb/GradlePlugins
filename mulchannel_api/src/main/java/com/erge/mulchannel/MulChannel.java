package com.erge.mulchannel;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MulChannel implements Closeable {
    private static final int N = 1<<9;
    //0x06054b50
    private static final int STAT0 = 0;
    private static final int STAT1 = 1;
    private static final int STAT2 = 2;
    private static final int STAT3 = 3;
    private static final int STAT4 = 4;

    private static final byte B1 = 0x50;
    private static final byte B2 = 0x4b;
    private static final byte B3 = 0x05;
    private static final byte B4 = 0x06;
    public static final int MAGIC = 0x06054b50;

    private long length;
    private LeRandomAccessFile laf;
    private EOCD eocd;
    private String apkPathName;

    public static String readComment(String apkPathName) throws IOException {
        MulChannel mc = new MulChannel();
        mc.init(apkPathName, "r");
        String comment = mc.getComment();
        mc.close();
        return comment;
    }

    public static String readComment(File apkPath) throws IOException {
        return readComment(apkPath.getAbsolutePath());
    }

    private MulChannel() {
    }

    public MulChannel(File apkPathName) throws IOException {
        init(apkPathName.getAbsolutePath(), "rw");
    }

    public MulChannel(String apkPathName) throws IOException {
        init(apkPathName, "rw");
    }

    public MulChannel(String apkPathName, String newApk) throws IOException {
        File toFile = new File(newApk);
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        FileUtil.copy(apkPathName, newApk);
        init(newApk, "rw");
    }

    private void init(String apkPathName, String mode) throws IOException {
        this.apkPathName = apkPathName;
        laf = new LeRandomAccessFile(this.apkPathName, mode);
        length = laf.length();
        if (length > 1024) {
            laf.seek(length - N);
            eocd = readEocd();
            if (eocd == null) {
                throw new IOException("no EOCD found in file " + apkPathName + ", are you sure that's a normal apk/zip file?");
            }
        }else{
            throw new IOException("length of "+apkPathName+" is "+length+"B (<= 1024B)");
        }
    }

    public void writeComment(String comment) throws IOException {
        length += utfLength(comment) - utfLength(eocd.comment);
        laf.setLength(length);
        laf.seek(eocd.commentOffset);
        laf.writeUTF(comment);
    }

    public EOCD getEocd() {
        return eocd;
    }

    public String getComment() {
        return eocd.comment;
    }

    public void close() throws IOException {
        laf.close();
    }

    public void writeEocd(EOCD eocd) throws IOException {
        if (eocd == null) {
            return;
        }

        if (eocd.signature != MAGIC) {
            throw new RuntimeException("illegal EOCD magic number: " + eocd.signature);
        }

        int dl = eocd.size() - this.eocd.size();
        length += dl;
        laf.setLength(length);

        eocd.offset = this.eocd.offset;
        laf.seek(eocd.offset);
        laf.writeInt(eocd.signature);
        laf.writeShort(eocd.diskNum);
        laf.writeShort(eocd.cdStarts);
        laf.writeShort(eocd.cdNum);
        laf.writeShort(eocd.cdTotal);
        laf.writeInt(eocd.cdSize);
        laf.writeInt(eocd.cdOffset);
        laf.writeUTF(eocd.comment);
        this.eocd = eocd;
    }

    private EOCD readEocd() throws IOException {
        int state = STAT0;
        int b;
        loop:
        while ((b = laf.read()) != -1) {
            switch (b) {
                case B1:
                    state = STAT1;
                    break;
                case B2:
                    if (state == STAT1) {
                        state = STAT2;
                    } else {
                        state = STAT0;
                    }
                    break;
                case B3:
                    if (state == STAT2) {
                        state = STAT3;
                    } else {
                        state = STAT0;
                    }
                    break;
                case B4:
                    if (state == STAT3) {
                        state = STAT4;
                        break loop;
                    } else {
                        state = STAT0;
                    }
                    break;
                default:
                    state = STAT0;
                    break;
            }
        }

        if (state != STAT4) {
            return null;
        }
        EOCD eocd = new EOCD();
        laf.skipBytes(-4);
        eocd.offset = laf.getFilePointer();
        eocd.signature = laf.readInt();
        eocd.diskNum = laf.readUnsignedShort();
        eocd.cdStarts = laf.readUnsignedShort();
        eocd.cdNum = laf.readUnsignedShort();
        eocd.cdTotal = laf.readUnsignedShort();
        eocd.cdSize = laf.readInt();
        eocd.cdOffset = laf.readInt();
        eocd.commentOffset = laf.getFilePointer();
        eocd.commentLength = laf.readUnsignedShort();
        laf.skipBytes(-2);
        eocd.comment = laf.readUTF();
        return eocd;
    }

    private static int utfLength(String comment) {
        try {
            return (comment == null ? 0 : comment.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
    }

    public static class EOCD {
        long offset;
        //Offset  Bytes   Description[24]
        //0   4   End of central directory signature = 0x06054b50
        int signature;
        //4   2   Number of this disk
        int diskNum;
        //6   2   Disk where central directory starts
        int cdStarts;
        //8   2   Number of central directory records on this disk
        int cdNum;
        //10  2   Total number of central directory records
        int cdTotal;
        //12  4   Size of central directory (bytes)
        int cdSize;
        //16  4   Offset of start of central directory, relative to start of archive
        int cdOffset;
        long commentOffset;
        //20  2   Comment length (n)
        int commentLength;
        //22  n   Comment
        String comment;

        public EOCD() {
            signature = MulChannel.MAGIC;
        }

        public int size() {
            return 22 + utfLength(comment);
        }
    }
}
