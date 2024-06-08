package com.marsraver.FadeCandySpring.animator;

import com.marsraver.FadeCandySpring.animations.Animation;
import com.marsraver.FadeCandySpring.fc.FadeCandyClient;
import javafx.animation.AnimationTimer;

import java.util.LinkedList;
import java.util.List;

public class Animator extends AnimationTimer {

    public List<Animation> animations = new LinkedList<>();

    private FadeCandyClient fadeCandyClient;

    public Animator(FadeCandyClient fadeCandyClient) {
        this.fadeCandyClient = fadeCandyClient;
    }

    public Animator add(Animation animation) {
        animations.add(animation);
        return this;
    }

    public void init() {
        animations.forEach(Animation::init);
    }

    @Override
    public void handle(long now) {
        animations.forEach(Animation::draw);
    }
}
