package pe.edu.upeu.syselectro.repository;

import pe.edu.upeu.syselectro.model.Marca;

public class MarcaRepository extends AbstractJpaRepository<Marca, Long> {

    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Marca entity) {
        return entity.getIdMarca();
    }

    @Override
    protected void setId(Marca entity, Long id) {
        entity.setIdMarca(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Carga datos de ejemplo una sola vez (no se vuelve a sembrar aunque se borre todo). */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Marca.builder()
                .nombre("Samsung")
                .paisOrigen("Corea del Sur")
                .build());
        save(Marca.builder()
                .nombre("LG")
                .paisOrigen("Corea del Sur")
                .build());
        save(Marca.builder()
                .nombre("Sony")
                .paisOrigen("Japón")
                .build());
        save(Marca.builder()
                .nombre("HP")
                .paisOrigen("Estados Unidos")
                .build());
        save(Marca.builder()
                .nombre("Lenovo")
                .paisOrigen("China")
                .build());
    }
}
