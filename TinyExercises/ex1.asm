main:
    //1A
    call init
    call println
    //1B
    call init
    call printFlipCase
    //1C
    call init
    call flipString
    li R0,FLIP
    call println
    halt

init://helper method for main
    li R0,TINY
    li R10,FLIP
    ret

print: //params (addr:R0)
    lbx R1,R0,0//load char from addr
    addq R0,1//Inc mem address by 1 (get next char)
    //check R1 for null
    cmpq R1,'\0'
    out R1
    bne print
    ret

println: //params(addr:R0)
    call print
    ldq R2,'\n'
    out R2
    ret

flipString://(in_addr:R0,out_addr:R10)
    //Load next char at R0, flip it
    lbx R5,R0,0
    call flipAndStore
    //increment in and out addrs
    addq R10,1
    addq R0,1
    //check for null
    cmpq R5,'\0'
    bne flipString
    ret

printFlipCase://(addr:R0)
    lbx R5,R0,0
    call flip
    addq R0,1
    cmpq R5,'\0'
    out R5
    bne printFlipCase
    ldq R2,'\n'
    out R2
    ret

flip: //(char:R5)
    li R6,'a'
    li R7,'A'
    li R8,'z'
    li R9,'Z'
upper:
    //if c>=A
    cmp R5,R7
    bl endFlip
    //&& c<=Z
    cmp R5,R9
    bg lower
    call utl
    //skips lowercase code if utl makes R5 lowercase
    b endFlip
lower:
    //if c>=a
    cmp R5,R6
    bl endFlip
    //&& c<=z
    cmp R5,R8
    bg endFlip
    call ltu
endFlip:
    ret

flipAndStore://(char:R5,addr:R10)
    //Stores flipped char in addr in R10
    call flip
    stbx R5,R10,0
    ret

ltu://lower to upper
    add R5,R7,R11
    sub R11,R6,R5
    ret

utl://upper to lower
    add R5,R6,R11
    sub R11,R7,R5
    ret

TINY: .ascii "Tiny is FUN"
FLIP: .space 12
