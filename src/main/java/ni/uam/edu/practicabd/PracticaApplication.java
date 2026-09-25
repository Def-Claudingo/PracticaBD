package ni.uam.edu.practicabd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PracticaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PracticaApplication.class.getResource("practica-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 670, 600);
        stage.setTitle("Práctica BD - Gestión de Productos y Categorías");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
