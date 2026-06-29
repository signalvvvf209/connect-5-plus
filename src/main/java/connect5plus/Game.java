package connect5plus;

import java.util.Scanner;
import java.util.Set;

/**
 * 1つの試合を管理するクラス
 * @author 羽井出
 */
public class Game {
    /** 現在のプレイヤー */
    private int player = 1;
    /** プレイヤーの最大数 */
    private final int playerMax = 2;
    /** 勝利したプレイヤー */
    private int winner = 0;
    /** 勝利時、揃っている駒のセット */
    private Set<Position> winningPositions = Set.of();
    /** この試合のボード */
    private final Board board;

    /**
     * 試合を担当するインスタンスを生成。
     * デフォルトでは 9 x 9 のボードが作られる。
     */
    public Game() {
        this.board = new Board(9);
    }

    /**
     * ゲームを始める
     */
    public void begin(){
        progress();
    }

    /**
     * ゲームの進行を管理する
     */
    protected void progress() {
        while (true) {
            System.out.println(board);

            int x = selectPosition();
            if (x < 0) {
                winner = -1;
                break;
            }

            if (!tryMove(x)) {
                System.out.println("そこには置けません");
                continue;
            }

            if (winner != 0) {
                break;
            }

            if (board.isFull()) {
                break;
            }

            nextPlayer();
        }

        System.out.println(board);
        if (winner > 0) {
            board.printWin(winningPositions, winner);
            StringBuilder sb = new StringBuilder().repeat('！', winningPositions.size() - 5);
            System.out.println("Player" + winner + "の勝ち！" + sb);
        } else if (winner == 0) {
            System.out.println(board);
            System.out.println("引き分け");
        } else {
            System.out.println("ゲームを終了しました。");
        }
    }

    /**
     * 次のプレイヤーの手番にする
     */
    protected void nextPlayer() {
        player++;
        if (player > playerMax) {
            player = 1;
        }
    }

    /**
     * 現在のプレイヤーを取得するゲッター
     * @return 現在のプレイヤー
     */
    protected int getCurrentPlayer() {
        return player;
    }

    /**
     * 勝利したプレイヤーを取得するゲッター
     * @return 勝利したプレイヤー
     */
    protected int getWinner() {
        return winner;
    }

    /**
     * ボードを取得するゲッター
     * @return ボード
     */
    protected Board getBoard() {
        return board;
    }

    /**
     * 勝利時に揃った駒の位置を返すゲッター
     * @return 揃った駒の位置のセット
     */
    protected Set<Position> getWinningPositions() {
        return winningPositions;
    }

    /**
     * 駒を落下させる位置を決める。
     * コンソールからボードの範囲内の x 座標 または ゲームを終了する指示("e"の入力)を受け取り、戻り値を返す。
     * @return x 座標 または -1 (終了指示)
     */
    protected int selectPosition() {
        Scanner scanner = ConsoleEncoding.newScanner();
        System.out.println("Player" + player + " (" + Token.typeToString(player) + ") の番です");
        while (true) {
            System.out.println("0 ~ " + (board.boardSize - 1) + " で置く場所を指定してください (eで終了)");
            String str = scanner.next();
            if ("e".equals(str)) {
                return -1;
            }
            try {
                int x = Integer.parseInt(str);
                if (board.isInsideBoard(x)){
                    return x;
                }
                System.out.println("範囲内の値を入力してください");
            } catch (NumberFormatException e) {
                System.out.println("入力が正しくありません");
            }
        }
    }

    /**
     * 駒を落とし、 true を返す。
     * さらに、落とした駒が揃った場合はそれらの位置と勝者を記録する。
     * 落とせない場合は false を返す。
     * @return 駒を落とせた場合 true
     */
    protected boolean tryMove(int x){
        if (winner != 0) {
            return false;
        }

        Set<Position> droppedTokens = board.putTokens(x, player);
        if (droppedTokens.isEmpty()) {
            return false;
        }

        for (Position p : droppedTokens) {
            Set<Position> winPos = board.findWinningPositions(p);
            if (!winPos.isEmpty()) {
                winner = player;
                this.winningPositions = winPos;
                break;
            }
        }
        return true;
    }
}
