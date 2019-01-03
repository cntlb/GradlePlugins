package com.erge.mulchannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {
    public static void copy(String from, String to) {
        try {
            FileChannel inChannel = new FileInputStream(from).getChannel();
            FileChannel outChannel = new FileOutputStream(to).getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
            while (inChannel.read(buffer) != -1) {
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
            inChannel.close();
            outChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
