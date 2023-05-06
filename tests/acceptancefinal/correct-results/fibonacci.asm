#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

fibonacci:			# 1: begin_block, fibonacci, _, _
	sw ra, -0(sp)

L2:			# 2: <=, x, 1, 4
	lw t1, -12(sp)
	li t2, 1
	ble t1, t2, L4

L3:			# 3: jump, _, _, 6
	j L6

L4:			# 4: ret, x, _, _
	lw t1, -12(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L5:			# 5: jump, _, _, 16
	j L16

L6:			# 6: -, x, 1, &1
	lw t1, -12(sp)
	li t2, 1
	sub t1, t1, t2
	sw t1, -16(sp)

L7:			# 7: par, &1, cv, _
	addi fp, sp, 36
	lw t0, -16(sp)
	sw t0, -12(fp)

L8:			# 8: par, &2, ret, _
	addi t0, sp, -20
	sw t0, -8(fp)

L9:			# 9: call, fibonacci, _, _
	lw t0, -4(sp)
	sw t0, -4(fp)
	addi sp, sp, 36
	jal fibonacci
	addi sp, sp, -36

L10:			# 10: -, x, 2, &3
	lw t1, -12(sp)
	li t2, 2
	sub t1, t1, t2
	sw t1, -24(sp)

L11:			# 11: par, &3, cv, _
	addi fp, sp, 36
	lw t0, -24(sp)
	sw t0, -12(fp)

L12:			# 12: par, &4, ret, _
	addi t0, sp, -28
	sw t0, -8(fp)

L13:			# 13: call, fibonacci, _, _
	lw t0, -4(sp)
	sw t0, -4(fp)
	addi sp, sp, 36
	jal fibonacci
	addi sp, sp, -36

L14:			# 14: +, &2, &4, &5
	lw t1, -20(sp)
	lw t2, -28(sp)
	add t1, t1, t2
	sw t1, -32(sp)

L15:			# 15: ret, &5, _, _
	lw t1, -32(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L16:			# 16: end_block, fibonacci, _, _
	lw ra, (sp)
	jr ra

main_fibonacci:			# 17: begin_block, main_fibonacci, _, _
	sw ra, -0(sp)

L18:			# 18: in, x, _, _
	li a7, 5
	ecall
	sw a0, -12(sp)

L19:			# 19: par, x, cv, _
	addi fp, sp, 36
	lw t0, -12(sp)
	sw t0, -12(fp)

L20:			# 20: par, &6, ret, _
	addi t0, sp, -16
	sw t0, -8(fp)

L21:			# 21: call, fibonacci, _, _
	sw sp, -4(fp)
	addi sp, sp, 36
	jal fibonacci
	addi sp, sp, -36

L22:			# 22: out, _, _, &6
	lw a0, -16(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L23:			# 23: end_block, main_fibonacci, _, _
	lw ra, (sp)
	jr ra

main:			# 24: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L25:			# 25: call, main_fibonacci, _, _
	addi fp, sp, 20
	sw sp, -4(fp)
	addi sp, sp, 20
	jal main_fibonacci
	addi sp, sp, -20

L26:			# 26: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L27:			# 27: end_block, main, _, _
