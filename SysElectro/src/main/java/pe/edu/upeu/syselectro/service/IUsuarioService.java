package pe.edu.upeu.syselectro.service;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Usuario;

import java.util.List;

public interface IUsuarioService extends ICrudGenericoService<Usuario, Long> {
    List<ComboBoxOption> listarRoles();
}
