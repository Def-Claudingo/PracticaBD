package ni.uam.edu.practicabd.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.uam.edu.practicabd.DAO.CategoriaDao;
import ni.uam.edu.practicabd.DAO.ProductoDao;
import ni.uam.edu.practicabd.Modelos.Categoria;
import ni.uam.edu.practicabd.Modelos.Producto;

import java.io.IOException;
import java.math.BigDecimal;

public class PracticaController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtRuta;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private ProductoDao productosDao = new ProductoDao();
    private CategoriaDao categoriaDao = new CategoriaDao();

    @FXML
    public void initialize() {
        cargarCategorias();
        configurarTabla();
        cargarProductos();
    }

    public void cargarCategorias() {
        if (cmbCategoria != null) {
            cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDao.listar()));
        }
    }

    private void configurarTabla() {
        if (tblProductos != null) {
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCategoria.setCellValueFactory(cell -> {
                Categoria cat = cell.getValue().getCategoria();
                String nombreCat = (cat != null && cat.getNombre() != null) ? cat.getNombre() : "";
                return new SimpleStringProperty(nombreCat);
            });
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
            colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
            colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        }
    }

    public void cargarProductos() {
        if (tblProductos != null) {
            tblProductos.setItems(FXCollections.observableArrayList(productosDao.listar()));
        }
    }

    @FXML
    public void btnGuardar() {
        if (!validaciones()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        Categoria categoria = cmbCategoria.getValue();
        BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
        int existencia = Integer.parseInt(txtExistencia.getText().trim());
        String ruta = (txtRuta != null && txtRuta.getText() != null) ? txtRuta.getText().trim() : "";
        boolean activo = chkActivo != null && chkActivo.isSelected();

        Producto producto = new Producto(null, nombre, codigo, categoria, precio, existencia, ruta, activo);
        productosDao.guardar(producto);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto guardado correctamente en la base de datos.");
        limpiarFormulario();
        cargarProductos();
    }

    @FXML
    public void abrirCategorias(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ni/uam/edu/practicabd/categoria-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Formulario Categoría");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            cargarCategorias();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de categoría: " + e.getMessage());
        }
    }

    private boolean validaciones() {
        if (txtCodigo == null || txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el código del producto.");
            return false;
        }

        if (txtNombre == null || txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el nombre del producto.");
            return false;
        }

        if (cmbCategoria == null || cmbCategoria.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe seleccionar una categoría.");
            return false;
        }

        if (txtPrecio == null || txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el precio del producto.");
            return false;
        }

        try {
            new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Dato inválido", "El precio debe ser un número válido.");
            return false;
        }

        if (txtExistencia == null || txtExistencia.getText() == null || txtExistencia.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar un valor en existencia.");
            return false;
        }

        try {
            Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Dato inválido", "La existencia debe ser un número entero.");
            return false;
        }

        return true;
    }

    @FXML
    public void limpiarFormulario() {
        if (txtCodigo != null) txtCodigo.clear();
        if (txtNombre != null) txtNombre.clear();
        if (txtPrecio != null) txtPrecio.clear();
        if (cmbCategoria != null) cmbCategoria.getSelectionModel().clearSelection();
        if (txtRuta != null) txtRuta.clear();
        if (txtExistencia != null) txtExistencia.clear();
        if (chkActivo != null) chkActivo.setSelected(true);
    }

    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
