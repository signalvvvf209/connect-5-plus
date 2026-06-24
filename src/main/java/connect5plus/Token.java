package connect5plus;

import java.util.Objects;

public class Token {
    private int type;

    public Token(){
        this(0);
    }

    public Token(int type){
        this.type = type;
    }

    public static String typeToString(int type){
        return switch (type){
            case 0 -> " ";
            case 1 -> "○";
            case 2 -> "●";
            default -> String.valueOf(type);
        };
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return typeToString(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token t) {
            return t.type == this.type;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
