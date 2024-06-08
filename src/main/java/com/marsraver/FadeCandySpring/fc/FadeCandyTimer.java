package com.marsraver.FadeCandySpring.fc;

import javafx.animation.AnimationTimer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FadeCandyTimer extends AnimationTimer {
    FadeCandyClient fadeCandyClient;

    @Override
    public void handle(long now) {
        fadeCandyClient.draw();
    }
}
