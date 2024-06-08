package com.marsraver.FadeCandySpring;


import com.marsraver.FadeCandySpring.fc.FadeCandyClient;
import com.marsraver.FadeCandySpring.fc.FadeCandyTimer;
import com.marsraver.FadeCandySpring.fc.OpcLayout;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
//import javax.imageio.ImageIO;

public class Paint extends Application {

    GraphicsContext gc;

    ColorPicker cpLine = new ColorPicker(Color.BLACK);
    ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);
    private FadeCandyClient fadeCandyClient;

    public static void main(String[] args) {
        launch(args);
    }

    public GraphicsContext gc() {
        return gc;
    }

    @Override
    public void start(Stage stage) {


        Slider sizeSlider = new Slider(1, 50, 3);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);


        /* ----------btns---------- */
        ToggleButton drawbtn = new DrawButton();
        ToggleButton rubberbtn = new EraserButton();
        ToggleButton linebtn = new LineButton();
        ToggleButton rectbtn = new RectangleButton();
        ToggleButton circlebtn = new CircleButton();
        ToggleButton elpslebtn = new EllipseButton();
        ToggleButton fillButton = new ResetButton();
        ToggleButton pixelsButton = new PixelGridButton();

        ToggleButton[] toolsArr = {drawbtn, rubberbtn, linebtn, rectbtn, circlebtn, elpslebtn, fillButton, pixelsButton};

        ToggleGroup tools = new ToggleGroup();

        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }

        Label line_color = new Label("Line Color");
        Label fill_color = new Label("Fill Color");
        Label line_width = new Label("3.0");

        CheckBox showPixelLocations = new CheckBox("Pixels");


        VBox btns = new VBox(10);
        btns.getChildren().addAll(drawbtn, rubberbtn, linebtn, rectbtn, circlebtn, elpslebtn, fillButton, pixelsButton, line_color, cpLine, fill_color, cpFill, line_width, sizeSlider, showPixelLocations);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: #999");
        btns.setPrefWidth(100);

        /* ----------Draw Canvas---------- */
        Canvas canvas = new Canvas(1080, 790);


        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());


        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();

        canvas.setOnMousePressed(me -> {
            Toggle selectedToggle = tools.getSelectedToggle();
            if (selectedToggle instanceof ToolButton) {
                ((ToolButton) selectedToggle).onMousePressed(me);
            }
        });
        canvas.setOnMouseDragged(me -> {
            Toggle selectedToggle = tools.getSelectedToggle();
            if (selectedToggle instanceof ToolButton) {
                ((ToolButton) selectedToggle).onMouseDragged(me);
            }
        });
        canvas.setOnMouseReleased(me -> {
            Toggle selectedToggle = tools.getSelectedToggle();
            if (selectedToggle instanceof ToolButton) {
                ((ToolButton) selectedToggle).onMouseReleased(me);
            }
        });


        // color picker
        cpLine.setOnAction(e -> {
            gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e -> {
            gc.setFill(cpFill.getValue());
        });

        // sizeSlider
        sizeSlider.valueProperty().addListener(e -> {
            double width = sizeSlider.getValue();
            line_width.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });



        /* ----------STAGE & SCENE---------- */
        BorderPane pane = new BorderPane();
        pane.setLeft(btns);
        pane.setCenter(canvas);

        int width = 256;
        int height = 512;

        Scene scene = new Scene(pane, width + 150, height + 20);


        stage.setTitle("Paint");
        stage.setScene(scene);


        OpcLayout layout = new OpcLayout(gc, new Robot());
        fadeCandyClient = new FadeCandyClient("192.168.7.63", 7890, layout);
        fadeCandyClient.ledGrid(0, 16, 32,
                (float) width / 2f, (float) height / 2f,
                (float) width / 16f,
                (float) height / 32f, 0, true);
        fadeCandyClient.showLocations(false);

        showPixelLocations.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                fadeCandyClient.showLocations(newValue);
            }
        });

        FadeCandyTimer fadeCandyTimer = new FadeCandyTimer(fadeCandyClient);
        fadeCandyTimer.start();

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        fadeCandyClient.clear();
    }

    private static abstract class ToolButton extends ToggleButton {
        public ToolButton(String text) {
            super(text);
        }

        public void onMousePressed(MouseEvent me) {
        }

        public void onMouseDragged(MouseEvent me) {
        }

        public void onMouseReleased(MouseEvent me) {
        }
    }

    private class DrawButton extends ToolButton {
        public DrawButton() {
            super("Draw");
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            gc.setStroke(cpLine.getValue());
            gc.beginPath();
            gc.lineTo(me.getX(), me.getY());
        }

        @Override
        public void onMouseDragged(MouseEvent me) {
            gc.lineTo(me.getX(), me.getY());
            gc.stroke();
        }
    }

    private class EraserButton extends ToolButton {
        public EraserButton() {
            super("Eraser");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(me.getX() - lineWidth / 2, me.getY() - lineWidth / 2, lineWidth, lineWidth);
        }

        @Override
        public void onMouseDragged(MouseEvent me) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(me.getX() - lineWidth / 2, me.getY() - lineWidth / 2, lineWidth, lineWidth);
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            double lineWidth = gc.getLineWidth();
            gc.clearRect(me.getX() - lineWidth / 2, me.getY() - lineWidth / 2, lineWidth, lineWidth);
        }
    }

    private class LineButton extends ToolButton {
        Line line;

        public LineButton() {
            super("Line");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            line = new Line();
            gc.setStroke(cpLine.getValue());
            line.setStartX(me.getX());
            line.setStartY(me.getY());
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            line.setEndX(me.getX());
            line.setEndY(me.getY());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    private class RectangleButton extends ToolButton {
        Rectangle rectangle;

        public RectangleButton() {
            super("Rectangle");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            rectangle = new Rectangle();
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            rectangle.setX(me.getX());
            rectangle.setY(me.getY());
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            rectangle.setWidth(Math.abs((me.getX() - rectangle.getX())));
            rectangle.setHeight(Math.abs((me.getY() - rectangle.getY())));
            if (rectangle.getX() > me.getX()) {
                rectangle.setX(me.getX());
            }
            if (rectangle.getY() > me.getY()) {
                rectangle.setY(me.getY());
            }

            gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
            gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

        }
    }

    private class CircleButton extends ToolButton {
        Circle circle;

        public CircleButton() {
            super("Circle");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            circle = new Circle();
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            circle.setCenterX(me.getX());
            circle.setCenterY(me.getY());
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            circle.setRadius((Math.abs(me.getX() - circle.getCenterX()) + Math.abs(me.getY() - circle.getCenterY())) / 2);

            if (circle.getCenterX() > me.getX()) {
                circle.setCenterX(me.getX());
            }
            if (circle.getCenterY() > me.getY()) {
                circle.setCenterY(me.getY());
            }

            gc.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            gc.strokeOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());

        }
    }

    private class EllipseButton extends ToolButton {
        Ellipse ellipse;

        public EllipseButton() {
            super("Ellipse");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            ellipse = new Ellipse();
            gc.setStroke(cpLine.getValue());
            gc.setFill(cpFill.getValue());
            ellipse.setCenterX(me.getX());
            ellipse.setCenterY(me.getY());
        }

        @Override
        public void onMouseReleased(MouseEvent me) {
            ellipse.setRadiusX(Math.abs(me.getX() - ellipse.getCenterX()));
            ellipse.setRadiusY(Math.abs(me.getY() - ellipse.getCenterY()));

            if (ellipse.getCenterX() > me.getX()) {
                ellipse.setCenterX(me.getX());
            }
            if (ellipse.getCenterY() > me.getY()) {
                ellipse.setCenterY(me.getY());
            }

            gc.strokeOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
            gc.fillOval(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());

        }
    }

    private class ResetButton extends ToolButton {
        public ResetButton() {
            super("Reset");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        }
    }

    private class PixelGridButton extends ToolButton {

        int width = 32;
        int height = 32;

        public PixelGridButton() {
            super("Pixels");
        }

        @Override
        public void onMousePressed(MouseEvent me) {
            double canvasHeight = gc.getCanvas().getHeight();
            double canvasWidth = gc.getCanvas().getWidth();
            double centerY = canvasHeight / 2.0;
            double centerX = canvasWidth / 2.0;
            double squareSize = (canvasWidth < canvasHeight) ? canvasWidth : canvasHeight;
            double spacing = squareSize / 32.0;

            double currentX = centerX - (14.0 * spacing);


            for (int x = 0; x < 32; x++) {
                double currentY = centerY - (14.0 * spacing);
                for (int y = 0; y < 32; y++) {
                    point(currentX, currentY);
                    currentY += spacing;
                }
                currentX += spacing;
            }

            System.out.println("height " + canvasHeight + " wdith " + canvasWidth);
        }

        private void point(double x, double y) {
            PixelWriter pixelWriter = gc.getPixelWriter();
            pixelWriter.setColor(Double.valueOf(x).intValue(), Double.valueOf(y).intValue(), Color.GREEN);
//            System.out.println("Setting Pixel " + x + " / " + y);
        }
    }
}