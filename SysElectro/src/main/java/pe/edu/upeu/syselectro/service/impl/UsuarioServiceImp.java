package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.enums.RolUsuario;
import pe.edu.upeu.syselectro.model.Usuario;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.repository.UsuarioRepository;
import pe.edu.upeu.syselectro.service.IUsuarioService;

import java.util.ArrayList;
import java.util.List;

public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, Long>
        implements IUsuarioService {

    private final UsuarioRepository repositorio;

    public UsuarioServiceImp(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return repositorio;
    }

    @Override
    public List<ComboBoxOption> listarRoles() {
        List<ComboBoxOption> lista = new ArrayList<>();
        for (RolUsuario valor : RolUsuario.values()) {
            lista.add(new ComboBoxOption(valor.name(), valor.getDescripcion()));
        }
        return lista;
    }
}
