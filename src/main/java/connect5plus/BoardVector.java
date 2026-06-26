package connect5plus;

/**
 * ボード上でのベクトルを表すレコード
 * @param dx x軸方向
 * @param dy y軸方向
 *
 * @author 羽井出
 */
public record BoardVector(int dx, int dy) {
    /**
     * ベクトルを加算
     * @return 新しいベクトル
     */
    BoardVector add(BoardVector vector) {
        return new BoardVector(dx + vector.dx, dy + vector.dy);
    }

    /**
     * ベクトルの乗算
     * @return 新しいベクトル
     */
    BoardVector multiply(int n) {
        return new BoardVector(n * dx, n * dy);
    }

    /**
     * ベクトルを左に90度回転
     * @return 新しいベクトル
     */
    BoardVector left90() {
        return new BoardVector(-dy, dx);
    }

    /**
     * ベクトルを右に90度回転
     * @return 新しいベクトル
     */
    BoardVector right90() {
        return new BoardVector(dy, -dx);
    }
}
