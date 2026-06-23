package connect5plus;

import java.util.List;
import java.util.Scanner;

public class Game {
    private int player = 1;
    private int winner = 0;
    private final int playerMax = 2;
    private final Board board;

    public Game() {
        this.board = new Board(9);
        progress();
    }

    protected void progress() {
        while (true) {
            System.out.println(board);

                int x = selectPosition();
                if (x == -1) {
                    break;
                }

                int direction = selectDirection();
                if (direction == -1) {
                    break;
                }

            if (!tryMove(x, direction)) {
                System.out.println("そこには置けません");
                continue;
            }

            if (winner != 0) {
                break;
            }

            nextPlayer();
        }

        System.out.println(board);
        if (winner != 0) {
            System.out.println("Player" + winner + "の勝ち！");
        }
    }

    protected void nextPlayer() {
        player++;
        if (player > playerMax) {
            player = 1;
        }
    }

    protected int getPlayer() {
        return this.player;
    }

    protected int getWinner() {
        return this.winner;
    }

    protected Board getBoard() {
        return this.board;
    }

    protected int selectPosition() {
        Scanner scanner = ConsoleEncoding.newScanner();
        System.out.println("Player" + player + "の番です");
        while (true) {
            System.out.println("0 ~ " + board.boardSize + " で置く場所を指定してください。(eで終了)");
            String str = scanner.next();
            if (str.equals("e")) {
                return -1;
            }
            try {
                int position = Integer.parseInt(str);
                if (0 <= position && position < board.boardSize){
                    return position;
                }
                System.out.println("範囲内の値を入力してください");
            } catch (NumberFormatException e) {
                System.out.println("入力が正しくありません");
            }
        }
    }

    protected int selectDirection() {
        Scanner scanner = ConsoleEncoding.newScanner();
        while (true) {
            System.out.println("h (横) または v (縦) を入力してください");
            String str = scanner.next();
            switch (str) {
                case "e" -> {
                    return -1;
                }
                case "h" -> {
                    return 0;
                }
                case "v" -> {
                    return 1;
                }
            }
            System.out.println("入力が正しくありません");
        }
    }

    protected boolean tryMove(int x, int direction){
        if (winner != 0) {
            return false;
        }

        List<Position> droppedTokens = board.putTokens(x, direction, new Token(player));
        if (droppedTokens.isEmpty()) {
            return false;
        }

        for (Position p : droppedTokens) {
            if (board.check(p)) {
                winner = player;
                break;
            }
        }
        return true;
    }
}
