package ni.uam.edu.practicabd.DAO;

import ni.uam.edu.practicabd.Interfaces.CRUD;
import ni.uam.edu.practicabd.Modelos.Categoria;
import ni.uam.edu.practicabd.Util.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao implements CRUD<Categoria> {

    @Override
    public void guardar(Categoria entidad) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, ?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setBoolean(2, entidad.isActiva());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar categoria: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria ORDER BY nombre";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBoolean("activa")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorias: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void eliminar(Categoria entidad) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entidad.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoria: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actualizar(Categoria entidad) {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setBoolean(2, entidad.isActiva());
            ps.setInt(3, entidad.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoria: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean existeNombre(String nombre, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean tieneProductos(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE categoria_id = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Categoria> buscarPorNombre(String texto) {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria WHERE nombre ILIKE ? ORDER BY nombre";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria c = new Categoria(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getBoolean("activa")
                    );
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categorias: " + e.getMessage());
        }
        return lista;
    }
}
