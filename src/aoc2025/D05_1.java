package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D05_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D05_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        List<Range> idRanges = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        parseInput(input, idRanges, ids);
        IO.println("Solution: " + getNumOfFreshItems(idRanges, ids));
    }

    @Test
    void testExample() {
        var input = """
                3-5
                10-14
                16-20
                12-18
                
                1
                5
                8
                11
                17
                32
                """;
        long expected = 3L;

        List<Range> idRanges = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        parseInput(input, idRanges, ids);
        long result = getNumOfFreshItems(idRanges, ids);

        assertEquals(expected, result);
    }

    long getNumOfFreshItems(List<Range> idRanges, List<Long> ids) {
        return ids.stream()
                .filter(i -> idIsInAnyRange(idRanges, i))
                .count();
    }

    private boolean idIsInAnyRange(List<Range> idRanges, long id) {
        return idRanges.stream()
                .anyMatch(range -> range.inRange(id));
    }

    void parseInput(String input, List<Range> idRanges, List<Long> ids) {
        var scanner = new Scanner((input));

        while (scanner.hasNext()) {
            var line = scanner.nextLine().split("-");

            if (line.length == 2) {
                idRanges.add(new Range(Long.parseLong(line[0]), Long.parseLong(line[1])));
            } else {
                if (line[0].isEmpty()) continue;
                ids.add(Long.parseLong(line[0]));
            }
        }
    }

    record Range(long from, long to) {
        boolean inRange(long val) {
            return from <= val && val <= to;
        }
    }
}
