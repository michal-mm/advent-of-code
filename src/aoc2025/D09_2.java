package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class D09_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D09_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var redTiles = parseInput(input);
        var solution = largestRedRectangle(redTiles);

        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                7,1
                11,1
                11,7
                9,7
                9,5
                2,5
                2,3
                7,3
                """;

        var redTiles = parseInput(input);

        var expected = 24L;
        var result = largestRedRectangle(redTiles);

        assertEquals(expected, result);
    }

    @Test
    void testTileInside() {
        var t = new Tile(2,1);

        var p1 = new Tile(5,9);
        var p2 = new Tile(3,2);
        var p3 = new Tile(3,9);
        var p4 = new Tile(5,2);
        var p5 = new Tile(3, 3);


        var input = """
                7,1
                11,1
                11,7
                9,7
                9,5
                2,5
                2,3
                7,3
                """;

        var redTiles = parseInput(input);

        boolean expected = false;
        assertEquals(expected, tileIsInside(redTiles, t));
        assertTrue(tileIsInside(redTiles, p5));
        assertTrue(tileIsInside(redTiles, p1));
        assertTrue(tileIsInside(redTiles, p2));
        assertTrue(tileIsInside(redTiles, p3));
        assertTrue(tileIsInside(redTiles, p4));

    }

    long largestRedRectangle(List<Tile> redTiles) {
        var minX = 0;
        var minY = 0;
        var maxY = redTiles.stream().mapToInt(Tile::y).max().orElse(0);

        var leftUpperCorner = new Tile(minX, minY);
        var leftLowerCorner = new Tile(minX, maxY);

        var closestLeftUpperTiles = getTilesClosestToCorner(redTiles, leftUpperCorner);
        var closestLeftLowerTiles = getTilesClosestToCorner(redTiles, leftLowerCorner);

        return Math.max(getMaxSizeRectangle(redTiles, closestLeftLowerTiles),
                getMaxSizeRectangle(redTiles, closestLeftUpperTiles));
    }

    private long getMaxSizeRectangle(List<Tile> redTiles, List<Tile> sortedRedtiles) {
        long maxSize = 0;

        for (int i=0; i<sortedRedtiles.size()-1; i++) {
            var p1 = sortedRedtiles.get(i);
            for (int j=sortedRedtiles.size()-1; j>i; j--) {
                var p2 = sortedRedtiles.get(j);
                var size = size(p1, p2);
                var p1prime = new Tile(p1.x(), p2.y());
                var p2prime = new Tile(p2.x(), p1.y());

                if (tileIsInside(redTiles, p1prime) &&
                        tileIsInside(redTiles, p2prime) &&
                        squareIsInside(redTiles, p1, p1prime, p2, p2prime) &&
                        size > maxSize) {
                    maxSize = size;
                }
            }
        }

        return maxSize;
    }

    private boolean squareIsInside(List<Tile> redTiles, Tile p1, Tile p2, Tile p3, Tile p4) {
        var prevT = redTiles.getLast();
        var currT = prevT;
        var idx = 0;

        do {
            currT = redTiles.get(idx);

            if (edgesIntersect(prevT, currT, p1, p2) ||
                    edgesIntersect(prevT, currT, p2, p3) ||
                    edgesIntersect(prevT, currT, p3, p4) ||
                    edgesIntersect(prevT, currT, p4, p1)) {
                return false;
            }

            idx++;
            prevT = currT;
        } while (idx < redTiles.size());

        return true;
    }

    private boolean edgesIntersect(Tile redEdgeT1, Tile redEdgeT2, Tile edgeT1, Tile edgeT2) {
        if (redEdgeT1.x() == redEdgeT2.x() && edgeT1.y() == edgeT2.y()) {
            return Math.min(edgeT1.x(), edgeT2.x()) < redEdgeT1.x() &&
                    redEdgeT1.x() < Math.max(edgeT1.x(), edgeT2.x()) &&
                    Math.min(redEdgeT1.y(), redEdgeT2.y()) < edgeT1.y() &&
                    edgeT1.y() < Math.max(redEdgeT1.y(), redEdgeT2.y());

        } else if (redEdgeT1.y() == redEdgeT2.y() && edgeT1.x() == edgeT2.x()) {
            return Math.min(edgeT1.y(), edgeT2.y()) < redEdgeT1.y() &&
                    redEdgeT1.y() < Math.max(edgeT1.y(), edgeT2.y()) &&
                    Math.min(redEdgeT1.x(), redEdgeT2.x()) < edgeT1.x() &&
                    edgeT1.x() < Math.max(redEdgeT1.x(), redEdgeT2.x());
        } else {
            return false;
        }
    }

    private boolean tileIsInside(List<Tile> redTiles, Tile t) {
        var prevT = redTiles.getLast();
        var currT = prevT;

        var xUp = 0;
        var xDown = 0;
        var xLeft = 0;
        var xRight = 0;

        if (isRedTile(t, currT)) return true;

        for (Tile redTile : redTiles) {
            currT = redTile;
            if (isRedTile(t, currT)) return true;
            if (isOnTheLine(t, prevT, currT)) return true;

            if (isCrossingRightToLeft(t, prevT, currT)) {
                xLeft++;
            } else if (isCrossingLeftToRight(t, prevT, currT)) {
                xRight++;
            } else if (isCrossingBottomUp(t, prevT, currT)) {
                xUp++;
            } else if (isCrossingTopDown(t, prevT, currT)) {
                xDown++;
            }

            prevT = currT;
        }

        return xLeft * xRight * xUp * xDown != 0;
    }

    private static boolean isCrossingTopDown(Tile t, Tile prevT, Tile currT) {
        return t.x() <= Math.min(prevT.x(), currT.x()) &&
                Math.min(prevT.y(), currT.y()) <= t.y() &&
                t.y() <= Math.max(prevT.y(), currT.y());
    }

    private static boolean isCrossingBottomUp(Tile t, Tile prevT, Tile currT) {
        return Math.max(prevT.x(), currT.x()) <= t.x() &&
                Math.min(prevT.y(), currT.y()) <= t.y() &&
                t.y() <= Math.max(prevT.y(), currT.y());
    }

    private static boolean isCrossingLeftToRight(Tile t, Tile prevT, Tile currT) {
        return t.y() <= Math.min(prevT.y(), currT.y()) &&
                Math.min(prevT.x(), currT.x()) <= t.x() &&
                t.x() <= Math.max(prevT.x(), currT.x());
    }

    private static boolean isCrossingRightToLeft(Tile t, Tile prevT, Tile currT) {
        return Math.max(prevT.y(), currT.y()) <= t.y() &&
                Math.min(prevT.x(), currT.x()) <= t.x() &&
                t.x() <= Math.max(prevT.x(), currT.x());
    }

    private static boolean isOnTheLine(Tile t, Tile prevT, Tile currT) {
        return Math.min(prevT.x(), currT.x()) <= t.x() &&
                t.x() <= Math.max(prevT.x(), currT.x()) &&
                Math.min(prevT.y(), currT.y()) <= t.y() &&
                t.y() <= Math.max(prevT.y(), currT.y());
    }

    private static boolean isRedTile(Tile t, Tile currT) {
        return Objects.equals(currT, t);
    }

    private List<Tile> getTilesClosestToCorner(List<Tile> tiles, Tile corner) {
        return tiles.stream()
                .sorted(Comparator.comparing(tile -> tile.distance(corner)))
                .toList();
    }

    List<Tile> parseInput(String input) {
        List<Tile> tiles = new ArrayList<>();

        try (var scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                var coords = scanner.nextLine().split(",");
                var x = Integer.parseInt(coords[1]);
                var y = Integer.parseInt(coords[0]);
                var tile = new Tile(x, y);
                tiles.add(tile);
            }
        }

        return tiles;
    }


    private long size(Tile t1, Tile t2) {
        long x = Math.abs(t1.x() - t2.x()) + 1L;
        long y = Math.abs(t1.y() - t2.y()) + 1L;

        return x * y;
    }

    record Tile(int x, int y) {
        public long distance(Tile t) {
            return Math.abs(t.x - x) + Math.abs(t.y - y);
        }
    }
}
