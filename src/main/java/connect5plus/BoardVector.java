package connect5plus;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * ボード上でのベクトルを表すレコード
 * @param dx x軸方向
 * @param dy y軸方向
 *
 * @author 羽井出
 */
@NullMarked
public record BoardVector(int dx, int dy) {
    /**
     * ベクトルを加算
     * @return 新しいベクトル
     */
    @Contract(value = "_ -> new", pure = true)
    BoardVector add(BoardVector vector) {
        return new BoardVector(dx + vector.dx, dy + vector.dy);
    }

    /**
     * ベクトルの乗算
     * @return 新しいベクトル
     */
    @Contract(value = "_ -> new", pure = true)
    BoardVector multiply(int n) {
        return new BoardVector(n * dx, n * dy);
    }

    /**
     * ベクトルを左に90度回転
     * @return 新しいベクトル
     */
    @Contract(value = "-> new", pure = true)
    BoardVector left90() {
        return new BoardVector(-dy, dx);
    }

    /**
     * ベクトルを右に90度回転
     * @return 新しいベクトル
     */
    @Contract(value = "-> new", pure = true)
    BoardVector right90() {
        return new BoardVector(dy, -dx);
    }
}
