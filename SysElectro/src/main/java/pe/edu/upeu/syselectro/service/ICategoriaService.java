package pe.edu.upeu.syselectro.service;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Categoria;

import java.util.List;

public interface ICategoriaService extends ICrudGenericoService<Categoria, Long> {
    List<ComboBoxOption> listarCombobox();
}
