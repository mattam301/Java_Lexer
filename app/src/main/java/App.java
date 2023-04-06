

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;


public class App {

    @SuppressWarnings("java:S899")
    public static void main(String[] args) throws IOException {
        
        if (args.length == 0) {
            System.out.println("No input file");
            return;
        }

        InputStream dfaStream = App.class.getResourceAsStream("/dfa.dat");
        LexicalScanner scanner = new LexicalScanner(dfaStream);
        for (String vcFile : args) {
            vcFile = "src/main/resources/" + vcFile;
            File file = new File(vcFile);
            if (!file.exists()) {
                System.out.println("Input file does not exist: " + vcFile);
                continue;
            } else{
                System.out.println("Input file có mà đây này " + vcFile);
            }

            //InputStream inputCodeStream = new FileInputStream(vcFile);
            String filename = vcFile.substring(0, vcFile.length() - 3);

            String outputPath = filename + ".verbose.vctok";
            //System.out.println("test " + outputPath);
            File outputFile = new File(outputPath);
            if (!outputFile.exists()) {
                System.out.println("Output file does not exist: " + outputFile);
                continue;
            } else{
                System.out.println("Output file có mà đây này: " + outputFile);
            }
            outputFile.createNewFile();


            try (InputStream inputCodeStream = new FileInputStream(vcFile);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputCodeStream);
                OutputStream outputStream = new FileOutputStream(outputFile)) {
                scanner.scan(bufferedInputStream, outputStream, filename);
            } catch (IOException e) {
                System.err.println("Error processing: " + e.getMessage());
        }
        }
    }

}
