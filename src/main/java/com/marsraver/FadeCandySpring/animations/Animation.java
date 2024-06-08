package com.marsraver.FadeCandySpring.animations;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

@AllArgsConstructor
public abstract class Animation {

    protected static final Random RANDOM = new Random();

    protected GraphicsContext gc;

    public abstract void init();

    public abstract void draw();

    public int getWidth() {
        return Double.valueOf(gc.getCanvas().getWidth()).intValue();
    }

    public int getHeight() {
        return Double.valueOf(gc.getCanvas().getHeight()).intValue();
    }

    public Image loadImage(String fileName) {
        try {
            return new Image(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Color getRandomColor() {
        return new Color(randomDouble(), randomDouble(), randomDouble(), 1.0);
    }

    public double randomDouble() {
        return RANDOM.nextDouble();
    }
}
