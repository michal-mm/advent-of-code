package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D04_2 {

    private static final String INPUT_FILE = "resources/aoc2025/D04_2_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));

        var grid = parseInput(input);
        analyzeGrid(grid);
        IO.println("Solution: " + getNumOfAccessibleRolls(grid));
    }

    @Test
    void testExample() {
        var input = """
                ..@@.@@@@.
                @@@.@.@.@@
                @@@@@.@.@@
                @.@@@@..@.
                @@.@@@@.@@
                .@@@@@@@.@
                .@.@.@.@@@
                @.@@@.@@@@
                .@@@@@@@@.
                @.@.@@@.@.
                """;
        long expected = 43L;

        var grid= parseInput(input);
        analyzeGrid(grid);
        var solution = getNumOfAccessibleRolls(grid);

        assertEquals(expected, solution);
    }

    long getNumOfAccessibleRolls(List<List<Field>> grid) {
        return grid.stream()
                .flatMap(Collection::stream)
                .filter(Field::isAccessible)
                .count();
    }

    void analyzeGrid(List<List<Field>> grid) {
        Queue<Field> queue = new LinkedList<>();

        initQueue(grid, queue);

        while(!queue.isEmpty()) {
            var field = queue.poll();

            if (field.isEmpty() || field.isAccessible()) continue;

            analyzeField(grid, field, queue);
        }
    }

    private void analyzeField(List<List<Field>> grid, Field field, Queue<Field> queue) {
        // just in case skip if nothing to do...
        if (field.isEmpty() || field.isAccessible()) return;

        var toCheck = field.getIndices().stream()
                .filter(item -> fieldExists(grid, item[0], item[1]))
                .map(item -> grid.get(item[0]).get(item[1]))
                .filter(Field::isRollOfPaper)
                .toList();

        if (toCheck.size() < 4) {
            field.makeItEmpty();
            queue.addAll(toCheck);
        }
    }

    private boolean fieldExists (List<List<Field>> grid, int row, int col) {
        if (row < 0 || row >= grid.size()) {
            return false;
        } else {
            var list = grid.get(row);
            return col >= 0 && col < list.size();
        }
    }

    private void initQueue (List<List<Field>> grid, Queue<Field> queue) {
        for (List<Field> row : grid) {
            for (Field f : row) {
                if (f.isRollOfPaper()) {
                    queue.add(f);
                }
            }
        }
    }

    List<List<Field>> parseInput(String input) {
        List<List<Field>> list = new ArrayList<>();

        var scanner = new Scanner(input);
        while(scanner.hasNext()) {
            List<Field> row = new ArrayList<>();
            for (char c : scanner.nextLine().toCharArray()) {
                int rowIdx = list.size();
                int colIdx = row.size();
                if (c == '@') {
                    row.add(Field.ofRollOfPaper(rowIdx, colIdx));
                } else {
                    row.add(Field.ofEmpty(rowIdx, colIdx));
                }
            }

            list.add(row);
        }

        return list;
    }

    enum FieldType {PAPER, EMPTY}

    static class Field {

        FieldType type;
        boolean accessible = false;

        int row;
        int col;

        private Field (FieldType type, int row, int col) {
            this.type = type;
            this.row = row;
            this.col = col;
        }

        static Field ofRollOfPaper(int row, int col) {
            return new Field(FieldType.PAPER, row, col);
        }

        static Field ofEmpty(int row, int col) {
            return new Field(FieldType.EMPTY, row, col);
        }

        void makeItEmpty() {
            this.type = FieldType.EMPTY;
            this.accessible = true;
        }

        boolean isRollOfPaper() {
            return type == FieldType.PAPER;
        }

        boolean isEmpty() {
            return type == FieldType.EMPTY;
        }

        boolean isAccessible() {
            return accessible;
        }

        List<int[]> getIndices() {
            return List.of(
                    new int[] {row-1, col-1},
                    new int[] {row-1, col},
                    new int[] {row-1, col+1},
                    new int[] {row, col-1},
                    new int[] {row, col+1},
                    new int[] {row+1, col-1},
                    new int[] {row+1, col},
                    new int[] {row+1, col+1}
            );
        }

        public String toString() {
            return "[" + row + "x" + col + "] " + type + " accessible=" + accessible;
        }
    }
}
