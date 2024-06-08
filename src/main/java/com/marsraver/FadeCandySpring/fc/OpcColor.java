package com.marsraver.FadeCandySpring.fc;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class OpcColor {
    private int red;
    private int green;
    private int blue;

    public OpcColor(Color color) {
        red = (int) (256 * color.getRed());
        green = (int) (256 * color.getGreen());
        blue = (int) (256 * color.getBlue());
    }

    public OpcColor(int rgb) {
        red = (rgb >> 16) & 0xff;
        green = (rgb >> 8) & 0xff;
        blue = (rgb) & 0xff;
    }

    public Color getColor() {
        return new Color(red / 256.0, green / 256.0, blue / 256.0, 1);
    }

    public int getColorValue() {
        if (red > 255) red = 255;
        else if (red < 0) red = 0;
        if (green > 255) green = 255;
        else if (green < 0) green = 0;
        if (blue > 255) blue = 255;
        else if (blue < 0) blue = 0;

        return 0xff000000 | (red << 16) | (green << 8) | blue;
    }
}
