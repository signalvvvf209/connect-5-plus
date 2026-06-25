package connect5plus;

public class Main {
    public static void main(String[] args) {
        ConsoleEncoding.configureUtf8();
        System.out.println("Connect 5 Plus v" + Version.VERSION);
        Game game = null;
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
