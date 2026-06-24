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

    public Board(Board board) {
        this.boardSize = board.boardSize;
        this.space = new Token[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board.space[y][x] != null) {
                    this.space[y][x] = new Token(board.space[y][x].getType());
                }
            }
        }
    }

    public Board boardCopy() {
        return new Board(this);
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

    public Board replaceTokens(List<Position> positions, int type) {
        Board newBoard = this.boardCopy();
        for (Position pos : positions) {
            newBoard.setSpace(pos, new Token(10 + type));
        }
        return newBoard;
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
        return space[pos1.y()][pos1.x()].equals(space[pos2.y()][pos2.x()]);
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

    public List<Position> putTokens(int x, int player) {
        List<Position> droppedTokens = new ArrayList<>();
        if (x < 0 || boardSize <= x) {
            return droppedTokens;
        }

        Position position = dropToken(x, new Token(player));

        if (position != null) {
            droppedTokens.add(position);
        }
        return droppedTokens;
    }

    public List<Position> findWinningPositions(Position pos) {
        if (!isInsideBoard(pos)) {
            return List.of();
        }
        List<Position> line = findLineWin(pos);
        if (!line.isEmpty()) {
            return line;
        }
        return findCrossWin(pos);
    }

    private List<Position> findLineWin(Position pos) {
        final List<BoardVector> vectors = List.of(
                new BoardVector(0, 1),
                new BoardVector(1, 1),
                new BoardVector(1, 0)
        );

        for (BoardVector vector : vectors) {
            List<Position> line = new ArrayList<>();
            line.add(pos);
            for (int sign = -1; sign <= 1; sign += 2) {
                for (int i = 1; i < 5; i++) {
                    Position p = pos.offset(vector.multiply(sign * i));
                    if (isSameToken(pos, p)) {
                        line.add(p);
                    } else {
                        break;
                    }
                }
            }
            if (line.size() >= 5) {
                return line;
            }
        }
        return List.of();
    }

    private List<Position> findCrossWin(Position pos) {

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
            return List.of(
                    pos,
                    pos.offset(new BoardVector(1, 1)),
                    pos.offset(new BoardVector(-1, -1)),
                    pos.offset(new BoardVector(1, -1)),
                    pos.offset(new BoardVector(-1, 1))
            );
        }

        for (BoardVector vector : vectors) {
            if (
                    isSameToken(pos, vector.multiply(2))
                            && isSameToken(pos, vector.add(vector.left90()))
                            && isSameToken(pos, vector.add(vector.right90()))
                            && isSameToken(pos, vector)
            ) {
                return List.of(
                        pos,
                        pos.offset(vector.multiply(2)),
                        pos.offset(vector.add(vector.left90())),
                        pos.offset(vector.add(vector.right90())),
                        pos.offset(vector)
                );
            }
        }
        return List.of();
    }

    public void printWin(List<Position> positions, int player) {
        System.out.println(this.boardCopy().replaceTokens(positions,  player));
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
