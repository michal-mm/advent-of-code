package aoc2025;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D12_1 {

    private static final String INPUT_FILE = "resources/aoc2025/D12_1_input.txt";

    void main() throws IOException {
        var input = Files.readString(Path.of(INPUT_FILE));
        var boardsAndPuzzles = parseInput(input);
        var solution = getRegionsThatCanFitAllGifts(boardsAndPuzzles);
        IO.println("Solution: " + solution);
    }
    // 1500 too high
    @Test
    void testExample() {
        var input = """
                0:
                ###
                ##.
                ##.
                
                1:
                ###
                ##.
                .##
                
                2:
                .##
                ###
                ##.
                
                3:
                ##.
                ###
                ##.
                
                4:
                ###
                #..
                ###
                
                5:
                ###
                .#.
                ###
                
                4x4: 0 0 0 0 2 0
                12x5: 1 0 1 0 2 2
                12x5: 1 0 1 0 3 2
                """;

        var puzllesAndBoards = parseInput(input);
        var result = getRegionsThatCanFitAllGifts(puzllesAndBoards);

        var expected = 2;

        assertEquals(expected, result);
    }

    int getRegionsThatCanFitAllGifts(PuzzlesAndBoards puzzlesAndBoards) {
        var boards = puzzlesAndBoards.getBoards();
        var puzzles = puzzlesAndBoards.getPuzzles();

        var result = 0;

        for (var board : boards) {
            var numOfGifts = board.getNumOfGifts();
            var sizeOfGifts = IntStream.range(0, numOfGifts.size())
                    .map(i -> numOfGifts.get(i) * puzzles.get(i).size())
                    .sum();

            if (sizeOfGifts <= board.getSize()) {
                result++;
            }
        }

        return result;
    }

    PuzzlesAndBoards parseInput(String input) {

        List<String> lines = new ArrayList<>();
        try(var scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        }

        Pattern boardDefinition = Pattern.compile("^\\d+x\\d+:(\\s\\d+)+$");

        var map = lines.stream()
                .collect(Collectors.partitioningBy(boardDefinition.asPredicate()));

        var boardDefs = map.get(true);
        var boards = getBoardList(boardDefs);

        var linesWithoutBoardsDef = map.get(false);
        var puzzles = getPuzzles(linesWithoutBoardsDef);

        var puzzlesAndBoards = new PuzzlesAndBoards();
        puzzlesAndBoards.setBoards(boards);
        puzzlesAndBoards.setPuzzles(puzzles);

        return puzzlesAndBoards;
    }

    private List<Board> getBoardList(List<String> lines) {
        return lines.stream()
                .map(this::createBoardObjectFromString)
                .toList();
    }

    private List<Puzzle> getPuzzles(List<String> linesWithoutBoardsDef) {
        List<Puzzle> puzzles = new ArrayList<>();
        
        for (var puzzleLine : linesWithoutBoardsDef) {
            if (puzzleLine.isEmpty()) continue;
            if (puzzleLine.matches("^\\d+:$")) puzzles.add(new Puzzle());
            puzzles.getLast().addRow(puzzleLine.toCharArray());
        }
        puzzles.forEach(Puzzle::setPuzzle);
        
        return puzzles;
    }

    Board createBoardObjectFromString(String boardDescription) {
        var board = new Board();
        var input = boardDescription
                .replaceAll("x", " ")
                .replaceAll(":", "");

        try(var scanner = new Scanner(input)) {
            var n = scanner.nextInt();
            var m = scanner.nextInt();
            board.setNxM(n, m);

            while(scanner.hasNextInt()) {
                board.addAnotherGift(scanner.nextInt());
            }
        }

        return board;
    }

    static class PuzzlesAndBoards {
        private List<Puzzle> puzzles;
        private List<Board> boards;
        private int totalSize;

        List<Board> getBoards() {
            return boards;
        }

        List<Puzzle> getPuzzles() {
            return puzzles;
        }

        void setPuzzles(List<Puzzle> puzzles) {
            this.puzzles = puzzles;
            calcTotalPuzzlesSize();
        }

        void setBoards(List<Board> boards) {
            this.boards = boards;
        }

        private void calcTotalPuzzlesSize() {
            totalSize =  puzzles.stream()
                    .mapToInt(Puzzle::size)
                    .sum();
        }
    }

    static class Board {
        private int sizeN;
        private int sizeM;
        private final List<Integer> numOfGifts = new ArrayList<>();

        List<Integer> getNumOfGifts() {
            return numOfGifts;
        }

        void addAnotherGift(int numberOf) {
            numOfGifts.add(numberOf);
        }

        void setNxM(int N, int M) {
            sizeN = N;
            sizeM = M;
        }

        int getSize() {
            return sizeN * sizeM;
        }

        public String toString() {
            return "[" + sizeN + "x" + sizeM + "]: " + numOfGifts;
        }
    }

    static class Puzzle {
        private char[][] board;
        private final List<char[]> listOfLines = new ArrayList<>();
        private int size;

        void addRow (char[] line) {
            listOfLines.add(line);
            size += (int) String.valueOf(line).chars().filter(ch -> ch == '#').count();
        }

        void setPuzzle () {
            int n = listOfLines.size();
            int m = listOfLines.getFirst().length;
            board = new char[n][m];
            for (int i=0; i<n; i++){
                System.arraycopy(listOfLines.get(i), 0, board[i], 0, m);
            }
        }

        int size() {
            return size;
        }
    }
}
