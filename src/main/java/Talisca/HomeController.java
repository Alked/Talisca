package Talisca;

import Talisca.model.Assignment;
import Talisca.model.TaliscaEngine;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
    private Rectangle weekRect, shadeRectG, shadeRectR;


    public HomeController(TaliscaEngine engine) {
        super(engine);
    }

    private GridPane asmt2Pane(Assignment assignment) {
        GridPane asmt = new GridPane();
        Label unit = new Label();
        unit.textProperty().bind(assignment.getUnit());
        unit.setMaxSize(135, 20);
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

        Label endOfList = new Label("No more published assignments.");
        endOfList.setAlignment(Pos.CENTER);
        endOfList.setFont(Font.font("Helvetica Neue Thin"));
        endOfList.setTextFill(Paint.valueOf("WHITE"));
        endOfList.setLayoutX(0);
        endOfList.setPrefSize(380, 100);

        if (assembled.getChildren().size() > 0) {
            Node lastAssignment = assembled.getChildren().get(assembled.getChildren().size() - 1);
            endOfList.setLayoutY(lastAssignment.getLayoutY() + 80);
        } else {
            endOfList.setLayoutY(80);
        }

        endOfList.setOnMouseClicked(event -> {
            Timeline timeline = new Timeline();
            KeyValue asmGoUp = new KeyValue(scrollPane.vvalueProperty(), 0);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(800), asmGoUp));
            timeline.play();
        });
        assembled.getChildren().add(endOfList);
        return assembled;
    }

    @FXML
    private void setSemInitDay() throws IOException, InterruptedException {
        weekRectLoadAnimate();
        taliscaEngine.updateWeekNumber();
    }

    private void weekRectLoadAnimate() {
        var tlDG = new TranslateTransition(Duration.millis(60), shadeRectG);
        tlDG.setByY(4);
        tlDG.setCycleCount(1);
        tlDG.setAutoReverse(false);

        var tlLG = new TranslateTransition(Duration.millis(60), shadeRectG);
        tlLG.setByX(-4);
        tlLG.setCycleCount(1);
        tlLG.setAutoReverse(false);

        var tlUG = new TranslateTransition(Duration.millis(60), shadeRectG);
        tlUG.setByY(-4);
        tlUG.setCycleCount(1);
        tlUG.setAutoReverse(false);

        var tlRG = new TranslateTransition(Duration.millis(60), shadeRectG);
        tlRG.setByX(4);
        tlRG.setCycleCount(1);
        tlRG.setAutoReverse(false);

        var tlUR = new TranslateTransition(Duration.millis(60), shadeRectR);
        tlUR.setByY(-4);
        tlUR.setCycleCount(1);
        tlUR.setAutoReverse(false);

        var tlRR = new TranslateTransition(Duration.millis(60), shadeRectR);
        tlRR.setByX(4);
        tlRR.setCycleCount(1);
        tlRR.setAutoReverse(false);

        var tlDR = new TranslateTransition(Duration.millis(60), shadeRectR);
        tlDR.setByY(4);
        tlDR.setCycleCount(1);
        tlDR.setAutoReverse(false);

        var tlLR = new TranslateTransition(Duration.millis(60), shadeRectR);
        tlLR.setByX(-4);
        tlLR.setCycleCount(1);
        tlLR.setAutoReverse(false);

        var plDGUR = new ParallelTransition();
        plDGUR.getChildren().addAll(tlDG, tlUR);
        plDGUR.setCycleCount(1);

        var plLGRR = new ParallelTransition();
        plLGRR.getChildren().addAll(tlLG, tlRR);
        plLGRR.setCycleCount(1);

        var plUGDR = new ParallelTransition();
        plUGDR.getChildren().addAll(tlUG, tlDR);
        plUGDR.setCycleCount(1);

        var plRGLR = new ParallelTransition();
        plRGLR.getChildren().addAll(tlRG, tlLR);
        plRGLR.setCycleCount(1);

        var seqt = new SequentialTransition();
        seqt.getChildren().addAll(plDGUR, plLGRR, plUGDR, plRGLR);
        seqt.setCycleCount(4);
        seqt.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        date.textProperty().bind(taliscaEngine.getDate());
        pekTime.textProperty().bind(taliscaEngine.getPekTime());
        sydTime.textProperty().bind(taliscaEngine.getSydTime());
        weekday.textProperty().bind(taliscaEngine.getWeekday());
        weekNo.textProperty().bind(taliscaEngine.getWeekNo());

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);

        List<GridPane> asmts = new ArrayList<>();
        for (Assignment assignment : taliscaEngine.getAssignments()) {
            if (assignment.isAvailable() && !assignment.isOverDue()) {
                asmts.add(asmt2Pane(assignment));
            }
        }

        scrollPane.setContent(assembleAsmts(asmts));
    }
}
