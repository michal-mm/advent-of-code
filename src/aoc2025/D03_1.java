package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D03_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D03_1_input.txt";

    void main() throws IOException {
        var inputString = Files.readString(Path.of(INPUT_FILE));

        IO.println(calculateTotalJoltage(inputString.transform(this::parseInput)));
    }

    public int calculateTotalJoltage (List<MaxPair> ratigs) {
        return ratigs.stream()
                .map(this::getJoltageRating)
                .mapToInt(i -> i)
                .sum();
    }

    @Test
    public void testExample() {
        String input = """
                987654321111111
                811111111111119
                234234234234278
                818181911112111
                """;
        
        var inputAsList = input.transform(this::parseInput);
        int expected = 357;

        assertEquals(expected, calculateTotalJoltage(inputAsList));
    }

    @Test
    public void testExample2() {
        String input = """
                818181911112111
                """;

        var inputAsList = input.transform(this::parseInput);
        int expected = 92;

        assertEquals(expected, calculateTotalJoltage(inputAsList));
    }

    @Test
    public void testJoltage() {
        MaxPair mp = new MaxPair(2, 9);
        int expected = 29;

        assertEquals(expected, getJoltageRating(mp));
    }

    @Test
    public void testGetNewMaxPair() {
        MaxPair mp = new MaxPair(1,2);
        MaxPair newMP = mp.newMaxPair(9);
        MaxPair expected = new MaxPair(2, 9);

        assertEquals(expected, newMP);
    }

    private int getJoltageRating(MaxPair maxPair){
        return maxPair.getJoltage();
    }

    private List<MaxPair> parseInput(String input) {
        List<MaxPair> listOfIntArray = new ArrayList<>();


        var scanner = new Scanner(input);

        while(scanner.hasNext()) {
            MaxPair maxPair = new MaxPair(0, 0);

            var  items = scanner.nextLine().chars()
                    .map(Character::getNumericValue)
                    .boxed()
                    .toList();

            for (int val : items) {
                maxPair = maxPair.newMaxPair(val);
            }

            listOfIntArray.add(maxPair);
        }

        return listOfIntArray;
    }

    public record MaxPair (int firstNum, int secondNum) {
        public MaxPair newMaxPair(int val) {
            if (val > Math.min(firstNum, secondNum)) {
                return new MaxPair(Math.max(firstNum, secondNum), val);
            } else if (firstNum < secondNum) {
                return new MaxPair(secondNum, val);
            } else {
                return this;
            }
        }

        public int getJoltage() {
            return firstNum * 10 + secondNum;
        }
    }
}
