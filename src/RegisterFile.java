public class RegisterFile {

    public Word [] registers;
    public int pc;
    public final int zeroRegister = 0;

    public RegisterFile(){
        registers = new Word[31];
    }

    public void setRegister(int reg, int value){
        registers[reg] = new Word(value);
    }

    public Word getRegister(int reg){
        return registers[reg];
    }

    public void setPC(int value){
        pc = value;
    }

    public int getPC(){
        return pc;
    }




}
