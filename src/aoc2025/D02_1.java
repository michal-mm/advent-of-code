package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D02_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D02_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));

        IO.println(getSumOfInvalidIDs(parseInput(input)));
    }

    private long getSumOfInvalidIDs(List<Range> idRanges) {
       return idRanges.stream()
               .map(Range::getFalseIds)
               .flatMap(Collection::stream)
               .mapToLong(l -> l)
               .sum();
    }

    @Test
    public void testExample() {
        String input = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224," +
                "1698522-1698528,446443-446449,38593856-38593862,565653-565659," +
                "824824821-824824827,2121212118-2121212124";

        long expected = 1227775554;

        assertEquals(expected, getSumOfInvalidIDs(parseInput(input)));
    }

    private List<Range> parseInput(String input) {

         return Arrays.stream(input.split(","))
                 .map(s -> s.split("-"))
                 .map(items -> new Range(Long.parseLong(items[0]), Long.parseLong(items[1])))
                 .toList();
    }

    record Range (long from, long to) {
        public List<Long> getFalseIds() {
            return LongStream.rangeClosed(from, to)
                    .filter(this::isInvalidID)
                    .boxed()
                    .collect(Collectors.toList());
        }

        public boolean isInvalidID (Long id) {
            String idStr = id.toString();
            var length = idStr.length();

            if (length % 2 != 0) {
                return false;
            } else {
                var head = idStr.substring(0, length/2);
                var tail = idStr.substring(length/2);

                return Objects.equals(head, tail);
            }
        }
    }
}
