package msweeper.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Class representing the state of the puzzle.
 */
@Data
@Slf4j
public class MsweeperState implements Cloneable {

    /**
     * Random object to generate random numbers.
     */
    private Random random = new Random();

    /**
     * Array representing where the mines are in the grid.
     */
    private int[][] minegrid;

    /**
     * Array representing where the flags are in the grid.
     */
    private int[][] flaggrid;

    /**
     * Array representing which squares are revealed in the grid.
     */
    private int[][] revealgrid;

    /**
     * Array representing the number of mines adjacent to each grid.
     */
    private int[][] aroundgrid;

    /**
     * The number of rows in the grid.
     */
    private int rownumber;

    /**
     * The number of columns in the grid.
     */
    private int colnumber;

    /**
     * Creates a {@code MsweeperState} object with mines randomly placed in it.
     * @param rows the number of rows in the grid
     * @param columns the number of columns in the grid
     * @param mines the number of mines to be randomly placed in the grid
     * @throws IllegalArgumentException if there are more mines than squares, or if the number of rows or columns are not positive
     */
    public MsweeperState(int rows, int columns, int mines) {
        if(rows*columns>=mines && rows>0 && columns>0){
        rownumber = rows;
        colnumber = columns;
        initGrid();
        placeMines(mines);
        calculateMinesAround();}
        else throw new IllegalArgumentException();
    }

    /**
     * Creates a {@code MsweeperState} object from a predefined grid.
     * @param incomingminegrid the predefined grid to create the object from
     * @throws IllegalArgumentException if the predefined grid isn't a valid minefield
     */
    public MsweeperState(int[][] incomingminegrid) {
        if (!isValidMinefield(incomingminegrid)) throw new IllegalArgumentException();
        rownumber = incomingminegrid.length;
        colnumber = incomingminegrid[0].length;
        initGrid();
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                minegrid[i][j] = incomingminegrid[i][j];
            }
        }
        calculateMinesAround();
    }

    private void initGrid() {
        minegrid = new int[rownumber][colnumber];
        flaggrid = new int[rownumber][colnumber];
        revealgrid = new int[rownumber][colnumber];
        aroundgrid = new int[rownumber][colnumber];

        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                minegrid[i][j] = 0;
                flaggrid[i][j] = 0;
                revealgrid[i][j] = 0;
                aroundgrid[i][j] = 0;
            }
        }
    }

    /**
     * Places a number of mines in the grid, randomly.
     * @param numberofminestoplace the number of mines to place in the grid
     */
    private void placeMines(int numberofminestoplace) {
        for (int i = 0; i < numberofminestoplace; ++i) {
            int x = random.nextInt(rownumber);
            int y = random.nextInt(colnumber);
            while (minegrid[x][y] == 1) {
                x = random.nextInt(rownumber);
                y = random.nextInt(colnumber);
            }
            minegrid[x][y] = 1;
        }
    }

    /**
     * Calculates the number of mines around each square in the grid, and sets the values of {@code aroundgrid} to it
     */
    private void calculateMinesAround() {
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                if(minegrid[i][j]==1) {
                    if (isExistingSquare(i - 1, j - 1)) aroundgrid[i - 1][j - 1]++;
                    if (isExistingSquare(i - 1, j)) aroundgrid[i - 1][j]++;
                    if (isExistingSquare(i - 1, j + 1)) aroundgrid[i - 1][j + 1]++;
                    if (isExistingSquare(i + 1, j - 1)) aroundgrid[i + 1][j - 1]++;
                    if (isExistingSquare(i + 1, j)) aroundgrid[i + 1][j]++;
                    if (isExistingSquare(i + 1, j + 1)) aroundgrid[i + 1][j + 1]++;
                    if (isExistingSquare(i, j - 1)) aroundgrid[i][j - 1]++;
                    if (isExistingSquare(i, j + 1)) aroundgrid[i][j + 1]++;
                }
            }
        }
    }

    private boolean isExistingSquare(int x, int y) {
        if (x >= 0 && y >= 0 && x < rownumber && y < colnumber) {
            return true;
        }
        return false;
    }

    private boolean isValidMinefield(int[][] minefield) {
        int firstrowlength = minefield[0].length;
        if(firstrowlength==0) return false;
        for (int[] row : minefield) {
            if (row.length != firstrowlength) return false;
            for (int field : row) {
                if (field != 1 && field != 0) return false;
            }
        }
        return true;
    }

    /**
     * Places or removes a flag from the targeted square, depending on if there was one there to begin with
     * @param x the x coordinate of the square
     * @param y the y coordinate of the square
     * @throws IllegalArgumentException if the targeted square does not exist
     */
    public void putFlag(int x, int y) {
        if (isExistingSquare(x, y)) {
            if (revealgrid[x][y] == 0) flaggrid[x][y] = (flaggrid[x][y] + 1) % 2;
        } else throw new IllegalArgumentException();
    }

    /**
     * Reveals the targeted square, and recursively reveals squares around it until one is found which has a mine around it.
     * @param x the x coordinate of the square
     * @param y the y coordinate of the square
     * @throws IllegalArgumentException if the targeted square does not exist
     */
    public void reveal(int x, int y) {
        if (isExistingSquare(x, y)) {
            if (revealgrid[x][y] == 0 && flaggrid[x][y] == 0) {
                revealgrid[x][y] = 1;
                if (aroundgrid[x][y] == 0) {
                    if (isExistingSquare(x - 1, y - 1)) reveal(x - 1, y - 1);
                    if (isExistingSquare(x - 1, y)) reveal(x - 1, y);
                    if (isExistingSquare(x - 1, y + 1)) reveal(x - 1, y + 1);
                    if (isExistingSquare(x, y - 1)) reveal(x, y - 1);
                    if (isExistingSquare(x, y + 1)) reveal(x, y + 1);
                    if (isExistingSquare(x + 1, y - 1)) reveal(x + 1, y - 1);
                    if (isExistingSquare(x + 1, y)) reveal(x + 1, y);
                    if (isExistingSquare(x + 1, y + 1)) reveal(x + 1, y + 1);
                }
            }
        } else throw new IllegalArgumentException();
    }

    /**
     * Checks whether the puzzle is lost.
     * @return {@code true} if the puzzle is lost, {@code false} otherwise
     */
    public boolean isLost() {
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                if (minegrid[i][j] == 1 && revealgrid[i][j] == 1) return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the puzzle is won.
     * @return {@code true} if the puzzle is won, {@code false} otherwise
     */
    public boolean isWon() {
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                if (minegrid[i][j] == 0 && revealgrid[i][j] == 0) return false;
            }
        }
        return true;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                sb.append(minegrid[i][j]).append(',').append(aroundgrid[i][j]).append(',').append(flaggrid[i][j])
                        .append(',').append(revealgrid[i][j]).append(' ');
            }
            sb.append('\n');
        }
        sb.append(this.isLost()).append(' ').append(this.isWon());
        return sb.toString();
    }

    public String displayToConsole() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                if (flaggrid[i][j] == 1) {
                    sb.append("' ");
                } else if (revealgrid[i][j] == 0) {
                    sb.append("□ ");
                } else if (minegrid[i][j] == 1) {
                    sb.append("* ");
                } else {
                    sb.append(aroundgrid[i][j]).append(' ');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * A function which determines what the player should see on each square of the grid.
     * @return an array representing the content of each grid, as to be seen by the player
     */
    public int[][] displayGrid() {
       int[][] tempgrid = new int[rownumber][colnumber];
        for (int i = 0; i < rownumber; i++) {
            for (int j = 0; j < colnumber; j++) {
                if (flaggrid[i][j] == 1) {
                    tempgrid[i][j] = 1;
                } else if (revealgrid[i][j] == 0) {
                    tempgrid[i][j] = 0;
                } else if (minegrid[i][j] == 1) {
                    tempgrid[i][j] = 2;
                } else {
                    tempgrid[i][j] = aroundgrid[i][j] + 3;
                }
            }
        }
        return tempgrid;
    }

    public static void main(String[] args) {
        int[][] examplefield = {{0, 0, 1}, {1, 0, 0}, {0, 0, 0}};
        MsweeperState state = new MsweeperState(examplefield);
        System.out.println(state.displayToConsole());
        state.putFlag(1, 1);
        System.out.println(state.displayToConsole());
        state.reveal(2, 0);
        System.out.println(state.displayToConsole());
        System.out.println(state);
        MsweeperState state2 = new MsweeperState(10,20,30);
        System.out.println(state2.displayToConsole());
        state2.putFlag(9, 13);
        System.out.println(state2.displayToConsole());
        state2.reveal(7, 11);
        System.out.println(state2.displayToConsole());
        System.out.println(state2);

    }
}