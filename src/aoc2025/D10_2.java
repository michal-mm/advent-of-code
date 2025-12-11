package aoc2025;

import org.apache.commons.math4.legacy.linear.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
        // 16569 -- too low
        // 16941 -- too high
        // 16765 -- just a guess -> it's correct!
    }

    @Test
    void testExample() {
        var input = """
                [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
                [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
                [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
                """;
        var machines = parseInput(input);
        var expected = 33L;
        var result = getMinNumOfSwitches(machines);

        assertEquals(expected, result);
    }


    @Test
    void testExampleFromFile0() {
        var input = """
                [#####..] (4,5,6) (0,1,3,4) (0,4,5,6) (0,1,3) (0,1,2,3,4) (1,4) {231,230,190,221,241,24,24}
                """;

        var machines = parseInput(input);
        var expected = 254L;
        var result = getMinNumOfSwitches(machines);
        IO.println("Result =" + result);
        assertEquals(expected, result);
    }

    @Test
    void testExampleFromFile() {
        var input = """
                [##.#.###] (0,1,3,4,5,7) (0,1,2,4,5,6) (5,6,7) (0,2,3,5) (0,1,3,5,6,7) (1,3,4,5) (1,4,5) (0,3,4,5,7) (3,4,5,6,7) {53,51,11,80,60,263,214,234}
                """;

        var machines = parseInput(input);
        var expected = 966L;
        var result = getMinNumOfSwitches(machines);
        IO.println("Result =" + result);
        assertEquals(expected, result);
    }

    @Test
    void testExampleFromFile2() {
        var input = """
                [##.###.##] (2,5) (2,4,5,6,7) (1,3,4,8) (2,4,6,7,8) (0,1,2,3,5,7,8) (1,2,3,5,6) (0,5,7) {8,24,37,24,12,36,22,16,13}
                """;

        var machines = parseInput(input);
        var expected = 192L;
        var result = getMinNumOfSwitches(machines);
        assertEquals(expected, result);
    }

    @Test
    void testExampleFromFile3() {
        var input = """
                [#####..] (4,5,6) (0,1,3,4) (0,4,5,6) (0,1,3) (0,1,2,3,4) (1,4) {231,230,190,221,241,24,24}
                """;

        var machines = parseInput(input);
        var expected = 1161L;
        var result = getMinNumOfSwitches(machines);
        assertEquals(expected, result);
    }


    @Test
    void testExampleFromFile4() {
        var input = """
                [##.#.###] (0,1,3,4,5,7) (0,1,2,4,5,6) (5,6,7) (0,2,3,5) (0,1,3,5,6,7) (1,3,4,5) (1,4,5) (0,3,4,5,7) (3,4,5,6,7) {53,51,11,80,60,263,214,234}
                """;

        var machines = parseInput(input);
        var expected = 966L;
        var result = getMinNumOfSwitches(machines);
        assertEquals(expected, result);
    }

    long getMinNumOfSwitches(List<Machine> machines) {
        return machines.stream()
                .parallel()
                .mapToLong(this::getMinNumOfSwitches)
                .peek(IO::println)
                .sum();
    }

    long getMinNumOfSwitches(Machine machine) {
        var currentJoltage = machine.getJoltage();

        var minIndices = getMinIndices(currentJoltage);
        var buttons = machine.getButtons();

        Queue<List<Integer>> buttonsToCheckQueue =  new LinkedList<>();
        Queue<Integer> indicesToCheckQueue = new LinkedList<>();

        getButtonsAffectingIndices(buttons, minIndices, buttonsToCheckQueue, indicesToCheckQueue);
        Set<List<Integer>> buttonsChecked = new HashSet<>();

        var currentValue = 0;

        var lowestPossible = currentJoltage.stream().mapToInt(i->i).max().orElse(0);
        var maxPossible = currentJoltage.stream().mapToInt(i->i).sum();
        AtomicInteger minValue = new AtomicInteger(maxPossible);
        AtomicBoolean flag = new AtomicBoolean(false);
        boolean bruteForce = false;

        getMinNumOfSwitches(machine, currentJoltage,
                buttonsToCheckQueue, indicesToCheckQueue,
                minValue, currentValue,
                buttonsChecked, lowestPossible, maxPossible, flag, bruteForce);

        if (!flag.get()) {
            IO.println("SOLUTION NOT FOUND");

            RealMatrix coefficients = new Array2DRowRealMatrix(machine.getAMatrix(), false);
            DecompositionSolver solver = new SingularValueDecomposition(coefficients).getSolver();//new LUDecomposition(coefficients).getSolver();
            RealVector constants = new ArrayRealVector(machine.getBMatrix(), false);
            RealVector solution = solver.solve(constants);
            var a = solution.toArray();
            long res = 0L;
            for (var d : a) {
                res += Math.round(Math.floor(d));
            }

            var v = IntStream.range(0, buttons.size())
                    .mapToDouble(solution::getEntry)
//                    .boxed()
//                    .map(Math::ceil)
//                    .mapToLong(Double::longValue)
                    .sum();

            IO.println("Replacing " + minValue.get() + " with " + res+" " +v + " " + Math.round(v));
            //return Math.round(v);


            bruteForce = true;
            getButtonsAffectingIndices(buttons, minIndices, buttonsToCheckQueue, indicesToCheckQueue);
            getMinNumOfSwitches(machine, currentJoltage,
                    buttonsToCheckQueue, indicesToCheckQueue,
                    minValue, currentValue,
                    buttonsChecked, lowestPossible, maxPossible, flag, bruteForce);

        }

        return minValue.get();
    }

    private void getMinNumOfSwitches(Machine machine,
                                     List<Integer> currentJoltage,
                                     Queue<List<Integer>> buttonsToCheckQueue,
                                     Queue<Integer> indicesToCheckQueue,
                                     AtomicInteger minValue,
                                     int currentValue,
                                     Set<List<Integer>> buttonsChecked,
                                     int lowestPossible, int maxPossible,
                                     AtomicBoolean flag, boolean bruteForce) {

        if (lowestPossible == minValue.get() || currentValue > maxPossible) return;

        while (!indicesToCheckQueue.isEmpty() && !buttonsToCheckQueue.isEmpty()) {
            var aButtonToCheck = buttonsToCheckQueue.poll();
            var repetitions = currentJoltage.get(indicesToCheckQueue.poll());
            if (bruteForce) {
                repetitions = 1;
            }
            var newJoltage = calcJoltage(currentJoltage, aButtonToCheck, repetitions);

            if (currentValue + repetitions >= minValue.get())
                continue;
            if (isJoltageBroken(newJoltage)) {
                continue;
            }

            if (!bruteForce) {
                buttonsChecked.add(aButtonToCheck);
            }

            if (isJoltageZero(newJoltage)) {
                if (currentValue + repetitions < minValue.get()) {
                    minValue.set(currentValue + repetitions);
                    flag.set(true);
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
                        minValue, currentValue+repetitions, buttonsChecked, lowestPossible, maxPossible,
                        flag, bruteForce);
            }

            if (!bruteForce) {
                buttonsChecked.remove(aButtonToCheck);
            }
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
            for (List<Integer> l : buttons) {
                if (l.contains(idx) && !buttonsToCheckQueue.contains(l)) {
                    buttonsToCheckQueue.add(l);
                    indicesToCheck.add(idx);
                }
            }
        }
        /*
            for (List<Integer> button : buttons) {
                if (button.contains(idx)) {
                    buttonsToCheckQueue.add(button);
                    indicesToCheck.add(idx);
            }
            */
    }

    private List<Integer> getMinIndices(List<Integer> joltage) {
        var minVal = joltage.stream()
                .mapToInt(i -> i)
                .filter(i -> i>0)
                .min()
                .orElse(-1);

        return IntStream.range(0, joltage.size())
                .filter(i -> joltage.get(i) == minVal)
                .boxed()
                .toList();

        /*
        return joltage.stream()
                .filter(i -> i == minVal)
                .toList();
         */
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

        public double[][] getAMatrix() {
            double[][] result = new double[joltage.size()][buttons.size()];
            List<List<Integer>> tmpButtons = buttons.stream().toList();
            for (int i=0; i<joltage.size(); i++) {
                for (int j=0; j<tmpButtons.size(); j++) {
                    result[i][j] = tmpButtons.get(j).contains(i) ? 1d : 0d;
                }
            }
            return result;
        }

        public double [] getBMatrix() {
            return joltage.stream().mapToDouble(i->i).toArray();
        }
    }
}
