import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Engine {

    public static HashMap<String, Integer> opcodes;
    public static HashMap<String, Character> types;
    public static HashMap<Integer, String> operations;
    public static Memory memory;
    public static RegisterFile registerFile;
    public static int mp; // memory pointer
    public static int size; // program size
    public static Queue<Integer> fetch;
    public static Queue<Instruction> decode;
    public static Queue<Instruction> execute;

    public Engine(){
        init();
    }

    public static void init() {
        opcodes = new HashMap<>();
        types = new HashMap<>();
        operations = new HashMap<>();
        memory = new Memory();
        registerFile = new RegisterFile();

        fetch = new ArrayDeque<>();
        decode = new ArrayDeque<>();
        execute = new ArrayDeque<>();

        // opcodes
        {
            // R-Type
            opcodes.put("ADD", 0);
            opcodes.put("SUB", 1);
            opcodes.put("SLL", 2);
            opcodes.put("SRL", 3);
            // I-Type
            opcodes.put("MULI", 4);
            opcodes.put("ADDI", 5);
            opcodes.put("BNE", 6);
            opcodes.put("ANDI", 7);
            opcodes.put("ORI", 8);
            opcodes.put("LW", 9);
            opcodes.put("SW", 10);
            // J-Type
            opcodes.put("J", 11);
        }


        // operations
        {
            // R-Type
            operations.put(0, "ADD");
            operations.put(1, "SUB");
            operations.put(2, "SLL");
            operations.put(3, "SRL");

            // I-Type
            operations.put(4, "MULI");
            operations.put(5, "ADDI");
            operations.put(6, "BNE");
            operations.put(7, "ANDI");
            operations.put(8, "ORI");
            operations.put(9, "LW");
            operations.put(10, "SW");

            // J-Type
            operations.put(11, "J");

        }

        // types
        {
            // R-type
            types.put("ADD", 'R');
            types.put("SUB", 'R');
            types.put("SLL", 'R');
            types.put("SRL", 'R');

            // I-type
            types.put("MULI", 'I');
            types.put("ADDI", 'I');
            types.put("BNE", 'I');
            types.put("ANDI", 'I');
            types.put("ORI", 'I');
            types.put("LW", 'I');
            types.put("SW", 'I');

            // J-type
            types.put("J", 'J');

        }
    }

    public void parseLine(String line) throws Exception {

        StringTokenizer st = new StringTokenizer(line);
        encode(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken());
        if (st.hasMoreTokens()) {
            throw new Exception("Invalid instruction");
        }
    }

    public void parseFile(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scan = new Scanner(file);
        try{
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                parseLine(line);
                mp++;
                size++;
            }
        }
        catch(Exception e){
            System.out.println("Error: Parse error");
        }

    }

    public void encode(String operation, String oprand1, String oprand2, String oprand3){
        int value;
        if (types.get(operation) == 'R') {
            int opcode = opcodes.get(operation);
            int r1 = Integer.parseInt(oprand1.charAt(1) + "") - 1;
            int r2 = Integer.parseInt(oprand2.charAt(1) + "") - 1;
            int r3 = Integer.parseInt(oprand3.charAt(1) + "") - 1;
            value = (opcode << 28) | (r1 << 23) | (r2 << 18) | (r3 << 13);
            memory.storeWord(mp, value);
        }
        else if (types.get(operation) == 'I') {
            int opcode = opcodes.get(operation);
            int r1 = Integer.parseInt(oprand1.charAt(1) + "") - 1;
            int r2 = Integer.parseInt(oprand2.charAt(1) + "") - 1;
            int immediate = Integer.parseInt(oprand3);
            value = (opcode << 28) | (r1 << 23) | (r2 << 18) | immediate;
            memory.storeWord(mp, value);
        }
        else {
            int opcode = opcodes.get(operation);
            int address = Integer.parseInt(oprand1);
            value = (opcode << 28) | address;
            memory.storeWord(mp, value);
        }
    }

    public void execute(){
        Instruction instruction = decode.poll();
        execute.add(instruction);

        switch (instruction != null ? instruction.getOpcode() : 0) {
            // ADD
            case 0 -> {
                RInstruction ri = (RInstruction) instruction;
                int value = registerFile.getRegister(ri.getR2()) + registerFile.getRegister(ri.getR3());
                registerFile.setRegister(ri.getR1(), value);
            }
            // SUB
            case 1 -> {
                RInstruction ri = (RInstruction) instruction;
                int value = registerFile.getRegister(ri.getR2()) - registerFile.getRegister(ri.getR3());
                registerFile.setRegister(ri.getR1(), value);
            }
            // sLL
            case 2 -> {
                RInstruction ri = (RInstruction) instruction;
                int value = registerFile.getRegister(ri.getR2()) << ri.getShamt();
                registerFile.setRegister(ri.getR1(), value);
            }
            // sRL
            case 3 -> {
                RInstruction ri = (RInstruction) instruction;
                int value = registerFile.getRegister(ri.getR2()) >> ri.getShamt();
                registerFile.setRegister(ri.getR1(), value);
            }
            // MULI
            case 4 -> {
                Iinstruction ii = (Iinstruction) instruction;
                int value = registerFile.getRegister(ii.getR2()) * ii.getImmediate();
                registerFile.setRegister(ii.getR1(), value);
            }
            // ADDI
            case 5 -> {
                Iinstruction ii = (Iinstruction) instruction;
                int value = registerFile.getRegister(ii.getR2()) + ii.getImmediate();
                registerFile.setRegister(ii.getR1(), value);
            }
            // BNQ
            case 6 -> {
                Iinstruction ii = (Iinstruction) instruction;
                registerFile.setPC(registerFile.getRegister(ii.getR1()) != registerFile.getRegister(ii.getR2()) ?
                        (registerFile.getPC() + 1 + ii.getImmediate()) : registerFile.getPC());
            }
            // ANDI
            case 7 -> {
                Iinstruction ii = (Iinstruction) instruction;
                int value = registerFile.getRegister(ii.getR2()) & ii.getImmediate();
                registerFile.setRegister(ii.getR1(), value);
            }
            // ORI
            case 8 -> {
                Iinstruction ii = (Iinstruction) instruction;
                int value = registerFile.getRegister(ii.getR2()) | ii.getImmediate();
                registerFile.setRegister(ii.getR1(), value);
            }
            // LW
            case 9 -> {
                Iinstruction ii = (Iinstruction) instruction;
                registerFile.setRegister(ii.getR1(), memory.loadWord(registerFile.getRegister(ii.getR2()) + ii.getImmediate()));
            }
            // SW
            case 10 -> {
                Iinstruction ii = (Iinstruction) instruction;
                memory.storeWord(registerFile.getRegister(ii.getR2()) + ii.getImmediate(), registerFile.getRegister(ii.getR1()));
            }
            case 11 -> {

            }
            default -> {
                System.out.println("Unknown instruction");
            }
        }


    }

    public void decode() {

        int value = fetch.poll();
        int opcode = (value >> 28) & 0xf;

        if(opcode <= 3) {
            RInstruction rInstruction = new RInstruction(value);
            decode.add(rInstruction);
        }
        else if (opcode <= 10) {
            Iinstruction iInstruction = new Iinstruction(value);
            decode.add(iInstruction);
        }
        else if (opcode == 11) {
            JInstruction jInstruction = new JInstruction(value);
            decode.add(jInstruction);
        }
        else {
            System.out.println("Error: Invalid instruction");
        }
    }

    public void fetch()  {
         fetch.add(memory.loadWord(registerFile.getPC()));
        registerFile.setPC(registerFile.getPC() + 1);
    }




    public static void main(String[] args) throws FileNotFoundException {
        Engine e = new Engine();
        e.parseFile("\\\\wsl$\\Ubuntu\\home\\amir\\Program.txt"); // <<- this is my path to the file put yours here
    }



}
