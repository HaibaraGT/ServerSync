package com.superzanti.serversync.GUIJavaFX;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PaneProgressBar extends VBox {

    private Label label;
    private ProgressBar progressBar;
    private Label statusLabel,pathLabel;
    private String statusText,pathText;
    private HBox hBoxBar;

    public PaneProgressBar(){
        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);
        label =  new Label("Copy files:");
        pathLabel = new Label("");
        progressBar = new ProgressBar(0);
        hBoxBar = new HBox();
        hBoxBar.getChildren().addAll(label,progressBar);
        hBoxBar.setAlignment(Pos.CENTER);
        statusLabel = new Label("");

        //statusLabel.setMaxWidth(250);
        statusLabel.setTextFill(Color.BLUE);
        progressBar.setProgress(0);
        progressBar.prefWidthProperty().bind(this.widthProperty().subtract(250));

        this.getChildren().addAll(hBoxBar,statusLabel,pathLabel);

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setStatusText(String string) {
        this.statusText = string;
    }

    public void setPathText(String string) {
        this.pathText = string;
    }

    public void updateGUI() {
        this.statusLabel.setText(this.statusText);
        this.pathLabel.setText(this.pathText);
    }

}
