package com.group9.util;

import javafx.geometry.NodeOrientation;
import javafx.scene.layout.AnchorPane;

public class LayoutOrienter {
    public void orientLayout(AnchorPane pane) {
        if (SessionManager.getLanguage().equals("Arabic")) {
            pane.nodeOrientationProperty().set(NodeOrientation.RIGHT_TO_LEFT);
        }
        else {
            pane.nodeOrientationProperty().set(NodeOrientation.LEFT_TO_RIGHT);
    }
}}

