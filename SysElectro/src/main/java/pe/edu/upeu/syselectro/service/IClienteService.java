package pe.edu.upeu.syselectro.service;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Cliente;

import java.util.List;

public interface IClienteService extends ICrudGenericoService<Cliente, Long> {
    List<ComboBoxOption> listarTiposDocumento();
}
