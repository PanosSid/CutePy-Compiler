#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

main_countdigits:			# 1: begin_block, main_countdigits, _, _
	sw ra, -0(sp)

L2:			# 2: in, x, _, _
	li a7, 5
	ecall
	sw a0, -12(sp)

L3:			# 3: :=, 0, _, count
	li t0, 0
	sw t0, -16(sp)

L4:			# 4: >, x, 0, 6
	lw t1, -12(sp)
	li t2, 0
	bgt t1, t2, L6

L5:			# 5: jump, _, _, 11
	j L11

L6:			# 6: //, x, 10, &1
	lw t1, -12(sp)
	li t2, 10
	div t1, t1, t2
	sw t1, -20(sp)

L7:			# 7: :=, &1, _, x
	lw t0, -20(sp)
	sw t0, -12(sp)

L8:			# 8: +, count, 1, &2
	lw t1, -16(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -24(sp)

L9:			# 9: :=, &2, _, count
	lw t0, -24(sp)
	sw t0, -16(sp)

L10:			# 10: jump, _, _, 4
	j L4

L11:			# 11: out, _, _, count
	lw a0, -16(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L12:			# 12: end_block, main_countdigits, _, _
	lw ra, (sp)
	jr ra

main:			# 13: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L14:			# 14: call, main_countdigits, _, _
	addi fp, sp, 28
	sw sp, -4(fp)
	addi sp, sp, 28
	jal main_countdigits
	addi sp, sp, -28

L15:			# 15: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L16:			# 16: end_block, main, _, _
