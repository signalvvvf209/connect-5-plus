package connect5plus;

public class Main {
    public static void main(String[] args) {
        ConsoleEncoding.configureUtf8();
        System.out.println("Connect 5 Plus v" + Version.VERSION);
        if (args.length > 0 && args[0].equals("-a")) {
            new AutoGame().begin();
        } else {
            new Game().begin();
        }
    }
}
