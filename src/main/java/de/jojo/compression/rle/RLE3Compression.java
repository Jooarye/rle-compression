package de.jojo.compression.rle;

import de.jojo.compression.Compression;
import de.jojo.exceptions.CompressionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RLE3Compression implements Compression {

    @Override
    public void Compress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException {
        outputStream.write("RL3".getBytes());

        byte[] data = inputStream.readAllBytes();
        byte last = 0;
        byte count = 0;

        for (byte cur : data) {
            if (last == cur) {
                count++;
            } else {
                if (count == 0) {
                    last = cur;
                    count = 1;
                } else {
                    this.writeToCompressionStream(last, count, outputStream);
                    last = cur;
                    count = 1;
                }
            }
        }

        this.writeToCompressionStream(last, count, outputStream);
        outputStream.flush();
    }

    private void writeToCompressionStream(byte last, int count, OutputStream stream) throws IOException {
        if (count > 3) {
            for (; count > 256; count -= 256) {
                stream.write((byte) 0x90);
                stream.write((byte) count);
                stream.write(last);
            }

            stream.write((byte) 0x90);
            stream.write((byte) count);
            stream.write(last);
        } else {
            for (int i = 0; i < count; i++) {
                if (last == (byte) 0x90) {
                    stream.write((byte) 0x90);
                    stream.write((byte) 0);
                } else {
                    stream.write(last);
                }
            }
        }
    }

    @Override
    public void Decompress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException {
        if (inputStream.readNBytes(3).equals("RL3".getBytes())) {
            throw new CompressionException("Invalid magic number!");
        }

        byte[] data = inputStream.readAllBytes();
        int i = 0;

        while (i < data.length) {
            byte cur = data[i];

            if (cur == (byte) 0x90) {
                byte len = data[i + 1];

                if (len == 0) {
                    this.writeToDecompressionStream((byte) 0x90, 1, outputStream);
                    i += 2;
                } else {
                    byte tmp = data[i + 2];
                    this.writeToDecompressionStream(tmp, len, outputStream);
                    i += 3;
                }
            } else {
                this.writeToDecompressionStream(cur, 1, outputStream);
                i++;
            }
        }
        outputStream.flush();
    }

    private void writeToDecompressionStream(byte cur, int count, OutputStream stream) throws IOException {
        for (int i = 0; i < count; i++) {
            stream.write(cur);
        }
    }
}
