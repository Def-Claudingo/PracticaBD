package ni.uam.edu.practicabd.DAO;

import ni.uam.edu.practicabd.Interfaces.CRUD;
import ni.uam.edu.practicabd.Modelos.Producto;
import ni.uam.edu.practicabd.Util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao implements CRUD<Producto> {
    List<Producto> listaProductos;
    public ProductoDao() {listaProductos=new ArrayList<>();}
    @Override
    public void guardar(Producto entidad) {
        listaProductos.add(entidad);
        String sql = "INSERT INTO producto (codigo, nombre, categoria_id, precio_venta, existencia, ruta_imagen, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entidad.getCodigo());
            ps.setString(2, entidad.getNombre());
            ps.setInt(3, entidad.getCategoria().getId());
            ps.setBigDecimal(4, entidad.getPrecioVenta());
            ps.setInt(5, entidad.getExistencia());
            ps.setString(6, entidad.getRutaImagen());
            ps.setBoolean(7, entidad.isActivo());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }

    @Override
    public List<Producto> listar() {
        return listaProductos;
    }

    @Override
    public void eliminar(Producto entidad) {
        listaProductos.remove(entidad);
    }

    @Override
    public Producto buscar(Producto entidad) {
        return listaProductos.stream().filter(producto -> producto.getId()
                .equals(entidad.getId())).findFirst().orElse(null);
    }

    @Override
    public void actualizar(Producto entidad) {
        Producto existe = buscar(entidad);
        if(existe!=null){
            int indice = listaProductos.indexOf(existe);
            listaProductos.set(indice, entidad);
        }
    }
}
