public class Instruction {
    private int binaryValue; // Store the binary representation of the instruction

    public Instruction(String hexInstruction) {
        this.binaryValue = hexToBinary(hexInstruction);
    }

    private int hexToBinary(String hexInstruction) {
        return Integer.parseInt(hexInstruction, 16);
    }

    public int getBinary() {
        return binaryValue;
    }

    // Getter methods for instruction fields
}
