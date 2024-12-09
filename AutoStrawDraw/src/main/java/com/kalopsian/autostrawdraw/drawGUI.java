package com.kalopsian.autostrawdraw;
import com.kalopsian.autostrawdraw.ImageToMouseRipoff.sizeGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;

import java.awt.event.InputEvent;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class drawGUI extends Application {
    static Point a;
    static Point b;
    static int totalX;
    static int totalY;
    public static sizeGUI szGUI = null;
    private int imageWidth, imageHeight;
    private static boolean autoColor = true;
    private BufferedImage imgMaster;
    private boolean brushready = false;
    private BufferedImage reducedImage;
    private static final Label StatusLabel = new Label("Welcome!");
    private static final Button drawBtn = new Button("Draw Image");
    private boolean drawReady = false;
    private boolean coordSet = false;
    private Stage canvasStage;
    private static boolean drawBtnPressed = false;
    private static final ProgressBar pb = new ProgressBar(0);
    private static final Label pLabel = new Label();
    private final Button drawLocation = new Button("Set Canvas Location");
    private int colorNum = 0;
    private Stage s;
    private VBox fileSelection;
    private VBox imagePreviewSection;
    private VBox colorPreviewSection;
    private ComboBox<String> imageDropdown;
    private static List<int[]> colorList;
    private BufferedImage[] colorSplit;
    private static int stepCounter = 0;
    public static AutoColorHandler autoColorHandler = new AutoColorHandler();
    private final ImageView imagePreview = new ImageView();
    private StackPane imagePreviewContainer;
    private final ImageView colorPreview = new ImageView();
    private final ArrayList<Point> blackPixels = new ArrayList<>();
    static List<Line> linesArray = Collections.synchronizedList(new ArrayList<>());
    private boolean canvasSet = false;
    private void setMouseLocations() {
        GlobalMouseCapturer capturer = new GlobalMouseCapturer();

        Platform.runLater(() -> StatusLabel.setText("Click on the color wheel..."));
        capturer.captureGlobalClickAsync(() -> {
            Point colorWheel = capturer.getLastCapturedPoint();
            System.out.println("[DEBUG] Captured Color Wheel: " + colorWheel);

            Platform.runLater(() -> StatusLabel.setText("Click on the red input box..."));
            capturer.captureGlobalClickAsync(() -> {
                Point redBox = capturer.getLastCapturedPoint();
                System.out.println("[DEBUG] Captured Red Box: " + redBox);

                Platform.runLater(() -> StatusLabel.setText("Click on the green input box..."));
                capturer.captureGlobalClickAsync(() -> {
                    Point greenBox = capturer.getLastCapturedPoint();
                    System.out.println("[DEBUG] Captured Green Box: " + greenBox);

                    Platform.runLater(() -> StatusLabel.setText("Click on the blue input box..."));
                    capturer.captureGlobalClickAsync(() -> {
                        Point blueBox = capturer.getLastCapturedPoint();
                        System.out.println("[DEBUG] Captured Blue Box: " + blueBox);

                        Platform.runLater(() -> StatusLabel.setText("Click on the OK button..."));
                        capturer.captureGlobalClickAsync(() -> {
                            Point okButton = capturer.getLastCapturedPoint();
                            System.out.println("[DEBUG] Captured OK Button: " + okButton);

                            // Validate all captured points
                            if (colorWheel == null || redBox == null || greenBox == null || blueBox == null || okButton == null) {
                                System.err.println("[ERROR] One or more captured locations are null!");
                                return;
                            }

                            // Set locations in AutoColorHandler
                            AutoColorHandler autoColorHandler = new AutoColorHandler();
                            autoColorHandler.setLocations(colorWheel, redBox, greenBox, blueBox, okButton);

                            System.out.println("[DEBUG] All locations set successfully.");

                            Platform.runLater(() -> StatusLabel.setText("Mouse locations successfully set!"));

                            drawGUI.autoColorHandler = autoColorHandler;
                        });
                    });
                });
            });
        });
    }

    public static String getCurrentColor() {
        if (colorList == null || stepCounter >= colorList.size()) {
            return "No colors remaining.";
        }

        int[] color = colorList.get(stepCounter);
        return "R: " + color[0] + ", G: " + color[1] + ", B: " + color[2];
    }

    @Override
    public void start(Stage stage) {
        // Set up main layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-font-family: 'Arial';");
        Image icon = new Image(Objects.requireNonNull(getClass().getResource("/icon.png")).toExternalForm());
        stage.getIcons().add(icon);
        // Title
        Label title = new Label("ColorStrawPainter");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        // Status Label
        StatusLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12px;");

        // File chooser



        Button setLocations = new Button("Set ColorChange Locations");
        setLocations.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        FileChooser fileChooser = new FileChooser();
        Button fileButton = new Button("Choose Image");
        fileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        fileButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                openFile(file);
                imagePreviewSection.setVisible(false);
                imageDropdown.getItems().add("Original Image");
                imageDropdown.getSelectionModel().selectFirst();
            }
        });
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        // Color input field
        TextField colorField = new TextField();
        colorField.setPromptText("Enter Number of Colors");
        colorField.setMaxWidth(150);
        colorField.setStyle("-fx-font-size: 12px;");
        s = new Stage();
        s.setOpacity(0.5);
        s.setWidth(200);
        s.setHeight(200);
        try {
            new sizeGUI().start(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Button colorSubmit = new Button("Submit");
        colorSubmit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        colorSubmit.setOnAction(event -> {
            if (reducedImage != null) {
                processColorInput(colorField.getText());
                ImageSplitter splitter = new ImageSplitter();
                colorSplit = splitter.splitImageByColor(reducedImage);
                colorList = splitter.getColorArray();
                drawReady = true;
            } else {
                StatusLabel.setText("Please choose an image first!");
            }
        });
        s.setWidth(200);
        s.setHeight(200);
        // Dropdown menu for images
        imageDropdown = new ComboBox<>();
        imageDropdown.setPromptText("View Image");
        imageDropdown.setDisable(true);
        imageDropdown.setOpacity(0);
        imageDropdown.setStyle("-fx-font-size: 12px;");
        imageDropdown.setOnAction(event -> {
            String selected = imageDropdown.getValue();
            if ("Original Image".equals(selected)) {
                imagePreview.setImage(convertBufferedImageToFXImage(imgMaster));
            } else if ("Color Reduced Image".equals(selected)) {
                imagePreview.setImage(bufferedImageToWritableImage(reducedImage));
            }
        });
        // ToggleSwitch for Auto-Coloring
        ToggleSwitch autoColorSwitch = new ToggleSwitch("Auto-Coloring");
        autoColorSwitch.setSelected(autoColor);

// Styling for the switch
        autoColorSwitch.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-color: transparent; " + // Transparent background for the label area
                        "-fx-border-color: #4CAF50; " +         // Border matching the rest of the UI
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-padding: 5px; " +
                        "-fx-alignment: center; " +
                        "-fx-font-weight: bold;"
        );

        setLocations.setOnAction(event -> {
            setMouseLocations();
        });
// Adjust toggle alignment and behavior
        autoColorSwitch.getStyleClass().add("toggle-switch");
        autoColorSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            autoColor = newValue;
            if(!(newValue)){
                setLocations.setDisable(true);
            }
            StatusLabel.setText("Auto-Coloring: " + (autoColor ? "Enabled" : "Disabled"));
        });

// Add CSS for better toggle visuals

        imageDropdown.getItems().add("Color Reduced Image");
        imagePreview.setPreserveRatio(true);
        imagePreview.setOnMouseClicked(event -> openZoomedImage(imagePreview.getImage(), "Preview"));
        imagePreviewSection = new VBox(10, new Label("Preview:"), imagePreview);
        imagePreviewSection.setAlignment(Pos.CENTER);
        imagePreviewSection.setVisible(true);

        // Color Preview Section
        imagePreviewContainer = new StackPane();
        imagePreviewContainer.setMaxSize(100,100); // Fixed size for the image preview
        imagePreviewContainer.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: #eee;");
        imagePreview.setFitWidth(100);
        imagePreview.setFitHeight(100);
        imagePreview.setPreserveRatio(true);
        imagePreviewContainer.getChildren().add(imagePreview);
        // Progress Bar and Label
        HBox progressSection = new HBox(10, pLabel, pb);
        progressSection.setAlignment(Pos.CENTER);
        pb.setMaxWidth(200);
        autoColorSwitch.setTextAlignment(TextAlignment.RIGHT);
        // Draw button
        drawBtn.setDisable(true);
        drawBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        drawBtn.setOnAction(event -> {
            if (drawReady && stepCounter < colorList.size() && canvasSet) {
                try {
                    drawBtnActionPerformed();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if(stepCounter >= colorList.size()){
                System.exit(1);
            }else{
                StatusLabel.setText("Set up properly before drawing!");
            }
        });
        imagePreviewContainer.getChildren().add(imagePreviewSection);
        // Set Canvas Location Button
        drawLocation.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        drawLocation.setOnAction(event ->
            toggleCanvasLocation(s));

        // Layout
        StatusLabel.setMinHeight(30);
        StatusLabel.setTranslateY(-10);
        HBox colorBox = new HBox(10, colorField, colorSubmit);
        colorBox.setAlignment(Pos.CENTER);
        root.setTranslateY(-20);
        root.getChildren().addAll(
                title,
                fileButton,
                imagePreviewContainer,
                imageDropdown,
                colorBox,
                autoColorSwitch,
                setLocations,

                drawLocation,
                drawBtn,
                progressSection,
                StatusLabel
        );

        // Scene and Stage
        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColorStrawPainter");
        stage.setScene(scene);
        stage.show();
    }

    private void processColorInput(String input) {
        try {
            imageDropdown.getItems().clear();
            imageDropdown.getItems().add("Color Reduced Image");
            imageDropdown.getItems().add("Original Image");
            colorNum = Integer.parseInt(input);
            reducedImage = PaletteReducer.reducePalette(imgMaster, colorNum);
            WritableImage writableImage = bufferedImageToWritableImage(reducedImage);
            colorPreview.setImage(writableImage);
            drawBtn.setDisable(false);
            imageDropdown.setOpacity(1);
            imageDropdown.setDisable(false);
            imagePreview.setImage(colorPreview.getImage());
            imageDropdown.getSelectionModel().selectFirst();
        } catch (NumberFormatException e) {
            StatusLabel.setText("Invalid input. Please enter a valid number.");
        }
    }
    private WritableImage convertBufferedImageToFXImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                pixelWriter.setArgb(x, y, argb);
            }
        }
        return writableImage;
    }


    private void toggleCanvasLocation(Stage parentStage) {
        if (coordSet) {
            totalX = (int) s.getWidth();
            totalY = (int) s.getHeight();
            a = new Point((int) s.getX(), (int) s.getY() + totalY);
            b = new Point((int) s.getX() + totalX, (int) s.getY());
            s.hide();
            drawLocation.setText("Set Canvas Location");
            StatusLabel.setText("Canvas Set!");
            canvasSet = true;
            coordSet = false;
        } else {
            s.show();
            drawLocation.setText("Confirm Canvas Location");
            coordSet = true;
        }
    }
    /*Z
        linesArray.clear();
        this.blackPixels.clear();
        BufferedImage imgSource = this.reducedImage;
        BufferedImage bi = this.createResizedCopy(imgSource, totalX, totalY, true);
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                if (red <= 100 && green <= 100 && blue <= 100) {
                    this.blackPixels.add(new Point(x + a.x, y + b.y));
                }
            }
        }

        for (int count = 0; count < this.blackPixels.size(); count++) {
            Point p = this.blackPixels.get(count);
            if (count == 0 || p.x - 1 != this.blackPixels.get(count - 1).x) {
                Point A = p;

                while (count != this.blackPixels.size() - 1 && p.x + 1 == this.blackPixels.get(count + 1).x) {
                    p = this.blackPixels.get(++count);
                }

                Point p2 = this.blackPixels.get(count);
                linesArray.add(new drawGUI.Line(A, p2));
            }
        }
    }
*/


    public void lineReadImage() {
        linesArray.clear();

        BufferedImage bi = reducedImage;
        BufferedImage imgSource = this.createResizedCopy(bi, totalX, totalY, true);
        int width = imgSource.getWidth();
        int height = imgSource.getHeight();
        int currentColor =  (colorList.get(stepCounter)[0] << 16) | (colorList.get(stepCounter)[1] << 8) | colorList.get(stepCounter)[2];
        for (int y = 0; y < height; y++) {
            Line currentLine = null;
            for (int x = 0; x < width; x++) {
                int rgb = imgSource.getRGB(x, y);

                // Only consider non-black pixels
                if ((rgb & 0xFFFFFF) == currentColor) {
                    Point currentPoint = new Point(x + a.x, y + b.y);

                    if (currentLine == null) {
                        currentLine = new Line(currentPoint, currentPoint);
                    } else {
                        currentLine.B = currentPoint; // Extend the line
                    }
                } else {
                    if (currentLine != null) {
                        linesArray.add(currentLine);
                        currentLine = null;
                    }
                }
            }

            // Add any remaining line at the end of the row
            if (currentLine != null) {
                linesArray.add(currentLine);
            }
        }
    }

    public static void lineDraw() {
        drawBtnPressed = false;

        if (colorList == null || stepCounter >= colorList.size()) {
            return;
        }

        (new Thread() {
            @Override
            public void run(){
                Robot bot;
                try {
                    bot = new Robot();
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }

                int totalLines = linesArray.size();
                for (int i = 0; i < totalLines; i++) {
                    Line line = linesArray.get(i);

                    bot.mouseMove(line.A.x, line.A.y);
                    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                    if (!line.A.equals(line.B)) {
                        bot.mouseMove(line.B.x, line.B.y);
                    }

                    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                    int finalI = i;
                    Platform.runLater(() -> {
                        pb.setProgress((double) (finalI + 1) / totalLines);
                        StatusLabel.setText((finalI + 1) + " of " + totalLines + " lines completed.");
                    });
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Platform.runLater(() -> {
                    pb.setProgress(1);
                    StatusLabel.setText("Color: " + (stepCounter + 1) + " of " + colorList.size());
                    drawBtnPressed = true;
                    if(autoColor){
                        stepCounter++;
                        drawBtn.fire();
                    }else{
                        stepCounter++;
                        drawBtn.setText("Brush Color Has Been Changed");
                        StatusLabel.setText("Change Brush Color to: " + getCurrentColor() + "\n Then Press the Button");
                    }
                });
            }
        }).start();
    }




    private WritableImage bufferedImageToWritableImage(BufferedImage bufferedImage) {
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int argb = bufferedImage.getRGB(x, y);
                pixelWriter.setArgb(x, y, argb);
            }
        }
        return writableImage;
    }

    public void openFile(File file) {
        try {
            imgMaster = ImageIO.read(file);
            reducedImage = imgMaster;
            Image fxImage = new Image(file.toURI().toString());
            imageWidth = (int) fxImage.getWidth();
            imageHeight = (int) fxImage.getHeight();
            s.setResizable(true);
            s.setWidth(imageWidth);
            s.setHeight(imageHeight);
            imagePreview.setImage(fxImage);
        } catch (IOException e) {
            StatusLabel.setText("Error loading image!");
        }
    }

    private void openZoomedImage(Image image, String title) {
        Stage zoomStage = new Stage();
        zoomStage.setTitle(title);
        ImageView zoomedView = new ImageView(image);
        zoomedView.setPreserveRatio(true);
        zoomedView.setFitWidth(600);
        VBox zoomRoot = new VBox(zoomedView);
        zoomRoot.setAlignment(Pos.CENTER);
        Scene zoomScene = new Scene(zoomRoot, 800, 600);
        zoomStage.setScene(zoomScene);
        zoomStage.show();
    }

    private void drawBtnActionPerformed() throws InterruptedException, AWTException {

        if (stepCounter >= colorList.size()) {
            System.out.println("[DEBUG] No more colors to process.");
            System.exit(1);
        } else {
            pb.setOpacity(1);
            reducedImage = colorSplit[stepCounter];
            linesArray.clear();
            lineReadImage();
            if (drawReady) {
                drawBtnPressed = true;
                try {
                    if (!autoColor) {
                        if (brushready) {
                            lineDraw();
                        }
                        drawBtn.setText("Brush Color Has Been Changed");
                        StatusLabel.setText("Change Brush Color to: " + getCurrentColor() + "\n Then Press the Button");
                        brushready = true;


                    } else {
                        Robot bot = new Robot();
                        CountDownLatch latch = new CountDownLatch(1);
                        autoColorHandler.setColor(bot, colorList.get(stepCounter), latch);
                        latch.await();
                        lineDraw();
                    }
                } catch (InterruptedException | AWTException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private BufferedImage createResizedCopy (BufferedImage originalImage,int width, int height, boolean preserveAlpha){
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage resizedImage = new BufferedImage(width, height, imageType);

        Graphics2D g = resizedImage.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    /*
    public static void lineDraw() throws InterruptedException {
        drawBtnPressed = false;

        System.out.println("[DEBUG] Starting lineDraw...");

        if (colorList == null || stepCounter >= colorList.size()) {
            System.out.println("[DEBUG] No more colors to process. Exiting...");
            return;
        }

        int[] currentColor = colorList.get(stepCounter);
        System.out.println("[DEBUG] Processing color: R=" + currentColor[0] + ", G=" + currentColor[1] + ", B=" + currentColor[2]);
        (new Thread() {
            @Override
            public void run() {
                Robot bot = null;

                try {
                    bot = new Robot();
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }

                int size = linesArray.size();

                for (Line line : linesArray) {
                    int index = linesArray.indexOf(line) + 1;
                    Point start = line.A;
                    Point end = line.B;
                    if (start == end) {
                        bot.mouseMove(start.x, start.y);
                        bot.mousePress(16);
                        bot.mouseRelease(16);
                    } else {
                        bot.mouseMove(start.x, start.y);
                        bot.mousePress(16);
                        bot.mouseMove(end.x, end.y);
                        bot.mouseRelease(16);
                    }
                    try {
                        Thread.sleep(4L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        pLabel.setText(index + " of " + size + " lines.");
                        pb.setProgress((double) index /size);

                    });
                }
                Platform.runLater(() -> {
                    pLabel.setText(size + " of " + size + " lines.");
                    pb.setProgress(1);
                    StatusLabel.setText("Color: " + (stepCounter + 1) + " of " + colorList.size());
                    drawBtnPressed = true;
                    if(autoColor){
                        stepCounter++;
                        drawBtn.fire();
                    }else{
                        stepCounter++;
                        drawBtn.setText("Brush Color Has Been Changed");
                        StatusLabel.setText("Change Brush Color to: " + getCurrentColor() + "\n Then Press the Button");
                    }



                });
            }

        }).start();


    }

     */







    public static void main(String[] args) {
        launch();
    }

    public class Line {
        public final Point A;
        public Point B;

        public Line(Point A, Point B) {
            this.A = A;
            this.B = B;
        }
    }
}
