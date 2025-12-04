package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D04_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D04_1_input.txt";


    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));

        var grid = parseInput(input);
        analyzeGrid(grid);
        IO.println("Solution: " + getNumOfAccessibleRolls(grid));
    }

    @Test
    void testExample() {
        var input = """
                ..@@.@@@@.
                @@@.@.@.@@
                @@@@@.@.@@
                @.@@@@..@.
                @@.@@@@.@@
                .@@@@@@@.@
                .@.@.@.@@@
                @.@@@.@@@@
                .@@@@@@@@.
                @.@.@@@.@.
                """;
        long expected = 13;

        var grid = parseInput(input);
        analyzeGrid(grid);
        var solution = getNumOfAccessibleRolls(grid);

        assertEquals(expected, solution);
    }

    long getNumOfAccessibleRolls(List<List<Field>> grid) {
        return grid.stream()
                .flatMap(Collection::stream)
                .filter(Field::isAccessible)
                .count();
    }

    void analyzeGrid(List<List<Field>> grid) {
        IntStream.range(0, grid.size())
                .forEach(row ->
                        IntStream.range(0, grid.get(row).size())
                                .forEach(col -> analyzeField(grid, row, col)));
    }

    void analyzeField(List<List<Field>> grid, int row, int col) {
        var indices = List.of(
                new int[]{row - 1, col - 1}, new int[]{row - 1, col}, new int[]{row - 1, col + 1},
                new int[]{row, col - 1}, new int[]{row, col + 1},
                new int[]{row + 1, col - 1}, new int[]{row + 1, col}, new int[]{row + 1, col + 1});

        var rollsOfPaper = indices.stream()
                .filter(item -> fieldExists(grid, item[0], item[1]))
                .map(item -> grid.get(item[0]).get(item[1]))
                .filter(RollOfPaper.class::isInstance)
                .count();

        if (rollsOfPaper < 4) {
            grid.get(row).get(col).setAccesible();
        }
    }

    private boolean fieldExists (List<List<Field>> grid, int row, int col) {
        if (row < 0 || row >= grid.size()) {
            return false;
        } else {
            var list = grid.get(row);
            return col >= 0 && col < list.size();
        }
    }

    private List<List<Field>> parseInput(String input) {
        List<List<Field>> grid = new ArrayList<>();

        var scanner = new Scanner(input);
        while (scanner.hasNext()) {
            List<Field> row = new ArrayList<>();
            for (char c : scanner.nextLine().toCharArray()) {
                switch (c) {
                    case '@' -> row.add(new RollOfPaper());
                    case '.' -> row.add(new Empty());
                }
            }
            grid.add(row);
        }

        return grid;
    }

    abstract sealed class Field permits RollOfPaper, Empty {
        public boolean accesible = false;

        boolean isAccessible () {
            return accesible;
        }

        abstract void setAccesible();
    }

    final class RollOfPaper extends Field {
        public void setAccesible() {
            accesible = true;
        }
    }

    final class Empty extends Field {
        public void setAccesible() {
            accesible = false;
        }
    }
}
