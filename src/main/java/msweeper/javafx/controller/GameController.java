package msweeper.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import msweeper.results.Result;
import msweeper.results.ResultDao;
import msweeper.state.MsweeperState;
import org.hibernate.annotations.common.util.impl.Log;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private ResultDao resultDao;

    private String playerName;
    private MsweeperState gameState;
    private Instant startTime;
    private List<Image> imageList;

    @FXML
    private Label messageLabel;

    @FXML
    private GridPane gameGrid;

    @FXML
    private Label stopwatchLabel;

    private Timeline stopwatchTimeline;

    @FXML
    private Button resetButton;

    @FXML
    private Button giveUpButton;

    private BooleanProperty gameOver = new SimpleBooleanProperty();

    public void setPlayerName(String playerName) {this.playerName = playerName;}

    @FXML
    public void initialize() {
        imageList = List.of(
                new Image(getClass().getResource("/images/hidden.png").toExternalForm()),
                new Image(getClass().getResource("/images/flagged.png").toExternalForm()),
                new Image(getClass().getResource("/images/bomb.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed0.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed1.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed2.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed3.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed4.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed5.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed6.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed7.png").toExternalForm()),
                new Image(getClass().getResource("/images/revealed8.png").toExternalForm())
        );
        gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.info("Game is over");
                log.debug("Saving result to database...");
                resultDao.persist(createGameResult());
                stopwatchTimeline.stop();
            }
        });
        resetGame();
    }

    private void resetGame(){
        gameState = new MsweeperState(5,10,15);
        startTime = Instant.now();
        gameOver.setValue(false);
        displayGameState();
        createStopWatch();
        Platform.runLater(() -> messageLabel.setText("Good luck, " + playerName + "!"));
    }

    private void displayGameState(){
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 10; j++){
                ImageView view = (ImageView) gameGrid.getChildren().get(i * 10+ j);
                if(view.getImage() != null) log.trace("Image({}, {} = {}", i, j, view.getImage().getUrl());
                view.setImage(imageList.get(gameState.displayGrid()[i][j]));
            }
        }
    }

    public void handleClickOnSquare(MouseEvent mouseEvent) {
        int row = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int col = GridPane.getColumnIndex((Node) mouseEvent.getSource());
        log.debug("Square ({}, {}) is pressed", row, col);
        if (! gameState.isWon()) {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) gameState.reveal(row,col);
            if(mouseEvent.getButton() == MouseButton.SECONDARY) gameState.putFlag(row,col);
            if (gameState.isWon()) {
                gameOver.setValue(true);
                log.info("Player {} has solved the game", playerName);
                messageLabel.setText("Congratulations, " + playerName + "!");
                resetButton.setDisable(true);
                giveUpButton.setText("Finish");
            }
        }
        displayGameState();
    }

    public void handleResetButton(ActionEvent actionEvent)  {
        log.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        log.info("Resetting game...");
        stopwatchTimeline.stop();
        resetGame();
    }

    public void handleGiveUpButton(ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} is pressed", buttonText);
        if (buttonText.equals("Give Up")) {
            log.info("The game has been given up");
        }
        gameOver.setValue(true);
        log.info("Loading high scores scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private Result createGameResult() {
        Result result = Result.builder()
                .playerName(playerName)
                .solved(gameState.isWon())
                .duration(Duration.between(startTime, Instant.now()))
                .build();
        return result;
    }

    private void createStopWatch() {
        stopwatchTimeline = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            long millisElapsed = startTime.until(Instant.now(), ChronoUnit.MILLIS);
            stopwatchLabel.setText(DurationFormatUtils.formatDuration(millisElapsed, "HH:mm:ss"));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        stopwatchTimeline.setCycleCount(Animation.INDEFINITE);
        stopwatchTimeline.play();
    }

}