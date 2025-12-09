package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D09_1 {

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
        var expected = 50L;
        var largestRedRectangle = largestRedRectangle(redTiles);

        assertEquals(expected, largestRedRectangle);
    }

    long largestRedRectangle(List<Tile> redTiles) {
        var minX = 0;
        var minY = 0;
        var maxX = redTiles.stream().mapToInt(Tile::x).max().orElse(0);
        var maxY = redTiles.stream().mapToInt(Tile::y).max().orElse(0);

        var leftUpperCorner = new Tile(minX, minY);
        var rightUpperCorner = new Tile(maxX, minY);
        var leftLowerCorner = new Tile(minX, maxY);
        var rightLowerCorner = new Tile(maxX, maxY);

        var closestLeftUpperTiles = getTilesClosestToCorner(redTiles, leftUpperCorner);
        var closestRightLowerTiles = getTilesClosestToCorner(redTiles, rightLowerCorner);
        var closestLeftLowerTiles = getTilesClosestToCorner(redTiles, leftLowerCorner);
        var closestRightUpperTiles = getTilesClosestToCorner(redTiles, rightUpperCorner);

        return Math.max(getMaxRectagnleSize(closestLeftUpperTiles, closestRightLowerTiles),
                getMaxRectagnleSize(closestLeftLowerTiles, closestRightUpperTiles));
    }
    
    private Long getMaxRectagnleSize(Set<Tile> t1, Set<Tile> t2) {
        return t1.stream()
                .flatMap(t1Tile -> t2.stream()
                        .map(t2Tile -> size(t1Tile, t2Tile)))
                .mapToLong(l -> l)
                .max()
                .orElse(0L);
    }

    private Set<Tile> getTilesClosestToCorner(List<Tile> tiles, Tile corner) {
        Set<Tile> closestTiles = new HashSet<>();

        var sortedTiles = tiles.stream()
                .sorted(Comparator.comparing(tile -> tile.distance(corner)))
                .toList();

        var lowestDistance = sortedTiles.getFirst().distance(corner);
        for (Tile t : sortedTiles) {
            if (t.distance(corner) == lowestDistance) {
                closestTiles.add(t);
            } else {
                break;
            }
        }

        return closestTiles;
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

    record Tile (int x, int y) {

        public long distance(Tile t) {
            return Math.abs(t.x - x) + Math.abs(t.y - y);
        }
    }
}
