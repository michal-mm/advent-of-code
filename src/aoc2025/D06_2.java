package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D06_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D06_2_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var problems = parseInput(input);

//        problems.forEach(IO::println);

        var solution = getTotalResult(problems);


        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                123 328  51 64\s
                 45 64  387 23\s
                  6 98  215 314
                *   +   *   + \s
                """;

        long expected = 3263827L;

        var problems = parseInput(input);
        problems.forEach(IO::println);

        assertEquals(expected, getTotalResult(problems));

    }

    @Test
    void testChunkSizeParsing() {
        var line = "  6 98  215 314";
        var chunkSize = 4;
        var expected = List.of("  6", "98 ", "215", "314");
        assertEquals(expected, parseToChunks(line, chunkSize));
    }

    @Test
    void testParsingChunksOfLongs() {
        var line = "123 328  51 64 ";
        var chunkSize = 4;
        var expected = List.of("123", "328", " 51", "64 ");
        assertEquals(expected, parseToChunks(line, chunkSize));
    }

    long getTotalResult(List<Problem> problems) {
        return problems.stream()
                .mapToLong(Problem::calculate)
                .sum();
    }

    List<Problem> parseInput(String input) {
        List<Problem> problems = new ArrayList<>();

        List<StringBuilder> columns = new ArrayList<>();

        var scanner = new Scanner(input);
        while (scanner.hasNext()) {
            var line  = scanner.nextLine();

            if (columns.isEmpty()) {
                IntStream.range(0, line.length())
                        .forEach(_ -> columns.add(new StringBuilder()));
            }

            if (line.matches(".*\\d.*")) {
                IntStream.range(0, line.length())
                        .forEach(i -> columns.get(i).append(line.charAt(i)));
            } else {
                assignStringsAndOperatorsToNumbers(columns, problems, line);
                /*var start = 0;
                var end = 1;
                var operator = getOperator(String.valueOf(line.charAt(start)));
                var problem = new Problem();
                problems.add(problem);
                problem.setOperator(operator);
                for (int i=1; i<line.length(); i++, end++) {
                    if (line.charAt(i) == '+' || line.charAt(i) == '*') {
                        // we have next operator
                        for (int idx = start; idx < end; idx++) {
                            problem.addNumber(columns.get(idx).toString());
                        }
                        start = i;
                        operator = getOperator(String.valueOf(line.charAt(start)));
                        problem = new Problem();
                        problems.add(problem);
                        problem.setOperator(operator);
                    }
                }
                for (int idx = start; idx < end; idx++) {
                    problem.addNumber(columns.get(idx).toString());
                }
*/
            }
        }

        return problems;
    }

    private void assignStringsAndOperatorsToNumbers(List<StringBuilder> columns,
                                                    List<Problem> problems,
                                                    String line) {

        var start = 0;
        var end = 1;
        var problem = new Problem();
        problem.setOperator(getOperator(line.substring(start, end)));
        problem.addNumber(columns.get(start).toString());
        problems.add(problem);

        for (int i=1; i<line.length(); i++, end++) {
            problem = problems.getLast();
            if (i == 3677) {
                IO.println("NOW!");
            }
            if (line.charAt(i) == '+' || line.charAt(i) == '*') {
                problem = new Problem();
                problem.setOperator(getOperator(line.substring(i, i+1)));
                problem.addNumber(columns.get(i).toString());
                problems.add(problem);
            } else {
                problem.addNumber(columns.get(i).toString());
            }
        }

        if (end < columns.size()) {
            IntStream.range(end, columns.size())
                    .forEach(i -> {
                        var p = problems.getLast();
                        p.addNumber(columns.get(i).toString());
                    });
        }
    }

    private Operator getOperator(String s) {
        return switch (s) {
            case "+" -> Operator.ADD;
            case "*" -> Operator.MULTIPLY;
            default -> Operator.OTHER;
        };
    }

    private List<String> parseToChunks(String line, int numOfchunks) {
        var chunkSize = line.length() / numOfchunks;
//        IO.println(line + "] -- cs=" + chunkSize + " #c=" + numOfchunks + " ll=" + line.length());

        List<String> chunks = new ArrayList<>(numOfchunks);
        var numOfWhiteSpaces = 0;
        for(int i=0; i<numOfchunks; i++, numOfWhiteSpaces++) {
            var start = i*chunkSize+numOfWhiteSpaces;
            try {
                var substr = line.substring(start, start + chunkSize);
//            IO.println("Adding: [" + substr + "]");
                chunks.add(substr);
            } catch (Exception e) {
                IO.println(line);
                IO.println(e.getMessage());
            }
        }

        return chunks;
    }


    enum Operator { ADD, MULTIPLY, OTHER}

    static class Problem{
        private Operator operator;

        private final List<String> numbers = new ArrayList<>();

        void setOperator(Operator operator) {
            this.operator = operator;
        }

        void addNumber(String number) {
            numbers.add(number);
        }

        long calculate() {
            return switch (operator) {
                case ADD -> sum();
                case MULTIPLY -> multiply();
                case OTHER -> -1L;
            };
        }

        private long sum() {
            return numbers.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToLong(Long::parseLong)
                    .sum();
        }

        private long multiply() {
            return numbers.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToLong(Long::parseLong)
                    .reduce(1L, (a,b) -> a * b);
        }

        public String toString() {
            return String.join(",", numbers) + " -> " + operator.toString();
        }
    }
}
