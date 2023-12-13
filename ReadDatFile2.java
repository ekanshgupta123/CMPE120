import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Flow.Processor;;

public class ReadDatFile2 {

    private static final int NUM_REGISTERS = 32;
    private static final int MEMORY_SIZE = 1024;

    private int[] registers;
    private int[] memory;
    private String programCounter;
    private String filePath;
    private ArrayList<String> breakpoint;

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

    public ReadDatFile2(String filePath) {
        registers = new int[NUM_REGISTERS];
        memory = new int[MEMORY_SIZE];
        programCounter = "0x00000000";
        registers[0] = 0;
        this.filePath = filePath;
        breakpoint = new ArrayList<>(5);
    }

    public int convertHexToDecimal(String hexString) {
        hexString = hexString.substring(2);
        int decimalValue = Integer.parseInt(hexString, 16);
        return decimalValue;
    }


    public void runNextInstruction() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            StringBuilder concatenatedLines = new StringBuilder();
            String line;
            int lineCount = 0;
            int skipLine = 0;
            // System.out.println("Register[5]: " + registers[5]);

            while (skipLine < convertHexToDecimal(this.programCounter)) {
                line = br.readLine();
                skipLine++;
            }

            while ((line = br.readLine()) != null && lineCount < 4) {
                // Concatenate the line to the StringBuilder
                concatenatedLines.append(line).append(" ");
    
                // Increment the line count
                lineCount++;
            }
    
            if (lineCount == 4) {
                // Process the concatenated lines
                System.out.println(concatenatedLines.toString());
                String imm = "";
                String rs1 = "";
                String rd = "";
                String func = "";
                String opcode = "";

                for (int i = 1; i <= 7; i++) {
                    opcode += concatenatedLines.charAt(i);
                }
                // imm = imm.replaceAll("\\s", "");

                // System.out.print("IMM: " + imm);
                // System.out.println(" Decimal: " + convertToDecimal(imm));
                // rs1 = rs1.replaceAll("\\s", "");
                // System.out.print("RS1: " + rs1);
                // System.out.println(" " + intToRegister.get(binaryStringToInt(rs1)));
                // System.out.println("Func3: " + func.replaceAll("\\s", ""));
                // rd = rd.replaceAll("\\s", "");
                // System.out.print("rd: " + rd);
                // System.out.println(" Register: " + intToRegister.get(binaryStringToInt(rd)));
                // System.out.println("opcode: " + opcode.replaceAll("\\s", ""));
                opcode = opcode.replaceAll("\\s", "");
                if (opcode.equals("0010011") || opcode.equals("0110011") || opcode.equals("0000011")) {
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
                    imm = imm.replaceAll("\\s", "");
                    rd += concatenatedLines.charAt(0);
                    rs1 = rs1.replaceAll("\\s", "");
                    rd = rd.replaceAll("\\s", "");
                    operation_i(opcode, rd, rs1, func, imm);
                }
                else if (opcode.equals("0000000")) {
                    nop();
                }
                programCounter = Integer.toHexString(convertHexToDecimal(this.programCounter) + 4);
                programCounter = "0x"+programCounter;
                // programCounter+=4;
                // System.out.println("Program Counter: " + programCounter);
                // System.out.println("Register rd: " + binaryStringToInt(rd) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rd))]);
                // System.out.println("Register rs1: " + binaryStringToInt(rs1) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rs1))]);
                // System.out.println(intToRegister.get(binaryStringToInt(rd)) + " = " + intToRegister.get(binaryStringToInt(rs1)) + " + " + imm);
                // registers[binaryStringToInt(rd)] = registers[Integer.valueOf(binaryStringToInt(rs1))] + convertToDecimal(imm);
                // instruction(intToRegister.get(binaryStringToInt(rd)), intToRegister.get(binaryStringToInt(rs1)), convertToDecimal(imm));
                // System.out.println("New value in register " + intToRegister.get(binaryStringToInt(rd))+ ": " + registers[binaryStringToInt(rd)]);
                // System.out.println("-------------------------------------");  // Separator
            }       
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String addiPrint(String rd, String rs1, String imm) {
        String newLine = "addi " + intToRegister.get(binaryStringToInt(rd)) + ", " + intToRegister.get(binaryStringToInt(rs1)) + ", " + convertToDecimal(imm);
        return newLine;
    }

    public void addi(String rd, String rs1, String imm) {
        registers[binaryStringToInt(rd)] = registers[Integer.valueOf(binaryStringToInt(rs1))] + convertToDecimal(imm);
        String fileName = "assembly.asm";
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String newLine = addiPrint(rd, rs1, imm);

            bufferedWriter.write(newLine);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nop () {
        String fileName = "assembly.asm";
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // need to get rid of "addi" make it dy
            String newLine = "nop";

            bufferedWriter.write(newLine);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void operation_i(String opcode, String rd, String rs1, String func, String imm) {
        if (opcode.equals("0010011")) {
            if (func.equals("000")) {
                addi(rd, rs1, imm);
            }
            else if (func.equals("010")) {
                // slti
            }
            else if (func.equals("100")) {
                // xori
            }
            else if (func.equals("110")) {
                // ori
            }
            else if (func.equals("011")) {
                // slli
            }
            else if (func.equals("111")) {
                // andi
            }
            else if (func.equals("101")) {
                if (imm.substring(0, 2).equals("01") ){
                    // srai
                }
                else {
                    //srli
                }
            }
            else if (func.equals("001")) {
                // slli
            }
        }
        else if (opcode.equals("0110011")) {
            if (func.equals("000")) {
                if (imm.substring(0, 2).equals("00") ){
                    // add
                }
                else if (imm.substring(0, 2).equals("01") ){
                    // sub
                }
            }
            else if (func.equals("001")) {
                // sll
            }
            else if (func.equals("010")) {
                // slt
            }
            else if (func.equals("011")) {
                // sltu
            }
            else if (func.equals("100")) {
                // xor
            }
            else if (func.equals("101")) {
                if (imm.substring(0, 2).equals("00") ){
                    // srl
                }
                else if (imm.substring(0, 2).equals("01") ){
                    // sra
                }
            }
            else if (func.equals("110")) {
                // or
            }
            else if (func.equals("111")) {
                // and
            }
        }
        else if (opcode.equals("0000011")) {
            if (func.equals("000")) {
                // lb
            }
            else if (func.equals("001")) {
                // lh
            }
            else if (func.equals("010")) {
                // lw
            }
            else if (func.equals("100")) {
                // lbu
            }
            else if (func.equals("101")) {
                // lhu
            }
        }
        else if (opcode.equals("0100011")) {
            if (func.equals("000")) {
                // sb
            }
            else if (func.equals("001")) {
                // sh
            }
            else if (func.equals("010")) {
                // sw
            }
        }
    }

    public String getProgramCounter() {
        return programCounter;
    }

    public void insn() {
        int tempPC = convertHexToDecimal(this.programCounter);
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            StringBuilder concatenatedLines = new StringBuilder();
            String line;
            int lineCount = 0;
            int skipLine = 0;

            while (skipLine < tempPC) {
                line = br.readLine();
                skipLine++;
            }

            while ((line = br.readLine()) != null && lineCount < 4) {
                // Concatenate the line to the StringBuilder
                concatenatedLines.append(line).append(" ");
    
                // Increment the line count
                lineCount++;
            }

            if (lineCount == 4) {
                // Process the concatenated lines
                // System.out.println(concatenatedLines.toString());
                String imm = "";
                String rs1 = "";
                String rd = "";
                String func = "";
                String opcode = "";
                for (int i = 1; i <= 7; i++) {
                    opcode += concatenatedLines.charAt(i);
                }

                if (opcode.equals("0010011") || opcode.equals("0110011") || opcode.equals("0000011")) {
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
                    imm = imm.replaceAll("\\s", "");
                    rs1 = rs1.replaceAll("\\s", "");
                    rd = rd.replaceAll("\\s", "");
                    String s = addiPrint(rd, rs1, imm);
                    System.out.println(s);
                }
                // System.out.print("IMM: " + imm);
                // System.out.println(" Decimal: " + convertToDecimal(imm));
                // System.out.print("RS1: " + rs1);
                // System.out.println(" " + intToRegister.get(binaryStringToInt(rs1)));
                // System.out.println("Func3: " + func.replaceAll("\\s", ""));
                // System.out.print("rd: " + rd);
                // System.out.println(" Register: " + intToRegister.get(binaryStringToInt(rd)));
                // System.out.println("opcode: " + opcode.replaceAll("\\s", ""));
                // programCounter+=4;
                // System.out.println("Program Counter: " + programCounter);
                // System.out.println("Register rd: " + binaryStringToInt(rd) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rd))]);
                // System.out.println("Register rs1: " + binaryStringToInt(rs1) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rs1))]);
                // System.out.println("addi " + intToRegister.get(binaryStringToInt(rd)) + ", " + intToRegister.get(binaryStringToInt(rs1)) + ", " + convertToDecimal(imm));
                // instruction(intToRegister.get(binaryStringToInt(rd)), intToRegister.get(binaryStringToInt(rs1)), imm);
                // registers[binaryStringToInt(rd)] = registers[Integer.valueOf(binaryStringToInt(rs1))] + convertToDecimal(imm);
                // System.out.println("New value in register " + intToRegister.get(binaryStringToInt(rd))+ ": " + registers[binaryStringToInt(rd)]);
                // System.out.println("-------------------------------------");  // Separator
            }       
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decimalToBinary32(int decimalValue) {
        // Using Integer.toBinaryString to convert decimal to binary
        String binaryString = Integer.toBinaryString(decimalValue);

        // Ensure the binary string is 32 bits long
        while (binaryString.length() < 32) {
            binaryString = "0" + binaryString;
        }

        return binaryString;
    }

    public String printRegisters(int value) {
        return "x" + value + ": " + (decimalToBinary32(registers[value])) + " (" + registers[value] + ")";
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

    public void runAllInstructions() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            StringBuilder concatenatedLines = new StringBuilder();
            String line;
            int lineCount = 0;
            int skipLine = 0;

            while (skipLine < convertHexToDecimal(this.programCounter)) {
                line = br.readLine();
                skipLine++;
            }

            while ((line = br.readLine()) != null ) {
                // Concatenate the line to the StringBuilder
                concatenatedLines.append(line).append(" ");

                // Increment the line count
                lineCount++;

                // Check if four lines have been read
                if (lineCount == 4) {
                    String bcpc = Integer.toHexString(convertHexToDecimal(this.programCounter) + 4);
                    bcpc  = "0x"+bcpc;
                    if (breakpoint.contains(bcpc)) {
                        System.out.println("Breakpoint hit at line: " + bcpc);
                        breakpoint.remove(0);
                        return;
                    }
                    System.out.println(concatenatedLines.toString());
                    String imm = "";
                    String rs1 = "";
                    String rd = "";
                    String func = "";
                    String opcode = "";
                    for (int i = 1; i <= 7; i++) {
                        opcode+=concatenatedLines.charAt(i);
                    }
                    // System.out.print("IMM: " + imm);
                    // System.out.println(" Decimal: " + convertToDecimal(imm));
                    // System.out.print("RS1: " + rs1);
                    // System.out.println(" " + intToRegister.get(binaryStringToInt(rs1)));
                    // System.out.println("Func3: " + func.replaceAll("\\s", ""));
                    // System.out.print("rd: " + rd);
                    // System.out.println(" Register: " + intToRegister.get(binaryStringToInt(rd)));
                    // System.out.println("opcode: " + opcode.replaceAll("\\s", ""));
                    if (opcode.equals("0010011") || opcode.equals("0110011") || opcode.equals("0000011")) {
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
                        imm = imm.replaceAll("\\s", "");
                        rs1 = rs1.replaceAll("\\s", "");
                        rd += concatenatedLines.charAt(0);
                        rd = rd.replaceAll("\\s", "");
                        operation_i(opcode, rd, rs1, func, imm);
                    }
                    else if (opcode.equals("0000000")) {
                        nop();
                    }
                    programCounter = Integer.toHexString(convertHexToDecimal(this.programCounter) + 4);
                    programCounter = "0x"+programCounter;
                    // System.out.println(programCounter);
                    // programCounter+=4;
                    // System.out.println("Program Counter: " + programCounter);
                    // System.out.println("Register rd: " + binaryStringToInt(rd) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rd))]);
                    // System.out.println("Register rs1: " + binaryStringToInt(rs1) + " Value in register: " + registers[Integer.valueOf(binaryStringToInt(rs1))]);
                    // System.out.println(intToRegister.get(binaryStringToInt(rd)) + " = " + intToRegister.get(binaryStringToInt(rs1)) + " + " + imm);
                    // registers[binaryStringToInt(rd)] = registers[Integer.valueOf(binaryStringToInt(rs1))] + convertToDecimal(imm);
                    // instruction(intToRegister.get(binaryStringToInt(rd)), intToRegister.get(binaryStringToInt(rs1)), convertToDecimal(imm));
                    // System.out.println("New value in register " + intToRegister.get(binaryStringToInt(rd))+ ": " + registers[binaryStringToInt(rd)]);
                    // System.out.println("-------------------------------------");  // Separator

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
    
    public static int countLines(String filePath) {
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineCount;
    }
    // public boolean hasMoreInstructions() {
    //     if (programCounter != countLines(filePath)) {
    //         return true;
    //     }
    //     return false;
    // }

    public static void main(String[] args) {
        // Change path with your own file
        // String filePath = "/Users/ekanshgupta/CMPE 120/SimulatorTests/addi_hazards.dat";
        String filePath = "/Users/ekanshgupta/CMPE 120/SimulatorTests/addi_nohazard.dat";
        ReadDatFile2 r1 = new ReadDatFile2(filePath);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Menu:");
            System.out.println("r) Run All Instructions");
            System.out.println("s) Run Next Instruction");
            System.out.println("Return the contents in the registers (x0 to x31)");
            System.out.println("p) Print Memory");
            System.out.println("pc) Returns value in PC");
            System.out.println("insn) Prints the “assembly of the instruction” that will be executed next");
            System.out.println("b) Enter a breakpoint");
            System.out.println("exit) Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "p":
                    // r1.printMemory();
                    break;
                case "r":
                    r1.runAllInstructions();
                    break;
                case "s":
                    r1.runNextInstruction();
                    break;
                case "b":
                    System.out.print("Enter Breakpoint: ");
                    String breakpoint = scanner.nextLine();
                    // scanner.nextLine();
                    r1.breakpoint.add(breakpoint);
                    break;
                case "insn":
                    r1.insn();
                    break;
                case "exit":
                    System.out.println("Exiting program.");
                    scanner.close();
                    System.exit(0);
                    break;
                case "x0":
                    System.out.println(r1.printRegisters(0));
                    break;
                case "pc":
                    System.out.println(r1.getProgramCounter());
                    break;
                case "x1":
                    System.out.println(r1.printRegisters(1));
                    break;
                case "x2":
                    System.out.println(r1.printRegisters(2));
                    break;
                case "x3":
                    System.out.println(r1.printRegisters(3));
                    break;
                case "x4":
                    System.out.println(r1.printRegisters(4));
                    break;
                case "x5":
                    System.out.println(r1.printRegisters(5));
                    break;
                case "x6":
                    System.out.println(r1.printRegisters(6));
                    break;
                case "x7":
                    System.out.println(r1.printRegisters(7));
                    break;
                case "x8":
                    System.out.println(r1.printRegisters(8));
                    break;
                case "x9":
                    System.out.println(r1.printRegisters(9));
                    break;
                case "x10":
                    System.out.println(r1.printRegisters(10));
                    break;
                case "x11":
                    System.out.println(r1.printRegisters(11));
                    break;
                case "x12":
                    System.out.println(r1.printRegisters(12));
                    break;
                case "x13":
                    System.out.println(r1.printRegisters(13));
                    break;
                case "x14":
                    System.out.println(r1.printRegisters(14));
                    break;
                case "x15":
                    System.out.println(r1.printRegisters(15));
                    break;
                case "x16":
                    System.out.println(r1.printRegisters(16));
                    break;
                case "x17":
                    System.out.println(r1.printRegisters(17));
                    break;
                case "x18":
                    System.out.println(r1.printRegisters(18));
                    break;
                case "x19":
                    System.out.println(r1.printRegisters(19));
                    break;
                case "x20":
                    System.out.println(r1.printRegisters(20));
                    break;
                case "x21":
                    System.out.println(r1.printRegisters(21));
                    break;
                case "x22":
                    System.out.println(r1.printRegisters(22));
                    break;
                case "x23":
                    System.out.println(r1.printRegisters(23));
                    break;
                case "x24":
                    System.out.println(r1.printRegisters(24));
                    break;
                case "x25":
                    System.out.println(r1.printRegisters(25));
                    break;
                case "x26":
                    System.out.println(r1.printRegisters(26));
                    break;
                case "x27":
                    System.out.println(r1.printRegisters(27));
                    break;
                case "x28":
                    System.out.println(r1.printRegisters(28));
                    break;
                case "x29":
                    System.out.println(r1.printRegisters(29));
                    break;
                case "x30":
                    System.out.println(r1.printRegisters(30));
                    break;
                case "x31":
                    System.out.println(r1.printRegisters(31));
                    break;        
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}