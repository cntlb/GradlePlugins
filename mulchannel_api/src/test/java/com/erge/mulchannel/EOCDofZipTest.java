package com.erge.mulchannel;

public class EOCDofZipTest {

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

    /**
     * <table class="wikitable">
     * <caption>End of central directory record (EOCD)
     * </caption>
     * <tbody><tr>
     * <th>Offset</th>
     * <th>Bytes</th>
     * <th>Description<sup id="cite_ref-appnote_24-4" class="reference"><a href="#cite_note-appnote-24">[24]</a></sup>
     * </th></tr>
     * <tr>
     * <td>0</td>
     * <td>4</td>
     * <td>End of central directory signature = 0x06054b50
     * </td></tr>
     * <tr>
     * <td>4</td>
     * <td>2</td>
     * <td>Number of this disk
     * </td></tr>
     * <tr>
     * <td>6</td>
     * <td>2</td>
     * <td>Disk where central directory starts
     * </td></tr>
     * <tr>
     * <td>8</td>
     * <td>2</td>
     * <td>Number of central directory records on this disk
     * </td></tr>
     * <tr>
     * <td>10</td>
     * <td>2</td>
     * <td>Total number of central directory records
     * </td></tr>
     * <tr>
     * <td>12</td>
     * <td>4</td>
     * <td>Size of central directory (bytes)
     * </td></tr>
     * <tr>
     * <td>16</td>
     * <td>4</td>
     * <td>Offset of start of central directory, relative to start of archive
     * </td></tr>
     * <tr>
     * <td>20</td>
     * <td>2</td>
     * <td>Comment length (<i>n</i>)
     * </td></tr>
     * <tr>
     * <td>22</td>
     * <td><i>n</i></td>
     * <td>Comment
     * </td></tr></tbody></table>
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LeRandomAccessFile raf = new LeRandomAccessFile("test/1.apk", "r");
        int state = STAT0;
        long offset = 0;
        long length = raf.length();
        if (length > 3 * 22) {
            offset = length - 2 * 22;
            raf.seek(offset);
        }
        while (true) {
            if (offset >= length)
                break;
            if (state == STAT4) {
                System.out.println("get sequence 0x06054b50(LD), offset 0x" + Long.toHexString(offset - 4));
                break;
            }

            switch (raf.readByte()) {
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
                    } else {
                        state = STAT0;
                    }
                    break;
                default:
                    state = STAT0;
                    break;
            }
            offset++;
        }

        if (state == STAT4) {
            long EOCDoff = offset - 4;
            raf.seek(EOCDoff + 20);
            int EOCDsize = 22 + raf.readShort();
            raf.seek(EOCDoff);
            int signature = (raf.readInt());
            int diskNum = raf.readUnsignedShort();
            int centralStart = raf.readUnsignedShort();
            int centralNum = raf.readUnsignedShort();
            int centralNumTotal = raf.readUnsignedShort();
            int centralSize = (raf.readInt());
            int centralOff = (raf.readInt());
            int commentLength = (raf.readShort());
            byte[] cb = new byte[commentLength];
            raf.read(cb);
            String comment = new String(cb);

            System.out.println("signature=0x" + Integer.toHexString(signature));
            System.out.println("diskNum=" + diskNum);
            System.out.println("centralStart=" + centralStart);
            System.out.println("centralNum=" + centralNum);
            System.out.println("centralNumTotal=" + centralNumTotal);
            System.out.println("centralSize=" + centralSize);
            System.out.println("centralOff=0x" + Integer.toHexString(centralOff));
            System.out.println("commentLength=" + commentLength);
            System.out.println("comment=" + comment);
        }
    }

}
