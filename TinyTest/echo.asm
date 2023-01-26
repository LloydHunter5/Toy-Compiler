echo:
  in R1
  out R1
  cmpq R1,'\n'
  bne echo
  
  halt
