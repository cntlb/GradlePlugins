package com.erge.mulchannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class LeRandomAccessFileTest {
    private static final String TEST_DIR = "test";
    private LeRandomAccessFile laf;

    @Before
    public void start() throws Exception {
        File file = new File(TEST_DIR);
        if (!file.exists())
            file.mkdirs();
        laf = new LeRandomAccessFile("test/1.txt", "rw");
    }

    @After
    public void testEnd() throws Exception {
        laf.close();
    }

    @Test
    public void setLength() throws Exception {
        laf.setLength(100);
        assert new File("test/1.txt").length() == 100;
    }

    @Test
    public void readShort() throws Exception {
        laf.writeShort(0x1234);
        laf.seek(0);
        assert laf.read() == 0x34 && laf.read() == 0x12;
        laf.seek(0);
        assert laf.readShort() == 0x1234;
    }

    @Test
    public void readUnsignedShort() throws Exception {
        laf.writeShort(0x1234);
        laf.seek(0);
        assert laf.read() == 0x34 && laf.read() == 0x12;
        laf.seek(0);
        assert laf.readUnsignedShort() == 0x1234;
    }

    @Test
    public void readChar() throws Exception {
        laf.write(1);
        laf.write(2);
        laf.seek(0);
        assert laf.read() == 1 && laf.read() == 2;
        laf.seek(0);
        assert laf.readChar() == 0x201;
    }

    @Test
    public void readInt() throws Exception {
        laf.write(1);
        laf.write(2);
        laf.write(3);
        laf.write(4);
        laf.seek(0);
        assert laf.readInt() == 0x04030201;
    }

    @Test
    public void readLong() throws Exception {
        laf.write(1);
        laf.write(2);
        laf.write(3);
        laf.write(4);
        laf.write(5);
        laf.write(6);
        laf.write(7);
        laf.write(8);

        laf.seek(0);
        assert laf.readLong() == 0x0807060504030201L;
    }

    @Test
    public void readFloat() throws Exception {
        laf.writeFloat(0.5f);
        laf.seek(0);
        assert laf.readInt() == Float.floatToIntBits(0.5f);
        laf.seek(0);
        assert Float.compare(laf.readFloat(), 0.5f) == 0;
    }

    @Test
    public void readDouble() throws Exception {
        laf.writeDouble(0.5);
        laf.seek(0);
        assert laf.readLong() == Double.doubleToLongBits(0.5);
        laf.seek(0);
        assert Double.compare(laf.readDouble(), 0.5) == 0;
    }

    @Test
    public void readUTF() throws Exception {
        String s = "abcdefghijklmn中国";
        laf.writeUTF(s);
        laf.seek(0);
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        assert laf.readUnsignedShort() == bytes.length;
        laf.skipBytes(-2);
        assert laf.readUTF().equals(s);
    }

    @Test
    public void writeUTF() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("test/2.txt", "rw");
        raf.writeUTF("abcdefghijklmn汉字");
        raf.seek(0);
        System.out.println(raf.readUTF());
        raf.close();
    }

    @Test
    public void skipBytes() throws Exception {
        laf.seek(50);

        assert laf.skipBytes(5)==5;
        assert laf.getFilePointer() == 55;

        assert laf.skipBytes(-5)==5;
        assert laf.getFilePointer() == 50;

        assert laf.skipBytes(-500) == 50;
        assert laf.getFilePointer() == 0;
    }
}