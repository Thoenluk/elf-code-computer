# elf-code-computer

An interpreter and runtime environment for a mostly-assembly-like language running in Java.

## Usage:

Write yer code in a text file, then run ECCRunner.java (or provide your code in a String[] to ElfCodeComputer's constructor, with each line being an item in the array) and watch the fireworks. See test.txt for an example.

Each instruction must be on its own separate line. Blank lines may be used, but contribute to line count. Each line must be one instruction named followed by a list of arguments.

Arguments may be:

* Integer literals
* R<n> (without the <>) which will dereference the value in register address n. Register addresses may be arbitrary integers. An uninitialised register has value 0.
* ACC for the accumulator (which is just a named register that can be increased with the ACC instruction, see below)
* IP for the instruction pointer. Contains the current zero-indexed line number. Is incremented by 1 AFTER each instruction.
* STK for the top value of the stack, which will be popped and thus removed when read.

You may define functions by writing labels into a separate line like:
  <n>:
Where n is any integer value. These serve as markers to jump to regardless of actual line number. See CAL below for how to jump to these markers.
    
## Available instructions:

NOP(): Do nothing. Identical to blank line.
ACC(arg0): Add the value of arg0 to the accumulator.
JMP(arg0): Set the instruction pointer to arg0 - 1, such that the next instruction executed will be at line arg0.
JRL(arg0): Set the instruction pointer to <current line number> + arg0 - 1, such that the next instruction executed will be arg0 lines after this one.
JIF(arg0, arg1): If arg0 is > 0, behave like JMP(arg1). Otherwise, do nothing.
JIR(arg0, arg1): If arg0 is > 0, behave like JRL(arg1). Otherwise, do nothing.
MOV(arg0, arg1): Set the register with address arg0 to value arg1.
PRT(arg0): Print arg0 to console as a number.
PRC(arg0): Print arg0 to console as the char it represents (probably throw exception if no such character exists)
PSH(arg0): Push arg0 to the stack.
POP(arg0): Pop the top value of the stack and store it in register address arg0 as by MOV(arg0).
ADD(arg0, arg1, arg2): Set the register with address arg0 to value arg1 + arg2.
SUB(arg0, arg1, arg2): Set the register with address arg0 to value arg1 - arg2.
MUL(arg0, arg1, arg2): Set the register with address arg0 to value arg1 * arg2.
DIV(arg0, arg1, arg2): Set the register with address arg0 to value arg1 / arg2. Integer division is used, so rounding down.
LSH(arg0, arg1, arg2): Set the register with address arg0 to value arg1 << arg2.
RSH(arg0, arg1, arg2): Set the register with address arg0 to value arg1 >> arg2.
CAL(arg0): Call the function with label arg0. There must be a line containing only <arg0>: at some point in the program. Behaves like JMP(n) wherein n is the line number of the function. The next instruction executed will be the first instruction below this function label. (Technically it's a NOP inserted at that line, but same thing.)
BRK(arg...): Stop the machine, print out its inner state, and wait until any input is given on console. If no arguments are supplied, the entire state is printed. If arguments are supplied, only the registers with the given addresses are printed.
