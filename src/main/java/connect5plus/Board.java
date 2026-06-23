package connect5plus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Board {
    public final int boardSize;
    private final Token[][] space;

    public Board() {
        this(9);
    }

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.space = new Token[boardSize][boardSize];
    }

    public Token getSpace(int x, int y) {
        if (isInsideBoard(x, y)) {
            return space[y][x];
        } else {
            return null;
        }
    }
    public Token getSpace(Position pos) {
        return (pos != null) ? getSpace(pos.x(), pos.y()) : null;
    }

    private void setSpace(Position pos, Token token) throws IllegalArgumentException {
        if (isInsideBoard(pos)) {
            this.space[pos.y()][pos.x()] = token;
        } else {
            throw new IllegalArgumentException("Setter Position Error");
        }
    }

    public boolean isInsideBoard(int x, int y) {
        return (0 <= x && x < boardSize && 0 <= y && y < boardSize);
    }

    public boolean isInsideBoard(Position pos) {
        return pos != null && isInsideBoard(pos.x(), pos.y());
    }

    public boolean isSameToken(Position pos1, Position pos2) {
        if (!isInsideBoard(pos1) || !isInsideBoard(pos2)) {
            return false;
        }
        return space[pos1.y()][pos1.x()].equals( space[pos2.y()][pos2.x()]);
    }

    public boolean isSameToken(Position pos, BoardVector vector) {
        return vector != null && isSameToken(pos, pos.offset(vector));
    }

    private Position dropToken(int x, Token token) {
        for (int y = boardSize - 1; y >= 0; y--) {
            if (space[y][x] == null) {
                space[y][x] = token;
                return new Position(x, y);
            }
        }
        return null;
    }

    public List<Position> putTokens(int x, int direction, Token token) {
        if (x < 0 || boardSize <= x) {
            return null;
        }

        if (direction == 0 && boardSize - 1 <= x) {
            return new ArrayList<Position>();
        }
        if (direction == 0
                && (space[0][x] != null || space[0][x + 1] != null)) {
            return new ArrayList<Position>();
        }
        if (direction == 1 && space[1][x] != null) {
            return new ArrayList<Position>();
        }

        List<Position> droppedTokens = new ArrayList<>();

        int offset = direction == 0 ? 1 : 0;
        for (int i = 0; i < 2; i++) {
            Position position = dropToken(x + i * offset, token);
            if (position == null) {
                return new ArrayList<Position>();
            }
            droppedTokens.add(position);
        }
        return droppedTokens;
    }

    public boolean check(Position pos) {
        if (!isInsideBoard(pos)) {
            return false;
        }
        return checkLine(pos) || checkCross(pos);
    }

    private boolean checkLine(Position pos) {
        final List<BoardVector> vectors = List.of(
                new BoardVector(0, 1),
                new BoardVector(1, 1),
                new BoardVector(1, 0)
        );

        for (BoardVector vector : vectors) {
            int cnt = 1;
            for (int sign = -1; sign <= 1; sign += 2) {
                for (int i = 1; i < 5; i++) {
                    if (isSameToken(pos, vector.multiply(sign * i))) {
                        cnt++;
                    } else {
                        break;
                    }
                }
            }
            if (cnt >= 5) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCross(Position pos) {

        List<BoardVector> vectors = List.of(
                new BoardVector(1, 0),
                new BoardVector(1, 1),
                new BoardVector(0, 1),
                new BoardVector(-1, 1),
                new BoardVector(-1, 0)
        );

        if (
                isSameToken(pos, new BoardVector(1, 1))
                        && isSameToken(pos, new BoardVector(-1, -1))
                        && isSameToken(pos, new BoardVector(1, -1))
                        && isSameToken(pos, new BoardVector(-1, 1))
        ) {
            return true;
        }

        for (BoardVector vector : vectors) {
            if (
                    isSameToken(pos, vector.multiply(2))
                            && isSameToken(pos, vector.add(vector.left90()))
                            && isSameToken(pos, vector.add(vector.right90()))
                            && isSameToken(pos, vector)
            ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("|");
        sb.repeat(" ", boardSize * 2 + 1);
        sb.append("|\n");

        for (int y = 0; y < boardSize; y++) {
            sb.append("| ");
            for (int x = 0; x < boardSize; x++) {
                Token t = space[y][x];
                sb
                        .append(t != null ? t : " ")
                        .append(" ");
            }
            sb.append("|\n");
        }

        sb
                .append("|-")
                .repeat("-", boardSize * 2)
                .append("|\n");

        sb.append("| ");
        for (int x = 0; x < boardSize; x++) {
            sb
                    .append(x)
                    .append(" ");
        }
        sb.append("|\n");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Board b) {
            if(b.boardSize != this.boardSize) {
                return false;
            }
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    if (b.space[y][x] != this.space[y][x]){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) space);
    }
}
