package Talisca;

import Talisca.model.TaliscaEngine;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractController implements Initializable {

    protected TaliscaEngine taliscaEngine;

    public AbstractController(TaliscaEngine engine) {
        taliscaEngine = engine;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
