package com.marsraver.FadeCandySpring.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FlashLight extends Animation {


    private Image dot;

    public FlashLight(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void init() {

    }

    @Override
    public void draw() {
        double centerX = getWidth() * randomDouble();
        double centerY = getHeight() * randomDouble();
        double x = getWidth() * randomDouble();
        double y = getHeight() * randomDouble();

        double radius = (Math.abs(x - centerX) + Math.abs(y - centerY)) / 2;
        Circle circle = new Circle(centerX, centerY, radius);
        Color randomColor = getRandomColor();
        gc.setFill(randomColor);
        gc.setStroke(randomColor);
        gc.fillOval(circle.getCenterX(), circle.getCenterY(), radius, radius);

    }
}
