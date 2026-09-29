package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.enums.RolUsuario;
import pe.edu.upeu.syselectro.model.Usuario;

public class UsuarioRepository extends AbstractJpaRepository<Usuario, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Usuario entity) {
        return entity.getIdUsuario();
    }

    @Override
    protected void setId(Usuario entity, Long id) {
        entity.setIdUsuario(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Carga datos de ejemplo una sola vez (no se vuelve a sembrar aunque se borre todo). */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Usuario.builder()
                .usuario("admin")
                .clave("admin123")
                .nombres("Administrador del sistema")
                .rol(RolUsuario.ADMINISTRADOR)
                .build());
        save(Usuario.builder()
                .usuario("vendedor1")
                .clave("ventas123")
                .nombres("Carlos Ramos Huamán")
                .rol(RolUsuario.VENDEDOR)
                .build());
    }
}
