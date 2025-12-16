// Main.java
import java.util.Random;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class Main extends Application {

    private static final int TILE_SIZE = 30;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final int[][][] SHAPES = {
        {{1,1,1,1}},        // I
        {{1,1},{1,1}},      // O
        {{0,1,0},{1,1,1}},  // T
        {{1,0,0},{1,1,1}},  // J
        {{0,0,1},{1,1,1}},  // L
        {{1,1,0},{0,1,1}},  // S
        {{0,1,1},{1,1,0}},  // Z
    };

    private Color[] shapeColors = {
        Color.CYAN, Color.YELLOW, Color.PINK, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED
    };

    private int[][] board = new int[HEIGHT][WIDTH];
    private int[][] currentShape;
    private Color currentColor;
    private int shapeX = WIDTH / 2-1;
    private int shapeY = 0;
    private boolean gameOver = false;
    private Random random = new Random();
    private boolean paused = false;
    private boolean running = true;
    private int[][] nextShape;
    private Color nextColor;
    private int score = 0;
    private int level = 1;
    private int levelScore = 1000;
    private double speed = 0.4;
    private int highScore = 0;
    private final String HIGHSCORE_FILE = "highscore.txt";

   
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hellofx.fxml"));
        //BorderPane borderPaneRoot = loader.load(); // load FXML and connect to Root

        loadHighscore();
       
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE + 200, HEIGHT * TILE_SIZE);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        // Sounds
        String musicFile = "tetris-theme.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(1);
        mediaPlayer.play();

        StackPane stackPane = new StackPane(canvas);
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(stackPane);
        
        Scene scene = new Scene(hBox);
        // Set Stage and Scene
        primaryStage.setTitle("TETRIS");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the game and draw the initial shape
        createShape();

        scene.setOnKeyPressed(e -> {
            if(gameOver) return;
            if(e.getCode() == KeyCode.LEFT) move(-1);
            else if(e.getCode() == KeyCode.RIGHT) move(1);
            else if(e.getCode() == KeyCode.DOWN) drop();
            else if(e.getCode() == KeyCode.UP) rotateCurrentShape();
            else if(e.getCode() == KeyCode.SPACE) togglePause();
            else if(e.getCode() == KeyCode.ENTER) startGame();
        });

        new AnimationTimer() {
            long lastUpdate = 0;
            public void handle(long now){
                if(!running || paused || gameOver) {
                    draw(graphicsContext);
                    return;
                }
                if(running && !paused && !gameOver) {
                    if(now - lastUpdate >= speed * 1000_000_000) { // 0.4 Second
                        update();
                        draw(graphicsContext); // Draw the shape on the canvas
                        lastUpdate = now;
                    }
                }
            }
        }.start();
    }

    private void drop(){
        while(!collides(shapeX, shapeY + 1, currentShape)){
            shapeY++;
        }
    }

    private void togglePause(){
        if(gameOver) return;
        paused = !paused;
    }

    private void startGame(){
        // Empty the game field
        for(int y = 0 ; y < HEIGHT ; y++){
            for (int x = 0; x < WIDTH; x++) {
                board[y][x] = 0;
            }
        }
        gameOver = false;
        paused = false;
        running = true;
        score = 0;
        createShape();
    }

    private void move(int dx){
        if(!collides(shapeX + dx, shapeY, currentShape)){
            shapeX += dx;
        }
    }

    private int[][] rotate(int[][] shape){
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];

        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                rotated[x][rows - 1 - y] = shape[y][x];
            }
        }
        return rotated;
    }

    private void rotateCurrentShape(){
        int[][] rotated = rotate(currentShape);
        if(!collides(shapeX, shapeY, rotated)){
            currentShape = rotated;
        }
    }

// Create a new random shape
private void createShape() {
    // If the game isn't running, don't generate anything
    if (!running || gameOver) {
        return;
    }

    // If there is a "next" shape waiting, use it
    if (nextShape != null) {
        currentShape = nextShape;
        currentColor = nextColor;
    } else {
        // Generate the very first shape
        int idx = random.nextInt(SHAPES.length);
        currentShape = SHAPES[idx];
        currentColor = shapeColors[idx];
    }

    // Generate the NEXT shape (for preview)
    int nextIdx = random.nextInt(SHAPES.length);
    nextShape = SHAPES[nextIdx];
    nextColor = shapeColors[nextIdx];

    // Reset position for the new shape
    shapeX = WIDTH / 2 - currentShape[0].length / 2;
    shapeY = 0;

    // Check for game over (collision on spawn)
    if (collides(shapeX, shapeY, currentShape)) {
        gameOver = true;
        running = false;
    }
}


    private void mergeShape() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] == 1) {
                    int x = shapeX + col;
                    int y = shapeY + row;

                    if (y >= 0 && x >= 0 && x < WIDTH && y < HEIGHT) {
                        board[y][x] = getShapeIndex(currentColor) + 1;
                    }
                }
            }
        }
    }

    private int getShapeIndex(Color c){
        
        for(int i = 0; i < shapeColors.length; i++){
            if(shapeColors[i] == c) return i;
        }
        return 0;
    }

    private void clearLines() {
        int clearLines = 0;

        for(int y = 0; y < HEIGHT; y++) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++) {
                if(board[y][x] == 0){
                    full = false;
                    break;
                }
            }
            if(full){
                clearLines++;
                for(int yy = y; yy > 0; yy--){
                    System.arraycopy(board[yy - 1], 0, board[yy], 0, WIDTH);
                }
            }
        }

        if (clearLines > 0) {
            score += 100 * clearLines * clearLines * level;
        }

        if(score >= level * levelScore) {
            level++;
            speed = Math.max(0.1, speed - 0.05);
        }

    }

     // Draw the current game board
    private void draw(GraphicsContext graphicsContext) {
        // Clear the canvas first
        graphicsContext.clearRect(0, 0, WIDTH * TILE_SIZE + 200, HEIGHT * TILE_SIZE);

        // Draw the game board (fill it with white for empty cells)
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                graphicsContext.setFill(Color.BLACK);
                graphicsContext.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                graphicsContext.setStroke(Color.DARKGRAY);
                graphicsContext.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        // Draw the placed shapes
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (board[y][x] != 0) {
                    graphicsContext.setFill(shapeColors[board[y][x] - 1]);
                    graphicsContext.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    graphicsContext.setStroke(Color.BLACK);
                    graphicsContext.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        // Draw the current falling shape on the board
        for (int y = 0; y < currentShape.length; y++) {
            for (int x = 0; x < currentShape[y].length; x++) {
                if (currentShape[y][x] == 1) {
                    graphicsContext.setFill(currentColor); // Set the color for the shape
                    graphicsContext.fillRect((shapeX + x) * TILE_SIZE, (shapeY + y) * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Draw the block
                    graphicsContext.setStroke(Color.BLACK); // Outline the block
                    graphicsContext.strokeRect((shapeX + x) * TILE_SIZE, (shapeY + y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        // Draw the next shape
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Next", WIDTH * TILE_SIZE + 20 , 30);

        double scale = 0.8;
        double offsetX = WIDTH * TILE_SIZE + 20;
        double offsetY = 40;

        if (nextShape != null) {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    graphicsContext.setFill(Color.BLACK);
                    graphicsContext.fillRect(offsetX + x * TILE_SIZE * scale,
                                            offsetY + y * TILE_SIZE * scale,
                                            TILE_SIZE * scale - 1,
                                            TILE_SIZE * scale - 1);
                }
            }

            for (int y = 0; y < nextShape.length; y++) {
                for (int x = 0; x < nextShape[y].length; x++) {
                    if (nextShape[y][x] == 1) {
                        graphicsContext.setFill(nextColor);
                        graphicsContext.fillRect(offsetX + x * TILE_SIZE * scale,
                                                offsetY + y * TILE_SIZE * scale,
                                                TILE_SIZE * scale - 1,
                                                TILE_SIZE * scale - 1);
                    }
                }
            }
        }
        // Score
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Score: " + score,  WIDTH * TILE_SIZE + 20 , HEIGHT * TILE_SIZE / 3) ;
        // level
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Level: " + level,  WIDTH * TILE_SIZE + 20 , 150) ;
        // High Score
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("High Score " + highScore,  WIDTH * TILE_SIZE + 20 , 250) ;

        // Pause the game
        if(paused){
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillText("PAUSE", WIDTH * TILE_SIZE / 2 - 40 , HEIGHT * TILE_SIZE / 2);
        }

        // GameOver
        if(gameOver) {
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillText("GAME OVER", WIDTH * TILE_SIZE /2 - 40, HEIGHT * TILE_SIZE / 2);
        }
    }

    private void update() {
        if (gameOver) return; 
        if (score > highScore) {
            highScore = score;
            saveHighscore();
        }
        if (!collides(shapeX, shapeY + 1, currentShape)) {
            shapeY++; // Move the shape down if no collision occurs
        } else {
            // The shape has landed, place it on the board
            mergeShape();
            clearLines();
            createShape(); // Create a new shape
        }
    }

    // Highscore
    private void loadHighscore(){
        try (BufferedReader br = new BufferedReader(new FileReader(HIGHSCORE_FILE))){
            String line = br.readLine();
            if(line != null){
                highScore = Integer.parseInt(line.trim());
            }
        }catch(IOException e){
            highScore = 0;
        }
    }

    private void saveHighscore(){
        try(PrintWriter pw = new PrintWriter(new FileWriter(HIGHSCORE_FILE))){
            pw.println(highScore);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean collides(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int newX = x + col; // Calculate the x coordinate
                    int newY = y + row; // Calculate the y coordinate

                    // Check if the new coordinates are outside the board bounds
                    if (newX < 0 || newX >= WIDTH || newY >= HEIGHT) {
                        return true;
                    }

                    // Check if the new position is already occupied by another block
                    if (newY >= 0 && board[newY][newX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false; // No collision detected
    }

    public static void main(String[] args) {
        launch(args);
    }

    
}
