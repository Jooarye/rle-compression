package de.jojo.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Compression {

    void Compress(InputStream inputStream, OutputStream outputStream) throws IOException;
    void Decompress(InputStream inputStream, OutputStream outputStream) throws IOException;

}
