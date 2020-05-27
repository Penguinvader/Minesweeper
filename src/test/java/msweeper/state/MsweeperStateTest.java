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

    @Test
    void testPutFlag(){
        int[][] minefield = {
                {1, 0, 0},
                {1, 0, 1},
                {0, 0, 0}
        };
        MsweeperState state = new MsweeperState(minefield);
        assertThrows(IllegalArgumentException.class, () -> state.putFlag(3,2));
        state.putFlag(2,2);
        assertArrayEquals(state.getFlaggrid(),new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 1}
        });
        state.putFlag(2,2);
        assertArrayEquals(state.getFlaggrid(),new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        });
    }

    @Test
    void testReveal(){
        int[][] minefield = {
                {1, 0, 0},
                {1, 0, 1},
                {0, 0, 0}
        };
        MsweeperState state = new MsweeperState(minefield);
        assertThrows(IllegalArgumentException.class, () -> state.reveal(3,2));
        state.reveal(1,2);
        assertArrayEquals(state.getRevealgrid(), new int[][]{
            {0, 1, 1},
            {0, 1, 1},
            {0, 1, 1}
        });
    }

    @Test
    void testIsLost(){
        int[][] minefield = {
                {1, 0, 0},
                {1, 0, 1},
                {0, 0, 0}
        };
        MsweeperState state = new MsweeperState(minefield);
        state.reveal(1,2);
        assertTrue(state.isLost());
    }

    @Test
    void testIsWon(){
        int[][] minefield = {
                {1, 1, 1},
                {1, 0, 1},
                {0, 1, 1}
        };
        MsweeperState state = new MsweeperState(minefield);
        state.reveal(1,1);
        state.reveal(2,0);
        assertTrue(state.isWon());
    }

    @Test
    void testDisplayGrid(){
        int[][] minefield = {
                {1, 1, 1},
                {1, 0, 1},
                {0, 1, 1}
        };
        MsweeperState state = new MsweeperState(minefield);
        state.putFlag(2,2);
        state.reveal(1,1);
        assertArrayEquals(state.displayGrid(), new int[][]{
                {0, 0, 0},
                {0, 10, 0},
                {0, 0, 1}
        });
        state.reveal(0,0);
        assertArrayEquals(state.displayGrid(), new int[][]{
                {2, 0, 0},
                {0, 10, 0},
                {0, 0, 1}
        });
    }

    @Test
    void testIsHidden(){
        int[][] minefield = {
                {1, 1, 1},
                {1, 0, 1},
                {0, 1, 1}
        };
        MsweeperState state = new MsweeperState(minefield);
        assertTrue(state.isHidden());
        state.reveal(1,1);
        assertFalse(state.isHidden());
    }

}
