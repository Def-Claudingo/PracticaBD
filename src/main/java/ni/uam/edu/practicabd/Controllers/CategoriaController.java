package ni.uam.edu.practicabd.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.uam.edu.practicabd.DAO.CategoriaDao;
import ni.uam.edu.practicabd.Modelos.Categoria;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private CategoriaDao categoriaDao = new CategoriaDao();

    @FXML
    public void initialize() {
        if (chkActiva != null) {
            chkActiva.setSelected(true);
        }
        if (tblCategorias != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
            cargarCategorias();
        }
    }

    private void cargarCategorias() {
        if (tblCategorias != null) {
            tblCategorias.setItems(FXCollections.observableArrayList(categoriaDao.listar()));
        }
    }

    @FXML
    public void guardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Debe ingresar el nombre de la categoría.");
            return;
        }

        Categoria c = new Categoria();
        c.setNombre(nombre.trim());
        c.setActiva(chkActiva != null && chkActiva.isSelected());

        categoriaDao.guardar(c);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría guardada correctamente en la base de datos.");
        txtNombre.clear();
        if (chkActiva != null) {
            chkActiva.setSelected(true);
        }
        cargarCategorias();
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
