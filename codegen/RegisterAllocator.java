package codegen;

public class RegisterAllocator {
    public static final int SP = 16;

    private final boolean[] r;
    private static final int NUM_REGISTERS = 15;
    private static final boolean FREE = true;
    private static final boolean OCCUPIED = false;

    public class RegisterAllocationException extends RuntimeException{
        public RegisterAllocationException(String m){super(m);}
    }

    public RegisterAllocator(){
        r = new boolean[NUM_REGISTERS];
    }

    public int getNextFreeRegister(){
        for(int i = 0; i < NUM_REGISTERS; i++){
            if(r[i] == FREE){
                r[i] = OCCUPIED;
                return i;
            }
        }
        throw new RegisterAllocationException("Could not allocate a free register");
    }

    public void freeRegister(int i){
        r[i] = FREE;
    }
}
