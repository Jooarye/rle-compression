package de.jojo.main;

import de.jojo.compression.*;
import de.jojo.compression.rle.RLE2Compression;
import de.jojo.compression.rle.RLE3Compression;
import de.jojo.exceptions.CompressionException;

import de.jojo.exceptions.FormatException;
import org.apache.commons.cli.*;

import java.io.*;

public class Main {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("c", "compress", false, "Compress file");
        options.addOption("d", "uncompress", false, "Uncompress file");
        options.addOption("i", "inputfile", true, "Input file");
        options.addOption("o", "outputfile", true, "Output file");
        options.addOption("t", "type", true, "Compression type");
        options.addOption("h", "help", false, "Display help menu");
        options.addOption("v", "verbose", false, "Verbose output");

        CommandLineParser parser = new BasicParser();
        CommandLine result = parser.parse(options, args);

        if (result.hasOption("h") || args.length == 0) {
            HelpFormatter fmt = new HelpFormatter();
            fmt.printHelp("runlength", options);
            System.exit(0);
        }

        if (!result.hasOption("c") && !result.hasOption("d")) {
            System.err.println("[ERR] One of -d / -c is needed!");
            System.exit(1);
        }

        if (result.hasOption("c") ^ result.hasOption("d")) {
            Compression compression = new RLE3Compression();

            if (!result.hasOption("i")) {
                System.err.println("[ERR] Input file is required!");
                System.exit(1);
            }

            if (result.hasOption("t")) {
                switch (result.getOptionValue("t")) {
                    case "2":
                        compression = new RLE2Compression();

                        if (result.hasOption("v"))
                            System.out.println("[INFO] Compression algorithm set to RLed2");

                        break;
                    case "3":
                        compression = new RLE3Compression();

                        if (result.hasOption("v"))
                            System.out.println("[INFO] Compression algorithm set to RLed3");

                        break;
                    default:
                        System.err.println("[ERR] Type can only accept '2' for RLed2 or '3' for RLed3.");
                        System.exit(1);
                }
            }

            String outputPath = result.getOptionValue("i") +
                    (compression instanceof RLE3Compression ? ".rld3" : ".rld2");

            if (result.hasOption('o')) {
                outputPath = result.getOptionValue('o');
            } else if (result.hasOption("v"))
                System.out.printf("[INFO] No output file specified using '%s'%n", outputPath);

            FileInputStream fis = null;
            FileOutputStream fos = null;

            try {
                fis = new FileInputStream(result.getOptionValue("i"));
            } catch (IOException e) {
                System.err.println("[ERR] Error whilst reading source file!");
                System.exit(2);
            }

            try {
                fos = new FileOutputStream(outputPath);
            } catch (IOException e) {
                System.err.println("[ERR] Error whilst reading destination file!");
                System.exit(3);
            }

            try {
                if (result.hasOption('d')) {
                    compression.Decompress(fis, fos);
                } else {
                    compression.Compress(fis, fos);
                }
            } catch (CompressionException | IOException e) {
                System.err.printf("[ERR] %s%n", e.getMessage());
                System.exit(5);
            } catch (FormatException e) {
                System.err.printf("[ERR] %s%n", e.getMessage());
                System.exit(4);
            }

            try {
                fis.close();
            } catch (IOException e) {
                System.err.println("[ERR] Failed to close input file!");
                System.exit(2);
            }

            try {
                fos.close();
            } catch (IOException e) {
                System.err.println("[ERR] Failed to close output file!");
                System.exit(3);
            }
        } else {
            System.err.println("[ERR] Can only use -o and -d separately!");
            System.exit(1);
        }

        if (result.hasOption("v"))
            System.out.printf("[INFO] %s was completed without issues.%n",
                    result.hasOption('d') ? "Decompression" : "Compression");

        System.exit(0);
    }
}
