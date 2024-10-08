// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

  // RAM[2] = 0
  @R2
  M=0
  // D = RAM[0]
  @R0
  D=M
  // RAM[i] = D
  @i
  M=D

(LOOP)
  // D = RAM[i]
  @i
  D=M
  // if D <= 0 goto END
  @END
  D;JLE
  // D = RAM[1]
  @R1
  D=M
  // RAM[2] += D
  @R2
  M=M+D
  // RAM[i]--
  @i
  M=M-1
  // goto LOOP
  @LOOP
  0;JMP

(END)
  // goto END
  @END
  0;JMP