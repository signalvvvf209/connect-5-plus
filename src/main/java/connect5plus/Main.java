package connect5plus;

/**
 * Connect 5 Plusを始めるためのクラス
 * @author 羽井出
 */
public class Main {
    /**
     * エントリーポイント
     * @param args 引数。 -eを指定するとコンピュータとの対戦
     */
    public static void main(String[] args) {
        ConsoleEncoding.configureUtf8();
        System.out.println("Connect 5 Plus v" + Version.VERSION);
        Game game;
        if (args.length > 0) {
            game = switch (args[0]) {
                case "-a" -> new AutoGame();
                case "-s" -> new SemiAutoGame();
                default -> new Game();
            };
        } else {
            game = new Game();
        }
        game.begin();
    }
}
