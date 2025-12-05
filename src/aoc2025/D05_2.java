package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D05_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D05_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var allIds = parseInput(input);
        IO.println("Solution: " + getNumOfFreshIds(allIds));
    }

    @Test
    void testExample() {
        var input = """
                3-5
                10-14
                16-20
                12-18
                """;
        long expected = 14L;

        var allIds = parseInput(input);
        var result = getNumOfFreshIds(allIds);

        assertEquals(expected, result);
    }

    @Test
    void edgeCaseInput() {
        var input = """
                3-8
                11-12
                5-6
                7-9
                """;
        // 3-9, 11-12 ==> (7 + 2 = 9)
        long expected = 9L;

        var allIds = parseInput(input);
        var result = getNumOfFreshIds(allIds);

        assertEquals(expected, result);
    }

    long getNumOfFreshIds(Set<Range> ids) {
        return ids.stream()
                .map(Range::rangeSize)
                .mapToLong(l -> l)
                .sum();
    }

    Set<Range> parseInput(String input) {
        Set<Range> allIds = new HashSet<>();

        var scanner = new Scanner(input);
        while(scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.isEmpty()) break;

            var ranges = line.split("-");
            var from = Long.parseLong(ranges[0]);
            var to = Long.parseLong(ranges[1]);

            var range = new Range(from, to);

            validateAndEventuallyAddRangeToSet(allIds, range);
        }

        return allIds;
    }

    void validateAndEventuallyAddRangeToSet (Set<Range> ids, Range r) {
        // special case for empty range set
        if (ids.isEmpty()) {
            ids.add(r);
            return;
        }

        var range = r;

        for (Range existing : List.copyOf(ids)) {
            if (existing.containsRange(range)) {
                // we have wider range that contains the range r - nothing to do
                ids.remove(existing);
                range  = existing;
                break;
            } else if (existing.insideRange(range)) {
                // new range contains existing in it -> remove existing, add r
                ids.remove(existing);
            } else if (existing.haveCommonPart(range)) {
                // we can build bigger wider range from both, remove existing, add existing+r
                ids.remove(existing);
                range = existing.newWiderRange(range);
            }
        }

        ids.add(range);
    }

    record Range (long from, long to) {

        boolean containsRange(Range range) {
            return from <= range.from && range.to <= to;
        }

        boolean insideRange(Range range) {
            return range.from <= from && to <= range.to;
        }

        boolean haveCommonPart(Range range) {
            return (from <= range.from && range.from <= to) ||
                    (from <= range.to && range.to <= to);
        }

        Range newWiderRange(Range r) {
            return new Range(Math.min(from, r.from), Math.max(to, r.to));
        }

        long rangeSize() {
            return to - from + 1;
        }
    }
}
