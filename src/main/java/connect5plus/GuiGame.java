package connect5plus;

/**
 * GUI版の試合を管理するクラス
 * @author 羽井出
 */
public class GuiGame extends Game {
    /**
     * 指定された x 座標に駒を落とす。
     * さらに、勝利判定を行い、続行する場合は次のプレイヤーの手番にする。
     * @param x 横方向の座標
     * @return 駒を落とせた場合 true
     */
    public boolean makeMove(int x) {
        if (!getBoard().canDrop(x)) {
            return false;
        }
        if (!tryMove(x)) {
            return false;
        }
        if (getWinner() == 0 && !getBoard().isFull()) {
            nextPlayer();
        }
        return true;
    }

    /**
     * ゲームが終了しているか判定する
     * @return 終了している場合 true
     */
    public boolean isOver() {
        return getWinner() != 0 || getBoard().isFull();
    }

    /**
     * 人間が盤面を操作できる状態か判定する
     * @return 操作可能な場合 true
     */
    public boolean acceptsHumanInput() {
        return !isOver();
    }

    /**
     * GUI版ではコンソール用の進行ループを使わない
     */
    @Override
    public void begin() {
    }
}
