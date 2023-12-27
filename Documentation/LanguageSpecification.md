#KBlang

##Overview

KBlang provides a higher-level language for the microKENBAK-1's operating codes without adding complex abstractions. For the most part, the syntax parallels the microKENBAK-1 byte sequences using mnemonic keywords.

Statements may not be split across lines. Blank lines are ignored.

##Keywords

Keywords are not case sensitive. LABEL, label, and Label as equivalent.

The complete list of keywords are:

`ADD, FROM, GOTO, HALT, IF, ISZERO, LABEL, LET, NOTZERO, SUBTRACT, SYSCALL, TO`

##Variables

The first three bytes of memory serve as registers for operations and are referred to as `A`, `B`, and `X`. Those names are used in KBlang to read and write values to these locations. These are the only variables used in the language.

##Convenience Mnemonic Names

Two convenience names are defined. These may be used with the `MEMCOPY` statement.

`DISPLAY` represents the memory location (0200) for setting the LEDs

`INPUT` represents the memory location (0377) for reading the button input value

##Comments

The hash (#) is used to begin a single-line comment. All remaining characters on the line following the # are ignored. Comments may be placed on a line by themselves or at the end of executable statements.

###Examples

`# Top of Loop`

`let a = 074 # set a to the number of seconds in a minute`

##Assignment

The `LET` statement is used for assignment. The form is:

`LET` *variable_name* `=` *value*

The *variable_name* must be one of: `A, B, X`

The *value* must be an octal value in the range of 0 to 0377

###Examples

`LET A = 043`

`let B = 0177`

##Labels

A label serves as a location for a jump. It is a name representing a memory location. The form is:

`LABEL` *label_name*

The *label_name* can be any set of characters, and is case sensitive

###Examples

`Label top`

`LABEL beginLoop`

##Unconditional Jump

The `GOTO` statement is used to jump to a label. The label can be defined anywhere in the program and execution will continue from that point. The form is:

`GOTO` *label_name*

The *label_name* must be defined elsewhere in the program using a `LABEL` statement

###Examples

`GOTO top`

`Goto beginLoop`

##Conditional Jump

**Currently only two of the KENBAK-1 conditional operations are supported: equal to zero and not equal to zero.**

The `IF` statement is used for conditional jumps. The form is:

`IF` *variable_name* *test_type* `GOTO` *label_name*

The *variable_name* must be one of `A, B, X` 

The *test_type* must be one of `ISZERO` or `NOTZERO`

The *label_name* must be defined elsewhere in the program using a `LABEL` statement

###Examples

`IF A NOTZERO GOTO Top`

`if b iszero goto beginLoop`

##Copy Memory Byte Values

The `MEMCOPY` statement is used to copy a variable's value to a memory location. The form is:

`MEMCOPY` *variable_name* `TO` *destination*

The *variable_name* must be one of `A, B, X` 

The *destination* must be an octal address, a variable name, or a mnemonic location name 

###Examples

`MEMCOPY A TO B`

`memcopy a to display`

`memcopy b to a`

`memcopy x to 0200`

##Addition

The `ADD` statement is used to add a value to a variable. The form is:

`ADD` *value* `TO` *variable_name*

The *value* must be an octal value in the range of 0 to 0377

The *variable_name* must be one of `A, B, X` 

###Examples

`ADD 1 TO A`

`add 010 to b`

##Subtraction

The `SUBTRACT` statement is used to subtract a value form a variable. The form is:

`SUBTRACT` *value* `FROM` *variable_name*

The *value* must be an octal value in the range of 0 to 0377

The *variable_name* must be one of `A, B, X` 

###Examples

`SUBTRACT 02 FROM B`

`subtract 0177 from a`

##Bitwise AND

The value in the variable A (memory location 0) can be bitwise ANDed with another value. The result is placed in A, replacing its original value. The form is:

`AND *value*`

The *value* must be an octal value in the range of 0 to 0377

###Examples

`AND 017 # Keep lower 4 bits`

`and 0376 # Keep upper 7 bits`

##Bitwise OR

The value in the variable A (memory location 0) can be bitwise ORed with another value. The result is placed in A, replacing its original value. The form is:

`OR *value*`

The *value* must be an octal value in the range of 0 to 0377

###Examples

`OR 0340 # Force upper 4 bits on`

`or 07 # Force lower 3 bits on`

##System call (microKENBAK-1 extension)

The `SYSCALL` statement is used to invoke a system call. This sends an instruction value of 0360. The documentation for the microKENBAK-1 explains the functionality available, and typically involves the setting the A and, possibly, B, variables, then executing the 0360 instruction, and, for calls that produce values, reading the result from B. The form is:

`SYSCALL`

###Examples

####Generate a random number (in B)
`let a = 021`

`syscall`

####Delay program execution for 255 milliseconds
`let a = 0222`

`let b = 0377`

`syscall`

##Halt program

The `HALT` statement is used to stop the program's execution. This sends the instruction value of 0000. The form is:

`HALT`

