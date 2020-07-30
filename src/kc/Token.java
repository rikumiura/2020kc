package kc;

/**
 * このクラスはFileScanner.javaが切り分けた文字（文字列）を用いてトークンごとに分ける
 * @author 三浦　陸
 *
 */
class Token {
	private Symbol symbol;//そのトークンの種別を表す.Symbol は enum 型のクラス
	private int intValue;//トークンの種別が整数 (INTEGER) または文字 (CHARACTER) であるとき，その整数値あるいは文字コードを保持する.
	private String strValue;//トークンの種別が名前 (NAME) または文字列 (STRING) であるとき，それを表す文字列を保持する.


    /**
     * 整数，文字，名前以外のトークンを生成するための，トークンの種別のみを 引数とするコンストラクタ.
     */
    public Token(Symbol symbol) {
    	this.symbol = symbol;
    }

    /**
     * 整数，文字のトークンを生成するための， トークンの種別と 値 (整数値もしくは文字コード) を引数とするコンストラクタ.
     */
    public Token(Symbol symbol, int intValue) {
    	this.symbol = symbol;
    	this.intValue = intValue;
    }


    /**
     * 名前，文字列のトークンを生成するための，トークンの種 別と文字列を引数とするコンストラクタ.
     */
    public Token(Symbol symbol, String strvalue) {
    	this.symbol = symbol;
    	this.strValue = strvalue;
    }


    /**
     * symbol フィールドが，引数 symbolType とトークン 種別と一致するかどうかを調べる.
     */
    public boolean checkSymbol(Symbol symbol) {
    	if(this.symbol.equals(symbol))
    		return true;
    	else
    		return false;
    }

    /**
     * symbol フィールドのゲッター
     */
    public  Symbol getSymbol() {
    	return this.symbol;//kakinaosi
    }


    /**
     * IntValue フィールドのゲッター
     */
    public int getIntValue() {
    	return this.intValue;
    }

    /**
     * strValue フィールドのゲッター
     */
    public String getStrValue() {
    	return this.strValue;
    }
}