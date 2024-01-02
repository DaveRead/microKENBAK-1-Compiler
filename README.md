#microKENBAK-1 Compiler

Compiles a small language defined to simplify the programming of the microKENBAK-1 computer.

![Front of microKENBAK-1 with several LEDs lit](microKENBAK-1.dsr.crop.small.jpg "Front of microKENBAK-1")

For example, here is a program, found as **samples/CountUp.kb1**,  to have the LEDs continuously display counting values from 0 to 255 with a 255 ms delay between updates of the LEDs:

---
	# Program to repeatedly count from 0 to 255, displaying each value on the LEDs

	# Set A and B for program delay syscall
	let a = 0377
	let b = 0200

	# Initialize X
	let x = 0

	# Top of loop to display counting on LEDs 
	label top
	add 1 to x
	memcopy x to display
	syscall
	goto top
---

The language description is found in **[documentation/LanguageSpecification.md](documentation/LanguageSpecification.md)** 

Example programs are found in the **samples** directory and **[videos of several sample programs running](https://www.youtube.com/playlist?list=PL6TXVZYCjsKYymB6kaxiJsRQTL8TwtUdY)** are on YouTube.

For information on the microKENBAK-1 computer kit see: [https://adwaterandstir.com/product/kenbak-1/](https://adwaterandstir.com/product/kenbak-1/)
