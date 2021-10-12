package de.jojo.compression.rle;

import de.jojo.compression.Compression;
import de.jojo.exceptions.CompressionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RLE2Compression implements Compression {

    @Override
    public void Compress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException {
        outputStream.write("RL2".getBytes());

        byte[] data = inputStream.readAllBytes();
        byte last = 0;
        int count = 0;

        for (byte cur : data) {
            if ((cur & 0x80) != 0)
                throw new CompressionException("This is not an ascii file, aborting!");

            if (last == cur) {
                count++;
            } else {
                if (count == 0) {
                    last = cur;
                    count = 1;
                    continue;
                }

                this.writeToCompressionStream(last, count, outputStream);
                last = cur;
                count = 1;
            }
        }

        this.writeToCompressionStream(last, count, outputStream);
        outputStream.flush();
    }

    private void writeToCompressionStream(byte last, int count, OutputStream stream) throws IOException {
        if (count <= 2) {
            for (int i = 0; i < count; i++) {
                stream.write(last);
            }
        } else {
            while (count > (byte) 127) {
                stream.write((byte) 0xff);
                stream.write(last);
                count -= 0x7f;
            }

            stream.write((byte) (0x80 | count));
            stream.write(last);
        }
    }

    @Override
    public void Decompress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException {
        if (inputStream.readNBytes(3).equals("RL2".getBytes())) {
            throw new CompressionException("Invalid magic number!");
        }

        byte[] data = inputStream.readAllBytes();

        for (int i = 0; i < data.length; i++) {
            byte cur = data[i];

            if ((cur & 0x80) != 0) {
                byte character = data[++i];

                if ((character & 0x80) != 0) {
                    throw new CompressionException("File corrupt, cannot have to modifier bytes in a row!");
                }
                this.writeToDecompressionStream(character, (int) (cur & 0x7f), outputStream);
            } else {
                this.writeToDecompressionStream(cur, 1, outputStream);
            }
        }
        outputStream.flush();
    }

    private void writeToDecompressionStream(byte last, int count, OutputStream stream) throws IOException {
        for (int i = 0; i < count; i++) {
            stream.write(last);
        }
    }

}
