#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

main_firstToTen:			# 1: begin_block, main_firstToTen, _, _
	sw ra, -0(sp)

L2:			# 2: in, a, _, _
	li a7, 5
	ecall
	sw a0, -12(sp)

L3:			# 3: in, b, _, _
	li a7, 5
	ecall
	sw a0, -16(sp)

L4:			# 4: <, a, 10, 6
	lw t1, -12(sp)
	li t2, 10
	blt t1, t2, L6

L5:			# 5: jump, _, _, 25
	j L25

L6:			# 6: <, b, 10, 8
	lw t1, -16(sp)
	li t2, 10
	blt t1, t2, L8

L7:			# 7: jump, _, _, 25
	j L25

L8:			# 8: :=, 0, _, i_a
	li t0, 0
	sw t0, -20(sp)

L9:			# 9: :=, 1, _, i_b
	li t0, 1
	sw t0, -24(sp)

L10:			# 10: <, i_a, 5, 12
	lw t1, -20(sp)
	li t2, 5
	blt t1, t2, L12

L11:			# 11: jump, _, _, 17
	j L17

L12:			# 12: +, a, 1, &1
	lw t1, -12(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -28(sp)

L13:			# 13: :=, &1, _, a
	lw t0, -28(sp)
	sw t0, -12(sp)

L14:			# 14: +, i_a, 1, &2
	lw t1, -20(sp)
	li t2, 1
	add t1, t1, t2
	sw t1, -32(sp)

L15:			# 15: :=, &2, _, i_a
	lw t0, -32(sp)
	sw t0, -20(sp)

L16:			# 16: jump, _, _, 10
	j L10

L17:			# 17: >, i_b, 0, 19
	lw t1, -24(sp)
	li t2, 0
	bgt t1, t2, L19

L18:			# 18: jump, _, _, 24
	j L24

L19:			# 19: *, b, 2, &3
	lw t1, -16(sp)
	li t2, 2
	mul t1, t1, t2
	sw t1, -36(sp)

L20:			# 20: :=, &3, _, b
	lw t0, -36(sp)
	sw t0, -16(sp)

L21:			# 21: -, i_b, 1, &4
	lw t1, -24(sp)
	li t2, 1
	sub t1, t1, t2
	sw t1, -40(sp)

L22:			# 22: :=, &4, _, i_b
	lw t0, -40(sp)
	sw t0, -24(sp)

L23:			# 23: jump, _, _, 17
	j L17

L24:			# 24: jump, _, _, 4
	j L4

L25:			# 25: >=, a, 10, 27
	lw t1, -12(sp)
	li t2, 10
	bge t1, t2, L27

L26:			# 26: jump, _, _, 31
	j L31

L27:			# 27: >=, b, 10, 29
	lw t1, -16(sp)
	li t2, 10
	bge t1, t2, L29

L28:			# 28: jump, _, _, 31
	j L31

L29:			# 29: out, _, _, 22222
	li a0, 22222
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L30:			# 30: jump, _, _, 38
	j L38

L31:			# 31: >=, a, 10, 33
	lw t1, -12(sp)
	li t2, 10
	bge t1, t2, L33

L32:			# 32: jump, _, _, 36
	j L36

L33:			# 33: out, _, _, 0
	li a0, 0
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L34:			# 34: out, _, _, a
	lw a0, -12(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L35:			# 35: jump, _, _, 38
	j L38

L36:			# 36: out, _, _, 1
	li a0, 1
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L37:			# 37: out, _, _, b
	lw a0, -16(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L38:			# 38: end_block, main_firstToTen, _, _
	lw ra, (sp)
	jr ra

main:			# 39: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L40:			# 40: call, main_firstToTen, _, _
	addi fp, sp, 44
	sw sp, -4(fp)
	addi sp, sp, 44
	jal main_firstToTen
	addi sp, sp, -44

L41:			# 41: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L42:			# 42: end_block, main, _, _
