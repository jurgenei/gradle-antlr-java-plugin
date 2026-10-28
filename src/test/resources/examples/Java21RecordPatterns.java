class Java21RecordPatterns {

    record Point(int x, int y) {
    }

    record Segment(Point from, Point to) {
    }

    int describe(Object value) {
        if (value instanceof Point(int x, int y) point) {
            return x == y ? x : point.x() + point.y();
        }
        return -1;
    }

    int switchDescribe(Object value) {
        return switch (value) {
            case Point(int x, int y) p when x == y -> p.x();
            case Segment(Point(int x1, int y1), Point(int x2, int y2)) -> x1 + y1 + x2 + y2;
            case Point(var x, var y) -> x - y;
            default -> -1;
        };
    }
}
