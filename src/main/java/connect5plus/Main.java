package connect5plus;

import javax.swing.*;

/**
 * Connect 5 Plusを始めるためのクラス
 * @author 羽井出
 */
public class Main {
    /**
     * エントリーポイント
     * @param args 引数。-g で GUI、-gs で GUI のコンピュータ対戦、-s でコンソールのコンピュータ対戦
     */
    public static void main(String[] args) {
        if (args.length > 0 && "-gs".equals(args[0])) {
            SwingUtilities.invokeLater(() -> new GameFrame(true));
        } else if (args.length == 0 || "-g".equals(args[0])) {
            SwingUtilities.invokeLater(() -> new GameFrame(false));
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
    }
}
