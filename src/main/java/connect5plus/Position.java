package connect5plus;

public record Position(int x, int y) {
    Position offset(BoardVector vector) {
        return new Position(x + vector.dx(), y + vector.dy());
    }
}
