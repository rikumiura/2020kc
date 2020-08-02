package kc;

/**
 * ここでは4つのフィールド変数
 * type(Type型),name(String型),address(int型),size(int型)
 * とそれぞれの変数のゲッターを持っている．
 * @author 1810370116 三浦陸
 * 提出日　2020/05/27
 */
class Var {
	private Type type;//Type は enum 型のクラスであり，Type.INT, Type.ARRAYOFINT, Type.NULL の いずれかの値を持つ
	private String name;//変数
	private int adderss;//Dsegg上のアドレス
	private int size;//配列の場合、そのサイズ


    /**
     * コンストラクタ
     * 引数でフィールドを初期化
     */
    Var(Type type, String name, int address, int size) {
    	this.type = type;
    	this.name = name;
    	this.adderss = address;
    	this.size = size;
    }

    /**
     * フィールドtypeのゲッター
     */
    Type getType() {
    	return this.type;
    }

    /**
     * フィールドnameのゲッター
     */
   String getName() {
    	return this.name;
    }

    /**
     *フィールドａｄｄｒｅｓｓのゲッター
     */
    int getAddress() {
    	return this.adderss;
    }

    /**
     *フィールドsizeのゲッター
     */
    int getSize() {
    	return this.size;
    }
}