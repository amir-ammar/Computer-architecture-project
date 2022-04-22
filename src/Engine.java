import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Engine {

    public static HashMap<String, Integer> opcodes;
    public static HashMap<String, Character> types;
    public static HashMap<Integer, String> operations;
    public static Memory memory;
    public static RegisterFile registerFile;
    public static int mp; // memory pointer
    public static int size; // program size
    public static activeInstruction activeInstruction; // current executing Instruction
    public static Instruction fetchedInstruction; // the currently fetched instruction

    public Engine(){
        init();
    }

    public static void init() {
        opcodes = new HashMap<>();
        types = new HashMap<>();
        operations = new HashMap<>();
        memory = new Memory();
        registerFile = new RegisterFile();

        for (int i = 0; i < registerFile.registers.length; i++) {
            registerFile.setRegister(i, 0);
        }

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
        String instruction = st.nextToken();
        int opcode = opcodes.get(instruction);

        if(types.get(instruction) == 'R'){
            int r1 = Integer.parseInt(st.nextToken().charAt(1) + "") - 1;
            int r2 = Integer.parseInt(st.nextToken().charAt(1) + "") - 1;
            int r3 = Integer.parseInt(st.nextToken().charAt(1) + "") - 1;
            encode('R',opcode, r1, r2, r3, 0, 0);
        }

        else if(types.get(instruction) == 'I'){
            int r1 = Integer.parseInt(st.nextToken().charAt(1) + "") - 1;
            int r2 = Integer.parseInt(st.nextToken().charAt(1) + "") - 1;
            int immediate = Integer.parseInt(st.nextToken());
            encode('I', opcode, r1, r2, 0, immediate, 0);
        }
        else if(types.get(instruction) == 'J'){
            int address = Integer.parseInt(st.nextToken());
            encode('J', opcode, 0, 0, 0, 0, address);
        }
        else{
            throw new Exception("Invalid instruction: " + instruction);
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

    public void encode(char type, int opcode, int r1, int r2, int r3, int immediate, int address){
        int word = 0;
        if (type == 'R') {
            word = (opcode << 28) | (r1 << 23) | (r2 << 18) | (r3 << 13);
            memory.storeWord(mp, 0, new RInstruction(word));
        }
        else if (type == 'I') {
            word = (opcode << 28) | (r1 << 23) | (r2 << 18) | immediate;
            memory.storeWord(mp, 0, new Iinstruction(word));
        }
        else {
            word = (opcode << 28) | address;
            memory.storeWord(mp, 0, new JInstruction(word));
        }
    }

    public void execute(){
        switch (activeInstruction.getInstructionMnemonic()) {
            case "ADD":
                registerFile.setRegister(activeInstruction.getR1(),
                        registerFile.getRegister(2).getWord() + registerFile.getRegister(activeInstruction.getR3()).getWord());
                break;
            case "SUB":
                registerFile.setRegister(activeInstruction.getR1(),
                        registerFile.getRegister(2).getWord() - registerFile.getRegister(activeInstruction.getR3()).getWord());
                break;
            case "MULI":
                registerFile.setRegister(activeInstruction.getR1(), registerFile.getRegister(2).getWord() * activeInstruction.getImmediate());
                break;
            case "ADDI":
                registerFile.setRegister(activeInstruction.getR1(), registerFile.getRegister(2).getWord() + activeInstruction.getImmediate());
                break;
            case "BNE":
            {
                if (registerFile.getRegister(1).getWord() != registerFile.getRegister(activeInstruction.getR2()).getWord()) {
                    registerFile.setPC(registerFile.getPC() + 1 + activeInstruction.getImmediate());
                }
            }
            break;
            case "ANDI":
                registerFile.setRegister(activeInstruction.getR1(), registerFile.getRegister(2).getWord() & activeInstruction.getImmediate());
                break;
            case "ORI":
                registerFile.setRegister(activeInstruction.getR1(), registerFile.getRegister(2).getWord() | activeInstruction.getImmediate());
                break;
            case "J":
                registerFile.setPC(activeInstruction.getAddress());
                break;
            case "SLL":
                registerFile.setRegister(activeInstruction.getR1(),
                        registerFile.getRegister(activeInstruction.getR2()).getWord() << activeInstruction.getShamt());
                break;
            case "SRL":
                registerFile.setRegister(activeInstruction.getR1(),
                        registerFile.getRegister(activeInstruction.getR2()).getWord() >> activeInstruction.getShamt());
                break;
            case "LW":
                registerFile.setRegister(activeInstruction.getR1(),
                        memory.loadWord(registerFile.getRegister(activeInstruction.getR2()).getWord() + activeInstruction.getImmediate()).getWord());
                break;
            case "SW":
                memory.storeWord(registerFile.getRegister(activeInstruction.getR2()).getWord() + activeInstruction.getImmediate() + 1023,
                        registerFile.getRegister(activeInstruction.getR1()).getWord(), null);
                break;
            default:
                System.out.println("Error: Invalid instruction");
        }
    }

    public void decode(){
        String Mnemonic = operations.get(fetchedInstruction.getOpcode());

        if (types.get(Mnemonic) == 'R') {
            RInstruction currentRInstruction = (RInstruction) fetchedInstruction;
            activeInstruction = new activeInstruction(Mnemonic, currentRInstruction.getR1(), currentRInstruction.getR2(),
                    currentRInstruction.getR3(), currentRInstruction.getShamt());
        }
        else if (types.get(Mnemonic) == 'I') {
            Iinstruction currentIInstruction = (Iinstruction) fetchedInstruction;
            activeInstruction = new activeInstruction(Mnemonic, currentIInstruction.getR1(), currentIInstruction.getR2(),
                    currentIInstruction.getImmediate());
        }
        else {
            JInstruction currentJInstruction = (JInstruction) fetchedInstruction;
            activeInstruction = new activeInstruction(Mnemonic, currentJInstruction.getAddress());

        }
    }

    public void fetch()  {
        fetchedInstruction = (Instruction) memory.loadWord(registerFile.getPC());
        registerFile.setPC(registerFile.getPC() + 1);
    }




    public static void main(String[] args) throws FileNotFoundException {
        Engine e = new Engine();
        e.parseFile("\\\\wsl$\\Ubuntu\\home\\amir\\Program.txt"); // <<- this is my path to the file put yours here
        //memory.printMemo(); // try this beauty print :) i spent some time on this

        /*
        for (int i = 0; i < 4; i++) {
            e.fetch();
            e.decode();
            e.execute();
        }
        */
        memory.printMemo();

    }



}
