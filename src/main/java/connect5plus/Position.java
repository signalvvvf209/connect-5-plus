package connect5plus;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * ボード上の位置を表すレコード
 * @param x 横方向
 * @param y 縦方向
 *
 * @author 羽井出
 */
@NullMarked
public record Position(int x, int y) {
    /**
     * 現在の位置にベクトルを加算し、新しい位置を返す
     * @param vector ベクトル
     * @return 新しい位置
     */
    @Contract(value = "_ -> new", pure = true)
    Position offset(BoardVector vector) {
        return new Position(x + vector.dx(), y + vector.dy());
    }
}
