package com.java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.util.Random;

public class MazeGame extends Application {
    private static final int MAZE_SIZE = 15; // Size of the maze
    private static final int CELL_SIZE = 40; // Size of each cell

    private int playerX = 0; // Initial player's X position
    private int playerY = 0; // Initial player's Y position
    private boolean[][] maze = new boolean[MAZE_SIZE][MAZE_SIZE]; // Representation of the maze
    private boolean gameStarted = false; // Flag to check if the game has started

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

    @Override
    public void start(Stage primaryStage) {
        // Create main layout
        @SuppressWarnings("unused")
        BorderPane mainLayout = new BorderPane();
        Scene menuScene = createMenuScene(primaryStage); // Create the menu scene
        primaryStage.setScene(menuScene); // Set the scene to the primary stage
        primaryStage.setTitle("JavaFX Maze Game"); // Set the title of the window
        primaryStage.setResizable(false); // Prevent resizing the window
        primaryStage.show(); // Show the primary stage
    }

    private Scene createMenuScene(Stage primaryStage) {
        StackPane menuPane = new StackPane(); // Create a StackPane for the menu
        Button startButton = new Button("Start Game"); // Button to start the game
        startButton.setOnAction(e -> startGame(primaryStage)); // Set action on button click
        menuPane.getChildren().add(startButton); // Add button to the menu pane
        return new Scene(menuPane, 400, 300); // Return the scene with specified dimensions
    }

    private void startGame(Stage primaryStage) {
        gameStarted = true; // Set game started flag
        primaryStage.setFullScreen(true); // Enable full screen
        primaryStage.setFullScreenExitHint(""); // Hide the exit hint
        primaryStage.setOnCloseRequest(e -> System.exit(0)); // Handle window close request

        // Generate and draw the maze
        generateMaze(); // Generate the maze
        Canvas canvas = new Canvas(MAZE_SIZE * CELL_SIZE, MAZE_SIZE * CELL_SIZE); // Create canvas for drawing
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Get graphics context from the canvas

        // Draw the initial maze and the player
        drawMaze(gc);
        drawPlayer(gc);

        BorderPane gameLayout = new BorderPane(); // Create game layout using BorderPane
        gameLayout.setCenter(canvas); // Set the canvas at the center of the layout

        Scene gameScene = new Scene(gameLayout); // Create the game scene with BorderPane
        gameScene.setOnKeyPressed(e -> {
            if (gameStarted) {
                movePlayer(e.getCode(), gc); // Move the player if the game has started
            }
        });

        primaryStage.setScene(gameScene); // Set the game scene to the primary stage
        primaryStage.show(); // Show the primary stage
    }

    private void generateMaze() {
        @SuppressWarnings("unused")
        Random rand = new Random(); // Create a Random object

        // Initialize the maze with walls
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                maze[i][j] = true; // Start with all walls
            }
        }

        // Start generating the maze from the first cell
        maze[0][0] = false; // Starting point
        generatePath(0, 0); // Generate path from the starting point

        // Ensure that the exit is open
        maze[MAZE_SIZE - 1][MAZE_SIZE - 1] = false; // Exit point
    }

    private void generatePath(int x, int y) {
        // Directions: right, down, left, up
        int[][] directions = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };

        Random rand = new Random();
        shuffleArray(directions, rand); // Shuffle the directions randomly

        for (int[] direction : directions) {
            int newX = x + direction[0] * 2; // Calculate new X position
            int newY = y + direction[1] * 2; // Calculate new Y position

            // Check if the new position is within bounds and is a wall
            if (isInBounds(newX, newY) && maze[newX][newY]) {
                maze[x + direction[0]][y + direction[1]] = false; // Remove the wall
                maze[newX][newY] = false; // Mark the new path
                generatePath(newX, newY); // Recursively generate the path
            }
        }
    }

    private void shuffleArray(int[][] array, Random rand) {
        // Shuffle the array randomly
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1); // Generate a random index
            // Swap elements
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < MAZE_SIZE && y >= 0 && y < MAZE_SIZE; // Check if (x, y) is within bounds
    }

    private void drawMaze(GraphicsContext gc) {
        gc.setFill(Color.WHITE); // Set the fill color to white
        gc.fillRect(0, 0, MAZE_SIZE * CELL_SIZE, MAZE_SIZE * CELL_SIZE); // Draw the background
        gc.setFill(Color.BLACK); // Set the fill color to black

        // Draw maze walls
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (maze[i][j]) {
                    gc.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw the wall
                }
            }
        }

        // Draw the exit point
        gc.setFill(Color.ORANGE); // Set the fill color to orange
        gc.fillRect((MAZE_SIZE - 1) * CELL_SIZE, (MAZE_SIZE - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw the exit
    }

    private void drawPlayer(GraphicsContext gc) {
        gc.setFill(Color.BLUE); // Set the fill color to blue
        gc.fillOval(playerX * CELL_SIZE, playerY * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw the player
    }

    private void movePlayer(KeyCode key, GraphicsContext gc) {
        int newX = playerX; // Initialize new X position
        int newY = playerY; // Initialize new Y position

        // Key mapping
        switch (key) {
            case UP: // Move up
            case W: // Move up
                newY--;
                break;
            case DOWN: // Move down
            case S: // Move down
                newY++;
                break;
            case LEFT: // Move left
            case A: // Move left
                newX--;
                break;
            case RIGHT: // Move right
            case D: // Move right
                newX++;
                break;
            default:
                return; // Do nothing for other keys
        }

        // Check if the new position is within bounds and is not a wall
        if (isInBounds(newX, newY) && !maze[newX][newY]) {
            playerX = newX; // Update player's X position
            playerY = newY; // Update player's Y position
            drawMaze(gc); // Redraw the maze
            drawPlayer(gc); // Redraw the player
        }

        // Check win condition
        if (playerX == MAZE_SIZE - 1 && playerY == MAZE_SIZE - 1) {
            displayWinMessage(gc); // Display win message if player reached the exit
        }
    }

    private void displayWinMessage(GraphicsContext gc) {
        gc.setFill(Color.GREEN); // Set the fill color to green
        gc.fillText("You Win!", 10, 20); // Display win message
        gc.fillText("Press R to Restart", 10, 40); // Display restart message
        gameStarted = false; // Disable game controls
        Scene currentScene = gc.getCanvas().getScene(); // Get the current scene
        currentScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                restartGame(gc); // Restart the game if 'R' is pressed
            }
        });
    }

    private void restartGame(GraphicsContext gc) {
        playerX = 0; // Reset player's X position
        playerY = 0; // Reset player's Y position
        generateMaze(); // Generate a new maze
        drawMaze(gc); // Redraw the maze
        drawPlayer(gc); // Redraw the player
        gameStarted = true; // Enable game controls again

        // Reconfigure controls
        Scene currentScene = gc.getCanvas().getScene(); // Get the current scene
        currentScene.setOnKeyPressed(e -> movePlayer(e.getCode(), gc)); // Enable player movement
    }
}
