package com.erge.mulchannel;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Little endian version of {@link RandomAccessFile}, whose default endian is big.
 * Override multi bytes reading methods to little endian below:
 * <ul>
 * <li>{@link #readShort()}</li>
 * <li>{@link #readUnsignedShort()}</li>
 * <li>{@link #readChar()}</li>
 * <li>{@link #readInt()}</li>
 * <li>{@link #readFloat()} </li>
 * <li>{@link #readLong()}  </li>
 * <li>{@link #readDouble()}  </li>
 * </ul>
 * and corresponding {@code write} methods.
 */
public class LeRandomAccessFile implements DataOutput, DataInput, Closeable {
    private RandomAccessFile af;

    public LeRandomAccessFile(String name, String mode) throws FileNotFoundException {
        af = new RandomAccessFile(name, mode);
    }

    public LeRandomAccessFile(File file, String mode) throws FileNotFoundException {
        af = new RandomAccessFile(file, mode);
    }

    @Override
    public void close() throws IOException {
        af.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        af.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        af.readFully(b, off, len);
    }

    /**
     * The actual number of bytes skipped is returned.  If {@code n}
     * is negative, bytes are skipped back.
     */
    @Override
    public int skipBytes(int n) throws IOException {
        if (n >= 0)
            return af.skipBytes(n);
        long filePointer = getFilePointer();
        if (filePointer <= -n) {
            seek(0);
            return (int) filePointer;
        }
        seek(filePointer + n);
        return -n;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return af.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return af.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return af.readUnsignedByte();
    }

    /**
     * Little endian version of {@link RandomAccessFile#readShort()}
     * <p></p>
     * Reads a signed 16-bit number from this file. The method reads two
     * bytes from this file, starting at the current file pointer.
     * If the two bytes read, in order, are
     * {@code b1} and {@code b2}, where each of the two values is
     * between {@code 0} and {@code 255}, inclusive, then the
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b2 &lt;&lt; 8) | b1)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a signed
     * 16-bit number.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    @Override
    public short readShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch2 << 8) | (ch1));
    }

    /**
     * Little endian version of {@link RandomAccessFile#readUnsignedShort()}
     * <p></p>
     * Reads an unsigned 16-bit number from this file. This method reads
     * two bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b2 &lt;&lt; 8) | b1
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as an unsigned
     * 16-bit integer.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    @Override
    public int readUnsignedShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch2 << 8) | (ch1);
    }

    /**
     * Little endian version of {@link RandomAccessFile#readChar()}
     * <p></p>
     * Reads a character from this file. This method reads two
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (char)((b2 &lt;&lt; 8) | b1)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this file, interpreted as a
     * {@code char}.
     * @throws EOFException if this file reaches the end before reading
     *                      two bytes.
     * @throws IOException  if an I/O error occurs.
     */
    @Override
    public char readChar() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char) ((ch2 << 8) | ch1);
    }

    /**
     * Little endian version of {@link RandomAccessFile#readInt()}
     * <p></p>
     * Reads a signed 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b4 &lt;&lt; 24) | (b3 &lt;&lt; 16) | (b2 &lt;&lt; 8) | b1
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as an
     * {@code int}.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     */
    @Override
    public int readInt() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) | (ch3 << 16) | (ch2 << 8) | (ch1));
    }

    /**
     * Little endian version of {@link RandomAccessFile#readLong()}
     * <p></p>
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1}, {@code b2}, {@code b3},
     * {@code b4}, {@code b5}, {@code b6},
     * {@code b7}, and {@code b8,} where:
     * <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote>
     * <p>
     * then the result is equal to:
     * <blockquote><pre>
     *     ((long)b8 &lt;&lt; 56) + ((long)b7 &lt;&lt; 48)
     *     + ((long)b6 &lt;&lt; 40) + ((long)b5 &lt;&lt; 32)
     *     + ((long)b4 &lt;&lt; 24) + ((long)b3 &lt;&lt; 16)
     *     + ((long)b2 &lt;&lt; 8) + b1
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a
     * {@code long}.
     * @throws EOFException if this file reaches the end before reading
     *                      eight bytes.
     * @throws IOException  if an I/O error occurs.
     */
    @Override
    public long readLong() throws IOException {
        return (readInt() & 0xFFFFFFFFL) + ((long) (readInt()) << 32);
    }

    /**
     * Little endian version of {@link RandomAccessFile#readFloat()}
     * <p></p>
     * Reads a {@code float} from this file. This method reads an
     * {@code int} value, starting at the current file pointer,
     * as if by the {@code readInt} method
     * and then converts that {@code int} to a {@code float}
     * using the {@code intBitsToFloat} method in class
     * {@code Float}.
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this file, interpreted as a
     * {@code float}.
     * @throws EOFException if this file reaches the end before reading
     *                      four bytes.
     * @throws IOException  if an I/O error occurs.
     * @see #readInt()
     * @see java.lang.Float#intBitsToFloat(int)
     */
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Little endian version of {@link RandomAccessFile#readDouble()}
     * <p></p>
     * Reads a {@code double} from this file. This method reads a
     * {@code long} value, starting at the current file pointer,
     * as if by the {@code readLong} method
     * and then converts that {@code long} to a {@code double}
     * using the {@code longBitsToDouble} method in
     * class {@code Double}.
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next eight bytes of this file, interpreted as a
     * {@code double}.
     * @throws EOFException if this file reaches the end before reading
     *                      eight bytes.
     * @throws IOException  if an I/O error occurs.
     * @see #readLong()
     * @see java.lang.Double#longBitsToDouble(long)
     */
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readLine() throws IOException {
        return af.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int len = readUnsignedShort();
        byte[] b = new byte[len];
        read(b);
        return new String(b, Charset.forName("UTF-8"));
    }

    public int read(byte b[]) throws IOException {
        return af.read(b);
    }

    /**
     * {@link RandomAccessFile#read()}
     */
    public int read() throws IOException {
        return af.read();
    }


    @Override
    public void write(int b) throws IOException {
        af.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        af.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        af.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        af.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        af.writeByte(v);
    }


    /**
     * Little endian version of {@link RandomAccessFile#writeShort(int)}
     */
    @Override
    public void writeShort(int v) throws IOException {
        write((v >>> 0) & 0xFF);
        write((v >>> 8) & 0xFF);
        //written += 2;
    }

    /**
     * Little endian version of {@link RandomAccessFile#writeChar(int)}
     */
    @Override
    public void writeChar(int v) throws IOException {
        write((v >>> 0) & 0xFF);
        write((v >>> 8) & 0xFF);
        //written += 2;
    }

    /**
     * Little endian version of {@link RandomAccessFile#writeInt(int)}
     */
    @Override
    public void writeInt(int v) throws IOException {
        write((v >>> 0) & 0xFF);
        write((v >>> 8) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>> 24) & 0xFF);
        //written += 4;
    }


    /**
     * Little endian version of {@link RandomAccessFile#writeLong(long)}
     */
    @Override
    public void writeLong(long v) throws IOException {
        write((int) (v >>> 0) & 0xFF);
        write((int) (v >>> 8) & 0xFF);
        write((int) (v >>> 16) & 0xFF);
        write((int) (v >>> 24) & 0xFF);
        write((int) (v >>> 32) & 0xFF);
        write((int) (v >>> 40) & 0xFF);
        write((int) (v >>> 48) & 0xFF);
        write((int) (v >>> 56) & 0xFF);
        //written += 8;
    }

    /**
     * Little endian version of {@link RandomAccessFile#writeFloat(float)}
     */
    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Little endian version of {@link RandomAccessFile#writeDouble(double)}
     */
    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        af.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        af.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        if (s == null) {
            writeShort(0);
        } else {
            byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
            writeShort(bytes.length);
            write(bytes);
        }
    }

    public final FileChannel getChannel() {
        return af.getChannel();
    }

    public final FileDescriptor getFD() throws IOException {
        return af.getFD();
    }

    public long getFilePointer() throws IOException {
        return af.getFilePointer();
    }

    public void seek(long pos) throws IOException {
        af.seek(pos);
    }

    public long length() throws IOException {
        return af.length();
    }

    public void setLength(long newLength) throws IOException {
        af.setLength(newLength);
    }
}
