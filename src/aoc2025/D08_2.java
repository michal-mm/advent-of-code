package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D08_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D08_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var points = parseInput(input);
        var distancesAndPairs = getDistancePairs(points);
        var solution = buildCircuitAndReturnResult(distancesAndPairs);

        IO.println("Solution: " + solution);
    }

    @Test
    void testExample() {
        var input = """
                162,817,812
                57,618,57
                906,360,560
                592,479,940
                352,342,300
                466,668,158
                542,29,236
                431,825,988
                739,650,466
                52,470,668
                216,146,977
                819,987,18
                117,168,530
                805,96,715
                346,949,466
                970,615,88
                941,993,340
                862,61,35
                984,92,344
                425,690,689
                """;

        var points = parseInput(input);
        var distancePairs = getDistancePairs(points);

        var expected = 25272L;
        var result = buildCircuitAndReturnResult(distancePairs);

        assertEquals(expected, result);
    }

    long buildCircuitAndReturnResult (TreeMap<Double, Set<List<Point>>> distancePairs) {
        Set<Set<Point>> circuits = new HashSet<>();

        var uniquePoints = distancePairs.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .flatMap(Collection::stream)
                .distinct()
                .count();

        for (Map.Entry<Double, Set<List<Point>>> entry : distancePairs.entrySet()) {
            for (List<Point> list : entry.getValue()) {
                var p1 = list.getFirst();
                var p2 = list.getLast();
                addNewPairToCicuits(circuits, p1, p2);
//                showCircuits(circuits);

                var uniquePointsInCircuits = circuits.stream()
                        .flatMap(Collection::stream)
                        .distinct()
                        .count();

                if (circuits.size() == 1 && uniquePointsInCircuits == uniquePoints) {
                    long p1X = p1.X();
                    long p2X = p2.X();
                    return p1X * p2X;
                }
            }
        }

        return 0L;
    }

    TreeMap<Double, Set<List<Point>>> getDistancePairs(List<Point> points) {
        TreeMap<Double, Set<List<Point>>> distancePairs = new TreeMap<>();

        for (int i=0; i<points.size()-1; i++) {
            var p1 = points.get(i);
            for (int j=i+1; j<points.size(); j++) {
                var p2 = points.get(j);
                var distance = distance(p1, p2);
                var setOfPairs = distancePairs.getOrDefault(distance, new HashSet<>());
                distancePairs.putIfAbsent(distance, setOfPairs);
                setOfPairs.add(List.of(p1, p2));
            }
        }

        return distancePairs;
    }

    List<Point> parseInput(String input) {
        List<Point> points = new ArrayList<>();

        var scanner = new Scanner(input);
        while(scanner.hasNextLine()) {
            var coords = scanner.nextLine().split(",");
            points.add(new Point(Integer.parseInt(coords[0]),
                    Integer.parseInt(coords[1]),
                    Integer.parseInt(coords[2])));
        }

        return points;
    }

    private double distance(Point p1, Point p2) {
        var x2 = Math.pow(p1.X() - p2.X(), 2);
        var y2 = Math.pow(p1.Y() - p2.Y(), 2);
        var z2 = Math.pow(p1.Z() - p2.Z(), 2);

        return Math.sqrt(x2 + y2 + z2);
    }

    private void showCircuits(Set<Set<Point>> circuits) {
        IO.println("Circuits:");
        circuits.forEach(s -> {
            var str = s.stream()
                    .map(Point::toString)
                    .collect(Collectors.joining(" <--> "));
            IO.println("(:)-> " + str + " <-(:)");
        });
    }

    private void addNewPairToCicuits(Set<Set<Point>> circuits, Point p1, Point p2) {
        var setWithP1 = getSetWithPoint(circuits, p1);
        var setWithP2 = getSetWithPoint(circuits, p2);

        if (setWithP1.isEmpty() && setWithP2.isEmpty()) {
            Set<Point> aSet = new HashSet<>();
            aSet.add(p1);
            aSet.add(p2);
            circuits.add(aSet);
        } else if (setWithP1.isEmpty()) {
            circuits.remove(setWithP2);
            setWithP2.add(p1);
            circuits.add(setWithP2);
        } else if (setWithP2.isEmpty()) {
            circuits.remove(setWithP1);
            setWithP1.add(p2);
            circuits.add(setWithP1);
        } else {
            // both set non-empy -> remove one and merge with the other
            circuits.remove(setWithP1);
            circuits.remove(setWithP2);
            setWithP2.addAll(setWithP1);
            circuits.add(setWithP2);
        }
    }

    private Set<Point> getSetWithPoint(Set<Set<Point>> sets, Point p) {
        return sets.stream()
                .filter(s -> s.contains(p))
                .findFirst()
                .orElseGet(HashSet::new);
    }

    record Point (int X, int Y, int Z){}
}
