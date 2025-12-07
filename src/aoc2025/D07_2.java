package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D07_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D07_2_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var grid = parseInput(input);
        var solution = getNumOfDifferentRoutes(grid);
        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                .......S.......
                ...............
                .......^.......
                ...............
                ......^.^......
                ...............
                .....^.^.^.....
                ...............
                ....^.^...^....
                ...............
                ...^.^...^.^...
                ...............
                ..^...^.....^..
                ...............
                .^.^.^.^.^...^.
                ...............
                """;

        var grid = parseInput(input);
        var expected = 40;

        var solution = getNumOfDifferentRoutes(grid);

        assertEquals(expected, solution);

    }

    long getNumOfDifferentRoutes(List<List<Character>> grid) {

        var startIdx = grid.getFirst().indexOf('S');
        Map<Integer, Long> toProcess = new HashMap<>();
        toProcess.put(startIdx, 1L);

        for (int i=1; i<grid.size()-1; i++) {
            var row = grid.get(i);
            toProcess =  getNewPositions(row, toProcess);
        }

        return toProcess.values().stream()
                .mapToLong(i -> i)
                .sum();
    }

    List<List<Character>> parseInput(String input) {
        List<List<Character>> grid = new ArrayList<>();

        var scanner = new Scanner(input);
        while(scanner.hasNextLine()) {
            List<Character> row = new ArrayList<>();
            grid.add(row);
            for (char c : scanner.nextLine().toCharArray()) {
                row.add(c);
            }
        }

        return grid;
    }

    private Map<Integer, Long> getNewPositions(List<Character> row,
                                                      Map<Integer, Long> toProcess) {
        Map<Integer, Long> newToProcess = new HashMap<>();

        for (Map.Entry<Integer, Long> entry : toProcess.entrySet()) {
            var idx = entry.getKey();
            var numOfPaths = entry.getValue();

            if (row.get(idx) == '^') {
                var leftVal = newToProcess.getOrDefault(idx-1, 0L) + numOfPaths;
                newToProcess.put(idx-1, leftVal);
                var rightVal = newToProcess.getOrDefault(idx+1, 0L) + numOfPaths;
                newToProcess.put(idx+1, rightVal);
            } else {
                newToProcess.put(idx, newToProcess.getOrDefault(idx, 0L) + numOfPaths);
            }
        }

        return newToProcess;

    }
}
