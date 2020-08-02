/*      */ package kc;
/*      */
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */ public class Kc20 {
/*      */    private LexicalAnalyzer lexer;
/*      */    private Token token;
/*      */    private VarTable variableTable;
/*      */    private PseudoIseg iseg;
/*      */    private ArrayList<Integer> breakAddressList;
/*      */    private ArrayList<Integer> continueAddressList;
/*      */    private boolean isInLoop;
/*      */
/*      */    Kc20(String sourceFileName) {
/*   23 */       this.lexer = new LexicalAnalyzer(sourceFileName);
/*   24 */       this.variableTable = new VarTable();
/*   25 */       this.iseg = new PseudoIseg();
/*   26 */       this.token = this.lexer.nextToken();
/*   27 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    void parseProgram() {
/*   33 */       this.parseMainFunction();
/*   34 */       if (this.token.checkSymbol(Symbol.EOF)) {
/*   35 */          this.iseg.appendCode(Operator.HALT);
/*      */       } else {
/*   37 */          this.syntaxError("ファイル末が期待されます");
/*      */       }
/*   39 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseMainFunction() {
/*   45 */       if (this.token.checkSymbol(Symbol.MAIN)) {
/*   46 */          this.token = this.lexer.nextToken();      } else {
/*   47 */          this.syntaxError("\"main\" が期待されます");      }
/*   48 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*   49 */          this.token = this.lexer.nextToken();      } else {
/*   50 */          this.syntaxError("'(' が期待されます");      }
/*   51 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*   52 */          this.token = this.lexer.nextToken();      } else {
/*   53 */          this.syntaxError("')' が期待されます");      }
/*   54 */       if (this.token.checkSymbol(Symbol.LBRACE)) {
/*   55 */          this.parseBlock();      } else {
/*   56 */          this.syntaxError("'{' が期待されます");      }
/*   57 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseBlock() {
/*   63 */       if (this.token.checkSymbol(Symbol.LBRACE)) {
/*   64 */          this.token = this.lexer.nextToken();      } else {
/*   65 */          this.syntaxError("'{' が期待されます");      }
/*   66 */       while(this.token.checkSymbol(Symbol.INT)) {
/*   67 */          this.parseVarDecl();
/*      */       }
/*   69 */       while(!this.token.checkSymbol(Symbol.RBRACE)) {
/*   70 */          this.parseStatement();
/*      */       }
/*   72 */       this.token = this.lexer.nextToken();
/*   73 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseVarDecl() {
/*   79 */       if (this.token.checkSymbol(Symbol.INT)) {
/*   80 */          this.token = this.lexer.nextToken();      }
/*   81 */       this.parseVarDeclList();
/*   82 */       while(this.token.checkSymbol(Symbol.COMMA)) {
/*   83 */          this.token = this.lexer.nextToken();
/*   84 */          this.parseVarDeclList();
/*      */       }
/*   86 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*   87 */          this.token = this.lexer.nextToken();      } else {
/*   88 */          this.syntaxError("';' が期待されます");      }
/*   89 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseVarDeclList() {
/*   95 */       String name = "";
/*   96 */       int size = 1;
/*      */
/*      */
/*   99 */       int sign = true;
/*      */
/*      */
/*  102 */       if (this.token.checkSymbol(Symbol.NAME)) {
/*  103 */          name = this.token.getStrValue();
/*  104 */          if (this.variableTable.exist(this.token.getStrValue())) {
/*  105 */             this.syntaxError(this.token.getStrValue() + " は既に宣言されています．");         }
/*  106 */          this.token = this.lexer.nextToken();      } else {
/*  107 */          this.syntaxError("名前が期待されます");      }      int initialValue;      int address;
/*  108 */       if (this.token.checkSymbol(Symbol.ASSIGN)) {
/*  109 */          this.token = this.lexer.nextToken();
/*  110 */          initialValue = this.parseConstant();
/*  111 */          this.variableTable.registerNewVariable(Type.INT, name, size);
/*  112 */          address = this.variableTable.getAddress(name);
/*  113 */          this.iseg.appendCode(Operator.PUSHI, initialValue);
/*  114 */          this.iseg.appendCode(Operator.POP, address);
/*  115 */       } else if (this.token.checkSymbol(Symbol.LBRACKET)) {
/*  116 */          this.token = this.lexer.nextToken();
/*  117 */          if (this.token.checkSymbol(Symbol.INTEGER)) {
/*  118 */             size = this.token.getIntValue();
/*  119 */             this.token = this.lexer.nextToken();
/*  120 */             if (this.token.checkSymbol(Symbol.RBRACKET)) {
/*  121 */                this.variableTable.registerNewVariable(Type.ARRAYOFINT, name, size);
/*  122 */                this.token = this.lexer.nextToken();            } else {
/*  123 */                this.syntaxError("']' が期待されます");            }
/*  124 */          } else if (this.token.checkSymbol(Symbol.RBRACKET)) {
/*      */
/*      */
/*  127 */             this.token = this.lexer.nextToken();
/*  128 */             if (this.token.checkSymbol(Symbol.ASSIGN)) {
/*  129 */                this.token = this.lexer.nextToken();            } else {
/*  130 */                this.syntaxError("'=' が期待されます");            }            int i;
/*  131 */             if (this.token.checkSymbol(Symbol.LBRACE)) {
/*  132 */                this.token = this.lexer.nextToken();
/*      */
/*  134 */                ArrayList<Integer> initialValueList = new ArrayList();
/*  135 */                initialValue = this.parseConstant();
/*      */
/*      */
/*  138 */                initialValueList.add(initialValue);
/*  139 */                while(this.token.checkSymbol(Symbol.COMMA)) {
/*  140 */                   this.token = this.lexer.nextToken();
/*  141 */                   ++size;
/*  142 */                   initialValue = this.parseConstant();
/*  143 */                   initialValueList.add(initialValue);
/*      */                }
/*  145 */                if (this.token.checkSymbol(Symbol.RBRACE)) {
/*      */
/*  147 */                   this.variableTable.registerNewVariable(Type.ARRAYOFINT, name, size);
/*  148 */                   address = this.variableTable.getAddress(name);
/*      */
/*  150 */                   for(i = 0; i < size; ++i) {
/*  151 */                      initialValue = (Integer)initialValueList.get(i);
/*  152 */                      this.iseg.appendCode(Operator.PUSHI, initialValue);
/*  153 */                      this.iseg.appendCode(Operator.POP, address + i);
/*      */                   }
/*  155 */                   this.token = this.lexer.nextToken();
/*      */                }
/*  157 */             } else if (this.token.checkSymbol(Symbol.STRING)) {
/*  158 */                String str = this.token.getStrValue();
/*  159 */                size = str.length();
/*      */
/*  161 */                this.variableTable.registerNewVariable(Type.ARRAYOFINT, name, size);
/*  162 */                address = this.variableTable.getAddress(name);
/*      */
/*  164 */                for(i = 0; i < size; ++i) {
/*  165 */                   int initialValue = str.charAt(i);
/*  166 */                   this.iseg.appendCode(Operator.PUSHI, initialValue);
/*  167 */                   this.iseg.appendCode(Operator.POP, address + i);
/*      */                }
/*  169 */                this.token = this.lexer.nextToken();            } else {
/*  170 */                this.syntaxError("'}'が期待されます");            }         } else {
/*  171 */             this.syntaxError("整数が期待されます");
/*      */          }      } else {
/*  173 */          this.variableTable.registerNewVariable(Type.INT, name, size);
/*      */       }
/*  175 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private int parseConstant() {
/*  183 */       int initialValue = 0;
/*  184 */       int sign = 1;
/*      */
/*  186 */       if (this.token.checkSymbol(Symbol.CHARACTER)) {
/*  187 */          initialValue = this.token.getIntValue();
/*  188 */          this.token = this.lexer.nextToken();
/*      */       } else {
/*  190 */          if (this.token.checkSymbol(Symbol.SUB)) {
/*  191 */             sign = -1;
/*  192 */             this.token = this.lexer.nextToken();
/*      */          }
/*  194 */          if (this.token.checkSymbol(Symbol.INTEGER)) {
/*  195 */             initialValue = sign * this.token.getIntValue();
/*  196 */             this.token = this.lexer.nextToken();         } else {
/*  197 */             this.syntaxError("整数または文字が期待されます");
/*      */          }      }
/*  199 */       return initialValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseStatement() {
/*  206 */       switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */       case 3:
/*  208 */          this.parseIfStatement();
/*  209 */          break;
/*      */       case 4:
/*  211 */          this.parseWhileStatement();
/*  212 */          break;
/*      */       case 5:
/*  214 */          this.parseForStatement();
/*  215 */          break;
/*      */       case 9:
/*  217 */          this.parseOutputChar();
/*  218 */          break;
/*      */       case 8:
/*  220 */          this.parseOutputInt();
/*  221 */          break;
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */       case 48:
/*  229 */          this.token = this.lexer.nextToken();
/*  230 */          while(!this.token.checkSymbol(Symbol.RBRACE)) {
/*  231 */             this.parseStatement();
/*      */          }
/*  233 */          this.token = this.lexer.nextToken();
/*  234 */          break;
/*      */       case 16:
/*  236 */          this.parseBreak();
/*  237 */          break;
/*      */
/*      */
/*      */
/*      */       case 45:
/*  242 */          this.token = this.lexer.nextToken();
/*  243 */          break;
/*      */       case 6:
/*      */       case 7:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 43:
/*      */       case 44:
/*      */       case 46:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/*  256 */          this.parseExpStatement();
/*  257 */          break;
/*      */       default:
/*  259 */          this.syntaxError("文が期待されます");
/*      */       }
/*      */
/*  262 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseIfStatement() {
/*  269 */       this.token = this.lexer.nextToken();
/*  270 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  271 */          this.token = this.lexer.nextToken();      } else {
/*  272 */          this.syntaxError("'(' が期待されます");      }
/*  273 */       if (this.isExpFirst()) {
/*  274 */          this.parseExpression();      } else {
/*  275 */          this.syntaxError("式が期待されます");      }
/*  276 */       int addressSkip = this.iseg.appendCode(Operator.BEQ);
/*  277 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  278 */          this.token = this.lexer.nextToken();      } else {
/*  279 */          this.syntaxError("')' が期待されます");      }
/*  280 */       this.parseStatement();
/*  281 */       int addressLastCode = this.iseg.getLastCodeAddress();
/*  282 */       this.iseg.replaceCode(addressSkip, addressLastCode + 1);
/*  283 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseWhileStatement() {
/*  289 */       int addressCheckCondition = this.iseg.getLastCodeAddress() + 1;
/*      */
/*  291 */       this.token = this.lexer.nextToken();
/*  292 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  293 */          this.token = this.lexer.nextToken();      } else {
/*  294 */          this.syntaxError("'(' が期待されます");      }
/*  295 */       if (this.isExpFirst()) {
/*  296 */          this.parseExpression();      } else {
/*  297 */          this.syntaxError("式が期待されます");      }
/*  298 */       int addressSkip = this.iseg.appendCode(Operator.BEQ);
/*  299 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  300 */          this.token = this.lexer.nextToken();      } else {
/*  301 */          this.syntaxError("')' が期待されます");
/*      */       }
/*  303 */       boolean outerIsInLoop = this.isInLoop;
/*  304 */       this.isInLoop = true;
/*  305 */       ArrayList<Integer> outerBreakAddressList = this.breakAddressList;
/*  306 */       this.breakAddressList = new ArrayList();
/*  307 */       ArrayList<Integer> outerContinueAddressList = this.continueAddressList;
/*  308 */       this.continueAddressList = new ArrayList();
/*      */
/*  310 */       this.parseStatement();
/*      */
/*  312 */       int addressJump = this.iseg.appendCode(Operator.JUMP, addressCheckCondition);
/*  313 */       this.iseg.replaceCode(addressSkip, addressJump + 1);
/*      */
/*  315 */       Iterator var8 = this.breakAddressList.iterator();      int addressContinue;      while(var8.hasNext()) {         addressContinue = (Integer)var8.next();
/*  316 */          this.iseg.replaceCode(addressContinue, addressJump + 1);
/*      */       }      var8 = this.continueAddressList.iterator();      while(var8.hasNext()) {
/*  318 */          addressContinue = (Integer)var8.next();
/*  319 */          this.iseg.replaceCode(addressContinue, addressCheckCondition);
/*      */       }
/*  321 */       this.isInLoop = outerIsInLoop;
/*  322 */       this.breakAddressList = outerBreakAddressList;
/*  323 */       this.continueAddressList = outerContinueAddressList;
/*  324 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseForStatement() {
/*  336 */       this.token = this.lexer.nextToken();
/*  337 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  338 */          this.token = this.lexer.nextToken();      } else {
/*  339 */          this.syntaxError("'(' が期待されます");
/*      */       }
/*  341 */       if (this.isExpFirst()) {
/*  342 */          this.parseExpression();
/*  343 */          this.iseg.appendCode(Operator.REMOVE);
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */       } else {
/*  354 */          this.syntaxError("式が期待されます");      }
/*  355 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  356 */          this.token = this.lexer.nextToken();      } else {
/*  357 */          this.syntaxError("';' が期待されます");
/*      */       }
/*  359 */       int addressCheckCondition = this.iseg.getLastCodeAddress() + 1;
/*  360 */       if (this.isExpFirst()) {
/*  361 */          this.parseExpression();      } else {
/*  362 */          this.syntaxError("式が期待されます");
/*      */
/*      */
/*      */       }
/*      */
/*  367 */       int addressSkip = this.iseg.appendCode(Operator.BEQ);
/*  368 */       int addressJump = this.iseg.appendCode(Operator.JUMP);
/*  369 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  370 */          this.token = this.lexer.nextToken();      } else {
/*  371 */          this.syntaxError("';' が期待されます");
/*      */       }
/*  373 */       int addressContinuation = addressJump + 1;
/*  374 */       if (this.isExpFirst()) {
/*  375 */          this.parseExpression();
/*  376 */          this.iseg.appendCode(Operator.REMOVE);
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*  385 */          this.iseg.appendCode(Operator.JUMP, addressCheckCondition);      } else {
/*  386 */          this.syntaxError("式が期待されます");
/*      */
/*      */
/*      */       }
/*      */
/*  391 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  392 */          this.token = this.lexer.nextToken();      } else {
/*  393 */          this.syntaxError("')' が期待されます");      }
/*  394 */       int addressLastCode = this.iseg.getLastCodeAddress();
/*  395 */       this.iseg.replaceCode(addressJump, addressLastCode + 1);
/*      */
/*  397 */       boolean outerIsInLoop = this.isInLoop;
/*  398 */       this.isInLoop = true;
/*  399 */       ArrayList<Integer> outerBreakAddressList = this.breakAddressList;
/*  400 */       this.breakAddressList = new ArrayList();
/*  401 */       ArrayList<Integer> outerContinueAddressList = this.continueAddressList;
/*  402 */       this.continueAddressList = new ArrayList();
/*      */
/*  404 */       this.parseStatement();
/*      */
/*  406 */       addressLastCode = this.iseg.appendCode(Operator.JUMP, addressContinuation);
/*      */
/*  408 */       this.iseg.replaceCode(addressSkip, addressLastCode + 1);
/*      */
/*  410 */       Iterator var10 = this.breakAddressList.iterator();      int addressContinue;      while(var10.hasNext()) {         addressContinue = (Integer)var10.next();
/*  411 */          this.iseg.replaceCode(addressContinue, addressLastCode + 1);
/*      */       }
/*  413 */       var10 = this.continueAddressList.iterator();      while(var10.hasNext()) {         addressContinue = (Integer)var10.next();
/*  414 */          this.iseg.replaceCode(addressContinue, addressContinuation);
/*      */       }
/*  416 */       this.isInLoop = outerIsInLoop;
/*  417 */       this.breakAddressList = outerBreakAddressList;
/*  418 */       this.continueAddressList = outerContinueAddressList;
/*  419 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseOutputChar() {
/*  425 */       this.token = this.lexer.nextToken();
/*  426 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  427 */          this.token = this.lexer.nextToken();
/*      */       } else {
/*  429 */          this.syntaxError("'(' が期待されます");      }
/*  430 */       if (this.isExpFirst()) {
/*  431 */          this.parseExpression();      } else {
/*  432 */          this.syntaxError("式が期待されます");      }
/*  433 */       this.iseg.appendCode(Operator.OUTPUTC);
/*  434 */       this.iseg.appendCode(Operator.OUTPUTLN);
/*  435 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  436 */          this.token = this.lexer.nextToken();      } else {
/*  437 */          this.syntaxError("')' が期待されます");      }
/*  438 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  439 */          this.token = this.lexer.nextToken();      } else {
/*  440 */          this.syntaxError("';' が期待されます");      }
/*  441 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseOutputInt() {
/*  447 */       this.token = this.lexer.nextToken();
/*  448 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  449 */          this.token = this.lexer.nextToken();      } else {
/*  450 */          this.syntaxError("'(' が期待されます");      }
/*  451 */       if (this.isExpFirst()) {
/*  452 */          this.parseExpression();      } else {
/*  453 */          this.syntaxError("式が期待されます");      }
/*  454 */       this.iseg.appendCode(Operator.OUTPUT);
/*  455 */       this.iseg.appendCode(Operator.OUTPUTLN);
/*  456 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  457 */          this.token = this.lexer.nextToken();      } else {
/*  458 */          this.syntaxError("')' が期待されます");      }
/*  459 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  460 */          this.token = this.lexer.nextToken();      } else {
/*  461 */          this.syntaxError("';' が期待されます");      }
/*  462 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseBreak() {
/*  468 */       if (!this.isInLoop) {
/*  469 */          this.syntaxError("loopの外でbreak文が出現しました");
/*      */       }
/*  471 */       this.token = this.lexer.nextToken();
/*  472 */       int addressJump = this.iseg.appendCode(Operator.JUMP);
/*  473 */       this.breakAddressList.add(addressJump);
/*  474 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  475 */          this.token = this.lexer.nextToken();      } else {
/*  476 */          this.syntaxError("';' が期待されます");      }
/*  477 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseContinue() {
/*  484 */       if (!this.isInLoop) {
/*  485 */          this.syntaxError("loopの外でcontinue文が出現しました");
/*      */       }
/*  487 */       this.token = this.lexer.nextToken();
/*  488 */       int addressJump = this.iseg.appendCode(Operator.JUMP);
/*  489 */       this.continueAddressList.add(addressJump);
/*  490 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  491 */          this.token = this.lexer.nextToken();      } else {
/*  492 */          this.syntaxError("';' が期待されます");      }
/*  493 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseOutputStr() {
/*  500 */       this.token = this.lexer.nextToken();
/*  501 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  502 */          this.token = this.lexer.nextToken();      } else {
/*  503 */          this.syntaxError("'(' が期待されます");      }
/*  504 */       this.parseOutputString();
/*  505 */       while(this.token.checkSymbol(Symbol.ADD)) {
/*  506 */          this.token = this.lexer.nextToken();
/*  507 */          this.parseOutputString();
/*      */       }
/*  509 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  510 */          this.token = this.lexer.nextToken();      } else {
/*  511 */          this.syntaxError("')' が期待されます");      }
/*  512 */       this.iseg.appendCode(Operator.OUTPUTLN);
/*  513 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  514 */          this.token = this.lexer.nextToken();      } else {
/*  515 */          this.syntaxError("';' が期待されます");      }
/*  516 */    }
/*      */
/*      */
/*      */    private void parseOutputString() {
/*      */       int size;
/*      */       String name;
/*      */       int address;
/*  523 */       if (this.token.checkSymbol(Symbol.STRING)) {
/*  524 */          name = this.token.getStrValue();
/*  525 */          this.token = this.lexer.nextToken();
/*  526 */          size = name.length();
/*  527 */          for(address = 0; address < size; ++address) {
/*  528 */             this.iseg.appendCode(Operator.PUSHI, name.charAt(address));
/*  529 */             this.iseg.appendCode(Operator.OUTPUTC);
/*      */          }
/*  531 */       } else if (this.token.checkSymbol(Symbol.NAME)) {
/*  532 */          name = this.token.getStrValue();
/*  533 */          this.token = this.lexer.nextToken();
/*  534 */          if (!this.variableTable.exist(name)) {
/*  535 */             this.syntaxError(name + " は宣言されていません");         }
/*  536 */          if (!this.variableTable.checkType(name, Type.ARRAYOFINT)) {
/*  537 */             this.syntaxError(name + " は配列型変数ではありません");         }
/*  538 */          address = this.variableTable.getAddress(name);
/*  539 */          size = this.variableTable.getSize(name);
/*  540 */          for(int i = 0; i < size; ++i) {
/*  541 */             this.iseg.appendCode(Operator.PUSH, address + i);
/*  542 */             this.iseg.appendCode(Operator.OUTPUTC);
/*      */          }      } else {
/*  544 */          this.syntaxError("文字列または配列型変数が期待されます");      }
/*  545 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseSetStr() {
/*  552 */       this.token = this.lexer.nextToken();
/*  553 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/*  554 */          this.token = this.lexer.nextToken();      }
/*  555 */       if (this.token.checkSymbol(Symbol.NAME)) {
/*  556 */          String name = this.token.getStrValue();
/*  557 */          this.token = this.lexer.nextToken();
/*  558 */          if (!this.variableTable.exist(name)) {
/*  559 */             this.syntaxError(name + " は宣言されていません");         }
/*  560 */          if (!this.variableTable.checkType(name, Type.ARRAYOFINT)) {
/*  561 */             this.syntaxError(name + " は配列型変数ではありません");         }
/*  562 */          int leftAddress = this.variableTable.getAddress(name);
/*  563 */          this.variableTable.getSize(name);
/*  564 */          int rightSize = false;
/*  565 */          if (this.token.checkSymbol(Symbol.COMMA)) {
/*  566 */             this.token = this.lexer.nextToken();         } else {
/*  567 */             this.syntaxError("',' が期待されます");         }
/*  568 */          for(int rightSize = this.parseAssignString(leftAddress); this.token.checkSymbol(Symbol.ADD); rightSize += this.parseAssignString(leftAddress + rightSize)) {
/*      */
/*  570 */             this.token = this.lexer.nextToken();
/*      */          }
/*      */       } else {
/*  573 */          this.syntaxError("配列型変数が期待されます");      }
/*  574 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  575 */          this.token = this.lexer.nextToken();      } else {
/*  576 */          this.syntaxError("')' が期待されます");      }
/*  577 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  578 */          this.token = this.lexer.nextToken();      } else {
/*  579 */          this.syntaxError("';' が期待されます");      }
/*  580 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private int parseAssignString(int leftAddress) {
/*  588 */       int size = -1;      String name;      int address;
/*  589 */       if (this.token.checkSymbol(Symbol.STRING)) {
/*  590 */          name = this.token.getStrValue();
/*  591 */          size = name.length();
/*  592 */          for(address = 0; address < size; ++address) {
/*  593 */             this.iseg.appendCode(Operator.PUSHI, name.charAt(address));
/*  594 */             this.iseg.appendCode(Operator.POP, leftAddress + address);
/*      */          }
/*  596 */          this.token = this.lexer.nextToken();
/*  597 */          return size;      } else {
/*  598 */          if (this.token.checkSymbol(Symbol.NAME)) {
/*  599 */             name = this.token.getStrValue();
/*  600 */             this.token = this.lexer.nextToken();
/*  601 */             if (!this.variableTable.exist(name)) {
/*  602 */                this.syntaxError(name + " は宣言されていません");            }
/*  603 */             if (!this.variableTable.checkType(name, Type.ARRAYOFINT)) {
/*  604 */                this.syntaxError(name + " は配列型変数ではありません");            }
/*  605 */             address = this.variableTable.getAddress(name);
/*  606 */             size = this.variableTable.getSize(name);
/*  607 */             for(int i = 0; i < size; ++i) {
/*  608 */                this.iseg.appendCode(Operator.PUSH, address + i);
/*  609 */                this.iseg.appendCode(Operator.POP, leftAddress + i);
/*      */             }         } else {
/*  611 */             this.syntaxError("文字列または配列型変数が期待されます");         }
/*  612 */          return size;
/*      */       }
/*      */    }
/*      */
/*      */
/*      */
/*      */    private void parseExpStatement() {
/*  619 */       this.parseExpression();
/*  620 */       if (this.token.checkSymbol(Symbol.SEMICOLON)) {
/*  621 */          this.token = this.lexer.nextToken();
/*  622 */          this.iseg.appendCode(Operator.REMOVE);      } else {
/*  623 */          this.syntaxError("';'が期待されます");      }
/*  624 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseExpression() {
/*  630 */       boolean isLeftValue = this.parseExp();
/*  631 */       if (this.token.checkSymbol(Symbol.ASSIGN) || this.token.checkSymbol(Symbol.ASSIGNADD) || this.token.checkSymbol(Symbol.ASSIGNSUB) || this.token.checkSymbol(Symbol.ASSIGNMUL) || this.token.checkSymbol(Symbol.ASSIGNDIV) || this.token.checkSymbol(Symbol.ASSIGNMOD)) {
/*      */
/*      */
/*      */
/*      */
/*      */
/*  637 */          if (!isLeftValue) {
/*  638 */             this.syntaxError("右辺値に代入しようとしました");         }
/*  639 */          switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */          case 37:
/*  641 */             this.token = this.lexer.nextToken();
/*  642 */             this.parseExpression();
/*  643 */             this.iseg.appendCode(Operator.ASSGN);
/*  644 */             break;
/*      */          case 38:
/*  646 */             this.iseg.appendCode(Operator.COPY);
/*  647 */             this.iseg.appendCode(Operator.LOAD);
/*  648 */             this.token = this.lexer.nextToken();
/*  649 */             this.parseExpression();
/*  650 */             this.iseg.appendCode(Operator.ADD);
/*  651 */             this.iseg.appendCode(Operator.ASSGN);
/*  652 */             break;
/*      */          case 39:
/*  654 */             this.iseg.appendCode(Operator.COPY);
/*  655 */             this.iseg.appendCode(Operator.LOAD);
/*  656 */             this.token = this.lexer.nextToken();
/*  657 */             this.parseExpression();
/*  658 */             this.iseg.appendCode(Operator.SUB);
/*  659 */             this.iseg.appendCode(Operator.ASSGN);
/*  660 */             break;
/*      */          case 40:
/*  662 */             this.iseg.appendCode(Operator.COPY);
/*  663 */             this.iseg.appendCode(Operator.LOAD);
/*  664 */             this.token = this.lexer.nextToken();
/*  665 */             this.parseExpression();
/*  666 */             this.iseg.appendCode(Operator.MUL);
/*  667 */             this.iseg.appendCode(Operator.ASSGN);
/*  668 */             break;
/*      */          case 41:
/*  670 */             this.iseg.appendCode(Operator.COPY);
/*  671 */             this.iseg.appendCode(Operator.LOAD);
/*  672 */             this.token = this.lexer.nextToken();
/*  673 */             this.parseExpression();
/*  674 */             this.iseg.appendCode(Operator.DIV);
/*  675 */             this.iseg.appendCode(Operator.ASSGN);
/*  676 */             break;
/*      */          case 42:
/*  678 */             this.iseg.appendCode(Operator.COPY);
/*  679 */             this.iseg.appendCode(Operator.LOAD);
/*  680 */             this.token = this.lexer.nextToken();
/*  681 */             this.parseExpression();
/*  682 */             this.iseg.appendCode(Operator.MOD);
/*  683 */             this.iseg.appendCode(Operator.ASSGN);
/*      */          }
/*      */       }
/*      */
/*  687 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseExp() {
/*  695 */       boolean isLeftValue = this.parseLogicalTerm();
/*  696 */       if (this.token.checkSymbol(Symbol.OR)) {
/*  697 */          isLeftValue = false;
/*  698 */          this.token = this.lexer.nextToken();
/*  699 */          this.parseExp();
/*  700 */          this.iseg.appendCode(Operator.OR);
/*      */       }
/*  702 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseLogicalTerm() {
/*  711 */       boolean isLeftValue = this.parseLogicalFactor();
/*  712 */       if (this.token.checkSymbol(Symbol.AND)) {
/*  713 */          isLeftValue = false;
/*  714 */          this.token = this.lexer.nextToken();
/*  715 */          this.parseLogicalTerm();
/*  716 */          this.iseg.appendCode(Operator.AND);
/*      */       }
/*  718 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseLogicalFactor() {
/*  726 */       boolean isLeftValue = this.parseArithmeticExpression();
/*  727 */       if (this.token.checkSymbol(Symbol.EQUAL) || this.token.checkSymbol(Symbol.NOTEQ) || this.token.checkSymbol(Symbol.LESS) || this.token.checkSymbol(Symbol.LESSEQ) || this.token.checkSymbol(Symbol.GREAT) || this.token.checkSymbol(Symbol.GREATEQ)) {
/*      */
/*      */
/*      */
/*      */
/*  732 */          isLeftValue = false;
/*      */          int addressCOMP;
/*  734 */          switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */          case 23:
/*  736 */             this.token = this.lexer.nextToken();
/*  737 */             this.parseArithmeticExpression();
/*  738 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  739 */             this.iseg.appendCode(Operator.BNE, addressCOMP + 4);
/*  740 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  741 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  742 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*      */
/*  744 */             break;
/*      */          case 24:
/*  746 */             this.token = this.lexer.nextToken();
/*  747 */             this.parseArithmeticExpression();
/*  748 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  749 */             this.iseg.appendCode(Operator.BEQ, addressCOMP + 4);
/*  750 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  751 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  752 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*      */
/*  754 */             break;
/*      */          case 25:
/*  756 */             this.token = this.lexer.nextToken();
/*  757 */             this.parseArithmeticExpression();
/*  758 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  759 */             this.iseg.appendCode(Operator.BGE, addressCOMP + 4);
/*  760 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  761 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  762 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*  763 */             break;
/*      */          case 27:
/*  765 */             this.token = this.lexer.nextToken();
/*  766 */             this.parseArithmeticExpression();
/*  767 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  768 */             this.iseg.appendCode(Operator.BGT, addressCOMP + 4);
/*  769 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  770 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  771 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*  772 */             break;
/*      */          case 26:
/*  774 */             this.token = this.lexer.nextToken();
/*  775 */             this.parseArithmeticExpression();
/*  776 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  777 */             this.iseg.appendCode(Operator.BLE, addressCOMP + 4);
/*  778 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  779 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  780 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*  781 */             break;
/*      */          case 28:
/*  783 */             this.token = this.lexer.nextToken();
/*  784 */             this.parseArithmeticExpression();
/*  785 */             addressCOMP = this.iseg.appendCode(Operator.COMP);
/*  786 */             this.iseg.appendCode(Operator.BLT, addressCOMP + 4);
/*  787 */             this.iseg.appendCode(Operator.PUSHI, 1);
/*  788 */             this.iseg.appendCode(Operator.JUMP, addressCOMP + 5);
/*  789 */             this.iseg.appendCode(Operator.PUSHI, 0);
/*      */          }
/*      */       }
/*      */
/*  793 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseArithmeticExpression() {
/*  801 */       boolean isLeftValue = this.parseArithmeticTerm();
/*  802 */       while(this.token.checkSymbol(Symbol.ADD) || this.token.checkSymbol(Symbol.SUB)) {
/*  803 */          isLeftValue = false;
/*  804 */          Symbol operator = this.token.getSymbol();
/*  805 */          this.token = this.lexer.nextToken();
/*  806 */          this.parseArithmeticTerm();
/*  807 */          if (operator == Symbol.ADD) {
/*  808 */             this.iseg.appendCode(Operator.ADD);
/*      */          } else {
/*  810 */             this.iseg.appendCode(Operator.SUB);
/*      */          }      }
/*  812 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseArithmeticTerm() {
/*  820 */       boolean isLeftValue = this.parseArithmeticFactor();
/*  821 */       while(this.token.checkSymbol(Symbol.MUL) || this.token.checkSymbol(Symbol.DIV) || this.token.checkSymbol(Symbol.MOD)) {
/*      */
/*  823 */          isLeftValue = false;
/*  824 */          Symbol operator = this.token.getSymbol();
/*  825 */          this.token = this.lexer.nextToken();
/*  826 */          this.parseArithmeticFactor();
/*  827 */          if (operator == Symbol.MUL) {
/*  828 */             this.iseg.appendCode(Operator.MUL);
/*  829 */          } else if (operator == Symbol.DIV) {
/*  830 */             this.iseg.appendCode(Operator.DIV);
/*      */          } else {
/*  832 */             this.iseg.appendCode(Operator.MOD);
/*      */          }      }
/*  834 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseArithmeticFactor() {
/*      */       boolean isLeftValue;
/*  843 */       switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */          break;      case 33:
/*  845 */          isLeftValue = false;
/*  846 */          this.token = this.lexer.nextToken();
/*  847 */          this.parseArithmeticFactor();
/*  848 */          this.iseg.appendCode(Operator.CSIGN);
/*      */
/*      */       case 31:
/*  851 */          isLeftValue = false;
/*  852 */          this.token = this.lexer.nextToken();
/*  853 */          this.parseArithmeticFactor();
/*  854 */          this.iseg.appendCode(Operator.NOT);
/*  855 */          break;
/*      */       case 32:      default:
/*  857 */          isLeftValue = this.parseUnsignedFactor();
/*      */       }
/*  859 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean parseUnsignedFactor() {
/*      */       boolean isLeftValue;
/*      */       String name;
/*      */       int address;
/*  871 */       switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */       case 55:
/*  873 */          name = this.token.getStrValue();
/*  874 */          if (!this.variableTable.exist(name)) {
/*  875 */             this.syntaxError(name + " は宣言されていません");         }
/*  876 */          address = this.variableTable.getAddress(name);
/*  877 */          this.token = this.lexer.nextToken();
/*  878 */          if (this.token.checkSymbol(Symbol.LBRACKET)) {
/*  879 */             if (!this.variableTable.checkType(name, Type.ARRAYOFINT)) {
/*  880 */                this.syntaxError(name + " の宣言と型が一致しません");            }
/*  881 */             this.iseg.appendCode(Operator.PUSHI, address);
/*  882 */             this.token = this.lexer.nextToken();
/*  883 */             if (this.isExpFirst()) {
/*  884 */                this.parseExpression();            } else {
/*  885 */                this.syntaxError("式が期待されます");            }
/*  886 */             if (this.token.checkSymbol(Symbol.RBRACKET)) {
/*  887 */                this.iseg.appendCode(Operator.ADD);
/*  888 */                this.token = this.lexer.nextToken();            } else {
/*  889 */                this.syntaxError("']' が期待されます");
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */             }
/*      */
/*  905 */             if (!this.token.checkSymbol(Symbol.ASSIGN) && !this.token.checkSymbol(Symbol.ASSIGNADD) && !this.token.checkSymbol(Symbol.ASSIGNSUB) && !this.token.checkSymbol(Symbol.ASSIGNMUL) && !this.token.checkSymbol(Symbol.ASSIGNDIV) && !this.token.checkSymbol(Symbol.ASSIGNMOD)) {
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */             } else {
/*  912 */                isLeftValue = true;
/*      */
/*  914 */                this.iseg.appendCode(Operator.LOAD);
/*  915 */                isLeftValue = false;
/*      */             }
/*      */          } else {
/*  918 */             if (!this.variableTable.checkType(name, Type.INT)) {
/*  919 */                this.syntaxError(name + " の宣言と型が一致しません");            }
/*  920 */             if (!this.token.checkSymbol(Symbol.INC) && !this.token.checkSymbol(Symbol.DEC)) {
/*      */                }            } else {
/*  922 */                isLeftValue = false;
/*  923 */                this.iseg.appendCode(Operator.PUSH, address);
/*  924 */                this.iseg.appendCode(Operator.PUSHI, address);
/*  925 */                this.iseg.appendCode(Operator.PUSH, address);
/*  926 */                if (this.token.checkSymbol(Symbol.INC)) {
/*  927 */                   this.iseg.appendCode(Operator.INC);               } else {
/*  928 */                   this.iseg.appendCode(Operator.DEC);               }
/*  929 */                this.iseg.appendCode(Operator.ASSGN);
/*  930 */                this.iseg.appendCode(Operator.REMOVE);
/*  931 */                this.token = this.lexer.nextToken();
/*  932 */                if (!this.token.checkSymbol(Symbol.ASSIGN) && !this.token.checkSymbol(Symbol.ASSIGNADD) && !this.token.checkSymbol(Symbol.ASSIGNSUB) && !this.token.checkSymbol(Symbol.ASSIGNMUL) && !this.token.checkSymbol(Symbol.ASSIGNDIV) && !this.token.checkSymbol(Symbol.ASSIGNMOD)) {
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */                } else {
/*  939 */                   this.iseg.appendCode(Operator.PUSHI, address);
/*  940 */                   isLeftValue = true;
/*      */
/*  942 */                   this.iseg.appendCode(Operator.PUSH, address);
/*  943 */                   isLeftValue = false;
/*      */
/*      */
/*      */       case 43:
/*      */       case 44:
/*      */          boolean isINC;
/*  949 */          if (this.token.checkSymbol(Symbol.INC)) {            isINC = true;         } else {            isINC = false;         }
/*  950 */          isLeftValue = false;
/*  951 */          this.token = this.lexer.nextToken();
/*  952 */          if (this.token.checkSymbol(Symbol.NAME)) {
/*  953 */             name = this.token.getStrValue();
/*  954 */             if (!this.variableTable.exist(name)) {
/*  955 */                this.syntaxError(name + " は宣言されていません");            }
/*  956 */             this.token = this.lexer.nextToken();
/*  957 */             address = this.variableTable.getAddress(name);
/*  958 */             this.iseg.appendCode(Operator.PUSHI, address);
/*  959 */             if (this.token.checkSymbol(Symbol.LBRACKET)) {
/*  960 */                if (!this.variableTable.checkType(name, Type.ARRAYOFINT)) {
/*  961 */                   this.syntaxError(name + " の宣言と型が一致しません");               }
/*  962 */                this.token = this.lexer.nextToken();
/*  963 */                if (this.isExpFirst()) {
/*  964 */                   this.parseExpression();               } else {
/*  965 */                   this.syntaxError("式が期待されます");               }
/*  966 */                if (this.token.checkSymbol(Symbol.RBRACKET)) {
/*  967 */                   this.iseg.appendCode(Operator.ADD);
/*  968 */                   this.token = this.lexer.nextToken();               } else {
/*  969 */                   this.syntaxError("']' が期待されます");               }
/*  970 */                this.iseg.appendCode(Operator.COPY);
/*  971 */                this.iseg.appendCode(Operator.LOAD);
/*  972 */                if (isINC) {                  this.iseg.appendCode(Operator.INC);               } else {
/*  973 */                   this.iseg.appendCode(Operator.DEC);               }
/*  974 */                this.iseg.appendCode(Operator.ASSGN);
/*      */             } else {
/*  976 */                if (!this.variableTable.checkType(name, Type.INT)) {
/*  977 */                   this.syntaxError(name + " の宣言と型が一致しません");               }
/*  978 */                this.iseg.appendCode(Operator.PUSH, address);
/*  979 */                if (isINC) {                  this.iseg.appendCode(Operator.INC);               } else {
/*  980 */                   this.iseg.appendCode(Operator.DEC);               }
/*  981 */                this.iseg.appendCode(Operator.ASSGN);
/*      */             }         } else {
/*  983 */             this.syntaxError("変数が期待されます");
/*      */          }         break;
/*      */       case 53:
/*      */       case 54:
/*  987 */          isLeftValue = false;
/*  988 */          this.iseg.appendCode(Operator.PUSHI, this.token.getIntValue());         }
/*  989 */          this.token = this.lexer.nextToken();         break;
/*  990 */          break;
/*      */       case 46:
/*  992 */          isLeftValue = false;
/*  993 */          this.token = this.lexer.nextToken();
/*  994 */          this.parseExpression();
/*  995 */          if (this.token.checkSymbol(Symbol.RPAREN)) {
/*  996 */             this.token = this.lexer.nextToken();
/*      */          } else {
/*  998 */             this.syntaxError("')' が期待されます");
/*      */
/*      */       case 7:
/* 1001 */          isLeftValue = false;
/* 1002 */          this.iseg.appendCode(Operator.INPUTC);
/* 1003 */          this.token = this.lexer.nextToken();
/* 1004 */          break;
/*      */       case 6:
/* 1006 */          isLeftValue = false;
/* 1007 */          this.iseg.appendCode(Operator.INPUT);
/* 1008 */          this.token = this.lexer.nextToken();
/* 1009 */          break;
/*      */       case 21:
/* 1011 */          isLeftValue = false;
/* 1012 */          this.iseg.appendCode(Operator.PUSHI, 1);
/* 1013 */          this.token = this.lexer.nextToken();
/* 1014 */          break;
/*      */       case 22:
/* 1016 */          isLeftValue = false;
/* 1017 */          this.iseg.appendCode(Operator.PUSHI, 0);
/* 1018 */          this.token = this.lexer.nextToken();
/* 1019 */          break;
/*      */       case 32:
/* 1021 */          isLeftValue = false;
/* 1022 */          this.parseSumFunction();
/* 1023 */          break;
/*      */       case 34:
/* 1025 */          isLeftValue = false;
/* 1026 */          this.parseProductFunction();
/* 1027 */          break;
/*      */             }         }         break;      default:
/* 1029 */          isLeftValue = false;
/* 1030 */          this.syntaxError("符号無し因子が期待されます");
/*      */       }
/* 1032 */       return isLeftValue;
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseSumFunction() {
/* 1039 */       this.token = this.lexer.nextToken();
/* 1040 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/* 1041 */          this.token = this.lexer.nextToken();      } else {
/* 1042 */          this.syntaxError("'(' が期待されます");      }
/* 1043 */       if (this.isExpFirst()) {
/* 1044 */          this.parseExpression();      } else {
/* 1045 */          this.syntaxError("式が期待されます");      }
/* 1046 */       for(; this.token.checkSymbol(Symbol.COMMA); this.iseg.appendCode(Operator.ADD)) {
/* 1047 */          this.token = this.lexer.nextToken();
/* 1048 */          if (this.isExpFirst()) {
/* 1049 */             this.parseExpression();         } else {
/* 1050 */             this.syntaxError("式が期待されます");
/*      */          }      }
/*      */
/* 1053 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/* 1054 */          this.token = this.lexer.nextToken();      } else {
/* 1055 */          this.syntaxError("'(' が期待されます");      }
/* 1056 */    }
/*      */
/*      */
/*      */
/*      */
/*      */    private void parseProductFunction() {
/* 1062 */       this.token = this.lexer.nextToken();
/* 1063 */       if (this.token.checkSymbol(Symbol.LPAREN)) {
/* 1064 */          this.token = this.lexer.nextToken();      } else {
/* 1065 */          this.syntaxError("'(' が期待されます");      }
/* 1066 */       if (this.isExpFirst()) {
/* 1067 */          this.parseExpression();      } else {
/* 1068 */          this.syntaxError("式が期待されます");      }
/* 1069 */       for(; this.token.checkSymbol(Symbol.COMMA); this.iseg.appendCode(Operator.MUL)) {
/* 1070 */          this.token = this.lexer.nextToken();
/* 1071 */          if (this.isExpFirst()) {
/* 1072 */             this.parseExpression();         } else {
/* 1073 */             this.syntaxError("式が期待されます");
/*      */          }      }
/*      */
/* 1076 */       if (this.token.checkSymbol(Symbol.RPAREN)) {
/* 1077 */          this.token = this.lexer.nextToken();      } else {
/* 1078 */          this.syntaxError("'(' が期待されます");      }
/* 1079 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private boolean isExpFirst() {
/* 1086 */       switch($SWITCH_TABLE$kc$Symbol()[this.token.getSymbol().ordinal()]) {
/*      */       case 6:
/*      */       case 7:
/*      */       case 21:
/*      */       case 22:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 43:
/*      */       case 44:
/*      */       case 46:
/*      */       case 53:
/*      */       case 54:
/*      */       case 55:
/* 1101 */          return true;
/*      */       default:
/* 1103 */          return false;
/*      */       }
/*      */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    void closeFile() {
/* 1112 */       this.lexer.closeFile();
/* 1113 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    void dump2file() {
/* 1120 */       this.iseg.dump2file();
/* 1121 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    void dump2file(String fileName) {
/* 1129 */       this.iseg.dump2file(fileName);
/* 1130 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    private void syntaxError(String message) {
/* 1137 */       System.out.print(this.lexer.analyzeAt());
/* 1138 */       System.out.println("で構文解析プログラムが構文エラーを検出");
/* 1139 */       System.out.println(message);
/* 1140 */       this.closeFile();
/* 1141 */       System.exit(0);
/* 1142 */    }
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */    public static void main(String[] args) {
/* 1151 */       if (args.length == 0) {
/* 1152 */          System.out.println("Usage: java kc.Kc20 file [objectfile]");
/* 1153 */          System.exit(0);
/*      */       }
/*      */
/* 1156 */       Kc20 parser = new Kc20(args[0]);
/*      */
/* 1158 */       parser.parseProgram();
/* 1159 */       parser.closeFile();
/*      */
/* 1161 */       if (args.length == 1) {
/* 1162 */          parser.dump2file();
/*      */       } else {
/* 1164 */          parser.dump2file(args[1]);      }
/* 1165 */    }
/*      */ }

/*
	DECOMPILATION REPORT

	Decompiled from: /Users/rikumiura/Documents/ProjI20/material/kc/Kc20.class
	Total time: 682 ms

	Decompiled with FernFlower.
*/