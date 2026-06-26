package connect5plus;

import java.util.*;
import java.util.stream.IntStream;

public class Board {
    public final int boardSize;
    private final Token[][] space;
    public int tokenCount = 0;

    public Board() {
        this(9);
    }

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.space = new Token[boardSize][boardSize];
    }

    public Board(Board board) {
        this(board.boardSize);
        this.tokenCount = board.tokenCount;
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
        return isInsideBoard(x, y) ? space[y][x] : null;
    }

    public Token getSpace(Position pos) {
        return (pos != null) ? getSpace(pos.x(), pos.y()) : null;
    }

    private void setSpace(Position pos, Token token) throws IllegalArgumentException {
        if (isInsideBoard(pos)) {
            if (this.space[pos.y()][pos.x()] == null) {
                tokenCount++;
            }
            this.space[pos.y()][pos.x()] = token;
        } else {
            throw new IllegalArgumentException("Setter Position Error");
        }
    }

    public Board replaceTokens(Set<Position> positions, int type) {
        Board newBoard = this.boardCopy();
        for (Position pos : positions) {
            newBoard.setSpace(pos, new Token(10 + type));
        }
        return newBoard;
    }

    public boolean isInsideBoard(int value) {
        return 0 <= value && value < boardSize;
    }

    public boolean isInsideBoard(int x, int y) {
        return isInsideBoard(x) && isInsideBoard(y);
    }

    public boolean isInsideBoard(Position pos) {
        return pos != null && isInsideBoard(pos.x(), pos.y());
    }

    public boolean isSameToken(Position pos1, Position pos2) {
        if (isInsideBoard(pos1) && isInsideBoard(pos2)) {
            return Objects.equals(space[pos1.y()][pos1.x()], space[pos2.y()][pos2.x()]);
        } else {
            return false;
        }
    }

    public boolean isSameToken(Position pos, BoardVector vector) {
        return vector != null && isSameToken(pos, pos.offset(vector));
    }

    private Position dropToken(int x, Token token) {
        for (int y = boardSize - 1; y >= 0; y--) {
            if (space[y][x] == null) {
                space[y][x] = token;
                tokenCount++;
                return new Position(x, y);
            }
        }
        return null;
    }

    public Set<Position> putTokens(int x, int player) {
        Set<Position> droppedTokens = new HashSet<>();
        if (!isInsideBoard(x)) {
            return Set.of();
        }
        Position position = dropToken(x, new Token(player));
        if (position != null) {
            droppedTokens.add(position);
        }
        return droppedTokens;
    }

    public Set<Position> findWinningPositions(Position pos) {
        if (!isInsideBoard(pos)) {
            return Set.of();
        }
        Set<Position> winPos = new HashSet<>();
        findLineWin(pos, winPos);
        findCrossWin(pos, winPos);
        return Set.copyOf(winPos);
    }

    private void findLineWin(Position pos, Set<Position> winPos) {
        final BoardVector[] vectors = {
                new BoardVector(0, 1),
                new BoardVector(1, 1),
                new BoardVector(1, 0),
                new BoardVector(1, -1)
        };

        for (BoardVector vector : vectors) {
            Set<Position> line = new HashSet<>(Set.of(pos));
            for (int sign = -1; sign <= 1; sign += 2) {
                for (int i = 1; i < 5; i++) {
                    Position target = pos.offset(vector.multiply(sign * i));
                    if (isSameToken(target, pos)) {
                        line.add(target);
                    } else {
                        break;
                    }
                }
            }
            if (line.size() >= 5) {
                winPos.addAll(line);
            }
        }
    }

    private void findCrossWin(Position pos, Set<Position> winPos) {
        BoardVector[] vectors = {
                new BoardVector(1, 0),
                new BoardVector(1, 1),
                new BoardVector(0, 1),
                new BoardVector(-1, 1),
                new BoardVector(-1, 0)
        };

        if (
                isSameToken(pos, new BoardVector(1, 1))
                        && isSameToken(pos, new BoardVector(-1, -1))
                        && isSameToken(pos, new BoardVector(1, -1))
                        && isSameToken(pos, new BoardVector(-1, 1))
        ) {
            winPos.addAll(
                    Set.of(
                            pos,
                            pos.offset(new BoardVector(1, 1)),
                            pos.offset(new BoardVector(-1, -1)),
                            pos.offset(new BoardVector(1, -1)),
                            pos.offset(new BoardVector(-1, 1))
                    )
            );
        }

        for (BoardVector vector : vectors) {
            if (
                    isSameToken(pos, vector.multiply(2))
                            && isSameToken(pos, vector.add(vector.left90()))
                            && isSameToken(pos, vector.add(vector.right90()))
                            && isSameToken(pos, vector)
            ) {
                winPos.addAll(
                        Set.of(
                                pos,
                                pos.offset(vector.multiply(2)),
                                pos.offset(vector.add(vector.left90())),
                                pos.offset(vector.add(vector.right90())),
                                pos.offset(vector)
                        )
                );
            }
        }
    }

    public void printWin(Set<Position> positions, int player) {
        System.out.println(this.boardCopy().replaceTokens(positions, player));
    }

    public boolean isFull() {
        return tokenCount >= (boardSize * boardSize);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("|");
        sb.repeat(' ', boardSize * 2 + 1);
        sb.append("|\n");

        for (int y = 0; y < boardSize; y++) {
            sb.append("| ");
            for (int x = 0; x < boardSize; x++) {
                Token t = space[y][x];
                sb.append(t != null ? t : ' ').append(' ');
            }
            sb.append("|\n");
        }

        sb
                .append("|")
                .repeat('-', boardSize * 2 + 1)
                .append("|\n");

        sb.append("| ");
        for (int x = 0; x < boardSize; x++) {
            sb.append(x).append(' ');
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
                    if (Objects.equals(b.space[y][x], this.space[y][x])){
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
