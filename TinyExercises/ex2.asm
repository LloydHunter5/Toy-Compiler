main:
  li R5,10//Load radix 10
secondmain:
  li R1,1
  call printlnIntDirect
  li R1,-1
  call printlnIntDirect
  li R1,11
  call printlnIntDirect
  li R1,-11
  call printlnIntDirect
  li R1,751
  call printlnIntDirect
  li R1,-751
  call printlnIntDirect
  li R1,1
  SALQ R1,14
  call printlnIntDirect
  li R1,1
  SALQ R1,15
  call printlnIntDirect
  //change radix
  li R8,16
  cmp R5,R8
  be ending
  li R5,16//load base 16 if not already
  b secondmain
ending:
  halt

printInt://params(addr:R0,radix:R5)
  lwx R1,R0,0
printIntDirect://params(num:R1,radix:R5)
  cmpq R1,0
  bg printNumRec
  //Special case for -0
  li R7,1
  SALQ R7,15
  cmp R1,R7
  beq printNegativeZero
  //if negative, put a negative sign and print the positive number
  li R6,'-'
  out R6
  neg R1
printNumRec://recursive part
  cmpq R1,0
  ble donePrintingNumber
  //push register, divide by 10
  push R1
  div R1,R5,R1
  call printNumRec
  pop R1
  mod R1,R5,R3
  cmpq R3,9
  ble regularNum
  li R2,'A'
  subq R2,10
  add R2,R3,R4
  out R4
  b donePrintingNumber
regularNum:
  li R2,'0'
  add R2,R3,R4
  out R4
donePrintingNumber:
  ret
printNegativeZero:
  li R7,'0'
  out R7
  ret

printlnInt:
  call printInt
  ldq R9,'\n'
  out R9
  ret

printlnIntDirect:
  call printIntDirect
  ldq R9,'\n'
  out R9
  ret

halt
NUM: .word 0
