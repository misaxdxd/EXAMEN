package pe.edu.upeu.syselectro.service;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Equipo;

import java.util.List;

public interface IEquipoService extends ICrudGenericoService<Equipo, Long> {
    List<ComboBoxOption> listarEstados();
}
