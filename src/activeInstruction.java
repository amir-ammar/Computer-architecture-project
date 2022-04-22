public class activeInstruction {

    String instructionMnemonic;
    int r1, r2, r3, address, immediate, shamt;

    public activeInstruction(String instructionMnemonic, int r1, int r2, int r3, int address, int immediate, int shamt) {
        this.instructionMnemonic = instructionMnemonic;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.address = address;
        this.immediate = immediate;
        this.shamt = shamt;
    }

    // for R-type instructions
    public activeInstruction (String instructionMnemonic, int r1, int r2, int r3, int shamt) {
        this(instructionMnemonic, r1, r2, r3, 0, 0, shamt);
    }

    // for I-type instructions
    public activeInstruction (String instructionMnemonic, int r1, int r2, int immediate) {
        this(instructionMnemonic, r1, r2, 0, 0, immediate, 0);
    }

    // for J-type instructions
    public activeInstruction (String instructionMnemonic, int address) {
        this(instructionMnemonic, 0, 0, 0, address, 0, 0);
    }

    public String getInstructionMnemonic() {
        return instructionMnemonic;
    }

    public int getR1() {
        return r1;
    }

    public int getR2() {
        return r2;
    }

    public int getR3() {
        return r3;
    }

    public int getAddress() {
        return address;
    }

    public int getImmediate() {
        return immediate;
    }

    public int getShamt() {
        return shamt;
    }


    public void setShamt(int shamt) {
        this.shamt = shamt;
    }

    public void setImmediate(int immediate) {
        this.immediate = immediate;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setR3(int r3) {
        this.r3 = r3;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public void setInstructionMnemonic(String instructionMnemonic) {
        this.instructionMnemonic = instructionMnemonic;
    }

    public String toString() {
        return instructionMnemonic + " " + r1 + " " + r2 + " " + r3 + " " + address + " " + immediate + " " + shamt;
    }

}
