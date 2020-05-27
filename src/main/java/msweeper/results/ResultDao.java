package msweeper.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import java.util.List;

/**
 * DAO class for the {@link Result} entity.
 */
public class ResultDao extends GenericJpaDao<Result>{

    public ResultDao() {
        super(Result.class);
    }


    /**
     * Returns the list of {@code n} best results with respect to the time
     * spent for solving the puzzle.
     *
     * @param n the maximum number of results to be returned
     * @return the list of {@code n} best results with respect to the time
     * spent for solving the puzzle
     */
    @Transactional
    public List<Result> findBest(int n) {
        return entityManager.createQuery("SELECT r FROM Result r WHERE r.solved = true ORDER BY r.duration ASC, r.created DESC", Result.class)
                .setMaxResults(n)
                .getResultList();
    }

}
