package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D11_2 {

    private static final String START = "svr";
    private  static final String END = "out";
    private static final Set<String> MUST_VISIT = Set.of("dac","fft");

    private static final String INPUT_FILE = "resources/aoc2025/D11_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var mapping = parseInput(input);
        var solution = getNumOfPathsFromTo(mapping, START, END);
        IO.println("Solution: " + solution);
        // 502447498690860
    }

    @Test
    void testExample() {
        var input = """
                svr: aaa bbb
                aaa: fft
                fft: ccc
                bbb: tty
                tty: ccc
                ccc: ddd eee
                ddd: hub
                hub: fff
                eee: dac
                dac: fff
                fff: ggg hhh
                ggg: out
                hhh: out
                """;
        var mapping = parseInput(input);
        var result = getNumOfPathsFromTo(mapping, START, END);

        var expected = 2L;

        assertEquals(expected, result);

    }

    long getNumOfPathsFromTo(Map<String, Set<String>> mapping, String from, String to) {
        Map<FromToPair, Long> cache = new HashMap<>();
        Set<String> toVisit = new HashSet<>(MUST_VISIT);
        return getNumOfPathsFromToThrough(mapping, from, to, cache, toVisit);
    }

    long getNumOfPathsFromToThrough(Map<String, Set<String>> mapping,
                                    String from, String to,
                                    Map<FromToPair, Long> cache,
                                    Set<String> toVisit){

        var fromToPair= new FromToPair(from, to, new HashSet<>(toVisit));
        if (cache.containsKey(fromToPair)){
            return cache.get(fromToPair);
        }

        Set<String> remainingToVisit = new HashSet<>(toVisit);
        remainingToVisit.remove(from);

        var result = 0L;
        if (from.equals(to)) {
            result = remainingToVisit.isEmpty() ? 1L : 0L;
        } else {
            result = mapping.get(from).stream()
                    .mapToLong(child ->
                            getNumOfPathsFromToThrough(mapping, child, to, cache, remainingToVisit))
                    .sum();
        }

        cache.put(fromToPair, result);
        return result;
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

    record FromToPair(String from, String to, Set<String> toVisit) {}
}
