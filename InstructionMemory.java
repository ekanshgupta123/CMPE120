import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructionMemory extends Memory {
    private List<Instruction> instructions;

    public InstructionMemory(String filePath) {
        super(0);
        this.instructions = readInstructionsFromFile(filePath);
    }


    private List<Instruction> readInstructionsFromFile(String filePath) {
        List<Instruction> instructions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                instructions.add(new Instruction(line.trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instructions;
    }

    public Instruction fetchInstruction(int pc) {
        return instructions.get(pc / 4); // Assuming each instruction is 4 bytes (32 bits)
    }
}