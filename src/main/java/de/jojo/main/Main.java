package de.jojo.main;

import de.jojo.compression.*;
import de.jojo.compression.rle.RLE2Compression;
import de.jojo.compression.rle.RLE3Compression;
import org.apache.commons.cli.*;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();

        options.addOption("a", "algorithm", true, "Algorithm");
        options.addOption("d", "decompress", false, "Decompress");
        options.addOption("o", "output", true, "Output file");

        CommandLineParser parser = new BasicParser();
        CommandLine result = parser.parse(options, args);

        if (result.getArgs().length != 1) {
            System.err.println("Need input file");
            return;
        }

        Compression compression;

        switch (result.getOptionValue('a')) {
            case "RLE2":
                compression = new RLE2Compression();
                break;
            case "RLE3":
                compression = new RLE3Compression();
                break;
            default:
                System.err.println("Algorithm can only be RLE2 or RLE3!");
                return;
        }

        String outputPath = result.getArgs()[0] + ".comp";

        if (result.hasOption('o')) {
            outputPath = result.getOptionValue('o');
        }

        FileInputStream fis = new FileInputStream(result.getArgs()[0]);
        FileOutputStream fos = new FileOutputStream(outputPath);

        if (result.hasOption('d')) {
            compression.Decompress(fis, fos);
        } else {
            compression.Compress(fis, fos);
        }

        fis.close();
        fos.close();
    }

}
