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

    public static final int rownumber = 10;
    public static final int colnumber = 20;
    public static final int minenumber = 30;

    public MsweeperState() {
        initGrid();
    }

    private void initGrid() {
        this.minegrid = new int[rownumber][colnumber];
        this.flaggrid = new int[rownumber][colnumber];
        this.revealgrid = new int[rownumber][colnumber];
        this.aroundgrid = new int[rownumber][colnumber];

        for(int i = 0; i<rownumber; ++i){
            for(int j = 0; j<colnumber; ++j){
                this.minegrid[i][j] = 0;
                this.flaggrid[i][j] = 0;
                this.revealgrid[i][j] = 0;
                this.aroundgrid[i][j] = 0;
            }
        }

        for(int i = 0; i<minenumber; ++i){
            int x = random.nextInt(rownumber);
            int y = random.nextInt(colnumber);
            while(minegrid[x][y] == 1){
                x = random.nextInt(rownumber);
                y = random.nextInt(colnumber);
            }
            minegrid[x][y] = 1;
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<rownumber; ++i) {
            for (int j = 0; j<colnumber; ++j) {
                sb.append(minegrid[i][j]).append(',').append(aroundgrid[i][j]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MsweeperState state = new MsweeperState();
        System.out.println(state);
    }
}
