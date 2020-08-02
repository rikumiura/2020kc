package kc;

/**
 *簡易の字句解析プログラム
 */
class SLexicalAnalyzer {
    private FileScanner sourceFileScanner; // 入力ファイルのFileScannerへの参照

    /**
     *
     */
    SLexicalAnalyzer(String sourceFileName) {
        this.sourceFileScanner = new FileScanner(sourceFileName);
    }

    /**
     * FileScanner.javaで読み込んだ文字列をトークンごとに
     * 分けていく．
     */
    public Token nextToken() {
    	Token ret = null;
    	char currentChar = this.sourceFileScanner.nextChar();
    	while(currentChar == '\n' || currentChar == '\t' || currentChar == ' ')
    		currentChar = this.sourceFileScanner.nextChar();
    	int intValue = 0;
    	if(currentChar == '0') {
    		ret = new Token(Symbol.INTEGER, 0);
    	}else if(Character.isDigit(currentChar)) {
    		intValue += Character.digit(currentChar, 10);
    		while(Character.isDigit(this.sourceFileScanner.lookAhead())){
    			intValue *= 10;
    			currentChar = this.sourceFileScanner.nextChar();
    			intValue += Character.digit(currentChar, 10);
    		}
    		ret = new Token(Symbol.INTEGER, intValue);

    	}else if(currentChar == '+') {
    		ret = new Token(Symbol.ADD);
    	}else if(currentChar == '!') {
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			currentChar = this.sourceFileScanner.nextChar();
    			ret = new Token(Symbol.NOTEQ);
    		}else {
    			ret = new Token(Symbol.NOT);
    		}
    	}else if(currentChar == '=') {
    		if(this.sourceFileScanner.lookAhead() == '=') {
    			currentChar = this.sourceFileScanner.nextChar();
    			ret = new Token(Symbol.EQUAL);
    		}else {
    			ret = new Token(Symbol.ASSIGN);
    		}
    	}else if(currentChar == 0) {
    		ret = new Token(Symbol.EOF);
    	}else {
    		//失敗の時（語彙力
    		this.syntaxError();
    	}
    	return ret;
    }

    /**
     *ファイルを閉じる
     */
    public void closeFile() {
    	sourceFileScanner.closeFile();
    }

    /**
     *エラー表示に関わる部分
     */
    void syntaxError() {
        System.out.print (sourceFileScanner.scanAt());
        //下記の文言は自動採点で使用するので変更しないでください。
        System.out.println ("で字句解析プログラムが構文エラーを検出");
        closeFile();
        System.exit(1);
    }
}