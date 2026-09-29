package pe.edu.upeu.syselectro.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.syselectro.enums.TipoDocumento;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Long idCliente;
    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;
    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,15}$", message = "El documento debe tener entre 8 y 15 caracteres alfanuméricos")
    private String numeroDocumento;
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;
    @Pattern(regexp = "^$|^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;
    @Email(message = "El correo no es válido")
    private String email;
    private String direccion;
}
