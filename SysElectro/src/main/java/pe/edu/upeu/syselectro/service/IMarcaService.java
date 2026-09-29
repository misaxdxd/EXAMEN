package pe.edu.upeu.syselectro.service;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Marca;

import java.util.List;

public interface IMarcaService extends ICrudGenericoService<Marca, Long> {
    List<ComboBoxOption> listarCombobox();
}
