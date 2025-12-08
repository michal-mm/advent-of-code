package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D08_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D08_1_input.txt";


    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var points = parseInput(input);
        var distancesAndPairs = getDistancePairs(points);
        var solution = getNLargestCircuitsLength(distancesAndPairs, 1000);

        IO.println("Solution: " + solution);
    }

    @Test
    void testSets() {
        Set<Set<Point>> c = new HashSet<>();
        Set<Point> s1 = new HashSet<>();
        Set<Point> s2 = new HashSet<>();



        var p1 = new Point(1,2,3);
        var p2 = new Point(4,5,6);
        var p3 = new Point(7,8,9);

        s1.add(p1);
        s1.add(p2);
        s2.add(p3);

        c.add(s1);
        c.add(s2);

        IO.println(c.contains(s1));
        IO.println(c.contains(s2));
        c.forEach(s-> { IO.println(); s.forEach(IO::print);});
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

        var expected = 40L;
        var solution = getNLargestCircuitsLength(distancePairs, 10);

        assertEquals(expected, solution);
    }

    long getNLargestCircuitsLength(TreeMap<Double, Set<List<Point>>> distancePairs, int limit) {
        Set<Set<Point>> circuits = new HashSet<>();

        distancePairs.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .limit(limit)
                .forEach(list -> {
                            var p1 = list.getFirst();
                            var p2 = list.getLast();
                            addNewPairToCicuits(circuits, p1, p2);
                        });

        return circuits.stream()
                .mapToLong(Set::size)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .reduce(1L, (val1, val2) -> val1 * val2);
    }

    void addNewPairToCicuits(Set<Set<Point>> circuits, Point p1, Point p2) {
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

    record Point (int X, int Y, int Z){}
}
