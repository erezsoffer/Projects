// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

  // RAM[addr] = SCREEN
  @SCREEN
  D=A
  @addr
  M=D

(KEYBOARD)
  // D = RAM[KBD]
  @KBD
  D=M

  // if D != 0 goto BLACK
  @BLACK
  D;JNE

  // else goto WHITE
  @WHITE
  0;JMP

(BLACK)
  // D = KBD - 1 (Last screen pixel)
  @KBD
  D=A-1
  // if KBD - 1 < addr goto KEYBOARD
  @addr
  D=D-M
  @KEYBOARD
  D;JLT
  // RAM[addr] = 1111 1111 1111 1111
  @addr
  A=M
  M=-1
  // addr++
  @addr
  M=M+1
  // goto KEYBOARD
  @KEYBOARD
  0;JMP

(WHITE)
  // D = SCREEN (First screen pixel)
  @SCREEN
  D=A
  // if addr < SCREEN goto KEYBOARD
  @addr
  D=M-D
  @KEYBOARD
  D;JLT
  // RAM[addr] = 0000 0000 0000 0000
  @addr
  A=M
  M=0
  // addr--
  @addr
  M=M-1
  // goto KEYBOARD
  @KEYBOARD
  0;JMP  