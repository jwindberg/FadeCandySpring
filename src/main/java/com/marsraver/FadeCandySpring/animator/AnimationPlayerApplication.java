package com.marsraver.FadeCandySpring.animator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AnimationPlayerApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Canvas canvas = new Canvas(1080, 790);
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        BorderPane pane = new BorderPane();
        pane.setCenter(canvas);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);


        primaryStage.show();
    }
}
