#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

F1111:			# 1: begin_block, F1111, _, _
	sw ra, -0(sp)

L2:			# 2: :=, 0, _, var
	li t0, 0
	sw t0, -12(sp)

L3:			# 3: +, glob, param, &1
	lw t0, -4(sp)
	lw t0, -4(t0)
	lw t0, -4(t0)
	lw t0, -4(t0)
	addi t0, t0, -12
	lw t1, (t0)
	lw t0, -4(sp)
	lw t0, -4(t0)
	lw t0, -4(t0)
	addi t0, t0, -12
	lw t2, (t0)
	add t1, t1, t2
	sw t1, -16(sp)

L4:			# 4: +, &1, var, &2
	lw t1, -16(sp)
	lw t2, -12(sp)
	add t1, t1, t2
	sw t1, -20(sp)

L5:			# 5: ret, &2, _, _
	lw t1, -20(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L6:			# 6: end_block, F1111, _, _
	lw ra, (sp)
	jr ra

F111:			# 7: begin_block, F111, _, _
	sw ra, -0(sp)

L8:			# 8: par, &3, ret, _
	addi fp, sp, 24
	addi t0, sp, -12
	sw t0, -8(fp)

L9:			# 9: call, F1111, _, _
	sw sp, -4(fp)
	addi sp, sp, 24
	jal F1111
	addi sp, sp, -24

L10:			# 10: ret, &3, _, _
	lw t1, -12(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L11:			# 11: end_block, F111, _, _
	lw ra, (sp)
	jr ra

F11:			# 12: begin_block, F11, _, _
	sw ra, -0(sp)

L13:			# 13: :=, glob, _, var
	lw t0, -4(sp)
	lw t0, -4(t0)
	addi t0, t0, -12
	lw t0, (t0)
	sw t0, -12(sp)

L14:			# 14: par, &4, ret, _
	addi fp, sp, 16
	addi t0, sp, -16
	sw t0, -8(fp)

L15:			# 15: call, F111, _, _
	sw sp, -4(fp)
	addi sp, sp, 16
	jal F111
	addi sp, sp, -16

L16:			# 16: ret, &4, _, _
	lw t1, -16(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L17:			# 17: end_block, F11, _, _
	lw ra, (sp)
	jr ra

F1:			# 18: begin_block, F1, _, _
	sw ra, -0(sp)

L19:			# 19: par, &5, ret, _
	addi fp, sp, 20
	addi t0, sp, -16
	sw t0, -8(fp)

L20:			# 20: call, F11, _, _
	sw sp, -4(fp)
	addi sp, sp, 20
	jal F11
	addi sp, sp, -20

L21:			# 21: ret, &5, _, _
	lw t1, -16(sp)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L22:			# 22: end_block, F1, _, _
	lw ra, (sp)
	jr ra

main_f:			# 23: begin_block, main_f, _, _
	sw ra, -0(sp)

L24:			# 24: :=, 1, _, glob
	li t0, 1
	sw t0, -12(sp)

L25:			# 25: par, glob, cv, _
	addi fp, sp, 20
	lw t0, -12(sp)
	sw t0, -12(fp)

L26:			# 26: par, &6, ret, _
	addi t0, sp, -16
	sw t0, -8(fp)

L27:			# 27: call, F1, _, _
	sw sp, -4(fp)
	addi sp, sp, 20
	jal F1
	addi sp, sp, -20

L28:			# 28: out, _, _, &6
	lw a0, -16(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L29:			# 29: end_block, main_f, _, _
	lw ra, (sp)
	jr ra

main:			# 30: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L31:			# 31: call, main_f, _, _
	addi fp, sp, 20
	sw sp, -4(fp)
	addi sp, sp, 20
	jal main_f
	addi sp, sp, -20

L32:			# 32: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L33:			# 33: end_block, main, _, _
