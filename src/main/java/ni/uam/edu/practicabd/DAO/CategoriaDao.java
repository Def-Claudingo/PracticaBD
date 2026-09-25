package ni.uam.edu.practicabd.DAO;

import ni.uam.edu.practicabd.Interfaces.CRUD;
import ni.uam.edu.practicabd.Modelos.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDao implements CRUD<Categoria> {
    List<Categoria> listaCategorias;
    public CategoriaDao() {listaCategorias = new ArrayList<>();}
    @Override
    public void guardar(Categoria entidad) {
        listaCategorias.add(entidad);




    }

    @Override
    public List<Categoria> listar() {
        return listaCategorias;
    }

    @Override
    public void eliminar(Categoria entidad) {
        listaCategorias.remove(entidad);
    }

    @Override
    public Categoria buscar(Categoria entidad) {
        return listaCategorias.stream().filter(
                categorias -> categorias.getId().
                        equals(entidad.getId())).findFirst().orElse(null);
    }

    @Override
    public void actualizar(Categoria entidad) {
        Categoria existente = buscar(entidad);
        if (existente != null) {
            int indice = listaCategorias.indexOf(existente);
            listaCategorias.set(indice, entidad);
        }
    }
}
