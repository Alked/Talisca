package Talisca;

import Talisca.model.TaliscaEngine;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.TouchEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class TaliscaInit {

    private final TaliscaEngine taliscaEngine;

    public TaliscaInit(TaliscaEngine engine, Stage stage) throws IOException {
        taliscaEngine = engine;
        System.setProperty("prism.lcdtext", "false");
        Font.loadFont(getClass().getResourceAsStream("/fonts/HelveticaNeue Medium.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/HelveticaNeue Thin.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/HelveticaNeue Light.ttf"), 14);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getClassLoader().getResource("home.fxml")).openConnection().getURL());
        AbstractController controller = new HomeController(engine);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root, 960, 580);
        stage.setTitle("Talisca");
        stage.setScene(scene);
        stage.setResizable(false);
	stage.setFullScreen(true);
        stage.show();
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> {
                    try {
                        update();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void update() throws IOException {
        taliscaEngine.update();
    }
}
