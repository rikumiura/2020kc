package kc;

import java.util.ArrayList;

/**
 * 18-0116 三浦 陸
 * 書かれたコードがマクロ構文に従っているか確認するプログラム
 * @author rikumiura
 *
 */
public class Kc {
	private LexicalAnalyzer lexer;
    private Token token;
    private VarTable variableTable;
    private PseudoIseg iseg;
    private ArrayList<Integer> breakAddressList;
//    private ArrayList<Integer> continueAddressList;
    private boolean isInLoop;
    /**
     * コンストラクタ
     * lexerの初期化、tokenに最初のトークンを持たせる
     */
    public Kc(String sourceFileName) {
    	this.lexer = new LexicalAnalyzer(sourceFileName);
    	this.token = this.lexer.nextToken();
    	this.variableTable = new VarTable();
    	this.iseg = new PseudoIseg();
    	this.breakAddressList = new ArrayList<Integer>();
    	this.isInLoop = false;
    }

    /**
     *	プログラムのスタート時点
     *  全ての始まり
     */
     private void paeseProgram() {
    	 if(this.token.checkSymbol(Symbol.MAIN)) {
    		 this.paeseMainFunction();
    	 }else {
    		 this.syntaxError("29: main");
    	 }
    	 if(this.token.checkSymbol(Symbol.EOF)) {
    		 this.lexer.closeFile();
    		 this.iseg.appendCode(Operator.HALT);
    	 }else {
    		 this.syntaxError("34: EOF");
    	 }
    }
     /**
      * mainがあるか判断
      */
     private void paeseMainFunction() {
    	 if(this.token.checkSymbol(Symbol.MAIN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("44: Main");
    	 }
    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("49: （");
    	 }
    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("54: )");
    	 }

    	 if(this.token.checkSymbol(Symbol.LBRACE)) {
    		 this.paeseBlock();
    	 }else {
    		 this.syntaxError("60: {");
    	 }
     }

     /**
      * ブロックとして成り立っているか判断
      */
     private void paeseBlock() {
    	 if(this.token.checkSymbol(Symbol.LBRACE)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("71: {");
    	 }

    	 while(this.token.checkSymbol(Symbol.INT)) {
    		 this.paeseVarDecl();
    	 }

    	 while(checkStatement(this.token)) {
    		 this.paeseStatement();
    	 }

    	 if(this.token.checkSymbol(Symbol.RBRACE)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("85: }");
    	 }
     }

     /**
      * int型の整数として成り立っているか判断
      */
     private void paeseVarDecl() {
    	 if(this.token.checkSymbol(Symbol.INT)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("90: int");
    	 }
    	 if(this.token.checkSymbol(Symbol.NAME)) {
    		 this.paeseName_list();
    	 }else {
    		 this.syntaxError("95: name");
    	 }
    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("100: ;が期待されます");
    	 }
     }

     /**
      * 一個以上の変数宣言ができているか判断
      */
     private void paeseName_list() {
    	 if(this.token.checkSymbol(Symbol.NAME)) {
    		 this.paeseName();
    	 }else {
    		 this.syntaxError("111: name");
    	 }
    	 while(this.token.checkSymbol(Symbol.COMMA)) {
    		 this.token = this.lexer.nextToken();
    		 if(this.token.checkSymbol(Symbol.NAME)) {
    			 this.paeseName();
    		 }else {
    			 this.syntaxError("118: name");
    		 }
    	 }
     }

     /**
      * 変数名が正しく宣言されているか判断
      * 初期値、配列など
      */
     private void paeseName() {
    	 String name = "";
    	 int value = 0;
    	 if(this.token.checkSymbol(Symbol.NAME)) {
    		 name = this.token.getStrValue();
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("131: name");
    	 }
    	 if(this.variableTable.exist(name)) {
    		 this.syntaxError("148: 二重登録");
    	 }
    	 Symbol nowToken = this.token.getSymbol();
    	 switch(nowToken) {
    	 	case ASSIGN:
    	 		this.token = this.lexer.nextToken();
    	 		if(this.token.checkSymbol(Symbol.SUB)
	 				||this.token.checkSymbol(Symbol.INTEGER)
	 				||this.token.checkSymbol(Symbol.CHARACTER)) {
    	 			value = this.paeseConstant();
    	 			this.variableTable.registerNewVariable(Type.INT, name, 1);//変数の登録
    	 			int address = this.variableTable.getAddress(name);
    	 			this.iseg.appendCode(Operator.PUSHI, value);
    	 			this.iseg.appendCode(Operator.POP, address);
    	 		}else {
    	 			this.syntaxError("142: -または整数または文字");
    	 		}
    	 		break;

    	 	case LBRACKET:
//    	 		int size = 0;
    	 		this.token = this.lexer.nextToken();
    	 		if(this.token.checkSymbol(Symbol.INTEGER)) {
    	 			 value = this.token.getIntValue();
    	    		 this.token = this.lexer.nextToken();
//    	    		 this.iseg.appendCode(Operator.PUSHI, value);
    	 			if(this.token.checkSymbol(Symbol.RBRACKET)) {
    	 				this.token = this.lexer.nextToken();
    	 			}else {
    	 				this.syntaxError("153: ]");
    	 			}

    	 			this.variableTable.registerNewVariable(Type.ARRAYOFINT, name, value);


    	 		}else if(this.token.checkSymbol(Symbol.RBRACKET)) {
    	 			this.token = this.lexer.nextToken();
    	 			ArrayList<Integer> valueList = new ArrayList<Integer>();
    	 			if(this.token.checkSymbol(Symbol.ASSIGN)) {
    	 				this.token = this.lexer.nextToken();
    	 			}else {
    	 				this.syntaxError("160: =");
    	 			}
    	 			if(this.token.checkSymbol(Symbol.LBRACE)) {
    	 				this.token = this.lexer.nextToken();
    	 			}else {
    	 				this.syntaxError("165: {");
    	 			}
    	 			if(this.token.checkSymbol(Symbol.SUB)
    		 				||this.token.checkSymbol(Symbol.INTEGER)
    		 				||this.token.checkSymbol(Symbol.CHARACTER)) {
    	    	 		valueList = this.paeseConstant_list();
	    	 		}else {
    	    	 			this.syntaxError("172: -または整数、文字");
	    	 		}



    	 			if(this.token.checkSymbol(Symbol.RBRACE)) {
    	 				this.token = this.lexer.nextToken();
    	 				int size = valueList.size();
    	 				this.variableTable.registerNewVariable(Type.ARRAYOFINT, name, size);
    	 				int address = this.variableTable.getAddress(name);
    	 				for(int i = 0; i < size; i++) {
    	 					this.iseg.appendCode(Operator.PUSHI,valueList.get(i));
    	 					this.iseg.appendCode(Operator.POP, address+i);
    	 				}
    	 				if(this.token.checkSymbol(Symbol.ASSIGN)) {
    	 					this.iseg.appendCode(Operator.ADD);
    	 				}
    	 			}else {
    	 				this.syntaxError("177: }");
    	 			}
    	 		}
    	 		break;

    	 	default:
    	 		this.variableTable.registerNewVariable(Type.INT, name, 1);//変数の登録
    	 		break;
    	 }
     }

     /**
      * コンマでつながっている間コンスタントに振ってるだけ
      * コンスタントリスト
      */
     private ArrayList<Integer> paeseConstant_list() {
    	 int value = 0;
    	 if(this.token.checkSymbol(Symbol.SUB)
	 				||this.token.checkSymbol(Symbol.INTEGER)
	 				||this.token.checkSymbol(Symbol.CHARACTER)) {
 	 			value = this.paeseConstant();
    	 }else {
    		 this.syntaxError("197: SUB,INTEGER,CHARACTER");
    	 }

    	 ArrayList<Integer> valuelist = new ArrayList<Integer>();
    	 valuelist.add(value);//LBRACKET入ってすぐ取ってる


    	 while(this.token.checkSymbol(Symbol.COMMA)) {
    		 this.token = this.lexer.nextToken();
    		 if(this.token.checkSymbol(Symbol.SUB)
 	 				||this.token.checkSymbol(Symbol.INTEGER)
 	 				||this.token.checkSymbol(Symbol.CHARACTER)) {
  	 			value = this.paeseConstant();
  	 			valuelist.add(value);
	     	 }else {
	     		 this.syntaxError("207: SUB,INTEGER,CHARACTER");
	     	 }
    	 }
    	 return valuelist;
     }

     /**
      * ー整数か文字で受付
      */
     private int paeseConstant() {
    	 int ret = 0;
    	 if(this.token.checkSymbol(Symbol.CHARACTER)) {
    		 ret = this.token.getIntValue();
    		 this.token = this.lexer.nextToken();
//    		 this.iseg.appendCode(Operator.PUSHI, ret);
    	 }else if(this.token.checkSymbol(Symbol.SUB)) {
    		 this.token = this.lexer.nextToken();
    		 if(this.token.checkSymbol(Symbol.INTEGER)) {
    			 ret = this.token.getIntValue() * -1;
        		 this.token = this.lexer.nextToken();
//        		 this.iseg.appendCode(Operator.PUSHI, ret);
    		 }else {
    			 this.syntaxError("223: integer");
    		 }
    	 }else if(this.token.checkSymbol(Symbol.INTEGER)) {
    		 ret = this.token.getIntValue();
    		 this.token = this.lexer.nextToken();
//    		 this.iseg.appendCode(Operator.PUSHI, ret);
 		 }else {
    		 this.syntaxError("226: character,sub");
    	 }
    	 return ret;
     }

     /**
      * if,for,whileなど各種の状態に遷移する．
      */
     private void paeseStatement() {
    	 if(checkStatement(this.token)) {
//    	/*	 this.token = this.lexer.nextToken();*/
    	 }else {
    		 this.syntaxError("237: StatementのFirst集合");
    	 }
    	 Symbol nowToken = this.token.getSymbol();
    	 switch(nowToken) {
    	 	case IF:
    	 		this.paeseIf_statement();
    	 		break;
    	 	case WHILE:
    	 		this.paeseWhile_statement();
    	 		break;
    	 	case FOR:
    	 		this.paeseFor_statement();
    	 		break;
	 		//{NAME,"++","--",
//    	 	INT,CHAR,"(","inputchar",
//    	 		"inputint","+","*","-","!"}
    	 	case NAME:
    	 	case INC:
    	 	case DEC:
    	 	case INTEGER:
    	 	case CHARACTER:
    	 	case LPAREN:
    	 	case INPUTCHAR:
    	 	case INPUTINT:
    	 	case ADD:
    	 	case SUB:
    	 	case MUL:
    	 	case NOT:
    	 		this.paeseExp_statement();
    	 		break;
    	 	case OUTPUTCHAR:
    	 		this.paeseOutputchar_statement();
    	 		break;
    	 	case OUTPUTINT:
    	 		this.paeseOutputint_statement();
    	 		break;
    	 	case BREAK:
    	 		this.paeseBreak_statement();
    	 		break;
    	 	case LBRACE:
    	 		this.token = this.lexer.nextToken();
    	 		while(checkStatement(this.token)) {
    	 			this.paeseStatement();
    	 		}
    	 		if(this.token.checkSymbol(Symbol.RBRACE)) {
    	 			this.token = this.lexer.nextToken();
    	 		}else {
    	 			this.syntaxError("292: }");
    	 		}
    	 		break;
    	 	case SEMICOLON:
    	 		this.token = this.lexer.nextToken();
       	 		break;
	 		default:
	 			this.syntaxError("329: なんらかのエラー");
	 			break;
    	 }
     }

     /**
      * Statementのファースト集合
      * @param tk
      * @return ファースト集合として正しいか
      */
     private boolean checkStatement(Token tk) {
//    	 NAME,"++","--",INT,CHAR,
//    	 "(","inputchar", "inputint","+","*","-
//    	 ","!","outputchar","outputint","break",
//    	 "if","for","while","{",";"
    	 if(tk.checkSymbol(Symbol.NAME) || tk.checkSymbol(Symbol.INC)||
			 tk.checkSymbol(Symbol.DEC) || tk.checkSymbol(Symbol.INTEGER)||
			 tk.checkSymbol(Symbol.CHARACTER)|| tk.checkSymbol(Symbol.LPAREN)||
			 tk.checkSymbol(Symbol.INPUTCHAR)||tk.checkSymbol(Symbol.INPUTINT)||
			 tk.checkSymbol(Symbol.ADD)||tk.checkSymbol(Symbol.MUL)||
			 tk.checkSymbol(Symbol.SUB)||tk.checkSymbol(Symbol.COMMA)||
			 tk.checkSymbol(Symbol.NOT)||tk.checkSymbol(Symbol.OUTPUTCHAR)||
			 tk.checkSymbol(Symbol.OUTPUTINT)||tk.checkSymbol(Symbol.BREAK)||
			 tk.checkSymbol(Symbol.IF)||tk.checkSymbol(Symbol.FOR)||
			 tk.checkSymbol(Symbol.WHILE)||tk.checkSymbol(Symbol.LBRACE)||
			 tk.checkSymbol(Symbol.SEMICOLON)) {
    		 return true;
    	 }else {
    		 return false;
    	 }
     }

     /**
      * ifの時
      */
     private void paeseIf_statement() {
    	 if(this.token.checkSymbol(Symbol.IF)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("331: if");
    	 }
    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("336: (");
    	 }
    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("341: ExpressionのFirst集合");
    	 }
    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("346: )");
    	 }
    	 int deqAddr = this.iseg.appendCode(Operator.BEQ, -1);
    	 if(checkStatement(this.token)) {
    		 this.paeseStatement();
    	 }else {
    		 this.syntaxError("351: StatementのFirst集合");
    	 }
    	 int nextAddr =this.iseg.getLastCodeAddress() + 1;
    	 this.iseg.replaceCode(deqAddr, nextAddr);
     }

     /**
      * whileの時
      */
     private void paeseWhile_statement() {
    	 if(this.token.checkSymbol(Symbol.WHILE)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("362: WHILE");
    	 }

    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("336: (");
    	 }
    	 int lastAddr = this.iseg.getLastCodeAddress();
    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("341: ExpressionのFirst集合");
    	 }
    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("346: )");
    	 }

    	 boolean outLoop = this.isInLoop;
    	 ArrayList <Integer> outerList = this.breakAddressList;
    	 this.isInLoop = true;
    	 this.breakAddressList = new ArrayList<Integer>();

    	 int beqAddr = this.iseg.appendCode(Operator.BEQ, -1);
    	 if(checkStatement(this.token)) {
    		 this.paeseStatement();
    	 }else {
    		 this.syntaxError("351: StatementのFirst集合");
    	 }


    	 int jumpAddr = this.iseg.appendCode(Operator.JUMP, lastAddr+1);

    	 for(int i = 0; i <  this.breakAddressList.size(); i++) {
    		 int breakAddr = this.breakAddressList.get(i);
    		 this.iseg.replaceCode(breakAddr, jumpAddr+1);
    	 }
    	 this.isInLoop = outLoop;
    	 this.breakAddressList = outerList;
    	 this.iseg.replaceCode(beqAddr, jumpAddr+1);
     }

     /**
      * 比較の時
      */
     private void paeseExp_statement() {
    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("395: ExpressionのFirst集合");
    	 }

    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    		 this.iseg.appendCode(Operator.REMOVE);
    	 }else {
    		 this.syntaxError("401: ；");
    	 }
     }

     /**
      * forの時
      */
     private void paeseFor_statement() {
    	 if(this.token.checkSymbol(Symbol.FOR)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("412: for");
    	 }
    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("417: (");
    	 }
    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("422: ExpressionのFirst集合");
    	 }

    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("428: ;");
    	 }
    	 int removeAddr = this.iseg.appendCode(Operator.REMOVE);

    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("422: ExpressionのFirst集合");
    	 }

    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("428: ;");
    	 }
    	 int beqAddr = this.iseg.appendCode(Operator.BEQ, -1);
    	 int jumpAddr = this.iseg.appendCode(Operator.JUMP, -1);

    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("422: ExpressionのFirst集合");
    	 }

    	 this.iseg.appendCode(Operator.REMOVE);
    	 int jumpAddrL2 = this.iseg.appendCode(Operator.JUMP, -1);

    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("452: )");
    	 }

    	 if(checkStatement(this.token)) {
    		 this.paeseStatement();
    	 }else {
    		 this.syntaxError("458: StatementのFirst集合");
    	 }
    	 int jumpAddrL3 = this.iseg.appendCode(Operator.JUMP, -1);
    	 this.iseg.replaceCode(jumpAddrL2, removeAddr+1);
    	 this.iseg.replaceCode(jumpAddrL3, jumpAddr+1);
    	 this.iseg.replaceCode(jumpAddr, jumpAddrL2+1);
    	 this.iseg.replaceCode(beqAddr, jumpAddrL3+1);
     }

     /**
      * outputcharの時
      */
     private void paeseOutputchar_statement() {
    	 if(this.token.checkSymbol(Symbol.OUTPUTCHAR)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("469: outputchar");
    	 }
    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("474: (");
    	 }

    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("480: ExpressionのFirst集合");
    	 }

    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("486: )");
    	 }

    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    		 this.iseg.appendCode(Operator.OUTPUTC);
    		 this.iseg.appendCode(Operator.OUTPUTLN);
    	 }else {
    		 this.syntaxError("492: ;");
    	 }
     }

     /**
      * outputintの時
      */
     private void paeseOutputint_statement() {
    	 if(this.token.checkSymbol(Symbol.OUTPUTINT)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("503: outputint");
    	 }

    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("509: (");
    	 }

    	 if(checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("515: Expression");
    	 }

    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    		 this.iseg.appendCode(Operator.OUTPUT);
    		 this.iseg.appendCode(Operator.OUTPUTLN);
    	 }else {
    		 this.syntaxError("521: )");
    	 }
    	 if (this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("568: ;");
    	 }
     }

     /**
      * breakの時
      */
     private void paeseBreak_statement() {
    	 if(this.token.checkSymbol(Symbol.BREAK)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("532: break");
    	 }

    	 int address = this.iseg.appendCode(Operator.JUMP,-1);
    	 this.breakAddressList.add(address);

    	 if(this.token.checkSymbol(Symbol.SEMICOLON)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("538: ;");
    	 }
     }

     /**
      * 計算式の時
      */
     private void paeseExpression() {
//    	 int num = 0;
    	 if(checkExpression(this.token)) {
    		 this.paeseExp();
    	 }else {
    		 this.syntaxError("549: exp");
    	 }

    	 Symbol nowToken = this.token.getSymbol();
    	 switch(nowToken) {
    	 	case ASSIGN:
    	 	case ASSIGNADD:
    	 	case ASSIGNSUB:
    	 	case ASSIGNMUL:
    	 	case ASSIGNDIV:
//    	 		int keep;
    	 		Symbol sm = this.token.getSymbol();
    	 		this.token = this.lexer.nextToken();
    	 		if(sm.equals(Symbol.ASSIGNADD)||sm.equals(Symbol.ASSIGNSUB)
    	 				||sm.equals(Symbol.ASSIGNMUL)||sm.equals(Symbol.ASSIGNDIV)) {
    	 			this.iseg.appendCode(Operator.COPY);
    	 			this.iseg.appendCode(Operator.LOAD);
    	 		}
    	 		if(checkExpression(this.token)) {
    	 			this.paeseExpression();
    	 		}else {
    	 			this.syntaxError("563: expression");
    	 		}
    	 		if(sm.equals(Symbol.ASSIGNADD)) {
    	 			this.iseg.appendCode(Operator.ADD);
    	 		}else if(sm.equals(Symbol.ASSIGNSUB)) {
    	 			this.iseg.appendCode(Operator.SUB);
    	 		}else if(sm.equals(Symbol.ASSIGNMUL)) {
    	 			this.iseg.appendCode(Operator.MUL);
    	 		}else if(sm.equals(Symbol.ASSIGNDIV)) {
    	 			this.iseg.appendCode(Operator.DIV);
    	 		}
    	 		this.iseg.appendCode(Operator.ASSGN);
    	 		break;
	 		default:
	 			break;
    	 }
//    	 return num;
     }

     private boolean checkExpression(Token tk) {
    	 /**
    	  * {NAME,"++","--",INT,CHAR,
    	  * "(","inputchar", "inputint",
    	  * "+","*","-","!"}
    	  */
    	 if(tk.checkSymbol(Symbol.NAME) || tk.checkSymbol(Symbol.INC)||
    			 tk.checkSymbol(Symbol.DEC) || tk.checkSymbol(Symbol.INTEGER)||
    			 tk.checkSymbol(Symbol.CHARACTER)|| tk.checkSymbol(Symbol.LPAREN)||
    			 tk.checkSymbol(Symbol.INPUTCHAR)||tk.checkSymbol(Symbol.INPUTINT)||
    			 tk.checkSymbol(Symbol.ADD)||tk.checkSymbol(Symbol.MUL)||
    			 tk.checkSymbol(Symbol.SUB)||
    			 tk.checkSymbol(Symbol.NOT)) {
        		 return true;
        	 }else {
        		 return false;
        	 }
     }

     /**
      * logicaltermに移動してからもう一度実行するか判断
      */
     private void paeseExp() {
    	 if(checkExpression(this.token)) {
    		 this.paeseLogical_term();
    	 }else {
    		 this.syntaxError("597: Logical_term");
    	 }

    	 if(this.token.checkSymbol(Symbol.OR)) {
    		 this.token = this.lexer.nextToken();
    		 if(checkExpression(this.token)) {
    			 this.paeseExp();
    		 }else {
    			 this.syntaxError("605: exp");
    		 }
    		 this.iseg.appendCode(Operator.OR);
    	 }
     }

     /**
      * paeseLogical_factorに移動
      */
     private void paeseLogical_term() {
    	 if(checkExpression(this.token)) {
    		 this.paeseLogical_factor();
    	 }else {
    		 this.syntaxError("617: logical factor");
    	 }

    	 if(this.token.checkSymbol(Symbol.AND)) {
    		 this.token = this.lexer.nextToken();
    		 if(checkExpression(this.token)) {
    			 this.paeseLogical_term();
    		 }else {
    			 this.syntaxError("625: logical term");
    		 }
    		 this.iseg.appendCode(Operator.AND);
    	 }
     }

     /**
      * Arithmetic_expressionに移動
      * そのあと比較
      */
     private void paeseLogical_factor() {
    	 if(checkExpression(this.token)) {
    		 this.paeseArithmetic_expression();
    	 }else {
    		 this.syntaxError("638: Logical_factor");
    	 }

    	 Symbol nowToken = this.token.getSymbol();
    	 switch(nowToken) {
    	 	case EQUAL:
    	 	case NOTEQ:
    	 	case LESS:
    	 	case GREAT:
    	 		this.token = this.lexer.nextToken();
    	 		if(checkExpression(this.token)) {
    	 			this.paeseArithmetic_expression();
    	 		}else {
    	 			this.syntaxError("650: Logical_factor");
    	 		}
    	 		int compAddr = this.iseg.appendCode(Operator.COMP);
    	 		if(nowToken.equals(Symbol.EQUAL)) {
    	 			this.iseg.appendCode(Operator.BEQ, compAddr+4);
    	 		}else if(nowToken.equals(Symbol.LESS)) {
    	 			this.iseg.appendCode(Operator.BLT, compAddr+4);
    	 		}else if(nowToken.equals(Symbol.GREAT)) {
    	 			this.iseg.appendCode(Operator.BGT, compAddr+4);
    	 		}else if(nowToken.equals(Symbol.NOTEQ)) {
    	 			this.iseg.appendCode(Operator.BNE, compAddr+4);
    	 		}
    	 		this.iseg.appendCode(Operator.PUSHI, 0);
    	 		this.iseg.appendCode(Operator.JUMP, compAddr+5);
    	 		this.iseg.appendCode(Operator.PUSHI, 1);
    	 		break;
    	 	default:
    	 		break;
    	 }
     }

     /**
      * Arithmetic_termに移動
      * ー、＋どちらかで再度Arithmetic_termに移動
      */
     private void paeseArithmetic_expression() {//termを参考に
    	 if(checkExpression(this.token)) {
    		 this.paeseArithmetic_term();
    	 }else {
    		 this.syntaxError("666: Arithmetic_term");
    	 }

    	 while(this.token.checkSymbol(Symbol.ADD) || this.token.checkSymbol(Symbol.SUB)) {
    		 Symbol op = this.token.getSymbol();
    		 this.token = this.lexer.nextToken();
    		 if(checkExpression(this.token)) {
        		 this.paeseArithmetic_term();
        	 }else {
        		 this.syntaxError("674: Arithmetic_term");
        	 }
    		 switch(op) {
    		 	case ADD:
    		 		this.iseg.appendCode(Operator.ADD);
    		 		break;
    		 	case SUB:
    		 		this.iseg.appendCode(Operator.SUB);
    		 		break;
		 		default:
		 			break;
    		 }
    	 }
     }

     /**
      * * / %いずれかの処理
      */
     private void paeseArithmetic_term() {
    	 if(checkExpression(this.token)) {
    		 this.paeseArithmetic_factor();
    	 }else {
    		 this.syntaxError("686: paeseArithmetic_factor");
    	 }

    	 while(this.token.checkSymbol(Symbol.MUL)||
    			this.token.checkSymbol(Symbol.DIV)||
    			this.token.checkSymbol(Symbol.MOD)) {

    		 Symbol op = this.token.getSymbol();
    		 this.token = this.lexer.nextToken();
    		 if(checkExpression(this.token)) {
        		 this.paeseArithmetic_factor();
        	 }else {
        		 this.syntaxError("696: paeseArithmetic_factor");
        	 }
    		 switch(op) {
    		 	case MUL:
    		 		this.iseg.appendCode(Operator.MUL);
    		 		break;
    		 	case DIV:
    		 		this.iseg.appendCode(Operator.DIV);
    		 		break;
    		 	case MOD:
    		 		this.iseg.appendCode(Operator.MOD);
    		 		break;
		 		default:
		 			break;
    		 }
    	 }
     }

     private void paeseArithmetic_factor() {//Csing
    	 if(checkExpression(this.token) && !this.token.checkSymbol(Symbol.SUB) && ! this.token.checkSymbol(Symbol.NOT)) {
    		 this.paeseUnsigned_factor();
    	 }else if(this.token.checkSymbol(Symbol.SUB)) {
    		 this.token = this.lexer.nextToken();
    		 this.paeseArithmetic_factor();
    		 this.iseg.appendCode(Operator.CSIGN);
    	 }else if(this.token.checkSymbol(Symbol.NOT)) {
    		 this.token = this.lexer.nextToken();
    		 this.paeseArithmetic_factor();
    		 this.iseg.appendCode(Operator.NOT);
    	 }else {
    		 this.syntaxError("842: paeseArithmetic_factor");
    	 }
     }

     /**
      * 宣言済み変数の処理
      */
     private void paeseUnsigned_factor() {
    	 String name = "";
    	 if(this.token.checkSymbol(Symbol.NAME)) {
    		 name = this.token.getStrValue();
    		 int address = this.variableTable.getAddress(this.token.getStrValue());
    		 this.token = this.lexer.nextToken();
    		 if(this.token.checkSymbol(Symbol.ASSIGN)
				 ||this.token.checkSymbol(Symbol.ASSIGNADD)
				 ||this.token.checkSymbol(Symbol.ASSIGNSUB)
				 ||this.token.checkSymbol(Symbol.ASSIGNMUL)
				 ||this.token.checkSymbol(Symbol.ASSIGNDIV)
				 || this.variableTable.getType(name).equals(Type.ARRAYOFINT)) {
    			 this.iseg.appendCode(Operator.PUSHI, address);
    		 }else {
    			 this.iseg.appendCode(Operator.PUSH, address);
    		 }
    		 if(this.token.checkSymbol(Symbol.INC) || this.token.checkSymbol(Symbol.DEC)) {
    			 Symbol sm = this.token.getSymbol();
    			 this.token = this.lexer.nextToken();
    			 if(sm.equals(Symbol.INC)) {
//    				 this.iseg.appendCode(Operator.PUSH,address);
    				 this.iseg.appendCode(Operator.COPY);
    				 this.iseg.appendCode(Operator.INC);
    				 this.iseg.appendCode(Operator.POP,address);
    			 }else if(sm.equals(Symbol.DEC)) {
//    				 this.iseg.appendCode(Operator.PUSH,address);
    				 this.iseg.appendCode(Operator.COPY);
    				 this.iseg.appendCode(Operator.DEC);
    				 this.iseg.appendCode(Operator.POP,address);
    			 }
    		 }else if(this.token.checkSymbol(Symbol.LBRACKET)) {
    			 this.token = this.lexer.nextToken();
    			 if(checkExpression(this.token)) {
    				 this.paeseExpression();
    			 }else {
    				 this.syntaxError("725: expression");
    			 }
    			 if(this.token.checkSymbol(Symbol.RBRACKET)) {
    				 this.token = this.lexer.nextToken();
    				 this.iseg.appendCode(Operator.ADD);
    			 }else {
    				 this.syntaxError("730: ]");
    			 }
    			 if(this.token.checkSymbol(Symbol.ASSIGN)
					 ||this.token.checkSymbol(Symbol.ASSIGNSUB)
					 ||this.token.checkSymbol(Symbol.ASSIGNMUL)
					 ||this.token.checkSymbol(Symbol.ASSIGNDIV)
					 ||this.token.checkSymbol(Symbol.ASSIGNADD)) {
    			 }else {
    				 this.iseg.appendCode(Operator.LOAD);
    			 }
    		 }
    	 }else if(this.token.checkSymbol(Symbol.INC)||this.token.checkSymbol(Symbol.DEC)){
    		 Symbol sm = this.token.getSymbol();
    		 this.token = this.lexer.nextToken();
    		 int address = 0;
    		 if(this.token.checkSymbol(Symbol.NAME)) {
    			 name = this.token.getStrValue();
    			 address = this.variableTable.getAddress(this.token.getStrValue());
    			 this.token = this.lexer.nextToken();
    		 }else {
    			 this.syntaxError("738: name");
    		 }

    		 if(this.token.checkSymbol(Symbol.LBRACKET)) {
    			 this.token = this.lexer.nextToken();

    			 if(this.checkExpression(this.token)) {
    				 this.iseg.appendCode(Operator.PUSHI,address);
    				 this.paeseExpression();
    				 this.iseg.appendCode(Operator.ADD);
    				 this.iseg.appendCode(Operator.COPY);
    				 this.iseg.appendCode(Operator.LOAD);
    				 if(sm.equals(Symbol.INC)) {
    					 this.iseg.appendCode(Operator.INC);
    				 }else {
    					 this.iseg.appendCode(Operator.DEC);
    				 }
    				 this.iseg.appendCode(Operator.ASSGN);
    			 }else {
    				 this.syntaxError("745: expression");
    			 }
    			 if(this.token.checkSymbol(Symbol.RBRACKET)) {
    				 this.token = this.lexer.nextToken();
    			 }else {
    				 this.syntaxError("750: ]");
    			 }
    		 }else {

    			 if(sm.equals(Symbol.INC)) {
    				 this.iseg.appendCode(Operator.PUSH,address);
    				 this.iseg.appendCode(Operator.INC);
    				 this.iseg.appendCode(Operator.COPY);
    				 this.iseg.appendCode(Operator.POP,address);
    			 }else if(sm.equals(Symbol.DEC)) {
    				 this.iseg.appendCode(Operator.PUSH,address);
    				 this.iseg.appendCode(Operator.DEC);
    				 this.iseg.appendCode(Operator.COPY);
    				 this.iseg.appendCode(Operator.POP,address);
    			 }

    		 }
    	 }else if(this.token.checkSymbol(Symbol.INTEGER)) {
    		 int value = this.token.getIntValue();
    		 this.token = this.lexer.nextToken();
    		 this.iseg.appendCode(Operator.PUSHI, value);
    	 }else if(this.token.checkSymbol(Symbol.CHARACTER)) {
    		 int charCode = this.token.getIntValue();
    		 this.token = this.lexer.nextToken();
    		 this.iseg.appendCode(Operator.PUSHI, charCode);
    	 }else if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    		 if(this.checkExpression(this.token)) {
    			 this.paeseExpression();
    		 }else {
    			 this.syntaxError("762: expression");
    		 }
    		 if(this.token.checkSymbol(Symbol.RPAREN)) {
    			 this.token = this.lexer.nextToken();
    		 }else {
    			 this.syntaxError("767: )");
    		 }
    	 }else if(this.token.checkSymbol(Symbol.INPUTCHAR)|| this.token.checkSymbol(Symbol.INPUTINT)) {
    		 Symbol symbol = this.token.getSymbol();
    		 if(symbol.equals(Symbol.INPUTCHAR)) {
    			 this.iseg.appendCode(Operator.INPUTC);
    		 }else {
    			 this.iseg.appendCode(Operator.INPUT);
    		 }
    		 this.token = this.lexer.nextToken();
    	 }else if(this.token.checkSymbol(Symbol.ADD)) {
    		 this.paeseSum_function();
    	 }else if(this.token.checkSymbol(Symbol.MUL)) {
    		 this.paeseProduct_function();
    	 }
     }

     /**
      * 足し算処理
      */
     private void paeseSum_function() {
    	 if(this.token.checkSymbol(Symbol.ADD)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("785: +");
    	 }

    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("791: (");
    	 }

    	 int size = 0;

    	 if(this.checkExpression(this.token)) {
    		 size = this.paeseExpression_list();
    	 }else {
    		 this.syntaxError("797: Expression_list");
    	 }

    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("803: )");
    	 }

    	 for(int i = 0 ; i < size-1; i++) {
    		 this.iseg.appendCode(Operator.ADD);
    	 }

     }

     /**
      * 掛け算処理
      */
     private void paeseProduct_function() {
    	 if(this.token.checkSymbol(Symbol.MUL)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("814: *");
    	 }

    	 int size = 0;

    	 if(this.token.checkSymbol(Symbol.LPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("820: (");
    	 }

    	 if(this.checkExpression(this.token)) {
    		 size = this.paeseExpression_list();
    	 }else {
    		 this.syntaxError("826: paeseExpression_list");
    	 }

    	 if(this.token.checkSymbol(Symbol.RPAREN)) {
    		 this.token = this.lexer.nextToken();
    	 }else {
    		 this.syntaxError("832: )");
    	 }

    	 for(int i = 0 ; i < size-1; i++) {
    		 this.iseg.appendCode(Operator.MUL);
    	 }
     }

     /**
      * Expressionに移動
      * コンマがあればもう一度移動
      */
     private int paeseExpression_list() {

    	 int size = 1;
    	 if(this.checkExpression(this.token)) {
    		 this.paeseExpression();
    	 }else {
    		 this.syntaxError("844: paeseExpression_list");
    	 }

    	 while(this.token.checkSymbol(Symbol.COMMA)) {
    		 this.token = this.lexer.nextToken();
    		 this.paeseExpression();
    		 size ++;
    	 }

    	 return size;
     }



	/**
	 * 現在読んでいるファイルを閉じる (lexerのcloseFile()に委譲)
	 */
	void closeFile() {
		lexer.closeFile();
	}

	/**
	 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
	 */
	void dump2file() {
		iseg.dump2file();
	}

	/**
	 * アセンブラコードをファイルに出力する (isegのdump2file()に委譲)
	 *
	 * @param fileName 出力ファイル名
	 */
	void dump2file(String fileName) {
		iseg.dump2file(fileName);
	}

	/**
	 * エラーメッセージを出力しプログラムを終了する
	 *
	 * @param message 出力エラーメッセージ
	 */
	private void syntaxError(String message) {
		System.out.print(lexer.analyzeAt());
		//下記の文言は自動採点で使用するので変更しないでください。
		System.out.println("で構文解析プログラムが構文エラーを検出");
		System.out.println(message+"が期待されます");
		closeFile();
		System.exit(1);
	}

	/**
	 * 引数で指定したK20言語ファイルを解析する 読み込んだファイルが文法上正しければアセンブラコードを出力する
	 */
	public static void main(String[] args) {
		Kc paeser;

		if (args.length == 0) {
			System.out.println("Usage: java kc.Kc20 file [objectfile]");
			System.exit(0);
		}

		paeser = new Kc(args[0]);

		paeser.paeseProgram();
		paeser.closeFile();

		if (args.length == 1)
			paeser.dump2file();
		else
			paeser.dump2file(args[1]);
	}
}