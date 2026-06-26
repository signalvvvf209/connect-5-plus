package connect5plus;

import java.util.Random;

/**
 * コンピュータとの試合を管理するクラス
 * @author 羽井出
 */
public class SemiAutoGame extends Game{
    /**
     * 駒を落下させる位置を決める。
     * 現在のプレイヤーが人の場合は通常通り入力を受け取り、
     * コンピュータの場合はランダムな位置を指定する (現在の実装)
     * @return x 座標 または 負の整数 (終了指示)
     */
    @Override
    protected int selectPosition() {
        if (getPlayer() != 1){
            return new Random().nextInt(getBoard().boardSize);
        } else {
            return super.selectPosition();
        }
    }
}
