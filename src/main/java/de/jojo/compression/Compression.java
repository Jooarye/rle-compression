package de.jojo.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.jojo.exceptions.CompressionException;

public interface Compression {

    void Compress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException;
    void Decompress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException;

}
