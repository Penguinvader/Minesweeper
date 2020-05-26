package msweeper.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MsweeperStateTest {
    @Test
    void testThreeArgConstructor_InvalidArg(){
        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(0,1,0));
        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(3,4,13));
        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(-1,-13, 10));
    }

    @Test
    void testThreeArgConstructor_ValidArg(){
        MsweeperState state = new MsweeperState(10,20,30);
        int counter = 0;
        for(int i = 0; i<10; ++i){
            for(int j = 0; j<20; ++j){
                if(state.getMinegrid()[i][j]==1) counter++;
            }
        }
        assertEquals(counter, 30);
    }

    @Test
    void testOneArgConstructor_InvalidArg(){
        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(new int[][] {
                {2,0,1},
                {0,0,1},
                {1,0,0}})
        );
        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(new int[][] {
                {1,0,1},
                {0,0,1,1},
                {1,0,0}})
        );

        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(new int[][] {
                {0,1},
                {0,0,1},
                {1,0,0}})
        );

        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(new int[][] {
                {1,0,1},
                {0,-1,1},
                {1,0,0}})
        );

        assertThrows(IllegalArgumentException.class, () -> new MsweeperState(new int[][] {
                {},
                {},
                {}})
        );
    }

    @Test
    void testOneArgConstructor_ValidArg(){
        int[][] minefield = {
                {1, 0, 0},
                {1, 0, 1},
                {0, 0, 0}
        };
        MsweeperState state = new MsweeperState(minefield);
        assertArrayEquals(state.getMinegrid(), minefield);
        assertArrayEquals(state.getAroundgrid(), new int[][]{
                {1, 3, 1},
                {1, 3, 0},
                {1, 2, 1}
        });
    }

}
