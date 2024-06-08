package com.marsraver.FadeCandySpring.fc;

import lombok.Getter;

@Getter
public class Pixel {

    public int x;
    public int y;

    public Pixel() {
        // empty constructor
    }

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
