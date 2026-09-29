package pe.edu.upeu.syselectro.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marca {

    private Long idMarca;
    @NotBlank(message = "El nombre de la marca es obligatorio")
    private String nombre;
    @NotBlank(message = "El país de origen es obligatorio")
    private String paisOrigen;
}
