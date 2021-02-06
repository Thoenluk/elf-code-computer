package thoenluk.elfcode;

/**
 *
 * @author Lukas Thöni lukas.thoeni@gmx.ch
 */
public class Line {
    private Instruction instruction;
    private String[] input;
    private int[] args;
    
    public Line(Instruction instruction, String[] input) {
        this.instruction = instruction;
        this.input = input;
        this.args = new int[input.length];
    }
    
    public Line(Instruction instruction) {
        this(instruction, new String[0]);
    }
    
    public Line(String instructionName, String[] input) {
        this(Instruction.valueOf(instructionName.toLowerCase()), input);
    }
    
    public Line(String instructionName) {
        this(instructionName, new String[0]);
    }
    
    public void execute(ElfCodeComputer target) {
        for(int i = 0; i < input.length; i++) {
            args[i] = target.resolve(input[i]);
        }
        this.instruction.code.accept(args, target);
    }
}
