package codegen;

import ast.AbstractSyntaxTree;
import ast.Method;
import ast.SymbolTableEntry;
import parser.nodes.ProgramNode;
import token.Identifier;
import token.Token;
import token.TokenType;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ToyGenerator {

    private static final int SP = 15;
    public enum OutMode{
        STDOUT,
        FILE;
    }
    private File file;
    private final RegisterAllocator allocator;
    private final ProgramNode parseTreeRoot;
    private final AbstractSyntaxTree ast;
    private final PrintWriter writer;
    private ToyGenerator(ProgramNode parseTreeRoot, AbstractSyntaxTree ast, OutMode out) throws IOException {
        allocator = new RegisterAllocator();
        this.ast = ast;
        this.parseTreeRoot = parseTreeRoot;
        if(out == OutMode.FILE) {
            // TODO: make this the directory that the java app is run from
            file = new File("src/codegen/programs/EVAN'S CODE TEST.asm");
            writer = new PrintWriter(file);
        }else{
            writer = new PrintWriter(System.out);
        }
    }

    // ===============
    // BUILD FUNCTIONS
    // ===============

    public static void build(ProgramNode parseTreeRoot, AbstractSyntaxTree ast) throws IOException {
        ToyGenerator generator = new ToyGenerator(parseTreeRoot,ast,OutMode.STDOUT);
        generator.sampleGenerate();
        generator.writer.close();
    }

    private static File buildToFile(ProgramNode parseTreeRoot, AbstractSyntaxTree ast) throws IOException {
        ToyGenerator generator = new ToyGenerator(parseTreeRoot,ast,OutMode.FILE);
        generator.sampleGenerate();
        generator.writer.close();
        return generator.file;
    }

    private static void testBuild(ProgramNode parseTreeRoot, AbstractSyntaxTree ast) throws IOException {
        ToyGenerator generator = new ToyGenerator(parseTreeRoot,ast,OutMode.STDOUT);
        generator.sampleGenerate();
        generator.writer.close();
    }

    private void sampleGenerate(){
        writeAllBasicInstructions();
    }

    private void generatePrologue(Method method){

    }

    // Generates a new register using the allocator
    private String reg(){
        int i = allocator.getNextFreeRegister();
        return reg(i);
    }

    private String reg(int i){
        return "R" + i;
    }

    //
    // ===================
    // HELPER INSTRUCTIONS
    // ===================
    //
    private String loadValue(int ra, int val){
        if (val < 15) return LDQ(ra,val);
        else return LI(ra,val);
    }

    private String add(int r, int value){
        if (value < 15) return ADDQ(r,value);
        else{
            int tempReg = allocator.getNextFreeRegister();
            String s
                    = LI(tempReg, value) + "\n"
                    + ADD(r,tempReg,r);
            allocator.freeRegister(tempReg);
            return s;
        }
    }

    private String subtract(int r, int value){
        if (value < 15) return SUBQ(r,value);
        else{
            int tempReg = allocator.getNextFreeRegister();
            String s
                    = LI(tempReg, value) + "\n"
                    + SUB(r,tempReg,r);
            allocator.freeRegister(tempReg);
            return s;
        }
    }

    private String increment(int r){
        return ADDQ(r,1);
    }

    private String decrement(int r){
        return SUBQ(r,1);
    }

    private String moveSP(int byteDistance){
        if(byteDistance < 0){
            byteDistance *= -1;
            return subtract(SP,byteDistance);
        }
        return add(SP,byteDistance);
    }

    private void writeAllBasicInstructions(){
        writer.println(MOV(1,2));
        writer.println(LDQ(1,14));
        writer.println(LI(1,751));
        writer.println(LB(3,751));
        writer.println(LW(5,751));
        writer.println(STB(8, 751));
        writer.println(STW(9,751));
        writer.println(LBX(10,11,4));
        writer.println(STBX(12,2,4));
        writer.println(PUSH(1));
        writer.println(POP(2));
        writer.println(IN(3));
        writer.println(OUT(4));
        writer.println(NEG(5));
        writer.println(ABS(6));
        writer.println(EXT(7));
        writer.println(CMP(8,9));
        writer.println(CMPQ(9,12));
        writer.println(ADD(7,3,4));
        writer.println(ADDQ(10,3));
        writer.println(SUB(14,12,0));
        writer.println(SUBQ(10,3));
        writer.println(MUL(2,6,1));
        writer.println(MULQ(10,3));
        writer.println(DIV(9,9,8));
        writer.println(DIVQ(10,3));
    }




    //
    // =================
    // BASE INSTRUCTIONS
    // =================
    //
    public class OversizedValueException extends IllegalArgumentException{
        public OversizedValueException(String m){
            super(m);
        }
    }
    // Generates string for an instruction, given registers as ints

    // MOV Rs,Rt [9]
    private String MOV(int ra, int rb){return "MOV  \t" + reg(ra) + "," + reg(rb);}
    // LDQ R,value [10]
    private String LDQ(int ra, int val){
        if(val > 15) throw new OversizedValueException("Cannot LDQ value " + val + " because it is larger than 15");
        return "LDQ  \t" + reg(ra) + "," + val;
    }
    // LI R,value [11]
    private String LI(int ra, int val){
        if(val > 65_535) throw new OversizedValueException("Cannot LI value " + val + " because it is larger than 65,535");
        return "LI  \t" + reg(ra) + "," + val;
    }

    // LB R,address [12]
    private String LB(int ra, int addr){
        return "LB  \t" + reg(ra) + "," + addr;
    }

    // LW R,address [13]
    private String LW(int ra, int addr){
        return "LW  \t" + reg(ra) + "," + addr;
    }

    // STB R,address [14]
    private String STB(int ra, int addr){
        return "STB  \t" + reg(ra) + "," + addr;
    }

    // STW R,address [15]
    private String STW(int ra, int addr){
        return "STW  \t" + reg(ra) + "," + addr;
    }

    // LBX R,Rx,offset [16]
    private String LBX(int ra, int rx, int offset){
        if(offset > 15) throw new OversizedValueException("Cannot LBX because the given offset " + offset + " is larger than 15");
        return "LBX  \t" + reg(ra) + "," + reg(rx) + "," + offset;
    }

    // STBX R, Rx, offset [17]
    private String STBX(int ra, int rx, int offset){
        if(offset > 15) throw new OversizedValueException("Cannot STBX because the given offset " + offset + " is larger than 15");
        return "STBX  \t" + reg(ra) + "," + reg(rx) + "," + offset;
    }

    // PUSH r [18]
    private String PUSH(int r){
        return "PUSH  \t" + reg(r);
    }

    // POP r [19]
    private String POP(int r){
        return "POP  \t" + reg(r);
    }

    // IN r [20]
    private String IN(int r){
        return "IN   \t" + reg(r);
    }

    // OUT r [21]
    private String OUT(int r){
        return "OUT   \t" + reg(r);
    }

    // NEG r [22]
    private String NEG(int r){
        return "NEG  \t" + reg(r);
    }

    // ABS r [23]
    private String ABS(int r){
        return "ABS  \t" + reg(r);
    }

    // EXT r [24]
    private String EXT(int r){
        return "EXT  \t" + reg(r);
    }

    // CMP r1, r2 [25]
    private String CMP(int ra, int rb){
        return "CMP  \t" + reg(ra) + "," +  reg(rb);
    }

    // CMPQ r, value [26]
    private String CMPQ(int ra, int value){
        if(value > 15) throw new OversizedValueException("Cannot CMPQ because value " + value + " > 15");
        return "CMPQ  \t" + reg(ra) + "," + value;
    }

    // ADD R1,R2,Rt [27]
    private String ADD(int ra, int rb, int rc){
        return "ADD  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // ADDQ R,value [28]
    private String ADDQ(int ra, int value){
        if(value > 15) throw new OversizedValueException("Cannot ADDQ because value " + value + " > 15");
        return "ADDQ  \t" + reg(ra) + "," + value;
    }
    // SUB R1,R2,Rt [29]
    private String SUB(int ra, int rb, int rc){
        return "SUB \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // SUBQ R,value [30]
    private String SUBQ(int ra, int value){
        if(value > 15) throw new OversizedValueException("Cannot SUBQ because value " + value + " > 15");
        return "SUBQ \t" + reg(ra) + "," + value;
    }

    // MUL R1,R2,Rt [31]
    private String MUL(int ra, int rb, int rc){
        return "MUL \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // MULQ R,value [32]
    private String MULQ(int ra, int value){
        if(value > 15) throw new OversizedValueException("Cannot MULQ because value " + value + " > 15");
        return "MULQ \t" + reg(ra) + "," + value;
    }

    // DIV R1,R2,Rt [33]
    private String DIV(int ra, int rb, int rc){
        return "MUL \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // DIVQ R,value [34]
    private String DIVQ(int ra, int value){
        if(value > 15) throw new OversizedValueException("Cannot DIVQ because value " + value + " > 15");
        return "DIVQ \t" + reg(ra) + "," + value;
    }




    public static void main(String[] args) {
        ProgramNode n = new ProgramNode(new Identifier(TokenType.IDENTIFIER,-1,-1, "CODEGEN_TEST"),null);
        try {
            ToyGenerator.buildToFile(n, null);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
}
