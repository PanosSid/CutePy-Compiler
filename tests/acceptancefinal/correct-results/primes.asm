#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

divides:			# 1: begin_block, divides, _, _
	sw ra, -0(sp)

L2:			# 2: //, y, x, &1
	lw t1, -16(sp)
	lw t2, -12(sp)
	div t1, t1, t2
	sw t1, -20(sp)

L3:			# 3: *, &1, x, &2
	lw t1, -20(sp)
	lw t2, -12(sp)
	mul t1, t1, t2
	sw t1, -24(sp)

L4:			# 4: ==, y, &2, 6
	lw t1, -16(sp)
	lw t2, -24(sp)
	beq t1, t2, L6

L5:			# 5: jump, _, _, 8
	j L8

L6:			# 6: ret, 1, _, _
	li t1, 1
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L7:			# 7: jump, _, _, 9
	j L9

L8:			# 8: ret, 0, _, _
	li t1, 0
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L9:			# 9: end_block, divides, _, _
	lw ra, (sp)
	jr ra

isPrime:			# 10: begin_block, isPrime, _, _
	sw ra, -0(sp)

L11:			# 11: :=, 2, _, i
	li t0, 2
	sw t0, -16(sp)

L12:			# 12: <, i, x, 14
	lw t1, -16(sp)
	lw t2, -12(sp)
	blt t1, t2, L14

L13:			# 13: jump, _, _, 25
	j L25

L14:			# 14: par, i, cv, _
	addi fp, sp, 28
	lw t0, -16(sp)
	sw t0, -12(fp)

L15:			# 15: par, x, cv, _
	lw t0, -12(sp)
	sw t0, -16(fp)

L16:			# 16: par, &3, ret, _
	addi t0, sp, -20
	sw t0, -8(fp)

L17:			# 17: call, divides, _, _
	sw sp, -4(fp)
	addi sp, sp, 28
	jal divides
	addi sp, sp, -28

L18:			# 18: ==, &3, 1, 20
	lw t1, -20(sp)
	li t2, 1
	beq t1, t2, L20

L19:			# 19: jump, _, _, 22
	j L22

L20:			# 20: ret, 0, _, _
	li t1, 0
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L21:			# 21: jump, _, _, 22
	j L22

L22:			# 22: +, i, 1, &4
	lw t1, -16(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -24(sp)

L23:			# 23: :=, &4, _, i
	lw t0, -24(sp)
	sw t0, -16(sp)

L24:			# 24: jump, _, _, 12
	j L12

L25:			# 25: ret, 1, _, _
	li t1, 1
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L26:			# 26: end_block, isPrime, _, _
	lw ra, (sp)
	jr ra

main_primes:			# 27: begin_block, main_primes, _, _
	sw ra, -0(sp)

L28:			# 28: :=, 2, _, i
	li t0, 2
	sw t0, -12(sp)

L29:			# 29: <=, i, 30, 31
	lw t1, -12(sp)
	li t2, 30
	ble t1, t2, L31

L30:			# 30: jump, _, _, 43
	j L43

L31:			# 31: par, i, cv, _
	addi fp, sp, 28
	lw t0, -12(sp)
	sw t0, -12(fp)

L32:			# 32: par, &5, ret, _
	addi t0, sp, -16
	sw t0, -8(fp)

L33:			# 33: call, isPrime, _, _
	sw sp, -4(fp)
	addi sp, sp, 28
	jal isPrime
	addi sp, sp, -28

L34:			# 34: ==, &5, 1, 36
	lw t1, -16(sp)
	li t2, 1
	beq t1, t2, L36

L35:			# 35: jump, _, _, 40
	j L40

L36:			# 36: out, _, _, i
	lw a0, -12(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L37:			# 37: +, i, 1, &6
	lw t1, -12(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -20(sp)

L38:			# 38: :=, &6, _, i
	lw t0, -20(sp)
	sw t0, -12(sp)

L39:			# 39: jump, _, _, 42
	j L42

L40:			# 40: +, i, 1, &7
	lw t1, -12(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -24(sp)

L41:			# 41: :=, &7, _, i
	lw t0, -24(sp)
	sw t0, -12(sp)

L42:			# 42: jump, _, _, 29
	j L29

L43:			# 43: end_block, main_primes, _, _
	lw ra, (sp)
	jr ra

main:			# 44: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L45:			# 45: call, main_primes, _, _
	addi fp, sp, 28
	sw sp, -4(fp)
	addi sp, sp, 28
	jal main_primes
	addi sp, sp, -28

L46:			# 46: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L47:			# 47: end_block, main, _, _
