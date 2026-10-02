package connect5plus;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * 1つの駒を表すレコードクラス
 * @author 羽井出
 * @param type 駒のタイプ。プレイヤーによって異なる値とする。
 */
@NullMarked
public record Token(int type) {
    @Contract(pure = true)
    public Token(){
        this(0);
    }

    /**
     * 指定されたタイプに対応する文字列を返す
     * @param type 駒のタイプ
     * @return 駒の文字列
     */
    @Contract(pure = true)
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
    @Contract(pure = true)
    public String toString() {
        return typeToString(type);
    }
}
