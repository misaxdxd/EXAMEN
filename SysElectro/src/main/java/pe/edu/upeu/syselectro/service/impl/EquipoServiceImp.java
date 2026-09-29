package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.enums.EstadoEquipo;
import pe.edu.upeu.syselectro.model.Equipo;
import pe.edu.upeu.syselectro.repository.EquipoRepository;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.service.IEquipoService;

import java.util.ArrayList;
import java.util.List;

public class EquipoServiceImp extends CrudGenericoServiceImp<Equipo, Long>
        implements IEquipoService {

    private final EquipoRepository repositorio;

    public EquipoServiceImp(EquipoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Equipo, Long> getRepo() {
        return repositorio;
    }

    @Override
    public List<ComboBoxOption> listarEstados() {
        List<ComboBoxOption> lista = new ArrayList<>();
        for (EstadoEquipo valor : EstadoEquipo.values()) {
            lista.add(new ComboBoxOption(valor.name(), valor.getDescripcion()));
        }
        return lista;
    }
}
