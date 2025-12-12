package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D11_1 {

    public static final String START = "you";
    public static final String END = "out";
    private static final String INPUT_FILE = "resources/aoc2025/D11_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var mapping = parseInput(input);
        var solution = getNumOfPathsFromTo(mapping, START, END);

        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                aaa: you hhh
                you: bbb ccc
                bbb: ddd eee
                ccc: ddd eee fff
                ddd: ggg
                eee: out
                fff: out
                ggg: out
                hhh: ccc fff iii
                iii: out
                """;

        var mapping = parseInput(input);
        var result = getNumOfPathsFromTo(mapping, START, END);

        var expected = 5L;

        assertEquals(expected, result);
    }

    long getNumOfPathsFromTo(Map<String, Set<String>> mapping, String from, String to) {
        if (from.equals(to)) {
            return 1L;
        }

        return  mapping.getOrDefault(from, Set.of()).stream()
                .mapToLong(str -> getNumOfPathsFromTo(mapping, str, to))
                .sum();
    }

    Map<String, Set<String>> parseInput(String input) {
        Map<String, Set<String>> mapping = new HashMap<>();

        try(var scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                var items = scanner.nextLine().split("\\s+");
                var from = items[0].substring(0, items[0].length()-1);
                var toSet = mapping.getOrDefault(from, new HashSet<>());
                Arrays.stream(items, 1, items.length)
                        .forEach(toSet::add);
                mapping.put(from, toSet);
            }
        }

        return mapping;
    }
}
