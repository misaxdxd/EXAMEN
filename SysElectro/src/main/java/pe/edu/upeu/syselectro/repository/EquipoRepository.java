package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.enums.EstadoEquipo;
import pe.edu.upeu.syselectro.model.Categoria;
import pe.edu.upeu.syselectro.model.Equipo;
import pe.edu.upeu.syselectro.model.Marca;
import java.util.List;

public class EquipoRepository extends AbstractJpaRepository<Equipo, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Equipo entity) {
        return entity.getIdEquipo();
    }

    @Override
    protected void setId(Equipo entity, Long id) {
        entity.setIdEquipo(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /**
     * Carga equipos de ejemplo. Recibe las listas de categorías y marcas
     * ya sembradas para poder referenciarlas (relación entre objetos).
     */
    public void seedData(List<Categoria> categorias, List<Marca> marcas) {
        if (sembrado) return;
        sembrado = true;
        save(Equipo.builder().nombre("Laptop IdeaPad 3").modelo("15ITL6").estado(EstadoEquipo.NUEVO)
                .precio(2299.90).stock(12).garantiaMeses(12)
                .categoria(categorias.get(0)).marca(marcas.get(4)).build());
        save(Equipo.builder().nombre("Smartphone Galaxy A55").modelo("SM-A556").estado(EstadoEquipo.NUEVO)
                .precio(1699.00).stock(25).garantiaMeses(12)
                .categoria(categorias.get(1)).marca(marcas.get(0)).build());
        save(Equipo.builder().nombre("Smart TV 55 pulgadas").modelo("UQ8050").estado(EstadoEquipo.REACONDICIONADO)
                .precio(1890.00).stock(4).garantiaMeses(6)
                .categoria(categorias.get(2)).marca(marcas.get(1)).build());
    }
}
