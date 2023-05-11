#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

getThousand:			# 1: begin_block, getThousand, _, _
	sw ra, -0(sp)

L2:			# 2: ret, 1000, _, _
	li t1, 1000
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L3:			# 3: end_block, getThousand, _, _
	lw ra, (sp)
	jr ra

main_negativeVars:			# 4: begin_block, main_negativeVars, _, _
	sw ra, -0(sp)

L5:			# 5: :=, 5, _, x
	li t0, 5
	sw t0, -16(sp)

L6:			# 6: *, -1, x, &1
	li t1, -1
	lw t2, -16(sp)
	mul t1, t1, t2
	sw t1, -20(sp)

L7:			# 7: :=, &1, _, neg_x
	lw t0, -20(sp)
	sw t0, -12(sp)

L8:			# 8: <, neg_x, 0, 10
	lw t1, -12(sp)
	li t2, 0
	blt t1, t2, L10

L9:			# 9: jump, _, _, 15
	j L15

L10:			# 10: *, -1, x, &2
	li t1, -1
	lw t2, -16(sp)
	mul t1, t1, t2
	sw t1, -24(sp)

L11:			# 11: ==, neg_x, &2, 13
	lw t1, -12(sp)
	lw t2, -24(sp)
	beq t1, t2, L13

L12:			# 12: jump, _, _, 15
	j L15

L13:			# 13: out, _, _, 1
	li a0, 1
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L14:			# 14: jump, _, _, 16
	j L16

L15:			# 15: out, _, _, 0
	li a0, 0
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L16:			# 16: par, &3, ret, _
	addi fp, sp, 12
	addi t0, sp, -28
	sw t0, -8(fp)

L17:			# 17: call, getThousand, _, _
	sw sp, -4(fp)
	addi sp, sp, 12
	jal getThousand
	addi sp, sp, -12

L18:			# 18: *, -1, &3, &4
	li t1, -1
	lw t2, -28(sp)
	mul t1, t1, t2
	sw t1, -32(sp)

L19:			# 19: >, 0, &4, 21
	li t1, 0
	lw t2, -32(sp)
	bgt t1, t2, L21

L20:			# 20: jump, _, _, 23
	j L23

L21:			# 21: out, _, _, 1
	li a0, 1
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L22:			# 22: jump, _, _, 24
	j L24

L23:			# 23: out, _, _, 0
	li a0, 0
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L24:			# 24: par, &5, ret, _
	addi fp, sp, 12
	addi t0, sp, -36
	sw t0, -8(fp)

L25:			# 25: call, getThousand, _, _
	sw sp, -4(fp)
	addi sp, sp, 12
	jal getThousand
	addi sp, sp, -12

L26:			# 26: *, -1, &5, &6
	li t1, -1
	lw t2, -36(sp)
	mul t1, t1, t2
	sw t1, -40(sp)

L27:			# 27: +, &6, 1000, &7
	lw t1, -40(sp)
	li t2, 1000
	add t1, t1, t2
	sw t1, -44(sp)

L28:			# 28: ==, 0, &7, 30
	li t1, 0
	lw t2, -44(sp)
	beq t1, t2, L30

L29:			# 29: jump, _, _, 32
	j L32

L30:			# 30: out, _, _, 1
	li a0, 1
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L31:			# 31: jump, _, _, 33
	j L33

L32:			# 32: out, _, _, 0
	li a0, 0
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L33:			# 33: end_block, main_negativeVars, _, _
	lw ra, (sp)
	jr ra

main:			# 34: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L35:			# 35: call, main_negativeVars, _, _
	addi fp, sp, 48
	sw sp, -4(fp)
	addi sp, sp, 48
	jal main_negativeVars
	addi sp, sp, -48

L36:			# 36: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L37:			# 37: end_block, main, _, _
