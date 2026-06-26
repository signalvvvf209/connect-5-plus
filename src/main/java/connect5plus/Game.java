package connect5plus;

import java.util.Scanner;
import java.util.Set;

public class Game {
    private int player = 1;
    private final int playerMax = 2;
    private int winner = 0;
    private Set<Position> winningPositions = Set.of();
    private final Board board;

    public Game() {
        this.board = new Board(9);
    }

    public void begin(){
        progress();
    }

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

            if (winner < 0) {
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
            StringBuilder sb = new StringBuilder().repeat("！", winningPositions.size() - 5);
            System.out.println("Player" + winner + "の勝ち！" + sb);
        } else if (winner == 0) {
            System.out.println(board);
            System.out.println("引き分け");
        } else {
            System.out.println("ゲームを終了しました。");
        }
    }

    protected void nextPlayer() {
        player++;
        if (player > playerMax) {
            player = 1;
        }
    }

    protected int getPlayer() {
        return player;
    }

    protected int getWinner() {
        return winner;
    }

    protected Board getBoard() {
        return board;
    }

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
