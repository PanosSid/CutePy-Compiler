#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

main_factorial:			# 1: begin_block, main_factorial, _, _
	sw ra, -0(sp)

L2:			# 2: in, x, _, _
	li a7, 5
	ecall
	sw a0, -12(sp)

L3:			# 3: :=, 1, _, fact
	li t0, 1
	sw t0, -20(sp)

L4:			# 4: :=, 1, _, i
	li t0, 1
	sw t0, -16(sp)

L5:			# 5: <=, i, x, 7
	lw t1, -16(sp)
	lw t2, -12(sp)
	ble t1, t2, L7

L6:			# 6: jump, _, _, 12
	j L12

L7:			# 7: *, fact, i, &1
	lw t1, -20(sp)
	lw t2, -16(sp)
	mul t1, t1, t2
	sw t1, -24(sp)

L8:			# 8: :=, &1, _, fact
	lw t0, -24(sp)
	sw t0, -20(sp)

L9:			# 9: +, i, 1, &2
	lw t1, -16(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -28(sp)

L10:			# 10: :=, &2, _, i
	lw t0, -28(sp)
	sw t0, -16(sp)

L11:			# 11: jump, _, _, 5
	j L5

L12:			# 12: out, _, _, fact
	lw a0, -20(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L13:			# 13: end_block, main_factorial, _, _
	lw ra, (sp)
	jr ra

main:			# 14: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L15:			# 15: call, main_factorial, _, _
	addi fp, sp, 32
	sw sp, -4(fp)
	addi sp, sp, 32
	jal main_factorial
	addi sp, sp, -32

L16:			# 16: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L17:			# 17: end_block, main, _, _
