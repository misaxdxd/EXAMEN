package pe.edu.upeu.syselectro.service.impl;

import pe.edu.upeu.syselectro.model.Proveedor;
import pe.edu.upeu.syselectro.repository.ICrudGenericoRepository;
import pe.edu.upeu.syselectro.repository.ProveedorRepository;
import pe.edu.upeu.syselectro.service.IProveedorService;

public class ProveedorServiceImp extends CrudGenericoServiceImp<Proveedor, Long>
        implements IProveedorService {

    private final ProveedorRepository repositorio;

    public ProveedorServiceImp(ProveedorRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected ICrudGenericoRepository<Proveedor, Long> getRepo() {
        return repositorio;
    }
}
