package msweeper.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;


/**
 * Class representing the result of a game.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Result {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the player.
     */
    @Column(nullable = false)
    private String playerName;

    /**
     * Indicates if the game has been solved.
     */
    private boolean solved;

    /**
     * How long the game has taken.
     */
    @Column(nullable = false)
    private Duration duration;

    /**
     * The time and date of when the result was saved.
     */
    @Column(nullable = false)
    private ZonedDateTime created;

    @PrePersist
    protected void onPersist() {
        created = ZonedDateTime.now();
    }

}
