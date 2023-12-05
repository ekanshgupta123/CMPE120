import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Memory {
    protected int[] memoryArray;

    public Memory(int size) {
        this.memoryArray = new int[size];
        // Initialize the memory array or perform other common memory setup
    }

    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int address = 0;
            while ((line = reader.readLine()) != null) {
                int data = Integer.parseInt(line.trim(), 16);
                writeWord(address, data);
                address += 4; // Assuming each word is 4 bytes (32 bits)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readWord(int address) {
        // Implement logic to read a 32-bit word from the memory at the specified address
        return memoryArray[address / 4]; // Assuming each word is 4 bytes (32 bits)
    }

    public void writeWord(int address, int data) {
        // Implement logic to write a 32-bit word to the memory at the specified address
        memoryArray[address / 4] = data;
    }
    
    // Additional methods and fields as needed
}