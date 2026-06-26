package connect5plus;

import java.util.Objects;

/**
 * 1つの駒を表すクラス
 * @author 羽井出
 */
public class Token {
    /** 駒のタイプ。プレイヤーによって異なる値とする。 */
    private int type;

    public Token(){
        this(0);
    }

    /**
     * 指定されたタイプの駒のインスタンスを生成する
     * @param type 駒のタイプ
     */
    public Token(int type){
        this.type = type;
    }

    /**
     * 指定されたタイプに対応する文字列を返す
     * @param type 駒のタイプ
     * @return 駒の文字列
     */
    public static String typeToString(int type){
        return switch (type){
            case 0 -> " ";
            case 1 -> "●";
            case 2 -> "○";
            case 11 -> "◆";
            case 12 -> "◇";
            default -> String.valueOf(type);
        };
    }

    /**
     * 駒のタイプを取得するゲッター
     * @return 駒のタイプ
     */
    public int getType() {
        return type;
    }

    /**
     * 駒のタイプを設定するセッター
     * @param type 駒のタイプ
     */
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return typeToString(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token t) {
            return t.type == this.type;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
