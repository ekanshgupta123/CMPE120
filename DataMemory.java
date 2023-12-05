import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataMemory extends Memory {

    public DataMemory(int size) {
        super(size);
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int address = 0x80000000; // Start address for data memory
            while ((line = reader.readLine()) != null) {
                int data = Integer.parseInt(line.trim(), 16);
                writeWord(address, data);
                address += 4; // Assuming each word is 4 bytes (32 bits)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other methods as needed...
}
