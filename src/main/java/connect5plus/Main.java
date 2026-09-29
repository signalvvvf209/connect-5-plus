package connect5plus;

import org.jspecify.annotations.NullMarked;

import javax.swing.*;

/**
 * Connect 5 Plusを始めるためのクラス
 * @author 羽井出
 */
@NullMarked
public class Main {
    /**
     * エントリーポイント
     *
     * @param args 引数。-g で GUI、-gs/-ga で CPU 対戦、-gar/-gaa でコンピュータ同士対戦
     */
    public static void main(String[] args) {

        if (args.length > 0) {
            if (args[0].startsWith("-g")) {
                SwingUtilities.invokeLater(
                        () -> new GameFrame(
                                switch (args[0]) {
                                    case "-gaa" -> GameFrame.OpponentMode.AUTO_ALPHABETA;
                                    case "-gar" -> GameFrame.OpponentMode.AUTO_RANDOM;
                                    case "-ga" -> GameFrame.OpponentMode.ALPHABETA;
                                    case "-gs" -> GameFrame.OpponentMode.RANDOM;
                                    default -> GameFrame.OpponentMode.NONE;
                                }
                        )
                );
            } else {
                ConsoleEncoding.configureUtf8();
                System.out.println("Connect 5+ v" + Version.VERSION);
                Game game = switch (args[0]) {
                    case "-a" -> new AutoGame();
                    case "-s" -> new SemiAutoGame();
                    default -> new Game();
                };
                game.begin();
            }
        } else {
            SwingUtilities.invokeLater(() -> new GameFrame(GameFrame.OpponentMode.NONE));
        }
    }
}
