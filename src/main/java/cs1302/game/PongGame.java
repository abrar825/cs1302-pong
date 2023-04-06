package cs1302.game;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.scene.shape.*;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.*;
import java.util.Random;

/**
 * A class representing the classic arcade game Pong.
 *
 */
public class PongGame extends Game {

    private Random random = new Random();
    private int width;
    private int height;

    private Rectangle player1;
    private double p1X;
    private double p1Y;
    private int p1Score = 0;
    private Text score1;

    private Rectangle player2;
    private double p2X;
    private double p2Y;
    private int p2Score = 0;
    private Text score2;

    private Circle ball;
    private double ballX;
    private double ballY;
    private double ballXSpeed = random.nextInt(3) - 1;
    private double ballYSpeed = random.nextInt(3) - 1;


    private boolean start = false;

    private Text welcome;
    private Text winText;

    private Button quit;
    private Button playOn;


    /**
     * Constructor for Pong Game.
     *
     * @param width preferred game width
     * @param height preferred game height
     *
     */
    public PongGame(int width, int height) {
        super(width, height, 60);
        this.setMaxHeight(height);
        this.setMaxWidth(width);
        this.width = width;
        this.height = height;

        this.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
        this.player1 = new Rectangle(12, 80);
        this.player2 = new Rectangle(12, 80);
        this.ball = new Circle(8);

        p1X = player1.getWidth();
        p1Y = height / 2;
        p2X = width - 2 * player2.getWidth();
        p2Y = height / 2;

        ballX = width / 2;
        ballY = height / 2;
    }

    /** {@inheritDoc} */
    @Override
    protected void init() {
        player1.setFill(Color.BLACK);
        player2.setFill(Color.BLACK);
        ball.setFill(Color.BLACK);

        player1.setX(p1X);
        player1.setY(p1Y);

        player2.setX(p2X);
        player2.setY(p2Y);

        ball.setCenterX(ballX);
        ball.setCenterY(ballY);

        welcome = new Text(width / 2 - 325, height / 2 - 200,
        "WELCOME TO PONG! CLICK TO PLAY!\nP1 uses W&S, P2 uses UP&DOWN");
        welcome.setFill(Color.BLACK);
        welcome.setFont(Font.font(null, FontWeight.BOLD, 32));
        getChildren().add(welcome);

        score1 = new Text(width / 4, height / 2 - 100, Integer.toString(p1Score));
        score2 = new Text(3 * width / 4, height / 2 - 100, Integer.toString(p2Score));
        score1.setFill(Color.BLACK);
        score1.setFont(Font.font(null, FontWeight.BOLD, 32));
        score2.setFill(Color.BLACK);
        score2.setFont(Font.font(null, FontWeight.BOLD, 32));

        quit = new Button("Quit");
        playOn = new Button("Continue");
        quit.setPrefHeight(75);
        quit.setPrefWidth(125);
        playOn.setPrefHeight(75);
        playOn.setPrefWidth(125);

        quit.setTranslateX(350);
        quit.setTranslateY(200);
        playOn.setTranslateX(350);
        playOn.setTranslateY(300);
        quit.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        playOn.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        EventHandler<ActionEvent> quitHandler = event -> System.exit(1);
        EventHandler<ActionEvent> playOnHandler = event -> playAgain(event);

        quit.setOnAction(quitHandler);
        playOn.setOnAction(playOnHandler);


        getChildren().addAll(player1, player2, ball, score1, score2);
        setOnMouseClicked(event -> clickToStart(event));

    }

    /** {@inheritDoc} */
    @Override
    protected void update() {
        score1.setText(Integer.toString(p1Score));
        score2.setText(Integer.toString(p2Score));

        // game start
        if (start) {

            ballX += ballXSpeed;
            ballY += ballYSpeed;

            ball.setCenterX(ballX);
            ball.setCenterY(ballY);
        }

        // player movement
        playerMovement();

        // ball bouncing and speed control
        if (ball.getCenterY() >= height || ball.getCenterY() <= 0) {
            ballYSpeed *= -1;
        }
        if (collides(ball, player2)) {
            ballXSpeed += 1.5;
            ballYSpeed += 1.5;

            ballXSpeed *= -1;
            ballYSpeed *= -1;
        }
        if (collides(ball, player1)) {
            ballXSpeed *= -1;
            ballYSpeed *= -1;

            ballXSpeed += 1.5;
            ballYSpeed += 1.5;
        }

        // awards points, resets ball
        if (ball.getCenterX() > player2.getX()) {
            p1Score++;
            reset();
        }
        if (ball.getCenterX() < player1.getX()) {
            p2Score++;
            reset();
        }

        // game win
        if (p1Score >= 5 || p2Score >= 5) {
            start = false;
            onWin();
        }


    }

    /**
     * Method that handles game start on click.
     *
     * @param me the mouse event which triggers the method
     *
     */
    protected void clickToStart(MouseEvent me) {
        getChildren().remove(welcome);
        start = true;
    }

    /**
     * Resets game including ball speed and position. Occurs after a player scores.
     *
     */
    protected void reset() {
        ballX = width / 2;
        ballY = height / 2;

        ball.setCenterX(ballX);
        ball.setCenterY(ballY);
        ballXSpeed = random.nextInt(3) - 1;
        while (ballXSpeed == 0) {
            ballXSpeed = random.nextInt(3) - 1;
        }
        ballYSpeed = random.nextInt(3) - 1;
    }

    /**
     * Collision check method to determine whether shapes intersect.
     *
     * @param one first shape to check
     * @param two second shape to check
     * @return returns true if shapes intersect
     *
     */
    protected boolean collides(Shape one, Shape two) {
        return one.getBoundsInParent().intersects(two.getBoundsInParent());
    }

    /**
     * Checks for input through UP, DOWN, W, and S buttons to determine
     * player movement.
     *
     */
    protected void playerMovement() {
        if (player1.getY() > 0) {
            isKeyPressed(KeyCode.W, () -> player1.setY(player1.getY() - 10.0));
        }
        if (player1.getY() < height - player1.getHeight()) {
            isKeyPressed(KeyCode.S, () -> player1.setY(player1.getY() + 10.0));

        }
        if (player2.getY() > 0) {
            isKeyPressed(KeyCode.UP, () -> player2.setY(player2.getY() - 10.0));
        }
        if (player2.getY() < height - player2.getHeight()) {
            isKeyPressed(KeyCode.DOWN, () -> player2.setY(player2.getY() + 10.0));
        }
    }

    /**
     * Starts the game loop over again if player chooses.
     *
     * @param e the click event
     *
     */
    protected void playAgain(ActionEvent e) {
        p1Score = 0;
        p2Score = 0;
        reset();
        getChildren().removeAll(winText, quit, playOn);
        start = true;
    }

    /**
     * Sets up game scene on player win.
     *
     */
    protected void onWin() {
        getChildren().addAll(quit, playOn);
        winText = new Text(width / 2 - 175, height / 2 - 200, " ");
        winText.setFill(Color.DARKORANGE);
        winText.setFont(Font.font(null, FontWeight.BOLD, 32));
        if (p1Score >= 5) {
            winText.setText("PLAYER 1 WINS !!!");
        } else {
            winText.setText("PLAYER 2 WINS !!!");
        }
        getChildren().add(winText);
    }


}
