//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

#include <stdio.h>

int main(){
	int a, b, i_a, i_b, T_1, T_2, T_3, T_4;
	L_1: 	// begin_block, main_firstToTen, _, _
	L_2: scanf("%d", &a);	// in, a, _, _
	L_3: scanf("%d", &b);	// in, b, _, _
	L_4: if (a < 10) goto L_6;	// <, a, 10, 6
	L_5: goto L_25;	// jump, _, _, 25
	L_6: if (b < 10) goto L_8;	// <, b, 10, 8
	L_7: goto L_25;	// jump, _, _, 25
	L_8: i_a = 0;	// :=, 0, _, i_a
	L_9: i_b = 1;	// :=, 1, _, i_b
	L_10: if (i_a < 5) goto L_12;	// <, i_a, 5, 12
	L_11: goto L_17;	// jump, _, _, 17
	L_12: T_1 = a + 1;	// +, a, 1, &1
	L_13: a = T_1;	// :=, &1, _, a
	L_14: T_2 = i_a + 1;	// +, i_a, 1, &2
	L_15: i_a = T_2;	// :=, &2, _, i_a
	L_16: goto L_10;	// jump, _, _, 10
	L_17: if (i_b > 0) goto L_19;	// >, i_b, 0, 19
	L_18: goto L_24;	// jump, _, _, 24
	L_19: T_3 = b * 2;	// *, b, 2, &3
	L_20: b = T_3;	// :=, &3, _, b
	L_21: T_4 = i_b - 1;	// -, i_b, 1, &4
	L_22: i_b = T_4;	// :=, &4, _, i_b
	L_23: goto L_17;	// jump, _, _, 17
	L_24: goto L_4;	// jump, _, _, 4
	L_25: if (a >= 10) goto L_27;	// >=, a, 10, 27
	L_26: goto L_31;	// jump, _, _, 31
	L_27: if (b >= 10) goto L_29;	// >=, b, 10, 29
	L_28: goto L_31;	// jump, _, _, 31
	L_29: printf("%d", 22222);	// out, _, _, 22222
	L_30: goto L_38;	// jump, _, _, 38
	L_31: if (a >= 10) goto L_33;	// >=, a, 10, 33
	L_32: goto L_36;	// jump, _, _, 36
	L_33: printf("%d", 0);	// out, _, _, 0
	L_34: printf("%d", a);	// out, _, _, a
	L_35: goto L_38;	// jump, _, _, 38
	L_36: printf("%d", 1);	// out, _, _, 1
	L_37: printf("%d", b);	// out, _, _, b
	L_38: 	// end_block, main_firstToTen, _, _
	L_39: 	// begin_block, main, _, _
	L_40: 	// call, main_firstToTen, _, _
	L_41: return 0;	// halt, _, _, _
	L_42: 	// end_block, main, _, _
}
