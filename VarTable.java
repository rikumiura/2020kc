package kc;

import java.util.ArrayList;

/**
 * 変数を管理するクラス
 */
public class VarTable {
    private ArrayList<Var> varList;  // 変数表
    private int nextAddress;         // 次の変数のDseg上の番地

    /**
     * 変数表VarListを初期化し，最初の変数の番地は0とする
     */
    VarTable() {
        varList = new ArrayList<Var>();
        nextAddress = 0;
    }

    /**
     * 引数で指定された名前の変数を取り出す
     * @param name 変数名
     * @return  変数(Varクラスのインスタンス)
     *          無ければnullを返す
     */
    private Var getVar (String name) {
        for (Var var: varList) {
            if (name.equals (var.getName())) {
                return var;
            }
        }
        return null;  // 見つからなければ null を返す
    }

    /**
     * 引数で指定された名前の変数が存在するかどうかをチェックする
     * @param name 変数名
     * @return 存在するかどうかを表す真理値
     */
    boolean exist (String name) {
        Var var = getVar (name);
        return (var != null);
    }

    /**
     * 変数の追加
     * @param type 変数の型
     * @param name 変数名
     * @param size 配列サイズ（スカラー変数のサイズは1）
     * @return 追加に成功したかどうかを表す真理値
     */
    boolean registerNewVariable (Type type, String name, int size) {
        if (!exist (name)) {
            // name という名前を持つ変数が表になければ，変数を表に追加
            varList.add (new Var (type,name, nextAddress, size));   // 追加
            nextAddress += size;    // 次の変数のアドレスはサイズ分だけ増やす
            return true;            // 追加成功を返す
        } else {
            // 既に name という名前の変数が表にあれば追加失敗
            return false;
        }
    }

    /**
     * 引数で指定された名前の変数のアドレスを求める
     * @param name 変数名
     * @return 変数が存在すればそのアドレス，無ければ-1
     */
    int getAddress (String name) {
        Var var = getVar (name);
        if (var != null) return var.getAddress();
        else return -1;
    }

    /**
     * 引数で指定された名前の変数の型を求める
     * @param name 変数名
     * @return 変数が存在すればその型，無ければType.NULL
     */
    Type getType (String name) {
        Var var = getVar (name);
        if (var != null) return var.getType();
        else return Type.NULL;
    }

    /**
     * 引数で指定された名前の変数の型が引数で指定したものと一致するかを調べる
     * @param name 変数名
     * @param type 型
     * @return 一致するかどうかを表す真理値
     */
    boolean checkType (String name, Type type) {
        Var var = getVar (name);
        return (type.equals (var.getType()));
    }

    /**
     * 引数で指定された名前の変数のサイズを求める
     * @param name 変数名
     * @return 変数が存在すればそのサイズ，無ければ-1
     */
    int getSize (String name) {
        Var var;
        if ((var= getVar (name)) != null) return var.getSize();
        else return -1;
    }

    /**
     * 動作確認用のメインメソッド
     * int型変数およびint型配列を表に登録し、その後登録された変数を表示する
     */
    public static void main (String[] args) {
        VarTable varTable = new VarTable();

        // int型変数 var0, ..., var3 を登録
        for (int i=0; i< 4; ++i)
            varTable.registerNewVariable (Type.INT, "var"+i, 1);
        // int型配列 var4[10] を登録
		varTable.registerNewVariable (Type.ARRAYOFINT, "var4", 10);

        // 登録されている変数の出力
        for (int i=0; i<5; ++i) {
            String name = "var" + i;
            if (varTable.checkType (name, Type.INT)) {              // 登録されている変数がint型の場合
	            System.out.printf ("変数%sの型は%s，番地は%d，サイズは%dです．\n",
                    name, varTable.getType (name), varTable.getAddress (name), varTable.getSize (name));
            } else if (varTable.checkType (name, Type.ARRAYOFINT)) { // 登録されている変数がint型配列の場合
	            System.out.printf ("変数%sの型は%s，番地は%d，サイズは%dです．\n",
                    name, varTable.getType (name), varTable.getAddress (name), varTable.getSize (name));
            }
        }
    }
}
