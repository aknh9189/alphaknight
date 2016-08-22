package com.alphaknight;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Part of MVC pattern. Provides a GUI to the Board representing the current game state.
 */
public class BoardView extends Application{
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        Menu view = new Menu("View");
        Menu help = new Menu("Help");
        menuBar.getMenus().addAll(file, view, help);

        Image board = new Image("/board.png");
        ImageView boardView = new ImageView(board);

        root.setTop(menuBar);
        root.setCenter(boardView);

        Scene scene = new Scene(root, Color.OLDLACE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("AlphaKnight");
        primaryStage.show();
    }
}
