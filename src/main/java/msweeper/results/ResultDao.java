package msweeper.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import java.util.List;

public class ResultDao extends GenericJpaDao<Result>{

    public ResultDao() {
        super(Result.class);
    }

    @Transactional
    public List<Result> findBest(int n) {
        return entityManager.createQuery("SELECT r FROM Result r WHERE r.solved = true ORDER BY r.duration ASC, r.created DESC", Result.class)
                .setMaxResults(n)
                .getResultList();
    }

}
