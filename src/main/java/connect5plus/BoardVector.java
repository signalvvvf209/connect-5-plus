package connect5plus;

public record BoardVector(int dx, int dy) {
    BoardVector add(BoardVector vector) {
        return new BoardVector(dx + vector.dx, dy + vector.dy);
    }

    BoardVector multiply(int n) {
        return new BoardVector(n * dx, n * dy);
    }

    BoardVector left90() {
        return new BoardVector(-dy, dx);
    }

    BoardVector right90() {
        return new BoardVector(dy, -dx);
    }
}
