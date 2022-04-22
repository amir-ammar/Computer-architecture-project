public class Memory {

    public Word [] memo;

    public Memory(){
        memo = new Word[2048];
    }

    public void storeWord(int address, int value, Word word){ // to differentiate between storing a reference or a value
        if (word == null) {
            memo[address] = new Word(value);
        }
        else {
            memo[address] = word;
        }
    }

    public Word loadWord(int address){
        return memo[address];
    }

    public String spaces(int n){
        String spaces = "";
        for(int i = 0; i < n; i++){
            spaces += " ";
        }
        return spaces;
    }

    // print the memory
    public void printMemo(){
        System.out.println(spaces(22) + "Welcome to our fantastic memory!");
        System.out.println(spaces(22) + "----------------------------------");
        for(int i = 0; i < memo.length; i++){
            if(memo[i] != null){
                System.out.println(spaces(16) + i + ": " + memo[i].toString());
            }
        }
    }


}
