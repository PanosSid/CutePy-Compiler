#SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

.data

str_nl: .asciz "\n"

.text

L0:
	j main

goToLower:			# 1: begin_block, goToLower, _, _
	sw ra, -0(sp)

L2:			# 2: >, x, lower_limit, 4
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	lw t0, -4(sp)
	addi t0, t0, -24
	lw t2, (t0)
	bgt t1, t2, L4

L3:			# 3: jump, _, _, 7
	j L7

L4:			# 4: -, x, 1, &1
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	li t2, 1
	sub t1, t1, t2
	sw t1, -12(sp)

L5:			# 5: :=, &1, _, x
	lw t0, -12(sp)
	mv t5, t0
	lw t0, -4(sp)
	addi t0, t0, -12
	sw t5, (t0)

L6:			# 6: jump, _, _, 2
	j L2

L7:			# 7: ret, x, _, _
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L8:			# 8: end_block, goToLower, _, _
	lw ra, (sp)
	jr ra

goToUpper:			# 9: begin_block, goToUpper, _, _
	sw ra, -0(sp)

L10:			# 10: <, x, upper_limit, 12
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	lw t0, -4(sp)
	addi t0, t0, -20
	lw t2, (t0)
	blt t1, t2, L12

L11:			# 11: jump, _, _, 15
	j L15

L12:			# 12: +, x, 1, &2
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	li t2, 1
	add t1, t1, t2
	sw t1, -12(sp)

L13:			# 13: :=, &2, _, x
	lw t0, -12(sp)
	mv t5, t0
	lw t0, -4(sp)
	addi t0, t0, -12
	sw t5, (t0)

L14:			# 14: jump, _, _, 10
	j L10

L15:			# 15: ret, x, _, _
	lw t0, -4(sp)
	addi t0, t0, -12
	lw t1, (t0)
	lw t0, -8(sp)
	sw t1, (t0)
	lw ra, (sp)
	jr ra

L16:			# 16: end_block, goToUpper, _, _
	lw ra, (sp)
	jr ra

main_gotolimits:			# 17: begin_block, main_gotolimits, _, _
	sw ra, -0(sp)

L18:			# 18: in, lower_limit, _, _
	li a7, 5
	ecall
	sw a0, -24(sp)

L19:			# 19: in, x, _, _
	li a7, 5
	ecall
	sw a0, -12(sp)

L20:			# 20: in, upper_limit, _, _
	li a7, 5
	ecall
	sw a0, -20(sp)

L21:			# 21: >=, x, 0, 23
	lw t1, -12(sp)
	li t2, 0
	bge t1, t2, L23

L22:			# 22: jump, _, _, 25
	j L25

L23:			# 23: <=, x, 0, 35
	lw t1, -12(sp)
	li t2, 0
	ble t1, t2, L35

L24:			# 24: jump, _, _, 25
	j L25

L25:			# 25: <, lower_limit, 0, 35
	lw t1, -24(sp)
	li t2, 0
	blt t1, t2, L35

L26:			# 26: jump, _, _, 27
	j L27

L27:			# 27: <, upper_limit, 0, 35
	lw t1, -20(sp)
	li t2, 0
	blt t1, t2, L35

L28:			# 28: jump, _, _, 29
	j L29

L29:			# 29: >=, lower_limit, upper_limit, 35
	lw t1, -24(sp)
	lw t2, -20(sp)
	bge t1, t2, L35

L30:			# 30: jump, _, _, 31
	j L31

L31:			# 31: <, x, lower_limit, 35
	lw t1, -12(sp)
	lw t2, -24(sp)
	blt t1, t2, L35

L32:			# 32: jump, _, _, 33
	j L33

L33:			# 33: >, x, upper_limit, 35
	lw t1, -12(sp)
	lw t2, -20(sp)
	bgt t1, t2, L35

L34:			# 34: jump, _, _, 37
	j L37

L35:			# 35: out, _, _, -99999
	li a0, -99999
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L36:			# 36: jump, _, _, 57
	j L57

L37:			# 37: -, x, lower_limit, &3
	lw t1, -12(sp)
	lw t2, -24(sp)
	sub t1, t1, t2
	sw t1, -28(sp)

L38:			# 38: -, upper_limit, x, &4
	lw t1, -20(sp)
	lw t2, -12(sp)
	sub t1, t1, t2
	sw t1, -32(sp)

L39:			# 39: !=, &3, &4, 43
	lw t1, -28(sp)
	lw t2, -32(sp)
	bne t1, t2, L43

L40:			# 40: jump, _, _, 41
	j L41

L41:			# 41: out, _, _, 0
	li a0, 0
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L42:			# 42: jump, _, _, 57
	j L57

L43:			# 43: -, x, lower_limit, &5
	lw t1, -12(sp)
	lw t2, -24(sp)
	sub t1, t1, t2
	sw t1, -36(sp)

L44:			# 44: -, upper_limit, x, &6
	lw t1, -20(sp)
	lw t2, -12(sp)
	sub t1, t1, t2
	sw t1, -40(sp)

L45:			# 45: <, &5, &6, 47
	lw t1, -36(sp)
	lw t2, -40(sp)
	blt t1, t2, L47

L46:			# 46: jump, _, _, 51
	j L51

L47:			# 47: par, &7, ret, _
	addi fp, sp, 16
	addi t0, sp, -44
	sw t0, -8(fp)

L48:			# 48: call, goToLower, _, _
	sw sp, -4(fp)
	addi sp, sp, 16
	jal goToLower
	addi sp, sp, -16

L49:			# 49: :=, &7, _, r
	lw t0, -44(sp)
	sw t0, -16(sp)

L50:			# 50: jump, _, _, 56
	j L56

L51:			# 51: out, _, _, x
	lw a0, -12(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L52:			# 52: out, _, _, upper_limit
	lw a0, -20(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L53:			# 53: par, &8, ret, _
	addi fp, sp, 16
	addi t0, sp, -48
	sw t0, -8(fp)

L54:			# 54: call, goToUpper, _, _
	sw sp, -4(fp)
	addi sp, sp, 16
	jal goToUpper
	addi sp, sp, -16

L55:			# 55: :=, &8, _, r
	lw t0, -48(sp)
	sw t0, -16(sp)

L56:			# 56: out, _, _, r
	lw a0, -16(sp)
	li a7, 1
	ecall
	la a0, str_nl
	li a7, 4
	ecall

L57:			# 57: end_block, main_gotolimits, _, _
	lw ra, (sp)
	jr ra

main:			# 58: begin_block, main, _, _
	addi sp, sp, 12
	mv gp, sp

L59:			# 59: call, main_gotolimits, _, _
	addi fp, sp, 52
	sw sp, -4(fp)
	addi sp, sp, 52
	jal main_gotolimits
	addi sp, sp, -52

L60:			# 60: halt, _, _, _
	li a0, 0
	li a7, 93
	ecall

L61:			# 61: end_block, main, _, _
