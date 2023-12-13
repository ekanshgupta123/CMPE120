import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ReadDatFile {

    private static final int NUM_REGISTERS = 32;
    private static final int MEMORY_SIZE = 1024;

    private int[] registers;
    private int[] memory;
    private int programCounter;
    private int[] breakpoint;

    private static final int TEXT_SEGMENT_START_ADDRESS = 0x00000000;
    private static final int DATA_SEGMENT_START_ADDRESS = 0x80000000;

    private static final HashMap<Integer, String> intToRegister = new HashMap<>();

    static {
        intToRegister.put(0, "x0");
        intToRegister.put(5, "$t0");
        intToRegister.put(6, "$t1");
        intToRegister.put(7, "$t2");
        intToRegister.put(28, "$t3");
        intToRegister.put(29, "$t4");
        intToRegister.put(30, "$t5");
        intToRegister.put(31, "$t6");
    }

    public ReadDatFile() {
        registers = new int[NUM_REGISTERS];
        memory = new int[MEMORY_SIZE];
        programCounter = 0;
        registers[0] = 0;
        breakpoint = new int[5];
    }

    public void executeInstruction(String filePath) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r"))  {
            StringBuilder concatenatedLines = new StringBuilder();
            String line;
            int lineCount = 0;
            raf.seek(programCounter);
    
            while ((line = raf.readLine()) != null && lineCount < 4) {
                // Concatenate the line to the StringBuilder
                concatenatedLines.append(line).append(" ");
    
                // Increment the line count
                lineCount++;
            }
    
            if (lineCount == 4) {
                // Process the concatenated lines
                System.out.println(concatenatedLines.toString());
                String imm = "";
                String rs1 = concatenatedLines.charAt(18) + "";
                String rd = "";
                String func = "";
                String opcode = "";
                for (int i = 27; i <= 34; i++) {
                    imm += concatenatedLines.charAt(i);
                }
                for (int i = 18; i <= 21; i++) {
                    imm += concatenatedLines.charAt(i);
                }
                for (int i = 22; i <= 25; i++) {
                    rs1 += concatenatedLines.charAt(i);
                }
                rs1 += concatenatedLines.charAt(9);
                for (int i = 10; i <= 12; i++) {
                    func += concatenatedLines.charAt(i);
                }
                for (int i = 13; i <= 16; i++) {
                    rd += concatenatedLines.charAt(i);
                }
                rd += concatenatedLines.charAt(0);
                for (int i = 1; i <= 7; i++) {
                    opcode += concatenatedLines.charAt(i);
                }
                imm = imm.replaceAll("\\s", "");
                System.out.print("IMM: " + imm);
                System.out.println(" Decimal: " + convertToDecimal(imm));
                rs1 = rs1.replaceAll("\\s", "");
                System.out.print("RS1: " + rs1);
                System.out.println(" " + intToRegister.get(binaryStringToInt(rs1)));
                System.out.println("Func3: " + func.replaceAll("\\s", ""));
                rd = rd.replaceAll("\\s", "");
                System.out.print("rd: " + rd);
                System.out.println(" Register: " + intToRegister.get(binaryStringToInt(rd)));
                System.out.println("opcode: " + opcode.replaceAll("\\s", ""));
                System.out.println("-------------------------------------");  // Separator
                programCounter+=4;
            }       
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRegisters() {
        System.out.println("Register Contents:");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            System.out.println("x" + i + ": " + registers[i]);
        }
    }

    private static int convertToDecimal(String binaryString) {
        // Check if the number is negative
        boolean isNegative = binaryString.charAt(0) == '1';

        // Calculate the decimal value
        int decimalValue = 0;
        for (int i = 1; i < binaryString.length(); i++) {
            char bit = binaryString.charAt(i);
            decimalValue = (decimalValue << 1) | (bit - '0');
        }

        // Apply two's complement if the number is negative
        if (isNegative) {
            decimalValue = decimalValue - (1 << (binaryString.length() - 1));
        }

        return decimalValue;
    }

    public static int binaryStringToInt(String binaryString) {
        // Using parseInt with radix 2 to convert binary string to integer
        return Integer.parseInt(binaryString, 2);
    }

    public static void readDat(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder concatenatedLines = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                // Concatenate the line to the StringBuilder
                concatenatedLines.append(line).append(" ");

                // Increment the line count
                lineCount++;

                // Check if four lines have been read
                if (lineCount == 4) {
                    System.out.println(concatenatedLines.toString());
                    String imm = "";
                    String rs1 = "";
                    String rd = "";
                    String func = "";
                    String opcode = "";
                    for (int i = 27; i <= 34; i++) {
                        imm+=concatenatedLines.charAt(i);
                    }
                    for (int i = 18; i <= 21; i++) {
                        imm+=concatenatedLines.charAt(i);
                    }
                    for (int i = 22; i <= 25; i++) {
                        rs1+=concatenatedLines.charAt(i);
                    }
                    rs1+=concatenatedLines.charAt(9);
                    for (int i = 10; i <= 12; i++) {
                        func+=concatenatedLines.charAt(i);
                    }
                    for (int i = 13; i <= 16; i++) {
                        rd+=concatenatedLines.charAt(i);
                    }
                    rd+=concatenatedLines.charAt(0);
                    for (int i = 1; i <= 7; i++) {
                        opcode+=concatenatedLines.charAt(i);
                    }
                    imm.replaceAll("\\s", "");
                    System.out.print("IMM: " + imm);
                    System.out.println(" Decimal: " + convertToDecimal(imm));
                    rs1.replaceAll("\\s", "");
                    System.out.print("RS1: " + rs1);
                    System.out.println(" " + intToRegister.get(binaryStringToInt(rs1)));
                    System.out.println("Func3: " + func.replaceAll("\\s", ""));
                    rd.replaceAll("\\s", "");
                    System.out.print("rd: " + rd);
                    System.out.println(" Register: " + intToRegister.get(binaryStringToInt(rd)));
                    System.out.println("opcode: " + opcode.replaceAll("\\s", ""));
                    System.out.println("-------------------------------------");  // Separator

                    // Reset the StringBuilder and line count for the next group
                    concatenatedLines.setLength(0);
                    lineCount = 0;
                }
            }

            // Check if there are remaining lines (not a multiple of four)
            if (lineCount > 0) {
                System.out.println(concatenatedLines.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printMachine(String concatenatedLines) {
        String imm = "";
        String rs1 = "";
        String rd = "";
        String func = "";
        String opcode = "";
        for (int i = 27; i <= 34; i++) {
            imm += concatenatedLines.charAt(i);
        }
        for (int i = 18; i <= 21; i++) {
            imm += concatenatedLines.charAt(i);
        }
        for (int i = 22; i <= 25; i++) {
            rs1 += concatenatedLines.charAt(i);
        }
        rs1 += concatenatedLines.charAt(9);
        for (int i = 10; i <= 12; i++) {
            func += concatenatedLines.charAt(i);
        }
        for (int i = 13; i <= 16; i++) {
            rd += concatenatedLines.charAt(i);
        }
        rd += concatenatedLines.charAt(0);
        for (int i = 1; i <= 7; i++) {
            opcode += concatenatedLines.charAt(i);
        }
        System.out.println("Imm: " + imm);
        System.out.println("rs1: " + rs1);
        System.out.println("func: " + func);
        System.out.println("rd: " + rd);
        System.out.println("opcode: " + opcode);

    }
    public static void main(String[] args) {
        // Replace 'your_data.dat' with the actual path to your .dat file
        String filePath = "/Users/ekanshgupta/CMPE 120/SimulatorTests/addi_hazards.dat";
        String srai =  "10010011 01011110 00011110 01000000";
        String andi = "00010011 01111111 00000011 01111111";
        printMachine(srai);
        printMachine(andi);
        // printMachine(binary2);
        // readDat(filePath);
    }
}