package Talisca;

import Talisca.model.Assignment;
import Talisca.model.TaliscaEngine;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController extends AbstractController{

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label weekNo;

    @FXML
    private Label weekday;

    @FXML
    private Label date;

    @FXML
    private Label pekTime;

    @FXML
    private Label sydTime;

    @FXML
    private Rectangle weekRect;

    public HomeController(TaliscaEngine engine) {
        super(engine);
    }

    private GridPane asmt2Pane(Assignment assignment) {
        GridPane asmt = new GridPane();
        Label unit = new Label();
        unit.textProperty().bind(assignment.getUnit());
        Label name = new Label();
        name.setMaxSize(135, 20);
        name.textProperty().bind(assignment.getName());
        Label dueIn = new Label();
        dueIn.textProperty().bind(assignment.getDueInProperty());
        Label dueAt = new Label();
        dueAt.textProperty().bind(assignment.getDueDateProperty());

        unit.setFont(Font.font("Helvetica Light", 23));
        unit.setTextAlignment(TextAlignment.LEFT);
        name.setFont(Font.font("Helvetica Light", 14));
        name.setTextAlignment(TextAlignment.LEFT);
        dueIn.setFont(Font.font("Helvetica Light", 23));
        dueIn.setTextAlignment(TextAlignment.LEFT);
        dueAt.setFont(Font.font("Helvetica Light", 14));
        dueAt.setTextAlignment(TextAlignment.LEFT);

        asmt.add(unit, 0, 0);
        asmt.add(name, 0, 1);
        asmt.add(dueIn, 1, 0);
        asmt.add(dueAt, 1, 1);
        asmt.setAlignment(Pos.CENTER_LEFT);
        asmt.setHgap(24);
        asmt.setVgap(7);
        return asmt;
    }

    private Rectangle buildRect4EachAsmt(int layoutY) {
        Rectangle rect = new Rectangle();
        rect.setWidth(380);
        rect.setHeight(100);
        rect.setLayoutX(0);
        rect.setLayoutY(layoutY);
        rect.setArcHeight(35);
        rect.setArcWidth(35);
        rect.setFill(Paint.valueOf("TRANSPARENT"));
        rect.setStroke(Paint.valueOf("WHITE"));
        rect.setStrokeWidth(3);
        rect.setStrokeType(StrokeType.INSIDE);
        return rect;
    }

    private AnchorPane assembleAsmts(List<GridPane> asmts) {
        AnchorPane assembled = new AnchorPane();
        for (int asmtCount = 0; asmtCount < asmts.size(); asmtCount++) {
            GridPane asmt = asmts.get(asmtCount);
            asmt.setLayoutX(20);
            asmt.setLayoutY(60 + asmtCount * 140);
            assembled.getChildren().addAll(asmt, buildRect4EachAsmt(40 + asmtCount * 140));
        }
        return assembled;
    }

    @FXML
    private void setSemInitDay() throws IOException, InterruptedException {
        TaliscaEngine.retrieveAsm();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        date.textProperty().bind(taliscaEngine.getDate());
        pekTime.textProperty().bind(taliscaEngine.getPekTime());
        sydTime.textProperty().bind(taliscaEngine.getSydTime());
        weekday.textProperty().bind(taliscaEngine.getWeekday());

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);

        scrollPane.setOnScroll(event -> {
            scrollPane.setVvalue(scrollPane.getVvalue() + event.getDeltaY());
        });

        scrollPane.setOnSwipeDown(event -> {
            scrollPane.setVvalue(scrollPane.getVvalue() + 100);
        });

        List<GridPane> asmts = new ArrayList<>();
        for (Assignment assignment : taliscaEngine.getAssignments()) {
            asmts.add(asmt2Pane(assignment));
        }

        scrollPane.setContent(assembleAsmts(asmts));
    }
}
