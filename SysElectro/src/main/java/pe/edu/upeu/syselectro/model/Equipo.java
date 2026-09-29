package pe.edu.upeu.syselectro.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.syselectro.enums.EstadoEquipo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {

    private Long idEquipo;
    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;
    @NotNull(message = "El estado es obligatorio")
    private EstadoEquipo estado;
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    @NotNull(message = "La garantía es obligatoria")
    @PositiveOrZero(message = "La garantía no puede ser negativa")
    private Integer garantiaMeses;
    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;
    @NotNull(message = "La marca es obligatoria")
    private Marca marca;
}
