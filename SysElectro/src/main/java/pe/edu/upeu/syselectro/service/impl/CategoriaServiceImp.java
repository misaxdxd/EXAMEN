package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Categoria;
import pe.edu.upeu.syselectro.repository.CategoriaRepository;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.service.ICategoriaService;

import java.util.ArrayList;
import java.util.List;

public class CategoriaServiceImp extends CrudGenericoServiceImp<Categoria, Long>
        implements ICategoriaService {

    private final CategoriaRepository repositorio;

    public CategoriaServiceImp(CategoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Categoria, Long> getRepo() {
        return repositorio;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> lista = new ArrayList<>();
        for (Categoria item : repositorio.findAll()) {
            lista.add(new ComboBoxOption(String.valueOf(item.getIdCategoria()), item.getNombre()));
        }
        return lista;
    }
}
