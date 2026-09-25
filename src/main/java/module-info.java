module ni.uam.edu.practicabd {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.uam.edu.practicabd to javafx.fxml;
    exports ni.uam.edu.practicabd;
}