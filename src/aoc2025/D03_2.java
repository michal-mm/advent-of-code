package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D03_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D03_2_input.txt";

    void main() throws IOException {

        var totalJoltage = Files.readString(Path.of(INPUT_FILE))
                .transform(this::parseInput)
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        IO.println("Total joltage: " + totalJoltage);
    }

    private List<Long> parseInput(String input) {
        List<Long> list = new ArrayList<>();

        var scanner = new Scanner(input);

        while (scanner.hasNext()) {
            var allDigits = scanner.nextLine().chars()
                    .mapToObj(Character::getNumericValue)
                    .mapToInt(i -> i)
                    .toArray();

            int[] max = new int[12];
            var selected = -1;

            for (int pos=0; pos<12; pos++) {
                selected = indexOfMaxLeftElem(allDigits, selected+1, allDigits.length-12+pos);
                max[pos] = allDigits[selected];
            }

            list.add(toLong(max));
        }

        return list;
    }

    private Long toLong(int [] nums) {
        return Arrays.stream(nums)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining())
                .transform(Long::parseLong);
    }

    private int indexOfMaxLeftElem(int[] array, int from, int to) {
        var max = Integer.MIN_VALUE;
        var result = -1;

        for (int i=from; i<=to; i++) {
            if (array[i] > max) {
                max = array[i];
                result = i;
            }
        }

        return result;
    }

    @Test
    public void testExample() {
        String input = """
                987654321111111
                811111111111119
                234234234234278
                818181911112111
                """;
        long expected = 3_121_910_778_619L;

        var result = (Long) parseInput(input)
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        assertEquals(expected, result);
    }
}
