package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.enums.TipoDocumento;
import pe.edu.upeu.syselectro.model.Cliente;
import pe.edu.upeu.syselectro.repository.ClienteRepository;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.service.IClienteService;

import java.util.ArrayList;
import java.util.List;

public class ClienteServiceImp extends CrudGenericoServiceImp<Cliente, Long>
        implements IClienteService {

    private final ClienteRepository repositorio;

    public ClienteServiceImp(ClienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Cliente, Long> getRepo() {
        return repositorio;
    }

    @Override
    public List<ComboBoxOption> listarTiposDocumento() {
        List<ComboBoxOption> lista = new ArrayList<>();
        for (TipoDocumento valor : TipoDocumento.values()) {
            lista.add(new ComboBoxOption(valor.name(), valor.getDescripcion()));
        }
        return lista;
    }
}
