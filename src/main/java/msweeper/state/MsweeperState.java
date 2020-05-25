package msweeper.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.Random;

@Data
@Slf4j
public class MsweeperState implements Cloneable {

    Random random = new Random();

    private int[][] minegrid;
    private int[][] flaggrid;
    private int[][] revealgrid;
    private int[][] aroundgrid;

    private int rownumber = 10;
    private int colnumber = 20;
    private int minenumber = 30;

    public MsweeperState() {
        initGrid();
        placeMines(minenumber);
        calculateMinesAround();
    }

    public MsweeperState(int[][] incomingminegrid){
        if(!isValidMinefield(incomingminegrid)) throw new IllegalArgumentException();
        rownumber = incomingminegrid.length;
        colnumber = incomingminegrid[0].length;
        initGrid();
        for (int i = 0; i<rownumber; ++i){
            for(int j = 0; j<colnumber; ++j){
                minegrid[i][j] = incomingminegrid[i][j];
                flaggrid[i][j] = 0;
                revealgrid[i][j] = 0;
                aroundgrid[i][j] = 0;
            }
        }
        calculateMinesAround();
    }

    private void initGrid() {
        minegrid = new int[rownumber][colnumber];
        flaggrid = new int[rownumber][colnumber];
        revealgrid = new int[rownumber][colnumber];
        aroundgrid = new int[rownumber][colnumber];

        for(int i = 0; i<rownumber; ++i){
            for(int j = 0; j<colnumber; ++j){
                minegrid[i][j] = 0;
                flaggrid[i][j] = 0;
                revealgrid[i][j] = 0;
                aroundgrid[i][j] = 0;
            }
        }
    }

    private void placeMines(int numberofminestoplace){
        for(int i = 0; i<numberofminestoplace; ++i){
            int x = random.nextInt(rownumber);
            int y = random.nextInt(colnumber);
            while(minegrid[x][y] == 1){
                x = random.nextInt(rownumber);
                y = random.nextInt(colnumber);
            }
            minegrid[x][y] = 1;
        }
    }

    private void calculateMinesAround(){
        for(int i = 0; i<rownumber; ++i) {
            for (int j = 0; j < colnumber; ++j) {
                if (minegrid[i][j] == 1) {
                    if (i > 0) {
                        if (j > 0) aroundgrid[i - 1][j - 1]++;
                        aroundgrid[i - 1][j]++;
                        if (j < colnumber - 1) aroundgrid[i - 1][j + 1]++;
                    }
                    if (i < rownumber - 1) {
                        if (j > 0) aroundgrid[i + 1][j - 1]++;
                        aroundgrid[i + 1][j]++;
                        if (j < colnumber - 1) aroundgrid[i + 1][j + 1]++;
                    }
                    if (j > 0) aroundgrid[i][j - 1]++;
                    if (j < colnumber - 1) aroundgrid[i][j + 1]++;

                }
            }
        }
    }

    public boolean isExistingSquare(int x, int y){
        if(x>=0 && y>=0 && x<rownumber && y < colnumber){
            return true;
        }
        return false;
    }

    public boolean isValidMinefield(int[][] minefield){
        for (int[] row : minefield){
            for (int field : row){
                if(field != 1 && field != 0) return false;
            }
        }
        return true;
    }



    public void putFlag(int x,int y){
        if(isExistingSquare(x,y)){
            if(revealgrid[x][y]==0) flaggrid[x][y] = (flaggrid[x][y] + 1) % 2;
        }
        else throw new IllegalArgumentException();
    }

    public void reveal(int x, int y){
        if(isExistingSquare(x,y)) {
            if (revealgrid[x][y] == 0 && flaggrid[x][y] == 0) {
                revealgrid[x][y] = 1;
                if (aroundgrid[x][y] == 0) {
                    reveal(x-1,y-1);
                    reveal(x-1,y);
                    reveal(x-1, y+1);
                    reveal(x,y-1);
                    reveal(x,y+1);
                    reveal(x+1,y-1);
                    reveal(x+1,y);
                    reveal(x+1,y+1);
                }
            }
        }
    }

    public boolean isLost(){
        for(int i = 0; i<rownumber; ++i){
            for(int j = 0; j<colnumber; ++j){
                if(minegrid[i][j]==1 && revealgrid[i][j]==1) return true;
            }
        }
        return false;
    }

    public boolean isWon(){
        for(int i = 0; i<rownumber; ++i){
            for(int j = 0; j<colnumber; ++j){
                if(minegrid[i][j]==0 && revealgrid[i][j]==0) return false;
            }
        }
        return true;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<rownumber; ++i) {
            for (int j = 0; j<colnumber; ++j) {
                sb.append(minegrid[i][j]).append(',').append(aroundgrid[i][j]).append(',').append(flaggrid[i][j])
                        .append(',').append(revealgrid[i][j]).append(' ');
            }
            sb.append('\n');
        }
        sb.append(this.isLost()).append(' ').append(this.isWon());
        return sb.toString();
    }

    public String displayToConsole(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<rownumber; ++i){
            for (int j = 0; j<colnumber; ++j){
                if(flaggrid[i][j]==1){
                    sb.append("' ");
                }
                else if(revealgrid[i][j]==0){
                    sb.append("□ ");
                }
                else if(minegrid[i][j]==1){
                    sb.append("* ");
                }
                else{
                    sb.append(aroundgrid[i][j]).append(' ');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int[][] examplefield = {{0,0,1},{1,0,0},{0,0,0}};
        MsweeperState state = new MsweeperState(examplefield);
        System.out.println(state.displayToConsole());
        state.putFlag(1,1);
        System.out.println(state.displayToConsole());
        state.reveal(2,0);
        System.out.println(state.displayToConsole());
        System.out.println(state);

    }
}