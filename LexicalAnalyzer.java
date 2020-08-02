package kc;

/**
 * 字句解析プログラム
 */
class LexicalAnalyzer {
	private FileScanner sourceFileScanner;//ソースファイルに対するスキャナー

    /**
     * ファイル名を引数とするコンストラクタ.
     * sourceFileName という名前のファイルのためのファイルスキャナを生成し,
     * sourceFileScannerにより参照する.
     */
    public LexicalAnalyzer(String sourceFileName) {
        this.sourceFileScanner = new FileScanner(sourceFileName);
    }

    /** トークンが切り出せたら，Token クラスのインスタンスを生成し，返り値とする.
     *  ファイル末に達している場合は EOF を返す.
     *   トークンが切り出せなかったらメソッド syntaxError を呼び出す
     */
    public Token nextToken() {
    	Token retToken = null;
    	char currentChar = this.sourceFileScanner.nextChar();
    	while(currentChar == '\n' || currentChar == '\t' || currentChar == ' ')//空白切り出し
    		currentChar = this.sourceFileScanner.nextChar();
    	int intValue = 0;//数字を覚えてる
    	if(currentChar == 0)
    		return new Token(Symbol.EOF);
    	else if(Character.isDigit(currentChar)) {
    		/*INTEGERトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == 'x' && currentChar =='0') {
    			/*16進数の場合*/
    			currentChar = this.sourceFileScanner.nextChar();
    			if(Character.digit(this.sourceFileScanner.lookAhead(), 16) != -1) {
	    			while(Character.digit(this.sourceFileScanner.lookAhead(), 16) != -1) {
	    				//数字または大文字・小文字a~fの場合のみ数字として認識
	    				currentChar = this.sourceFileScanner.nextChar();
	    				intValue *= 16;
	    				intValue += Character.digit(currentChar, 16);
	    			}
    			}else
    				this.syntaxError();
    		}else {
    			/*10進数の場合*/
    			intValue += Character.digit(currentChar, 10);
    			if(currentChar != '0') {
	    			while(Character.isDigit(this.sourceFileScanner.lookAhead())) {
	    				/*数字のみ続ける*/
	    				currentChar = this.sourceFileScanner.nextChar();
	    				intValue *= 10;
	    				intValue += Character.digit(currentChar, 10);
	    			}
    			}
    		}
    		retToken = new Token(Symbol.INTEGER, intValue);
    	}else if(currentChar == '+') {
    		/*ADDトークンまたはINCトークン,ASSIGNADDトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '+') {
    			/*
    			 *　INCトークンの場合
    			 */
    			retToken = new Token(Symbol.INC);
    			currentChar = this.sourceFileScanner.nextChar();
    		}else if(this.sourceFileScanner.lookAhead() == '=') {
    			/*ASSIGNADDトークンの作成*/
    			retToken = new Token(Symbol.ASSIGNADD);
    			currentChar = this.sourceFileScanner.nextChar();
    		}else {
    			/*ADDトークンの作成*/
    			retToken = new Token(Symbol.ADD);
    		}
    	}else if(currentChar == '-') {
    		/*SUBトークンまたはASSIGNSUBトークン、DECトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '-') {
    			/*ASSIGNSUBトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.DEC);
    		}else if(this.sourceFileScanner.lookAhead() == '=') {
    			/*DECトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.ASSIGNSUB);
    		}else {
    			/*SUBトークンの作成*/
    			retToken = new Token(Symbol.SUB);
    		}
    	}else if(currentChar == '%') {
    		/*MODトークンまたはASSIGNMODトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*ASSIGNMODトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.ASSIGNMOD);
    		}else
    			/*
    			 *MODトークンの作成
    			 */
    			retToken = new Token(Symbol.MOD);
    	}else if(currentChar == '*') {
    		/*ASSIGNMULまたはMULトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*ASSINGNMULトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.ASSIGNMUL);
    		}else
    			/*MULトークンの作成*/
    			retToken = new Token(Symbol.MUL);
    	}else if (currentChar == '/') {
    		/*ASSINGDIVまたはDIVトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*ASSIGNDIVトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.ASSIGNDIV);
    		}else
    			/*DIVトークンの作成*/
    			retToken = new Token(Symbol.DIV);
    	}else if(currentChar == '!') {
    		/*NOTEQまたはNOTトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*NOTEQトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.NOTEQ);
    		}else {
    			/*NOTトークンの作成*/
    			retToken = new Token(Symbol.NOT);
    		}
    	}else if(currentChar == '=') {
    		/*EQUALまたはASSINGNトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*EQUALトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.EQUAL);
    		}else {
    			/*ASSINトークンの作成*/
    			retToken = new Token(Symbol.ASSIGN);
    		}
    	}else if(currentChar == '<') {
    		/*LESSEQまたはLESSの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*LESSEQトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.LESSEQ);
    		}else
    			/*LESSトークンの作成*/
    			retToken = new Token(Symbol.LESS);
    	}else if(currentChar == '>') {
    		/*GREATまたはGREATEQトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			/*GREATEQトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.GREATEQ);
    		}else
    			/*GREATトークンの作成*/
    			retToken = new Token(Symbol.GREAT);
    	}else if(currentChar == '&') {
    		/*ANDトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '&') {
    			/*ANDトークンの作成*/
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.AND);
    		}else
    			/*シンタックスエラー*/
    			this.syntaxError();
    	}else if(currentChar == '|') {
    		/*ORトークンの作成*/
    		if(this.sourceFileScanner.lookAhead() == '|') {
    			currentChar = this.sourceFileScanner.nextChar();
    			retToken = new Token(Symbol.OR);
    		}else
    			/*シンタックスエラー*/
    			this.syntaxError();
    	}else if(currentChar == ';') {
    		retToken = new Token(Symbol.SEMICOLON);
    	}else if(currentChar == '(') {
    		retToken = new Token(Symbol.LPAREN);
    	}else if(currentChar == ')') {
    		retToken = new Token(Symbol.RPAREN);
    	}else if(currentChar == '{') {
    		retToken = new Token(Symbol.LBRACE);
    	}else if(currentChar == '}') {
    		retToken = new Token(Symbol.RBRACE);
    	}else if(currentChar == '[') {
    		retToken = new Token(Symbol.LBRACKET);
    	}else if(currentChar == ']') {
    		retToken = new Token(Symbol.RBRACKET);
    	}else if(currentChar == ',') {
    		retToken = new Token(Symbol.COMMA);
    	}else if(currentChar == '\'') {
    		/*CHARACTERトークンの作成*/
    		currentChar = this.sourceFileScanner.nextChar();
    		/*文字列の切り出し*/
    		String S = this.wordCut(currentChar);
    		currentChar = this.sourceFileScanner.nextChar();
    		/*
    		 * 文字列の大きさが1かつ次の文字が'である時のみCHARACTERトークンの作成
    		 */
    		if(S.length() == 1 && currentChar == '\'') {
    			retToken = new Token(Symbol.CHARACTER, S.charAt(0));
    		}else {
    			this.syntaxError();
    		}
    		/*文字列の切り出し*/
    	}else if(currentChar == '"') {
    		currentChar = this.sourceFileScanner.nextChar();
    		String S = this.wordCut(currentChar);
    		currentChar = this.sourceFileScanner.nextChar();
    		/* ”で閉められている時のみ文字列と判断 */
    		if(currentChar =='"') {
    			retToken = new Token(Symbol.STRING,S);
//    			currentChar = this.sourceFileScanner.nextChar();
    		}else {
    			this.syntaxError();
    		}
    	}else {
    		/*文字列切り出し*/
    		String S = wordCut(currentChar);
    		if(S.equals("main"))
    			retToken = new Token(Symbol.MAIN);
    		else if(S.equals("null"))
    			retToken = new Token(Symbol.NULL);
    		else if(S.equals("if"))
    			retToken = new Token(Symbol.IF);
    		else if(S.equals("while"))
    			retToken = new Token(Symbol.WHILE);
    		else if(S.equals("for")) {
    			retToken = new Token(Symbol.FOR);
    		}else if(S.equals("inputint"))
    			retToken = new Token(Symbol.INPUTINT);
    		else if(S.equals("inputchar"))
    			retToken = new Token(Symbol.INPUTCHAR);
    		else if(S.equals("outputint"))
    			retToken = new Token(Symbol.OUTPUTINT);
    		else if(S.equals("outputchar"))
    			retToken = new Token(Symbol.OUTPUTCHAR);
    		else if(S.equals("outputstr"))
    			retToken = new Token(Symbol.OUTPUTSTR);
    		else if(S.equals("setstr"))
    			retToken = new Token(Symbol.SETSTR);
    		else if(S.equals("else"))
    			retToken = new Token(Symbol.ELSE);
    		else if(S.equals("do"))
    			retToken = new Token(Symbol.DO);
    		else if(S.equals("switch"))
    			retToken = new Token(Symbol.SWITCH);
    		else if(S.equals("case"))
    			retToken = new Token(Symbol.CASE);
    		else if(S.equals("break"))
    			retToken = new Token(Symbol.BREAK);
    		else if(S.equals("continue"))
    			retToken = new Token(Symbol.CONTINUE);
    		else if(S.equals("int")) {
    			retToken = new Token(Symbol.INT);
    		}
    		else if(S.equals("char")) {
    			retToken = new Token(Symbol.CHAR);
    		}
    		else if(S.equals("boolean"))
    			retToken = new Token(Symbol.BOOLEAN);
    		else if(S.equals("ture"))
    			retToken = new Token(Symbol.TRUE);
    		else if(S.equals("false"))
    			retToken = new Token(Symbol.FALSE);
    		else if(S.length() == 1 && !charCheck(S.charAt(0))) {
    			this.syntaxError();
    		}else {
    			retToken = new Token(Symbol.NAME,S);
    		}
    	}

    	return retToken;
    }




    /*文字列の切り出し*/
    private String wordCut(char currentChar) {
    	String S="";
    	S += currentChar;
    	if(charCheck(currentChar)) {
	    	/*英数字または_からなる文字列を取り出し*/
	    	while(charCheck(this.sourceFileScanner.lookAhead())) {
				currentChar = this.sourceFileScanner.nextChar();
				S += currentChar;
			}
    	}
    	return S;
    }

    /*英数字または_判定*/
    private boolean charCheck(char c) {
    	if(Character.isLowerCase(c) || Character.isUpperCase(c) || Character.isDigit(c) || c == '_')
    		return true;
    	else return false;
    }

    /**
     * 読んでいるファイルを閉じる.
     */
    public void closeFile() {
    	sourceFileScanner.closeFile();
    }

    /**
     * 現在，入力ファイルのどの部分を解析中であるのかを表現する文字列を返す.
     * sourceFileScanner の scanAt に移譲している．
     */
    public String analyzeAt() {
    	return this.sourceFileScanner.scanAt();
    }

    /**
     * 字句解析時に構文エラーを検出したときに呼ばれるメソッド.
     * プログラム例 3 のとおりに作成すること.
     */
    private void syntaxError() {
        System.out.print (sourceFileScanner.scanAt());
        //下記の文言は自動採点で使用するので変更しないでください。
        System.out.println ("で字句解析プログラムが構文エラーを検出");
        closeFile();
        System.exit(1);
    }
}