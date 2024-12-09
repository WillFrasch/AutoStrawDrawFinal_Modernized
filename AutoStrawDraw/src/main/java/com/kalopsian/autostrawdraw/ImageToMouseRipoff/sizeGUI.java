package com.kalopsian.autostrawdraw.ImageToMouseRipoff;
import com.kalopsian.autostrawdraw.drawGUI;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;




public class sizeGUI extends Application {
    private Label sizeLbl;
    private Stage stage;
    private File imageFile;
    public sizeGUI() {
        Platform.setImplicitExit(false);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        sizeLbl = new Label();
        stage = primaryStage;
        stage.setTitle("Canvas");
        Scene scene = new Scene(sizeLbl);
        stage.setOpacity(0.5);
        stage.setScene(scene);
        formWindowOpened(new WindowEvent(null, null));
        new sizeGUI();
        this.initComponents();
    }


    private void initComponents () {
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(this::formWindowClosing);
        stage.setOnShown(this::formWindowOpened);
        sizeLbl.setFont(new Font("Tahoma", 24));
        sizeLbl.setAlignment(Pos.CENTER);
        sizeLbl.setText("Size");
    }

    private void formWindowOpened (javafx.stage.WindowEvent evt) {

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            sizeGUI.this.sizeLbl.setText((int)stage.getWidth() + " x " + (int)stage.getHeight());
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            sizeGUI.this.sizeLbl.setText((int)stage.getWidth() + " x " + (int)stage.getHeight());
        });

    }


    private void formWindowClosing (javafx.stage.WindowEvent evt){
        drawGUI.szGUI = null;
        stage.hide();
    }

    public static void main (String[]args){
        Application.launch(args);
    }
}
