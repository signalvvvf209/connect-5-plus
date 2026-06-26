package connect5plus;

import java.util.*;

/**
 * ボードを表すクラス
 * @author 羽井出
 */
public class Board {
    /** ボードの大きさ */
    public final int boardSize;
    /** ボードのマスを表す配列 */
    private final Token[][] space;
    /** ボードにある駒の数 */
    public int tokenCount = 0;

    public Board() {
        this(9);
    }

    /**
     * ボードの大きさを指定してインスタンスを生成
     * @param boardSize ボードの大きさ
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.space = new Token[boardSize][boardSize];
    }

    /**
     * ボードを指定して、同じ盤面のインスタンスを生成（ディープコピー）
     * @param board 複製元のボード
     */
    public Board(Board board) {
        this(board.boardSize);
        this.tokenCount = board.tokenCount;
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board.space[y][x] != null) {
                    this.space[y][x] = new Token(board.space[y][x].getType());
                }
            }
        }
    }

    /**
     * ボードを複製（ディープコピー）し、新しいボードのインスタンスを返す
     * @return 複製されたボード
     */
    public Board boardCopy() {
        return new Board(this);
    }

    /**
     * 座標 (x, y) のマスを返すゲッター。
     * 引数がボードの範囲外の場合はnullを返す。
     * @param x 横方向の座標
     * @param y 縦方向の座標
     * @return 指定された座標のマス（範囲外の場合はnull）
     */
    public Token getSpace(int x, int y) {
        return isInsideBoard(x, y) ? space[y][x] : null;
    }

    /**
     * 指定された位置のマスを返すゲッター。
     * 引数がボードの範囲外の場合はnullを返す。
     * @param pos 位置
     * @return 指定された座標のマス（範囲外の場合はnull）
     */
    public Token getSpace(Position pos) {
        return (pos != null) ? getSpace(pos.x(), pos.y()) : null;
    }

    /**
     * 指定された位置に受け取った駒を置くセッター。
     * 引数がボードの範囲外の場合は例外をスローする。
     * @param pos 位置
     * @param token 駒
     * @throws IllegalArgumentException 位置がボード範囲外の場合
     */
    private void setSpace(Position pos, Token token) throws IllegalArgumentException {
        if (isInsideBoard(pos)) {
            if (this.space[pos.y()][pos.x()] == null) {
                tokenCount++;
            }
            this.space[pos.y()][pos.x()] = token;
        } else {
            throw new IllegalArgumentException("Setter Position Error");
        }
    }

    /**
     * 位置のセットと駒のタイプを受け取り、指定された複数の位置に駒を置いた新しいボードを返す。
     * @param positions 位置のセット
     * @param type 駒のタイプ
     * @return 駒を置いた新しいボード
     */
    public Board replaceTokens(Set<Position> positions, int type) {
        Board newBoard = this.boardCopy();
        for (Position pos : positions) {
            newBoard.setSpace(pos, new Token(10 + type));
        }
        return newBoard;
    }

    /**
     * 縦または横方向の値がボードの範囲内か判定する
     * @param value 値
     * @return 範囲内の場合 true
     */
    public boolean isInsideBoard(int value) {
        return 0 <= value && value < boardSize;
    }

    /**
     * 座標 (x, y) がボードの範囲内か判定する
     * @param x 横方向の座標
     * @param y 縦方向の座標
     * @return 範囲内の場合 true
     */
    public boolean isInsideBoard(int x, int y) {
        return isInsideBoard(x) && isInsideBoard(y);
    }

    /**
     * 指定された位置がボードの範囲内か判定する
     * @param pos 位置
     * @return 範囲内の場合 true
     */
    public boolean isInsideBoard(Position pos) {
        return pos != null && isInsideBoard(pos.x(), pos.y());
    }

    /**
     * 指定された2つの位置にある駒が同じかどうか判定
     * @param pos1 位置1
     * @param pos2 位置2
     * @return 同じ場合 true。駒がない場合 false
     */
    public boolean isSameToken(Position pos1, Position pos2) {
        if (isInsideBoard(pos1) && isInsideBoard(pos2)) {
            return Objects.equals(space[pos1.y()][pos1.x()], space[pos2.y()][pos2.x()]);
        } else {
            return false;
        }
    }

    /**
     * 指定された位置と、そこにベクトルを加算した位置にある駒が同じかどうか判定
     * @param pos 位置
     * @param vector ベクトル
     * @return 同じ場合 true。駒がない場合 false
     */
    public boolean isSameToken(Position pos, BoardVector vector) {
        return vector != null && isSameToken(pos, pos.offset(vector));
    }

    /**
     * 横方向 x に受け取った駒を落とし、落ちた位置を返す
     * @param x 横方向の座標
     * @param token 落とす駒
     * @return 落ちた駒の位置。落とせない場合 null
     */
    private Position dropToken(int x, Token token) {
        for (int y = boardSize - 1; y >= 0; y--) {
            if (space[y][x] == null) {
                space[y][x] = token;
                tokenCount++;
                return new Position(x, y);
            }
        }
        return null;
    }

    /**
     * 横方向 x を基準に、現在のプレイヤーの新しい駒を落とす
     * @param x 横方向の座標
     * @param player 現在のプレイヤー
     * @return 落ちた駒の位置のセット
     */
    public Set<Position> putTokens(int x, int player) {
        Set<Position> droppedTokens = new HashSet<>();
        if (!isInsideBoard(x)) {
            return Set.of();
        }
        Position position = dropToken(x, new Token(player));
        if (position != null) {
            droppedTokens.add(position);
        }
        return droppedTokens;
    }

    /**
     * 指定された位置を含んで駒が揃っているか確かめる。
     * 揃っている場合は、その駒の位置のセットを返す。
     * @param pos 基準にする位置
     * @return 揃っている駒の位置のセット
     */
    public Set<Position> findWinningPositions(Position pos) {
        if (!isInsideBoard(pos)) {
            return Set.of();
        }
        Set<Position> winPos = new HashSet<>();
        findLineWin(pos, winPos);
        findCrossWin(pos, winPos);
        return Set.copyOf(winPos);
    }

    /**
     * 指定された位置を含んで直線に駒が揃っているか確かめる。
     * 揃っている場合は、その駒の位置をセットに追加する。
     * @param pos 基準にする位置
     * @param winPos 既に揃っている駒の位置を含むセット。他に揃っていない場合は空。
     */
    private void findLineWin(Position pos, Set<Position> winPos) {
        final BoardVector[] vectors = {
                new BoardVector(0, 1),
                new BoardVector(1, 1),
                new BoardVector(1, 0),
                new BoardVector(1, -1)
        };

        for (BoardVector vector : vectors) {
            Set<Position> line = new HashSet<>(Set.of(pos));
            for (int sign = -1; sign <= 1; sign += 2) {
                for (int i = 1; i < 5; i++) {
                    Position target = pos.offset(vector.multiply(sign * i));
                    if (isSameToken(target, pos)) {
                        line.add(target);
                    } else {
                        break;
                    }
                }
            }
            if (line.size() >= 5) {
                winPos.addAll(line);
            }
        }
    }

    /**
     * 指定された位置を含んで十字やX字に駒が揃っているか確かめる。
     * 揃っている場合は、その駒の位置をセットに追加する。
     * @param pos 基準にする位置
     * @param winPos 既に揃っている駒の位置を含むセット。他に揃っていない場合は空。
     */
    private void findCrossWin(Position pos, Set<Position> winPos) {
        BoardVector[] vectors = {
                new BoardVector(1, 0),
                new BoardVector(1, 1),
                new BoardVector(0, 1),
                new BoardVector(-1, 1),
                new BoardVector(-1, 0)
        };

        if (
                isSameToken(pos, new BoardVector(1, 1))
                        && isSameToken(pos, new BoardVector(-1, -1))
                        && isSameToken(pos, new BoardVector(1, -1))
                        && isSameToken(pos, new BoardVector(-1, 1))
        ) {
            winPos.addAll(
                    Set.of(
                            pos,
                            pos.offset(new BoardVector(1, 1)),
                            pos.offset(new BoardVector(-1, -1)),
                            pos.offset(new BoardVector(1, -1)),
                            pos.offset(new BoardVector(-1, 1))
                    )
            );
        }

        for (BoardVector vector : vectors) {
            if (
                    isSameToken(pos, vector.multiply(2))
                            && isSameToken(pos, vector.add(vector.left90()))
                            && isSameToken(pos, vector.add(vector.right90()))
                            && isSameToken(pos, vector)
            ) {
                winPos.addAll(
                        Set.of(
                                pos,
                                pos.offset(vector.multiply(2)),
                                pos.offset(vector.add(vector.left90())),
                                pos.offset(vector.add(vector.right90())),
                                pos.offset(vector)
                        )
                );
            }
        }
    }

    /**
     * 勝利時の盤面を出力する。
     * 揃った駒はダイヤモンド形で表示される。
     * @param positions 揃っている駒の位置のセット
     * @param player 現在のプレイヤー
     */
    public void printWin(Set<Position> positions, int player) {
        System.out.println(this.boardCopy().replaceTokens(positions, player));
    }

    /**
     * ボードが満杯かどうか判定する
     * @return 満杯の場合 true
     */
    public boolean isFull() {
        return tokenCount >= (boardSize * boardSize);
    }

    /**
     * ボードを表示するために、文字列として結果を返す
     * @return 盤面の文字列
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("|");
        sb.repeat(' ', boardSize * 2 + 1);
        sb.append('|').append('\n');

        for (int y = 0; y < boardSize; y++) {
            sb.append('|').append(' ');
            for (int x = 0; x < boardSize; x++) {
                Token t = space[y][x];
                sb.append(t != null ? t : ' ').append(' ');
            }
            sb.append('|').append('\n');
        }

        sb
                .append('|')
                .repeat('-', boardSize * 2 + 1)
                .append('|')
                .append('\n');

        sb.append('|').append(' ');
        for (int x = 0; x < boardSize; x++) {
            sb.append(x).append(' ');
        }
        sb.append('|').append('\n');

        return sb.toString();
    }

    /**
     * ボードが同じかどうか判定する
     * @return 同じ場合 true
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Board b) {
            if(b.boardSize != this.boardSize) {
                return false;
            }
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    if (Objects.equals(b.space[y][x], this.space[y][x])){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * ボードのハッシュコードを返す
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash((Object) space);
    }
}
