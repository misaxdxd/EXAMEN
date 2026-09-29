package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.model.Categoria;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Categoria entity) {
        return entity.getIdCategoria();
    }

    @Override
    protected void setId(Categoria entity, Long id) {
        entity.setIdCategoria(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Carga datos de ejemplo una sola vez (no se vuelve a sembrar aunque se borre todo). */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Categoria.builder()
                .nombre("Laptops")
                .descripcion("Computadoras portátiles")
                .build());
        save(Categoria.builder()
                .nombre("Celulares")
                .descripcion("Teléfonos inteligentes")
                .build());
        save(Categoria.builder()
                .nombre("Televisores")
                .descripcion("Pantallas y Smart TV")
                .build());
        save(Categoria.builder()
                .nombre("Audio")
                .descripcion("Parlantes y audífonos")
                .build());
    }
}
