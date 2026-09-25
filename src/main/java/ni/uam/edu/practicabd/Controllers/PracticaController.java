package ni.uam.edu.practicabd.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.uam.edu.practicabd.DAO.ProductoDao;
import ni.uam.edu.practicabd.Modelos.Categoria;
import ni.uam.edu.practicabd.Modelos.Producto;
import org.w3c.dom.Text;

import java.awt.*;
import java.awt.Button;
import java.awt.TextField;
import java.math.BigDecimal;

public class PracticaController {
    int contador_id = 1;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtRuta;
    @FXML private TextField txtExistencia;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private RadioButton rbSi;
    @FXML private RadioButton rbNo;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;

    ProductoDao productosDao = new ProductoDao();
    @FXML
    public void initialize() {
        cmbCategoria.getItems().addAll("Bebidas", "Alimentos", "Limpieza");
    }

    @FXML protected void btnGuardar(){
        guardarDatos();
    }

    private void guardarDatos(){
        if (!validaciones()) {
            return;
        }
        String nombre = txtNombre.getText();
        String codigo = txtCodigo.getText();
        BigDecimal precio =  new BigDecimal(txtPrecio.getText());
        String ruta = txtRuta.getText();
        Categoria categoria = new Categoria();
        int existencia = Integer.parseInt(txtExistencia.getText());
        boolean activo =  chkActivo.isSelected();

        productosDao.guardar(new Producto(contador_id, nombre,codigo,categoria,precio, existencia,ruta,activo ));

    }

    private boolean validaciones(){

        if (txtCodigo == null || txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el codigo del producto.");
            return false;
        }

        if (txtNombre == null || txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el nombre del producto.");
            return false;
        }

        if (cmbCategoria == null || cmbCategoria.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo Vacío", "Debe seleccionar un tipo de categoria");
            return false;
        }

        if (txtPrecio == null || txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar el precio del producto.");
            return false;
        }

        if (txtExistencia == null || txtExistencia.getText() == null || txtExistencia.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar un valor en existencia.");
            return false;
        }

        if (txtRuta == null || txtRuta.getText() == null || txtRuta.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campo vacío", "Debe ingresar la ciudad del cliente.");
            return false;
        }

        return true;
    }

    @FXML
    public void limpiarFormulario() {
        if (txtCodigo != null) txtCodigo.setText("");
        if (txtNombre != null) txtNombre.setText("");
        if (txtPrecio != null) txtPrecio.setText("");
        if (cmbCategoria != null) cmbCategoria.getSelectionModel().clearSelection();
        if (txtRuta != null) txtRuta.setText("");
        if (txtExistencia != null) txtExistencia.setText("");
        if (chkActivo != null){ chkActivo.setSelected(false);}

    }
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
