package ni.uam.edu.practicabd.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.uam.edu.practicabd.DAO.CategoriaDao;
import ni.uam.edu.practicabd.Modelos.Categoria;

import java.sql.SQLException;
import java.util.Optional;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDatoFiltrar;
    @FXML private CheckBox chkActiva;
    @FXML private Button btnGuardar;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private CategoriaDao categoriaDao = new CategoriaDao();
    private Categoria categoriaSeleccionada = null;

    @FXML
    public void initialize() {
        if (chkActiva != null) {
            chkActiva.setSelected(true);
        }
        if (tblCategorias != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
            configurarContextMenu();
            cargarCategorias();
        }
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemActualizar = new MenuItem("Actualizar");
        MenuItem itemEliminar = new MenuItem("Eliminar");

        itemActualizar.setOnAction(e -> prepararActualizar());
        itemEliminar.setOnAction(e -> eliminarCategoria());

        contextMenu.getItems().addAll(itemActualizar, itemEliminar);
        tblCategorias.setContextMenu(contextMenu);
    }

    private void prepararActualizar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar la categoría que desea actualizar.");
            return;
        }
        categoriaSeleccionada = seleccionada;
        txtNombre.setText(seleccionada.getNombre());
        chkActiva.setSelected(seleccionada.isActiva());
    }

    private void eliminarCategoria() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar la categoría que desea eliminar.");
            return;
        }

        try {
            if (categoriaDao.tieneProductos(seleccionada.getId())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Integridad referencial", "No puede eliminar la categoría porque tiene productos asociados.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar la categoría \"" + seleccionada.getNombre() + "\"?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> respuesta = confirm.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
                categoriaDao.eliminar(seleccionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Categoría eliminada", "La categoría fue eliminada correctamente.");
                limpiar();
                cargarCategorias();
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", "No fue posible eliminar la categoría.");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error al intentar eliminar la categoría.");
            System.err.println(e.getMessage());
        }
    }

    private void cargarCategorias() {
        if (tblCategorias != null) {
            tblCategorias.setItems(FXCollections.observableArrayList(categoriaDao.listar()));
        }
    }

    private Categoria obtenerCategoriaFormulario() throws IllegalArgumentException {
        if (txtNombre == null || txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            if (txtNombre != null) {
                txtNombre.requestFocus();
            }
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }

        String nombre = txtNombre.getText().trim();
        boolean activa = chkActiva != null && chkActiva.isSelected();

        return new Categoria(null, nombre, activa);
    }

    @FXML
    public void guardar(ActionEvent event) {
        try {
            Categoria c = obtenerCategoriaFormulario();

            if (categoriaSeleccionada == null) {
                if (categoriaDao.existeNombre(c.getNombre())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Categoría duplicada", "Ya existe una categoría con ese nombre.");
                    txtNombre.requestFocus();
                    return;
                }
                categoriaDao.guardar(c);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Categoría registrada", "La información fue almacenada correctamente.");
            } else {
                if (categoriaDao.existeNombre(c.getNombre(), categoriaSeleccionada.getId())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Categoría duplicada", "Ya existe otra categoría con ese nombre.");
                    txtNombre.requestFocus();
                    return;
                }
                categoriaSeleccionada.setNombre(c.getNombre());
                categoriaSeleccionada.setActiva(c.isActiva());
                categoriaDao.actualizar(categoriaSeleccionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Categoría actualizada", "La información fue actualizada correctamente.");
            }

            limpiar();
            cargarCategorias();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", "No fue posible registrar o actualizar la categoría.");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error al procesar la categoría.");
            System.err.println(e.getMessage());
        }
    }

    private void limpiar() {
        txtNombre.clear();
        if (chkActiva != null) {
            chkActiva.setSelected(true);
        }
        categoriaSeleccionada = null;
    }

    @FXML
    public void filtarDatosCategoria() {
        if (txtDatoFiltrar == null) return;
        String filtrar = txtDatoFiltrar.getText() != null ? txtDatoFiltrar.getText().trim() : "";
        if (filtrar.isEmpty()) {
            cargarCategorias();
        } else {
            tblCategorias.setItems(FXCollections.observableArrayList(categoriaDao.buscarPorNombre(filtrar)));
        }
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
