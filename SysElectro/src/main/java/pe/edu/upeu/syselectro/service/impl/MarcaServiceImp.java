package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Marca;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.repository.MarcaRepository;
import pe.edu.upeu.syselectro.service.IMarcaService;

import java.util.ArrayList;
import java.util.List;

public class MarcaServiceImp extends CrudGenericoServiceImp<Marca, Long>
        implements IMarcaService {

    private final MarcaRepository repositorio;

    public MarcaServiceImp(MarcaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Marca, Long> getRepo() {
        return repositorio;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> lista = new ArrayList<>();
        for (Marca item : repositorio.findAll()) {
            lista.add(new ComboBoxOption(String.valueOf(item.getIdMarca()), item.getNombre()));
        }
        return lista;
    }
}
