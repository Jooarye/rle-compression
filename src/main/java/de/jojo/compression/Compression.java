package de.jojo.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.jojo.exceptions.CompressionException;
import de.jojo.exceptions.FormatException;

public interface Compression {

    void Compress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException, FormatException;
    void Decompress(InputStream inputStream, OutputStream outputStream) throws IOException, CompressionException, FormatException;

}
