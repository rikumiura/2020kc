package kc;

import java.util.ArrayList;

/**
 * このクラスではVarのオブジェクトの管理を行う．
 * 全てのVar
 * オブジェクトをリストに補完し、オブジェクトの追加・名前検索などができるようにした．
 * @author 1810370116 三浦陸
 * 提出日　2020/05/27
 */
class VarTable {

	private ArrayList<Var> varList;
	private int nextAddress;//次に登録される変数のアドレス

    /**
     *　コンストラクタ
     *　Varオブジェクトを保持するArrayList　varListを初期化
     *　次に登録される変数のアドレスの初期値として0を代入
     */
    VarTable() {
    	this.varList = new ArrayList<Var>();
    	this.nextAddress = 0;
    }

    /**
     *引数nameを変数名としたVarオブジェクトが存在するか確認
     *存在する場合そのオブジェクトを返す
     *なければnullを返す
     */
    private Var getVar(String name) {
    	for(Var search: this.varList) {//リスト内の全てのオブジェクトで調べる
    		if(search.getName().equals(name)) {//引数nameとオブジェクトのnameが同じか判定
    			return search;//同じならばそのオブジェクトを返して終了
    		}
    	}
    	return null;//見つからなければnullを返す
    }

    /**
     *引数nameを変数名としたVarオブジェクトが存在するか確認
     *存在する場合はtrueしない場合はfalse
     */
    public boolean exist(String name) {
    	for(Var search: this.varList) {//リスト内の全てのオブジェクトで調べる
    		if(search.getName().equals(name)) {//引数nameとオブジェクトのnameが同じか判定
    			return true;//同じならばtrueを返して終了
    		}
    	}
    	return false;//見つからなければfalse
    }

    /**
     *同名のVarオブジェクトが存在しなければ、
     *引数情報にフィールドnextAddressを追加したものでVarオブジェクトを作成．
     *フィールドvarListに作成したオブジェクトを追加
     *オブジェクトを作成できた場合はtrue
     *できなければfalse
     */
    public boolean registerNewVariable(Type type, String name, int size) {
    	if(exist(name))//同名のオブジェクトが存在しないかチェック
    		return false;//存在するなら終了
    	this.varList.add(new Var(type,name, this.nextAddress, size));//Varオブジェクトを作成してvarListに追加
    	return true;//作成できたのでtrueを返す
    }

    /**
     *　引数と同名のVarオブジェクトが保持するaddressのゲッター
     */
    public int getAddress(String name) {
    	return getVar(name).getAddress();//getVarで同名のオブジェクトを取得．VarクラスのgetAddressメソッドを利用してアドレスを返す．
    }

    /**
     *引数と同名のVarオブジェクトが保持するgetVarのゲッター
     */
    public Type getType(String name) {
    	return getVar(name).getType();//getVarで同名のオブジェクトを取得．VarクラスのgetTypeメソッドを利用してtypeを返す
    }

    /**
     *引数で与えられた名前とTypeを持つVarオブジェクトが存在するかチェック
     *存在すればtrueしなければfalse
     */
    public boolean checkType(String name, Type type) {
    	if(getType(name).equals(type))return true;//引数nameを元にgetTypeメソッドで取得したTypeと引数のtypeが等しければtrueを返す．
    	else return false;//違うければfalse
    }

    /**
     *	引数と同名のVarオブジェクのサイズのゲッター
     */
    public int getSize(String name) {
    	return getVar(name).getSize();//getVarで同名のオブジェクトを取得．VarクラスのgetSizeメソッドを利用してサイズを返す
    }


    /**
     * 動作確認用のメインメソッド
     * int型変数およびint型配列を表に登録し、その後登録された変数を表示する
     */
    public static void main(String[] args) {
    	VarTable varTable = new VarTable();
    	for(int i = 0; i < 4; i++) {
    		varTable.registerNewVariable(Type.INT, "var"+String.valueOf(i), 1);
    	}
    	varTable.registerNewVariable(Type.ARRAYOFINT, "var4", 10);

    	for(int i = 0; i < 5; i++) {
    		if(varTable.checkType("var"+String.valueOf(i), Type.INT)) {
    			System.out.println("Type    : "+ varTable.getType("var"+String.valueOf(i)));
    			System.out.println("Address : "+ varTable.getAddress("var"+String.valueOf(i)));
    		}else if(varTable.checkType("var"+String.valueOf(i), Type.ARRAYOFINT)){
    			System.out.println("Type    : "+ varTable.getType("var"+String.valueOf(i)));
    			System.out.println("Address : "+ varTable.getAddress("var"+String.valueOf(i)));
    		}
    	}
    }
}