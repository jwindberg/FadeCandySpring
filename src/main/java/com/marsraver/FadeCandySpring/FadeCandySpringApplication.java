package com.marsraver.FadeCandySpring;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


public class FadeCandySpringApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer = ac -> {
            ac.registerBean(Application.class, () -> FadeCandySpringApplication.this);
            ac.registerBean(Parameters.class, this::getParameters);
            ac.registerBean(HostServices.class, this::getHostServices);
        };
        this.context = new SpringApplicationBuilder().sources(BootFadeCandySpringApplication.class).initializers(initializer).run(getParameters().getRaw().toArray(new String[0]));

    }

    @Override
    public void start(Stage stage) throws Exception {
        this.context.publishEvent(new StageReadyEvent(stage));
//        stage.show();
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }

    static class StageReadyEvent extends ApplicationEvent {

        public StageReadyEvent(Stage source) {
            super(source);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}
