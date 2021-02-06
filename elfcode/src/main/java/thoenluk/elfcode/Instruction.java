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

import java.io.IOException;
import java.util.Scanner;
import java.util.function.BiConsumer;

/**
 *
 * @author Lukas Thöni lukas.thoeni@gmx.ch
 */
public enum Instruction {
    nop((int[] args, ElfCodeComputer target) -> {}),
    acc((int[] args, ElfCodeComputer target) -> {
        target.setAcc(target.getAcc() + args[0]);
    }),
    jmp((int[] args, ElfCodeComputer target) -> {
       target.setIp(args[0] - 1);
    }),
    jrl((int[] args, ElfCodeComputer target) -> {
        target.setIp(target.getIp() + args[0] - 1);
    }),
    jif((int[] args, ElfCodeComputer target) -> {
        if(args[0] > 0) {
            target.setIp(args[1]);
        }
    }),
    jir((int[] args, ElfCodeComputer target) -> {
        if(args[0] > 0) {
            target.setIp(target.getIp() + args[1] - 1);
        }
    }),
    mov((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1]);
    }),
    prt((int[] args, ElfCodeComputer target) -> {
        System.out.print(args[0]);
    }),
    prc((int[] args, ElfCodeComputer target) -> {
        System.out.print((char) args[0]);
    }),
    psh((int[] args, ElfCodeComputer target) -> {
        target.push(args[0]);
    }),
    pop((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], target.pop());
    }),
    add((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] + args[2]);
    }),
    sub((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] - args[2]);
    }),
    mul((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] * args[2]);
    }),
    div((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] / args[2]);
    }),
    lsh((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] << args[2]);
    }),
    rsh((int[] args, ElfCodeComputer target) -> {
        target.setMemory(args[0], args[1] >> args[2]);
    }),
    cal((int[] args, ElfCodeComputer target) -> {
        target.callFunction(args[0]);
    }),
    brk((int[] args, ElfCodeComputer target) -> {
        if(args.length == 0) {
            System.out.println(target.toString());
        } else {
            for(Integer register : args) {
                System.out.println("MEM #" + register + " = " + target.readMemory(register));
            }
        }
        try {
            System.in.read();
        } catch(IOException io) {
            System.out.println("oh noes");
        }
    });


    public final BiConsumer<int[], ElfCodeComputer> code;
    
    private Instruction(BiConsumer<int[], ElfCodeComputer> code) {
        this.code = code;
    }
}
