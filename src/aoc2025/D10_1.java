package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D10_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D10_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var machines = parseInput(input);
        var solution = getMinNumOfSwitches(machines);

        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
                [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
                [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
                """;
        var machines = parseInput(input);
        machines.forEach(IO::println);
        var expected = 7L;
        var result = getMinNumOfSwitches(machines);

        assertEquals(expected, result);
    }

    @Test
    void testBiggestExample() {
        var input = """
                [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
                """;

        var machines = parseInput(input);

        var expected = 2L;
        var result = getMinNumOfSwitches(machines);

        assertEquals(expected, result);
    }

    @Test
    void testIndexMask() {
        var machine = parseLineToMachine("[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}".split(" "));
        var mask = List.of(false, true, true, false);
        var buttons = Set.of(List.of(3),
                List.of(1, 3),
                List.of(2),
                List.of(2,3),
                List.of(0,2),
                List.of(0,1));
        var joltage = List.of(3,5,4,7);

        assertEquals(mask, machine.getLightsIndicatorMask());
        assertEquals(buttons, machine.getButtons());
        assertEquals(joltage, machine.getJoltage());
    }

    long getMinNumOfSwitches(List<Machine> machines) {
        return machines.stream()
                .mapToLong(this::getMinNumOfSwitches)
                .sum();
    }

    List<Machine> parseInput(String input) {
        List<Machine> machines = new ArrayList<>();

        try(var scanner = new Scanner(input)) {
            while(scanner.hasNextLine()) {
                machines.add(parseLineToMachine(scanner.nextLine().split(" ")));
            }
        }

        return machines;
    }

    private int getMinNumOfSwitches(Machine machine) {
        Queue<List<List<Integer>>> toProcess = new LinkedList<>();
        toProcess.add(List.of(machine.getMask()));

        while (!toProcess.isEmpty()) {
            var buttonSeq = toProcess.poll();
            var calcResult = buttonSeq.stream()
                    .reduce(this::click)
                    .orElse(buttonSeq.getFirst());

            var allButtons = machine.getButtons();

            if (allButtons.contains(calcResult)) {
                return buttonSeq.size();
            }

            buttonSeq.forEach(allButtons::remove);
            for (List<Integer> b : allButtons) {
                var combninedList = new ArrayList<>(List.copyOf(buttonSeq));
                combninedList.add(b);
                toProcess.add(combninedList);
            }
        }

        return 0;
    }


    private List<Integer> click(List<Integer> state, List<Integer> button) {
        var set1 = Set.copyOf(state);
        var set2 = Set.copyOf(button);

        return Stream.concat(
               set1.stream().filter(e -> !set2.contains(e)),
               set2.stream().filter(e -> !set1.contains(e))
        ).sorted().toList();
    }

    private Machine parseLineToMachine(String[] line) {
        var machine = new Machine();

        // get mask, skip [] characters
        var lightsIndicatorMask = splitToItemsSkipingfirstAndLastChar(line[0], "");
        machine.setLigthsIndicatorMask(lightsIndicatorMask);

        // get joltage mask, skip
        var joltageList = splitToItemsSkipingfirstAndLastChar(line[line.length-1], ",");
        machine.setJoltageList(joltageList);

        List<List<String>> buttons = new ArrayList<>();
        IntStream.range(1, line.length-1)
                .forEach(i -> buttons.add(splitToItemsSkipingfirstAndLastChar(line[i], ",")));
        machine.setButtons(buttons);

        return machine;
    }

    private List<String> splitToItemsSkipingfirstAndLastChar(String item, String splitBy) {
        return List.of(item.substring(1, item.length() - 1).split(splitBy));
    }

    static class Machine {

        private List<Boolean> lightsIndicatorMask;
        private List<Integer> mask;
        private Set<List<Integer>> buttons;
        private List<Integer> joltage;

        void setLigthsIndicatorMask (List<String> mask) {
            lightsIndicatorMask = mask.stream()
                    .map(s -> s.equals("#"))
                    .toList();

            this.mask = IntStream.range(0, lightsIndicatorMask.size())
                    .filter(i -> lightsIndicatorMask.get(i) == true)
                    .boxed()
                    .toList();
        }

        List<Integer> getMask() {
            return mask;
        }

        List<Boolean> getLightsIndicatorMask() {
            return lightsIndicatorMask;
        }

        void setJoltageList(List<String> joltageStr) {
            joltage = joltageStr.stream()
                    .map(Integer::parseInt)
                    .toList();
        }

        List<Integer> getJoltage() {
            return joltage;
        }

        void setButtons(List<List<String>> buttonsStr) {
            buttons = buttonsStr.stream()
                    .map(f -> f.stream()
                            .map(Integer::parseInt)
                            .toList())
                    .collect(Collectors.toSet());
        }

        Set<List<Integer>> getButtons() {
            Set<List<Integer>> newSet= new HashSet<>(buttons.size());
            newSet.addAll(buttons);

            return newSet;
        }

        public String toString() {
            return mask.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",", "<", ">"));
        }
    }
}
