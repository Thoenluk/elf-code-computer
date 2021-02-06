/*
 * Copyright (C) 2020 Lukas Thöni lukas.thoeni@gmx.ch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package thoenluk.elfcode;

import thoenluk.utility.DefaultValueProvider;
import thoenluk.utility.Ut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author Lukas Thöni lukas.thoeni@gmx.ch
 */
public class ElfCodeComputer {    
    private static final DefaultValueProvider<Integer> ZERO_PROVIDER = () -> { return 0; };
    
    private int ip = 0;
    private int acc = 0;
    int[] args = new int[3];
    private final ArrayList<Instruction> program = new ArrayList<>();
    private final ArrayList<String[]> inputs = new ArrayList<>();
    private final HashSet<Integer> visitedLines = new HashSet<>();
    private final HashMap<Integer, Integer> memory = new HashMap<>();
    private final HashMap<Integer, Integer> functions = new HashMap<>();
    private final LinkedList<Integer> inputBuffer = new LinkedList<>();
    private final LinkedList<Integer> outputBuffer = new LinkedList<>();
    private final Stack<Integer> stack = new Stack();
    
    public ElfCodeComputer(String[] programDescription) {
        String[] instructionSet;
        for(String line : programDescription) {
            if(line.length() == 0) {
                program.add(Instruction.nop);
                inputs.add(new String[0]);
            } else {
                instructionSet = line.split(" ");
                if(instructionSet.length == 1) {
                    if(instructionSet[0].equals("BRK")) {
                        program.add(Instruction.brk);
                        inputs.add(new String[0]);
                    } else {
                        functions.put(Ut.cachedParseInt(instructionSet[0].substring(0, instructionSet[0].length() - 1)), ip);
                        program.add(Instruction.nop);
                        inputs.add(new String[0]);
                    }
                } else {
                    program.add(Instruction.valueOf(instructionSet[0].toLowerCase()));
                    inputs.add(Arrays.copyOfRange(instructionSet, 1, instructionSet.length));
                }
            }
            ip++;
        }
        ip = 0;
    }
    
    /**
     * Run the program within this ECC until it terminates (IP goes out of bounds),
     * regardless of if it might loop. Since the Halting Problem is what it is, this
     * method may have you stuck in an infinite loop, but we consciously don't check.
     */
    public void runProgram() {
        String[] input;
        int i;
        while(ipIsValid()) {
            input = inputs.get(ip);
            for(i = 0; i < input.length; i++) {
                args[i] = resolve(input[i]);
            }
            program.get(ip).code.accept(args, this);
            ip++;
        }
    }
    
    /**
     * Run the program within this ECC until a line is repeated, which may indicate a loop.
     * @return true if the program terminated (IP went beyond program bounds), false if it looped.
     * Eat your heart out, Turing!
     */
    public boolean runProgramUntilLoop() {
        String[] input;
        int i;
        while(!isLooping() && ipIsValid()) {
            input = inputs.get(ip);
            for(i = 0; i < input.length; i++) {
                args[i] = resolve(input[i]);
            }
            program.get(ip).code.accept(args, this);
            ip++;
        }
        return !ipIsValid();
    }
    
    private boolean ipIsValid() {
        return 0 <= ip && ip < program.size();
    }
    
    private boolean isLooping() {
        return !visitedLines.add(ip);
    }
    
    private int resolve(String directOrReference) {
        switch(directOrReference.charAt(0)) {
            case 'A':
                // ACC
                return acc;
            case 'I':
                // IPT
                return directOrReference.charAt(1) == 'P' ? ip : readInput();
            case 'R':
                // Register reference
                return Ut.getOrDefault(memory, Ut.cachedParseInt(directOrReference.substring(1)), ZERO_PROVIDER);
            case 'S':
                // STK (Stack)
                return stack.pop();
            default:
                // Literal
                return Ut.cachedParseInt(directOrReference);
        }
    }
    
    protected int getAcc() {
        return acc;
    }
    
    protected void setAcc(int acc) {
        this.acc = acc;
    }

    protected int getIp() {
        return ip;
    }

    protected void setIp(int ip) {
        this.ip = ip;
    }
    
    protected void setMemory(int address, int value) {
        memory.put(address, value);
    }
    
    protected int readMemory(int address) {
        return Ut.getOrDefault(memory, address, ZERO_PROVIDER);
    }
    
    protected void addInput(int input) {
        inputBuffer.add(input);
    }
    
    protected int readInput() {
        return inputBuffer.pop();
    }
    
    protected void addOutput(int output) {
        outputBuffer.add(output);
    }
    
    protected int readOutput() {
        return outputBuffer.pop();
    }
    
    protected void push(int value) {
        stack.push(value);
    }
    
    protected int pop() {
        return stack.pop();
    }
    
    protected void callFunction(int descriptor) {
        setIp(functions.get(descriptor) - 1);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IP: ").append(ip).append("\n");
        sb.append("MEM: ").append(memory.toString()).append("\n");
        sb.append("IPT: ").append(inputBuffer.toString()).append("\n");
        sb.append("OPT: ").append(outputBuffer.toString()).append("\n");
        sb.append("STK: ").append(stack.toString()).append("\n");
        return sb.toString();
    }
}