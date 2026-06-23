package connect5plus;

public class Main {
    public static void main(String[] args) {
        ConsoleEncoding.configureUtf8();
        System.out.println("Connect 5 Plus v" + Version.VERSION);
        new Game();
    }
}
