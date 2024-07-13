import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
        throw new IllegalArgumentException("2 arguments required: Main [input file] [output file]");
    }

	Pharmacy pharmacy = new Pharmacy();

    String inputFileName = args[0];
    String outputFileName = args[1];

    FileReader fileReader = new FileReader(inputFileName);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    FileWriter fileWriter = new FileWriter(outputFileName);
    String line;

    int prescriptionCount = 0;

    while ((line = bufferedReader.readLine()) != null) {
        if (line.contains("DATE")) {
            LocalDate newDate = LocalDate.parse(line.split("[ \t]+")[1]);

            pharmacy.upDate(newDate, fileWriter);
        } else if (line.contains("APPROV")) {
            pharmacy.handleApprov(fileWriter, bufferedReader);
            continue;
        } else if (line.contains("PRESCRIPTION")) {
            prescriptionCount++;
            pharmacy.handlePrescription(bufferedReader, fileWriter, prescriptionCount);
        } else if (line.contains("STOCK")) {
            pharmacy.writeStock(fileWriter);
        }
    }

    fileWriter.close();
    }
}
