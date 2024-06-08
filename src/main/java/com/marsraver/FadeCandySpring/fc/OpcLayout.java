package com.marsraver.FadeCandySpring.fc;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import lombok.Getter;

@Getter
public class OpcLayout {
    private int height;
    private int width;
    private GraphicsContext graphicsContext;
    private WritableImage writableImage;
//    private Robot robot;

    public OpcLayout(GraphicsContext graphicsContext, Robot robot) {

        this.graphicsContext = graphicsContext;
        this.height = Double.valueOf(graphicsContext.getCanvas().getHeight()).intValue();
        this.width = Double.valueOf(graphicsContext.getCanvas().getWidth()).intValue();
    }

    public OpcLayout(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public void snap() {
        writableImage = graphicsContext.getCanvas().snapshot(new SnapshotParameters(), null);
//        robot = new Robot();
    }

    public Color getPixelColor(int xValue, int yValue) {
        return (writableImage == null) ? null : writableImage.getPixelReader().getColor(xValue, yValue);
//        return robot.getPixelColor(xValue, yValue);
    }


    public void setPixel(Pixel pixel, int val) {
        graphicsContext.getPixelWriter().setColor(pixel.getX(), pixel.getY(), new OpcColor(val).getColor());
    }

    public Color getPixelColor(Pixel pixel) {
        return getPixelColor(pixel.x, pixel.y);
    }

    public OpcColor getOpcColor(int x, int y) {
        return new OpcColor(getPixelColor(x, y));
    }

    public OpcColor getOpcColor(Pixel pixel) {

        Color pixelColor = getPixelColor(pixel);
//        System.out.println("Red: " + pixelColor.getRed() + "\tGreen: " + pixelColor.getGreen() + "\tBlue: " + pixelColor.getBlue());

        return pixelColor == null ? null : new OpcColor(pixelColor);
    }


}
