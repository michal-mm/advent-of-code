package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D07_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D07_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));

        var grid = parseInput(input);
        var solution = tachyonBeam(grid);

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

        long expected = 21;
        long solution = tachyonBeam(grid);

        assertEquals(expected, solution);
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

    int tachyonBeam(List<List<Character>> grid) {

        List<Integer> positionsToProcess = List.of(grid.getFirst().indexOf('S'));
        AtomicInteger numOfSplits = new AtomicInteger(0);

        for (int i=1; i<grid.size()-1; i++) {
            var row = grid.get(i);
            positionsToProcess = getNewPositions(row, positionsToProcess, numOfSplits);
        }

        return numOfSplits.get();
    }

    List<Integer> getNewPositions(List<Character> line, List<Integer> toProcess, AtomicInteger numOfSplits) {
        Set<Integer> newItemsToProcess = new HashSet<>();

        for (Integer item : toProcess) {
            if (line.get(item) == '^') {
                newItemsToProcess.add(item - 1);
                line.set(item-1, '|');
                newItemsToProcess.add(item + 1);
                line.set(item+1, '|');
                numOfSplits.incrementAndGet();
            } else {
                line.set(item, '|');
                newItemsToProcess.add(item);
            }
        }

        return newItemsToProcess.stream()
                .sorted()
                .toList();
    }
}
