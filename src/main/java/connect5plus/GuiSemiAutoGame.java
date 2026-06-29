package connect5plus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GUI版のコンピュータとの試合を管理するクラス。
 * Player1 が人、Player2 がコンピュータとなる。
 * @author 羽井出
 */
public class GuiSemiAutoGame extends GuiGame {
    private final Random random = new Random();

    /**
     * 人間（Player1）の手番か判定する
     * @return 人間の手番の場合 true
     */
    public boolean isHumanTurn() {
        return !isOver() && getCurrentPlayer() == 1;
    }

    /**
     * コンピュータ（Player2）の手番か判定する
     * @return コンピュータの手番の場合 true
     */
    public boolean isComputerTurn() {
        return !isOver() && getCurrentPlayer() != 1;
    }

    /**
     * コンピュータが駒を落とす列をランダムに選ぶ。
     * 置ける列がない場合は -1 を返す。
     * @return 横方向の座標。置けない場合は -1
     */
    public int selectComputerMove() {
        List<Integer> columns = new ArrayList<>();
        for (int x = 0; x < getBoard().boardSize; x++) {
            if (getBoard().canDrop(x)) {
                columns.add(x);
            }
        }
        if (columns.isEmpty()) {
            return -1;
        }
        return columns.get(random.nextInt(columns.size()));
    }

    /**
     * コンピュータの手を1手進める
     * @return 駒を落とせた場合 true
     */
    public boolean makeComputerMove() {
        int x = selectComputerMove();
        if (x < 0) {
            return false;
        }
        return makeMove(x);
    }

    /**
     * 人間（Player1）の手番のときのみ盤面操作を受け付ける
     * @return 操作可能な場合 true
     */
    @Override
    public boolean acceptsHumanInput() {
        return isHumanTurn();
    }
}
