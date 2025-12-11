package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D10_2 {

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
        /// 1  1  1  1  1  0 x S0
        /// 1  0  0  1  1  0 x S1
        /// 1  1  1  0  1  1 x S2
        /// 0  1  1  0  0  0 x S3
        /// -----------------
        /// 10 11 11 5  10 5
        var machines = parseInput(input);
//        machines.forEach(IO::println);
        var expected = 33L;
        var result = getMinNumOfSwitches(machines);

        assertEquals(expected, result);
    }

    long getMinNumOfSwitches(List<Machine> machines) {
        return machines.stream()
                .mapToLong(this::getMinNumOfSwitches)
                .sum();
    }

    int getMinNumOfSwitches(Machine machine) {
        var currentJoltage = machine.getJoltage();

        var minIndices = getMinIndices(currentJoltage);
        var buttons = machine.getButtons();
        Queue<List<Integer>> buttonsToCheckQueue =  new LinkedList<>();
        Queue<Integer> indicesToCheckQueue = new LinkedList<>();

        getButtonsAffectingIndices(buttons, minIndices, buttonsToCheckQueue, indicesToCheckQueue);
        Set<List<Integer>> buttonsChecked = new HashSet<>();

        AtomicInteger minValue = new AtomicInteger(Integer.MAX_VALUE);
        var currentValue = 0;

        var lowestPossible = currentJoltage.stream().mapToInt(i->i).max().getAsInt();

        getMinNumOfSwitches(machine, currentJoltage,
                buttonsToCheckQueue, indicesToCheckQueue,
                minValue, currentValue,
                buttonsChecked, lowestPossible);

        return minValue.get();
    }

    private void getMinNumOfSwitches(Machine machine,
                                     List<Integer> currentJoltage,
                                     Queue<List<Integer>> buttonsToCheckQueue,
                                     Queue<Integer> indicesToCheckQueue,
                                     AtomicInteger minValue,
                                     int currentValue,
                                     Set<List<Integer>> buttonsChecked,
                                     int lowestPossible) {

        if (lowestPossible == minValue.get()) return;

        while (!indicesToCheckQueue.isEmpty() && !buttonsToCheckQueue.isEmpty()) {
            var aButtonToCheck = buttonsToCheckQueue.poll();
            var repetitions = indicesToCheckQueue.poll();
            var newJoltage = calcJoltage(currentJoltage, aButtonToCheck, repetitions);

            if (currentValue + repetitions >= minValue.get() ||
                    isJoltageBroken(newJoltage)) {
                continue;
            }

            buttonsChecked.add(aButtonToCheck);

            if (isJoltageZero(newJoltage)) {
                if (currentValue + repetitions < minValue.get()) {
                    minValue.set(currentValue + repetitions);
                }
            } else {
                // recursively check another min indices
                Queue<List<Integer>> newButtonsToCheckQueue =  new LinkedList<>();
                Queue<Integer> newIndicesToCheckQueue = new LinkedList<>();
                var allButtons = machine.getButtons();
                allButtons.removeAll(buttonsChecked);

                getButtonsAffectingIndices(allButtons, getMinIndices(newJoltage),
                        newButtonsToCheckQueue, newIndicesToCheckQueue);

                getMinNumOfSwitches(machine, newJoltage,
                        newButtonsToCheckQueue, newIndicesToCheckQueue,
                        minValue, currentValue + repetitions, buttonsChecked, lowestPossible);
            }

            buttonsChecked.remove(aButtonToCheck);
        }
    }

    private boolean isJoltageBroken(List<Integer> joltage) {
        return joltage.stream()
                .anyMatch(i -> i< 0);
    }

    private boolean isJoltageZero(List<Integer> joltage) {
        return joltage.stream()
                .allMatch(i -> i == 0);
    }

    private List<Integer> calcJoltage(List<Integer> joltage, List<Integer> button, int repetitions) {
        List<Integer> newJoltage = new ArrayList<>(joltage);

        for (var idx : button) {
            newJoltage.set(idx, joltage.get(idx)-repetitions);
        }

        return newJoltage;
    }

    private void getButtonsAffectingIndices(Set<List<Integer>> buttons, List<Integer> indices,
                                            Queue<List<Integer>> buttonsToCheckQueue,
                                            Queue<Integer> indicesToCheck) {

        for (int idx : indices) {
            for (List<Integer> button : buttons) {
                buttonsToCheckQueue.add(button);
                indicesToCheck.add(idx);
            }
        }
    }

    private List<Integer> getMinIndices(List<Integer> joltage) {
        var minVal = joltage.stream()
                .mapToInt(i -> i)
                .filter(i -> i> 0)
                .min()
                .orElse(-1);

        return joltage.stream()
                .filter(i -> i == minVal)
                .toList();
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

        void setJoltageList(List<String> joltageStr) {
            joltage = joltageStr.stream()
                    .map(Integer::parseInt)
                    .toList();
        }

        List<Integer> getJoltage() {
            return List.copyOf(joltage);
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
