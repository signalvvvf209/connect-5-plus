package connect5plus;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class ConsoleEncoding {
    private ConsoleEncoding() {
    }

    public static void configureUtf8() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }

    public static Scanner newScanner() {
        return new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
}
