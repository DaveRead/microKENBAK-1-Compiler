#microKENBAK-1 Compiler

Compiles a small language defined to simplify the programming of the microKENBAK-1 computer.

For example, here is a program, found as **samples/CountUp.kb1**,  to have the LEDs continuously display counting values from 0 to 255 with a 255 ms delay between updating the LEDs:

---
\# Program to repeatedly count from 0 to 255, displaying each value on the LEDs  
  
\# Set A and B for program delay syscall  
let a = 0222  
let b = 0200  
  
\# Initialize X  
let x = 0  
  
\# Top of loop to display counting on LEDs  
label top  
add 1 to x  
memcopy x to display  
syscall  
goto top
---

The language description is found in **documentation/LanguageSpecification.md** 

Example programs are found in the **samples** directory

For information on the microKENBAK-1 computer kit see: [https://adwaterandstir.com/product/kenbak-1/](https://adwaterandstir.com/product/kenbak-1/)
