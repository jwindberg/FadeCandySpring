package com.marsraver.FadeCandySpring;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageListener implements ApplicationListener<FadeCandySpringApplication.StageReadyEvent> {

    @Value("${spring.application.ui.title}")
    private String applicationTitle;

    @Override
    public void onApplicationEvent(FadeCandySpringApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setTitle(applicationTitle);
        stage.show();
    }


}
