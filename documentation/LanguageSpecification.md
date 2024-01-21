#KBlang

##Overview
KBlang provides a higher-level language for the microKENBAK-1's operating codes without adding complex abstractions. For the most part, the syntax parallels the microKENBAK-1 byte sequences using mnemonic keywords.

Statements may not be split across lines. Blank lines are ignored.

## Numeric Literals
Numbers may be expressed in decimal, octal, or hexadecimal. A number without a leading 0 is interpreted as decimal, a number beginning with a leading 0 is interpreted as octal, and numbers beginning with 0x are interpreted as hexadecimal.

Most of the examples in this guide use octal values to align with the original KENBAK-1 programming guide which generally used octal numbers. However, KBlang allows equivalent decimal and hexadecimal numbers to be used anywhere an integer is expected.

### Examples
Decimal values: 8, 15, 16, 127, 128, 255

Octal values: 010, 017, 020, 0177, 0200, 0377

Hexadecimal values: 0x8, 0xF, 0x10, 0x7F, 0x80, 0xFF

##Keywords
Keywords are not case sensitive. `LABEL`, `label`, and `Label` are equivalent.

The complete list of keywords are:

`ADD, ADDRESSIN, BITSHIFT, BYTES, FROM, GOTO, HALT, IF, ISZERO, LABEL, LEFT, LET, NOTZERO, RIGHT, SUBTRACT, SYSCALL, TO, VALUEIN`

##Variables
The first three bytes of memory serve as registers for operations and are referred to as `A`, `B`, and `X`. Those names are used in KBlang to read and write values to these locations. These are the only variables used in the language.

##Convenience Mnemonic Names
Three convenience names are defined. These may be used with the `MEMCOPY` and `VALUEIN` statement. The mnemonic names are not case sensitive.

`DISPLAY` represents the memory location (0200) for controlling the data LEDs

`INPUT` represents the memory location (0377) for reading the button input value

`P` represents the program instruction counter (03)

##Comments
The hash (#) is used to begin a single-line comment. All remaining characters on the line following the # are ignored. Comments may be placed on a line by themselves or at the end of executable statements.

###Examples
	# Top of Loop
	let a = 074 # set a to the number of seconds in a minute

##Assignment
The `LET` statement is used for assignment. The form is:

`LET` *variable_name* `=` *value*

or

`LET` *variable_name* `= VALUEIN` *memory_location*


The *variable_name* must be one of: `A, B, X`

The *value* must be an octal value in the range of 0 to 0377

The *memory_location* must be a memory address in the range 0 to 0377 or a mnemonic name

###Examples
	LET A = 043
	let B = 0177
	let X = VALUEIN DISPLAY
	let a = valuein 0377
	
##Labels
A label serves as a location for a jump. It is a name representing a memory location. The form is:

`LABEL` *label_name*

The *label_name* can be any set of characters, and **is case sensitive**

###Examples
	Label top
	LABEL beginLoop

##Unconditional Jump
The `GOTO` statement is used to jump to a label. The label can be defined anywhere in the program and execution will continue from that point. The form is:

`GOTO` *label_name*

The *label_name* must be defined elsewhere in the program using a `LABEL` statement

###Examples
	GOTO top
	Goto beginLoop

##Conditional Jump
**Currently only two of the KENBAK-1 conditional operations are supported: equal to zero and not equal to zero.**

The `IF` statement is used for conditional jumps. It can be used based on a variable's value being zero or non-zero. It can also be used to jump based on whether a variable overflow (carry) flag is set (e.g., on by the last add or subtract done using the variable). The form is:

`IF` *variable_name* *test_type* `GOTO` *label_name*

The *variable_name* must be one of `A, B, X` 

The *test_type* must be one of `ISZERO`, `NOTZERO`, or `OVERFLOW`

The *label_name* must be defined elsewhere in the program using a `LABEL` statement

###Examples
	IF A NOTZERO GOTO Top`
	if b iszero goto beginLoop`
	if a overflow goto handleOverflow

##Copy Memory Byte Values
The `MEMCOPY` statement is used to copy a variable's value to a memory location. There are two forms, depending on how the destination value is interpreted. The forms are:

`MEMCOPY` *variable_name* `TO` *destination*

or

`MEMCOPY` *variable_name* `TO` `ADDRESSIN` *destination*

The *variable_name* must be one of `A, B, X` 

The *destination* must be a memory address, a variable name, or a mnemonic location name 

In the first form, the address to be written is supplied directly. In the second form, the address to be written is contained within the supplied address.

###Examples
	MEMCOPY A TO B
	memcopy a to display
	memcopy b to a
	memcopy x to 0200`

---
	# circuitous way to set LEDs to 0252, demonstrating ADDRESSIN
	let a = 128 # Demonstrating use of decimal (octal 0200)
	let b = 0xAA  # Demonstrating use of hexadecimal (octal 252)
	memcopy b to addressin a
---

##Addition
The `ADD` statement is used to add a value to a variable. The form is:

`ADD` *source* `TO` *variable_name*

The *source* must be a *variable_name* or a literal (octal value in the range of 0 to 0377)

The *variable_name* must be one of `A, B, X` 

###Examples
	ADD 1 TO A
	add 010 to b
	add x to a

##Subtraction
The `SUBTRACT` statement is used to subtract a value from a variable. The form is:

`SUBTRACT` *source* `FROM` *variable_name*

The *source* must be a *variable_name* or a literal (octal value in the range of 0 to 0377)

The *variable_name* must be one of `A, B, X` 

###Examples
	SUBTRACT 02 FROM B
	subtract 0177 from a
	subtract a from b

##Bitwise AND
The value in the variable A (memory location 0) can be bitwise ANDed with another value. The result is placed in A, replacing its previous value. The form is:

`AND *value*`

The *value* must be an octal value in the range of 0 to 0377

###Examples
	AND 017 # Keep lower 4 bits
	and 0376 # Keep upper 7 bits

##Bitwise OR
The value in the variable A (memory location 0) can be bitwise ORed with another value. The result is placed in A, replacing its previous value. The form is:

`OR *value*`

The *value* must be an octal value in the range of 0 to 0377

###Examples
	OR 0340 # Force upper 4 bits on
	or 07 # Force lower 3 bits on


##Bit shift
**Only the A and B variables may be shifted and shifts are limited to 1 to 4 bits.**

The value in the variable (A or B) is shifted 1 to 4 bits to the left or right. The update is made in place, changing the variable's value directly. The form is:

`BITSHIFT *variable_name* *direction* *bit_count*`

The *variable_name* must be A or B

The *direction* must be RIGHT (toward LSB) or LEFT (toward MSB)

The *bit_count* value is optional and must be in the range 1 to 4 (1 is the default)

Note that a left shift sets bit 0 to 0, while a right shift retains the value in bit 7 (e.g., performs a signed right shift). If an unsigned right shift is desired it can be approximated by using register A to perform a one-bit right shift and then ANDing the value with 0177. 

###Examples
	BITSHIFT A RIGHT
	BITSHIFT B LEFT 3
	bitshift a left 2
---
	# Unsigned right shift of 3 bits (must use variable A)
	bitshift a right 1
	and 0177
	bitshift a right 2
---

##Halt program
The `HALT` statement is used to stop the program's execution. This sends the instruction value of 0000. The form is:

`HALT`

##System call (microKENBAK-1 extension)
The `SYSCALL` statement is used to invoke a system call. This sends an instruction value of 0360. The documentation for the microKENBAK-1 explains the functionality available, and typically involves the setting the A, and possibly B, variables, then executing the 0360 instruction, and, for calls that produce values, reading the result from B. The form is:

`SYSCALL`

###Examples

####Generate a random number (in B)
	let a = 021
	syscall

####Delay program execution for 255 milliseconds
	let a = 0222
	let b = 0377
	syscall

##Raw byte value
The `BYTES` statement is used to include raw byte values in the program. The statement expects one or more byte values to be supplied. Each will be placed sequentially in memory beginning at the location the statement is found in the program. The form is:

`BYTES *byte_values*`

The *byte_values* must be values in the range 0-255 (decimal) separated by spaces. Values may be expressed in octal, decimal, or hexadecimal as described in the *Numeric Literals* section.

###Examples

####Directly use the assign operation codes to assign decimal 10 to A (location 0)
`BYTES 023 012`

