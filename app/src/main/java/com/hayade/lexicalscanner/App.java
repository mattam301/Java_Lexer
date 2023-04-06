package com.hayade.lexicalscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class App {

    @SuppressWarnings("java:S899")
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No input file");
            return;
        }

        InputStream dfaStream = ClassLoader.getSystemResourceAsStream("dfa.dat");
        LexicalScanner scanner = new LexicalScanner(dfaStream);
        for (String vcFile : args) {
            InputStream inputCodeStream = ClassLoader.getSystemResourceAsStream(vcFile);

            String filename = vcFile.substring(0, vcFile.length() - 3);
            String outputPath =
                    Objects.requireNonNull(App.class.getResource("")).getFile()
                            + filename + ".verbose.vctok";
            File outputFile = new File(outputPath);
            outputFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(outputFile);

            scanner.scan(inputCodeStream, outputStream, filename);
        }
    }

}
