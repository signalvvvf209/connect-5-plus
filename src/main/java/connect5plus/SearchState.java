package connect5plus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * アルファベータ法探索用の盤面状態
 */
final class SearchState {
    final Board board;
    final int playerToMove;
    /** 0 = 進行中または引き分け、1 または 2 = 勝者 */
    final int winner;

    SearchState(Board board, int playerToMove, int winner) {
        this.board = board;
        this.playerToMove = playerToMove;
        this.winner = winner;
    }

    /**
     * 現在の盤面と手番から探索用状態を生成する
     * @param board 盤面
     * @param playerToMove 次に打つプレイヤー
     * @return 探索用状態
     */
    static SearchState from(Board board, int playerToMove) {
        return new SearchState(board.boardCopy(), playerToMove, 0);
    }

    /**
     * ゲームが終了した状態か判定する
     * @return 終了している場合 true
     */
    boolean isTerminal() {
        return winner != 0 || board.isFull();
    }

    /**
     * 合法手の一覧を返す（中央に近い列を優先）
     * @return 合法手の x 座標リスト
     */
    List<Integer> legalMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int x = 0; x < board.boardSize; x++) {
            if (board.canDrop(x)) {
                moves.add(x);
            }
        }
        int center = board.boardSize / 2;
        moves.sort(Comparator.comparingInt(x -> Math.abs(x - center)));
        return moves;
    }

    /**
     * 指定列に駒を落とした後の状態を返す
     * @param x 横方向の座標
     * @return 着手後の状態。置けない場合 null
     */
    SearchState applyMove(int x) {
        Board nextBoard = board.boardCopy();
        Set<Position> dropped = nextBoard.putTokens(x, playerToMove);
        if (dropped.isEmpty()) {
            return null;
        }

        for (Position position : dropped) {
            if (!nextBoard.findWinningPositions(position).isEmpty()) {
                return new SearchState(nextBoard, playerToMove, playerToMove);
            }
        }

        if (nextBoard.isFull()) {
            return new SearchState(nextBoard, playerToMove, 0);
        }

        int nextPlayer = playerToMove == 1 ? 2 : 1;
        return new SearchState(nextBoard, nextPlayer, 0);
    }
}
