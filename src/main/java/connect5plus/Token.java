package connect5plus;

import java.util.Objects;

/**
 * 1つの駒を表すレコードクラス
 * @author 羽井出
 * @param type 駒のタイプ。プレイヤーによって異なる値とする。
 */
public record Token(int type) {
    public Token(){
        this(0);
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

    @Override
    public String toString() {
        return typeToString(type);
    }
}
