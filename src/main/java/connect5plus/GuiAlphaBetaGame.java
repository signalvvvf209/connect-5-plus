package connect5plus;

/**
 * GUI版のアルファベータ法コンピュータとの試合を管理するクラス。
 * Player1 が人、Player2 がコンピュータとなる。
 * @author 羽井出
 */
public class GuiAlphaBetaGame extends GuiSemiAutoGame {
    private final AlphaBetaAI ai = new AlphaBetaAI(2);

    /**
     * アルファベータ法でコンピュータの最善手を選ぶ
     * @return 横方向の座標。置けない場合は -1
     */
    @Override
    public int selectComputerMove() {
        return ai.findBestMove(getBoard(), getCurrentPlayer());
    }
}
