package codegen;

import ast.AbstractSyntaxTree;
import ast.Method;
import parser.nodes.ProgramNode;
import token.Identifier;
import token.TokenType;

import java.io.File;
import java.io.IOException;
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

    // load static value based on it's size
    private String loadValue(int ra, int val){
        if (val < 15) return LDQ(ra,val);
        else return LI(ra,val);
    }

    // generate add instruction, depending on value size
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

    // generate subtract instruction, depending on the size of the value
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

    // decrement a given register
    private String increment(int r){
        return ADDQ(r,1);
    }

    // increment a given register
    private String decrement(int r){
        return SUBQ(r,1);
    }

    // Moves the stack pointer the specified amount of bytes
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
        writer.println(MOD(1,2,3));
        writer.println(MODQ(3,12));
        writer.println(SAL(12,13,14));
        writer.println(SLL(12,13,14));
        writer.println(SALQ(3,2));
        writer.println(SLLQ(5,10));
        writer.println(SAR(7,0,2));
        writer.println(SARQ(2,3));
        writer.println(SLR(3,4,6));
        writer.println(SLRQ(4,6));
        writer.println(NOT(6,6));
        writer.println(AND(2,4,12));
        writer.println(ANDQ(4,3));
        writer.println(OR(4,4,4));
        writer.println(ORQ(2,4));
        writer.println(XOR(5,4,1));
        writer.println(XORQ(5,6));
        writer.println(B(25));
        writer.println(BEQ(23));
        writer.println(BE(24));
        writer.println(BZ(44));
        writer.println(BNE(43));
        writer.println(BNZ(53));
        writer.println(BL(2));
        writer.println(BLT(22));
        writer.println(BLE(44));
        writer.println(BG(3));
        writer.println(BGT(1279));
        writer.println(BGE(1209));
        writer.println(BLTU(23));
        writer.println(BLU(123));
        writer.println(BC(1239));
        writer.println(BLEU(7483));
        writer.println(BGTU(273));
        writer.println(BGU(349));
        writer.println(BGEU(398));
        writer.println(BNC(9202));
        writer.println(JUMP(12));
        writer.println(CALL(34));
        writer.println(EXEC(2));
        writer.println(RET());
        writer.println(REGS());
        writer.println(DUMP());
    }




    //
    // =================
    // BASE INSTRUCTIONS
    // =================
    //
    public static class OversizeValueException extends IllegalArgumentException{
        public OversizeValueException(String m){
            super(m);
        }
    }
    // Generates string for an instruction, given registers as ints

    // MOV Rs,Rt [9]
    private String MOV(int ra, int rb){return "MOV  \t" + reg(ra) + "," + reg(rb);}
    // LDQ R,value [10]
    private String LDQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot LDQ value " + val + " because it is larger than 15");
        return "LDQ  \t" + reg(ra) + "," + val;
    }
    // LI R,value [11]
    private String LI(int ra, int val){
        if(val > 65_535) throw new OversizeValueException("Cannot LI value " + val + " because it is larger than 65,535");
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
        if(offset > 15) throw new OversizeValueException("Cannot LBX because the given offset " + offset + " is larger than 15");
        return "LBX  \t" + reg(ra) + "," + reg(rx) + "," + offset;
    }

    // STBX R, Rx, offset [17]
    private String STBX(int ra, int rx, int offset){
        if(offset > 15) throw new OversizeValueException("Cannot STBX because the given offset " + offset + " is larger than 15");
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
        if(value > 15) throw new OversizeValueException("Cannot CMPQ because value " + value + " > 15");
        return "CMPQ  \t" + reg(ra) + "," + value;
    }

    // ADD R1,R2,Rt [27]
    private String ADD(int ra, int rb, int rc){
        return "ADD  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // ADDQ R,value [28]
    private String ADDQ(int ra, int value){
        if(value > 15) throw new OversizeValueException("Cannot ADDQ because value " + value + " > 15");
        return "ADDQ  \t" + reg(ra) + "," + value;
    }
    // SUB R1,R2,Rt [29]
    private String SUB(int ra, int rb, int rc){
        return "SUB \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // SUBQ R,value [30]
    private String SUBQ(int ra, int value){
        if(value > 15) throw new OversizeValueException("Cannot SUBQ because value " + value + " > 15");
        return "SUBQ \t" + reg(ra) + "," + value;
    }

    // MUL R1,R2,Rt [31]
    private String MUL(int ra, int rb, int rc){
        return "MUL \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // MULQ R,value [32]
    private String MULQ(int ra, int value){
        if(value > 15) throw new OversizeValueException("Cannot MULQ because value " + value + " > 15");
        return "MULQ \t" + reg(ra) + "," + value;
    }

    // DIV R1,R2,Rt [33]
    private String DIV(int ra, int rb, int rc){
        return "MUL \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // DIVQ R,value [34]
    private String DIVQ(int ra, int value){
        if(value > 15) throw new OversizeValueException("Cannot DIVQ because value " + value + " > 15");
        return "DIVQ \t" + reg(ra) + "," + value;
    }

    // MOD R1,R2,Rt [35]
    private String MOD(int ra, int rb, int rc){
        return "MOD  \t" + reg(ra) + "," + reg(rb) + reg(rc);
    }

    // MODQ R1,R2,Rt [36]
    private String MODQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot MODQ because value " + val + " > 15");
        return "MODQ  \t" + reg(ra) + "," + val;
    }

    // SAL R1,R2,Rt [37]
    private String SAL(int ra, int rb, int rc){
        return "SAL  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // SLL R1,R2,Rt (same op as SAL) [37]
    private String SLL(int ra, int rb, int rc){
        return SAL(ra,rb,rc);
    }

    // SALQ R1,value [38]
    private String SALQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot SALQ because value " + val + " > 15");
        return "SALQ  \t" + reg(ra) + "," + val;
    }
    // SLLQ R1,value [38]
    private String SLLQ(int ra, int val){
        return SALQ(ra,val);
    }

    // SAR R1,R2,Rt [39]
    private String SAR(int ra, int rb, int rc){
        return "SAR  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // SARQ R1,R2,Rt [40]
    private String SARQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot SARQ because value " + val + " > 15");
        return "SARQ  \t" + reg(ra) + "," + val;
    }

    // SLR R1,R2,Rt [41]
    private String SLR(int ra, int rb, int rc){
        return "SLR  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }
    // SLRQ R1,R2,Rt [42]
    private String SLRQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot SLRQ because value " + val + " > 15");
        return "SLRQ  \t" + reg(ra) + "," + val;
    }

    // NOT Rs,Rt [43]
    private String NOT(int ra, int rb){
        return "NOT  \t" + reg(ra) + "," + reg(rb);
    }

    // AND R1,R2,Rt [44]
    private String AND(int ra, int rb, int rc){
        return "ADD  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // ANDQ R,value [45]
    private String ANDQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot ANDQ because value " + val + " > 15");
        return "ANDQ  \t" + reg(ra) + "," + val;
    }

    // OR R1,R2,Rt [46]
    private String OR(int ra, int rb, int rc){
        return "OR  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }
    // ORQ R,value [47]
    private String ORQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot ORQ because value " + val + " > 15");
        return "ORQ  \t" + reg(ra) + "," + val;
    }

    // XOR R1,R2,Rt [48]
    private String XOR(int ra, int rb, int rc){
        return "XOR  \t" + reg(ra) + "," + reg(rb) + "," + reg(rc);
    }

    // XORQ R,value [49]
    private String XORQ(int ra, int val){
        if(val > 15) throw new OversizeValueException("Cannot XORQ because value " + val + " > 15");
        return "XORQ  \t" + reg(ra) + "," + val;
    }

    // B address [50]
    private String B(int addr){
        return "B   \t" + addr;
    }

    // BEQ address [51]
    private String BEQ(int addr){
        return "BEQ  \t" + addr;
    }

    // BE address [51]
    private String BE(int addr){
        return BEQ(addr);
    }

    // BZ address [51]
    private String BZ(int addr){
        return "BZ  \t" + addr;
    }

    // BNE address [52]
    private String BNE(int addr){
        return "BNE  \t" + addr;
    }

    // BNZ address [52]
    private String BNZ(int addr){
        return "BNZ  \t" + addr;
    }

    // BL address [53]
    private String BL(int addr){
        return BLT(addr);
    }

    // BLT address [53]
    private String BLT(int addr){
        return "BLT  \t" + addr;
    }

    // BLE address [54]
    private String BLE(int addr){
        return "BLE  \t" + addr;
    }

    // BG address [55]
    private String BG(int addr){
        return BGT(addr);
    }

    // BGT address [55]
    private String BGT(int addr){
        return "BGT  \t" + addr;
    }

    // BGE address [56]
    private String BGE(int addr){
        return "BGE  \t" + addr;
    }

    // BLTU address [57]
    private String BLTU(int addr){
        return "BLTU  \t" + addr;
    }

    // BLU address [57]
    private String BLU(int addr){
        return "BLU  \t" + addr;
    }

    // BC address [57]
    private String BC(int addr){
        return "BC  \t" + addr;
    }

    // BLEU address [58]
    private String BLEU(int addr){
        return "BLEU  \t" + addr;
    }

    // BGTU address [59]
    private String BGTU(int addr){
        return "BGTU  \t" + addr;
    }

    // BGU address [59]
    private String BGU(int addr){
        return BGTU(addr);
    }

    // BGEU address [60]
    private String BGEU(int addr){
        return "BGEU  \t" + addr;
    }

    // BNC address [60]
    private String BNC(int addr){
        return "BNC  \t" + addr;
    }

    // JUMP address [61]
    private String JUMP(int addr){
        return "JUMP  \t" + addr;
    }

    // HALT [62]
    private String HALT(){
        return "HALT";
    }

    // CALL address [63]
    private String CALL(int addr){
        return "CALL  \t" + addr;
    }

    // EXEC R [64]
    private String EXEC(int ra){
        return "EXEC  \t" + reg(ra);
    }

    // RET [65]
    private String RET(){
        return "RET";
    }

    // REGS [66]
    private String REGS(){
        return "REGS";
    }

    // DUMP [67]
    private String DUMP(){
        return "DUMP";
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
