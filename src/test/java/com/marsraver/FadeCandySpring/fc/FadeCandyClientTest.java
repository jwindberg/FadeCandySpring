package com.marsraver.FadeCandySpring.fc;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FadeCandyClientTest {

    int height = 512;
    int width = 256;
    OpcLayout layout = new OpcLayout(height, width);
    @Mock
    private GraphicsContext graphicsContext;
    @Mock
    private Canvas canvas;
    @Mock
    private Robot robot;
    private FadeCandyClient client;

    @BeforeEach
    void beforeEach() {
        when(graphicsContext.getCanvas()).thenReturn(canvas);
        when(canvas.getHeight()).thenReturn((double) height);
        when(canvas.getWidth()).thenReturn((double) width);
        when(robot.getPixelColor(anyDouble(), anyDouble())).thenReturn(Color.PURPLE);
        OpcLayout layout = new OpcLayout(graphicsContext, robot);
        client = new FadeCandyClient("192.168.7.63", 7890, layout);
    }

    @Test
    public void testFaceCandyClient() {
        client.ledGrid(0, 16, 32, width / 2f, height / 2f, width / 16f,
                height / 32f, 0, true);
        client.draw();
        client.draw();
    }

}