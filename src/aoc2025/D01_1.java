package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D01_1 {

    int calculatePassword(String input) {
        int result = 0;
        var inputList = parseInput(input);
        int index = 50;

        for (Rotation r : inputList) {
            index = switch (r.direction) {
                case LEFT -> rotateLeft(index, r.number);
                case RIGHT -> rotateRight(index, r.number);
            };
            if (index == 0) result++;
        }

        return result;
    }

    int rotateLeft(int from, int number) {
        number = number % 100;
        return from < number ? (100+from-number) % 100 : from-number;
    }

    int rotateRight(int from, int number) {
        return (from + number) % 100;
    }

    @Test
    public void testSpec() {
        var input = """
                L68
                L30
                R48
                L5
                R60
                L55
                L1
                L99
                R14
                L82
                """;

        int expected = 3;

        assertEquals(expected, calculatePassword(input));
    }

    @Test
    public void testPuzzle() throws IOException {
        var input = Files.readString(Path.of("resources/aoc2025/D01_1_input.txt"));
        int expected = 1168;

        assertEquals(expected, calculatePassword(input));
    }



    private List<Rotation> parseInput(String input) {
        List<Rotation> rotations = new ArrayList<>();

        var scanner = new Scanner(input);

        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            var rotation = line.charAt(0) == 'L' ? Direction.LEFT : Direction.RIGHT;
            var number = Integer.valueOf(line.substring(1));

            rotations.add(new Rotation(rotation, number));
        }

        return rotations;
    }

    enum Direction {
        LEFT, RIGHT
    }

    record Rotation (Direction direction, Integer number) {}
}
