package ni.uam.edu.practicabd.Controllers;

import javafx.collections.transformation.FilteredList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.uam.edu.practicabd.DAO.CategoriaDao;
import ni.uam.edu.practicabd.DAO.ProductoDao;
import ni.uam.edu.practicabd.Modelos.Categoria;
import ni.uam.edu.practicabd.Modelos.Producto;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class ProductoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtRuta;
    @FXML private TextField txtExistencia;
    @FXML private TextField txtDatoFiltrar;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<String> cmbCriterios;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;
    @FXML private TableColumn<Producto, String> colRuta;

    private ProductoDao productosDao = new ProductoDao();
    private CategoriaDao categoriaDao = new CategoriaDao();
    private Producto productoSeleccionado = null;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private FilteredList<Producto> listaFiltrada;

    @FXML
    public void initialize() {
        cargarCategorias();
        cargarCriterios();
        configurarTabla();
        configurarContextMenu();
        configurarSeleccionTabla();
        cargarProductos();
    }

    public void cargarCategorias() {
        if (cmbCategoria != null) {
            cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDao.listar()));
        }
    }

    public void cargarCriterios() {
        if (cmbCriterios != null) {
            cmbCriterios.setItems(FXCollections.observableArrayList("Todos", "Código", "Nombre", "Categoría",
                    "Precio", "Existencia"));
            cmbCriterios.setValue("Todos");
        }
        if (txtDatoFiltrar != null) {
            txtDatoFiltrar.setDisable(false);
        }

        if (cmbCriterios != null) {
            cmbCriterios.setOnAction(event -> filtrarPorCriterio());
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
            colRuta.setCellValueFactory(new PropertyValueFactory<>("rutaImagen"));

            listaFiltrada = new FilteredList<>(listaProductos, p -> true);
            tblProductos.setItems(listaFiltrada);
        }
    }

    private void configurarSeleccionTabla() {
        if (tblProductos != null) {
            tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    cargarProductoEnFormulario(newVal);
                }
            });
        }
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemActualizar = new MenuItem("Actualizar");
        MenuItem itemEliminar = new MenuItem("Eliminar");

        itemActualizar.setOnAction(e -> prepararActualizar());
        itemEliminar.setOnAction(e -> eliminarProducto());

        contextMenu.getItems().addAll(itemActualizar, itemEliminar);
        tblProductos.setContextMenu(contextMenu);
    }

    private void cargarProductoEnFormulario(Producto seleccionado) {
        if (seleccionado == null) return;
        productoSeleccionado = seleccionado;
        txtCodigo.setText(seleccionado.getCodigo());
        txtCodigo.setDisable(true);
        txtNombre.setText(seleccionado.getNombre());
        txtPrecio.setText(seleccionado.getPrecioVenta() != null ? seleccionado.getPrecioVenta().toString() : "");
        txtExistencia.setText(String.valueOf(seleccionado.getExistencia()));
        txtRuta.setText(seleccionado.getRutaImagen() != null ? seleccionado.getRutaImagen() : "");
        if (chkActivo != null) {
            chkActivo.setSelected(seleccionado.isActivo());
        }

        if (cmbCategoria != null && seleccionado.getCategoria() != null) {
            for (Categoria cat : cmbCategoria.getItems()) {
                if (cat.getId() != null && cat.getId().equals(seleccionado.getCategoria().getId())) {
                    cmbCategoria.setValue(cat);
                    break;
                }
            }
        }
    }

    private void prepararActualizar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar el producto que desea actualizar.");
            return;
        }
        cargarProductoEnFormulario(seleccionado);
    }

    private void eliminarProducto() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección requerida", "Debe seleccionar el producto que desea eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar el producto \"" + seleccionado.getNombre() + "\"?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> respuesta = confirm.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            try {
                productosDao.eliminar(seleccionado);
                mostrarExito("Producto eliminado", "El producto fue eliminado correctamente.");
                limpiarFormulario();
                cargarProductos();
            } catch (Exception e) {
                mostrarError("Error de base de datos", "No fue posible eliminar el producto.");
                System.err.println(e.getMessage());
            }
        }
    }

    public void cargarProductos() {
        listaProductos.setAll(productosDao.listar());
    }

    @FXML
    public void seleccionarRuta(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.webp", "*.gif")
        );
        Stage stage = null;
        if (txtRuta != null && txtRuta.getScene() != null) {
            stage = (Stage) txtRuta.getScene().getWindow();
        }
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            txtRuta.setText(archivo.getAbsolutePath());
        }
    }

    private Producto obtenerProductoFormulario() throws IllegalArgumentException {
        if (txtCodigo == null || txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
            if (txtCodigo != null) txtCodigo.requestFocus();
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }

        if (txtNombre == null || txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            if (txtNombre != null) txtNombre.requestFocus();
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (cmbCategoria == null || cmbCategoria.getValue() == null) {
            if (cmbCategoria != null) cmbCategoria.requestFocus();
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }

        if (txtPrecio == null || txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) {
            if (txtPrecio != null) txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio del producto es obligatorio.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            if (txtPrecio != null) txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio debe ser un valor numérico.");
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            if (txtPrecio != null) txtPrecio.requestFocus();
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }

        if (txtExistencia == null || txtExistencia.getText() == null || txtExistencia.getText().trim().isEmpty()) {
            if (txtExistencia != null) txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia del producto es obligatoria.");
        }

        int existencia;
        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            if (txtExistencia != null) txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia debe ser un número entero.");
        }

        if (existencia < 0) {
            if (txtExistencia != null) txtExistencia.requestFocus();
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        Categoria categoria = cmbCategoria.getValue();
        String ruta = (txtRuta != null && txtRuta.getText() != null) ? txtRuta.getText().trim() : "";
        boolean activo = chkActivo != null && chkActivo.isSelected();

        return new Producto(null, nombre, codigo, categoria, precio, existencia, ruta, activo);
    }

    @FXML
    public void btnGuardar() {
        try {
            Producto producto = obtenerProductoFormulario();

            if (productoSeleccionado == null) {
                if (productosDao.existeCodigo(producto.getCodigo())) {
                    mostrarAdvertencia("Código duplicado", "Ya existe un producto con ese código.");
                    txtCodigo.requestFocus();
                    return;
                }
                productosDao.guardar(producto);
                mostrarExito("Producto registrado", "La información fue almacenada correctamente.");
            } else {
                if (productosDao.existeCodigo(producto.getCodigo(), productoSeleccionado.getCodigo())) {
                    mostrarAdvertencia("Código duplicado", "Ya existe otro producto con ese código.");
                    txtCodigo.requestFocus();
                    return;
                }
                productosDao.actualizar(producto);
                mostrarExito("Producto actualizado", "La información fue actualizada correctamente.");
            }

            limpiarFormulario();
            cargarProductos();
        } catch (IllegalArgumentException e) {
            mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarError("Error de base de datos", "No fue posible registrar o actualizar el producto.");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado", "Ocurrió un error al procesar el producto.");
            System.err.println(e.getMessage());
        }
    }

    @FXML
    public void filtrarPorCriterio() {
        if (listaFiltrada == null) return;

        String txtFiltrar = (txtDatoFiltrar != null && txtDatoFiltrar.getText() != null)
                ? txtDatoFiltrar.getText().trim().toLowerCase()
                : "";
        String criterio = (cmbCriterios != null && cmbCriterios.getValue() != null)
                ? cmbCriterios.getValue().trim()
                : "Todos";

        listaFiltrada.setPredicate(producto -> {
            if (txtFiltrar.isEmpty()) {
                return true;
            }

            switch (criterio) {
                case "Código":
                    return producto.getCodigo() != null && producto.getCodigo().toLowerCase().contains(txtFiltrar);
                case "Nombre":
                    return producto.getNombre() != null && producto.getNombre().toLowerCase().contains(txtFiltrar);
                case "Categoría":
                    return producto.getCategoria() != null && producto.getCategoria().getNombre() != null
                            && producto.getCategoria().getNombre().toLowerCase().contains(txtFiltrar);
                case "Precio":
                    return producto.getPrecioVenta() != null && producto.getPrecioVenta().toString().contains(txtFiltrar);
                case "Existencia":
                    return String.valueOf(producto.getExistencia()).contains(txtFiltrar);
                default:
                    boolean coincideCodigo = producto.getCodigo() != null && producto.getCodigo().toLowerCase().contains(txtFiltrar);
                    boolean coincideNombre = producto.getNombre() != null && producto.getNombre().toLowerCase().contains(txtFiltrar);
                    boolean coincideCat = producto.getCategoria() != null && producto.getCategoria().getNombre() != null
                            && producto.getCategoria().getNombre().toLowerCase().contains(txtFiltrar);
                    return coincideCodigo || coincideNombre || coincideCat;
            }
        });
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
            mostrarError("Error al abrir ventana", "No se pudo abrir la vista de categoría: " + e.getMessage());
        }
    }

    @FXML
    public void limpiarFormulario() {
        productoSeleccionado = null;
        if (txtCodigo != null) {
            txtCodigo.clear();
            txtCodigo.setDisable(false);
        }
        if (txtNombre != null) txtNombre.clear();
        if (txtPrecio != null) txtPrecio.clear();
        if (cmbCategoria != null) cmbCategoria.getSelectionModel().clearSelection();
        if (txtRuta != null) txtRuta.clear();
        if (txtExistencia != null) txtExistencia.clear();
        if (chkActivo != null) chkActivo.setSelected(true);
        if (txtDatoFiltrar != null) txtDatoFiltrar.clear();
        if (listaFiltrada != null) listaFiltrada.setPredicate(p -> true);
        if (tblProductos != null) tblProductos.getSelectionModel().clearSelection();
    }

    public void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrarExito(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
