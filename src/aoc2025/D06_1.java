package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D06_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D06_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var problems = parseInput(input);

        IO.println("Solution: " + getTotalResult(problems));
    }

    @Test
    void testExample() {
        var input = """
                123 328  51 64 
                 45 64  387 23 
                  6 98  215 314
                *   +   *   + 
                """;

        long expected = 4277556L;
        var problems = parseInput(input);

        assertEquals(expected, getTotalResult(problems));

    }

    @Test
    void testParseLineOfOperators() {
        var line = " *   +   *   + ";
        var expected = List.of(MathFunction.MULTIPLY,
                MathFunction.ADD, MathFunction.MULTIPLY, MathFunction.ADD);

        assertEquals(expected, getAllFunctionsFromLine(line));
    }

    @Test
    void testAdd() {
        Problem p = new Problem();
        p.setFunction(MathFunction.ADD);
        p.addNumber(1L);
        p.addNumber(2L);
        p.addNumber(4L);

        var expected = 7L;
        assertEquals(expected, p.calculate());
    }

    @Test
    void testMultiply() {
        Problem p = new Problem();
        p.setFunction(MathFunction.MULTIPLY);
        p.addNumber(1L);
        p.addNumber(2L);
        p.addNumber(4L);

        var expected = 8L;
        assertEquals(expected, p.calculate());
    }

    @Test
    void testParseLineOfLongs() {
        var line = " 45 64  387 23 ";
        var expected = List.of(45L, 64L, 387L, 23L);
        assertEquals(expected, getAllLongsFromLine(line));
    }

    long getTotalResult(List<Problem> problems) {
        return problems.stream()
                .mapToLong(Problem::calculate)
                .sum();
    }

    List<Problem> parseInput(String input) {
        List<Problem> problems = new ArrayList<>();

        var scanner = new Scanner(input);
        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            IO.println("line: " + line);
            if (line.matches(".*\\d.*")) {
                var allItems = getAllLongsFromLine(line);
                if (problems.isEmpty()) {
                    initProblemList(problems, allItems.size());
                }
                addAllNumbersToProblems(problems, allItems);
            } else {
                var allFunctions = getAllFunctionsFromLine(line);
                addAllFunctionsToProblems(problems, allFunctions);
            }
        }

        return problems;
    }

    private void addAllFunctionsToProblems(List<Problem> problems, List<MathFunction> functions) {
        for (int i=0; i<functions.size(); i++) {
            problems.get(i).setFunction(functions.get(i));
        }
    }

    private List<MathFunction> getAllFunctionsFromLine(String line) {
        return Arrays.stream(line.trim().split("\\s+"))
                .map(s -> switch (s) {
                    case "+" -> MathFunction.ADD;
                    case "*" -> MathFunction.MULTIPLY;
                    default -> MathFunction.OTHER;
                })
                .toList();
    }

    private void addAllNumbersToProblems(List<Problem> problems, List<Long> numbers) {
        for (int i=0; i<numbers.size(); i++) {
            problems.get(i).addNumber(numbers.get(i));
        }
    }

    private void initProblemList(List<Problem> problems, int size) {
        IntStream.range(0, size)
                .mapToObj(_ -> new Problem())
                .forEach(problems::add);
    }

    private List<Long> getAllLongsFromLine(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .map(Long::parseLong)
                .toList();
    }

    enum MathFunction { ADD, MULTIPLY, OTHER}

    static class Problem {
        private final List<Long> items = new ArrayList<>();
        private MathFunction function;

        void addNumber(long number) {
            this.items.add(number);
        }

        void setFunction(MathFunction function) {
            this.function = function;
        }

        long calculate() {
            return switch (function) {
                case ADD -> add();
                case MULTIPLY -> multiply();
                case OTHER -> -1L;
            };
        }

        private long add() {
            return items.stream()
                    .mapToLong(l -> l)
                    .sum();
        }

        private long multiply() {
            return items.stream()
                    .reduce(1L, (a, b) -> a*b);
        }
    }
}
