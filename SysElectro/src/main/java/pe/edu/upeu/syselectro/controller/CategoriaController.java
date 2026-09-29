package pe.edu.upeu.syselectro.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pe.edu.upeu.syselectro.components.ColumnInfo;
import pe.edu.upeu.syselectro.components.TableViewHelper;
import pe.edu.upeu.syselectro.components.Toast;
import pe.edu.upeu.syselectro.components.ToltipCustom;
import pe.edu.upeu.syselectro.dto.ComboBoxOption;
import pe.edu.upeu.syselectro.model.Categoria;
import pe.edu.upeu.syselectro.service.ICategoriaService;

import java.util.*;
import java.util.function.Consumer;

/**
 * Controlador del módulo Categorías (Integrante 1).
 * Recibe sus servicios por constructor (los inyecta AppContext).
 */
public class CategoriaController {

    private final ICategoriaService servicio;

    public CategoriaController(ICategoriaService servicio) {
        this.servicio = servicio;
    }

    @FXML TextField txtNombre, txtDescripcion, txtFiltroDato;
    @FXML TableView<Categoria> tableView;
    @FXML Label lbnMsg;
    @FXML AnchorPane miContenedor;

    private Validator validator;
    private Categoria formulario;
    private Long idEditando = 0L;
    private final ToltipCustom ttc = new ToltipCustom();

    // ------------------------------------------------------------------
    //  INICIALIZACIÓN (se ejecuta al cargar el FXML)
    // ------------------------------------------------------------------
    @FXML
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Categoria> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("idCategoria", 60.0));
        columnas.put("Nombre", new ColumnInfo("nombre", 200.0));
        columnas.put("Descripción", new ColumnInfo("descripcion", 300.0));

        Consumer<Categoria> accionEditar = x -> editForm(x);
        Consumer<Categoria> accionEliminar = x -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Desea eliminar este registro?", ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText(null);
            if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }
            servicio.delete(x.getIdCategoria());
            if (idEditando.equals(x.getIdCategoria())) {
                clearForm();
            }
            mostrarToast("Se eliminó correctamente!!");
            listar();
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columnas, accionEditar, accionEliminar);
        tableView.setTableMenuButtonVisible(true);
        txtFiltroDato.textProperty().addListener((obs, anterior, nuevo) -> filtrar(nuevo));
        listar();
    }

    // ------------------------------------------------------------------
    //  LISTAR Y FILTRAR
    // ------------------------------------------------------------------
    public void listar() {
        filtrar(txtFiltroDato.getText());
    }

    @FXML
    public void buscar() {
        filtrar(txtFiltroDato.getText());
    }

    private void filtrar(String texto) {
        String q = texto == null ? "" : texto.trim().toLowerCase();
        List<Categoria> todos = servicio.findAll();
        List<Categoria> resultado = q.isEmpty() ? todos
                : todos.stream().filter(e -> coincide(e, q)).toList();
        tableView.getItems().setAll(resultado);
    }

    private boolean coincide(Categoria e, String q) {
        return contiene(e.getNombre(), q) ||
                contiene(e.getDescripcion(), q);
    }

    private boolean contiene(Object valor, String q) {
        return valor != null && valor.toString().toLowerCase().contains(q);
    }

    // ------------------------------------------------------------------
    //  FORMULARIO: leer -> validar -> guardar
    // ------------------------------------------------------------------
    @FXML
    public void validarFormulario() {
        formulario = new Categoria();
        formulario.setNombre(txtNombre.getText());
        formulario.setDescripcion(txtDescripcion.getText());

        Set<ConstraintViolation<Categoria>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        boolean esEdicion = idEditando > 0L;
        if (esEdicion) {
            formulario.setIdCategoria(idEditando);
            servicio.update(idEditando, formulario);
        } else {
            servicio.save(formulario);
        }
        clearForm();
        listar();
        mostrarToast(esEdicion ? "Se actualizó correctamente!!" : "Se guardó correctamente!!");
        lbnMsg.setText(esEdicion ? "Registro actualizado" : "Registro guardado");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
    }

    private void mostrarErroresValidacion(Set<ConstraintViolation<Categoria>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("descripcion", txtDescripcion);

        Control primerControl = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> entrada : campos.entrySet()) {
            Optional<ConstraintViolation<Categoria>> violacion = violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(entrada.getKey()))
                    .findFirst();
            if (violacion.isPresent()) {
                String mensaje = violacion.get().getMessage().trim();
                ttc.marcarError(entrada.getValue(), mensaje);
                if (primerControl == null) {
                    primerControl = entrada.getValue();
                    primerMensaje = mensaje;
                }
            }
        }
        if (primerControl != null) {
            lbnMsg.setText(primerMensaje);
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            Control foco = primerControl;
            Platform.runLater(foco::requestFocus);
        }
    }

    // ------------------------------------------------------------------
    //  EDITAR / LIMPIAR
    // ------------------------------------------------------------------
    public void editForm(Categoria x) {
        txtNombre.setText(x.getNombre() == null ? "" : x.getNombre());
        txtDescripcion.setText(x.getDescripcion() == null ? "" : x.getDescripcion());
        idEditando = x.getIdCategoria();
        limpiarError();
        lbnMsg.setText("Editando el registro " + idEditando);
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 14px;");
    }

    @FXML
    public void clearForm() {
        txtNombre.clear();
        txtDescripcion.clear();
        idEditando = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void limpiarError() {
        List<Control> controles = List.of(
                txtNombre,
                txtDescripcion);
        controles.forEach(ttc::limpiarCampo);
    }

    // ------------------------------------------------------------------
    //  UTILIDADES PRIVADAS
    // ------------------------------------------------------------------
    private void mostrarToast(String mensaje) {
        Stage stage = (Stage) miContenedor.getScene().getWindow();
        double x = stage.getWidth() / 1.5;
        double y = stage.getHeight() / 2;
        Toast.showToast(stage, mensaje, 2000, x, y);
    }

    private void seleccionar(ComboBox<ComboBoxOption> combo, String clave) {
        combo.getSelectionModel().select(combo.getItems().stream()
                .filter(o -> o.getKey().equals(clave))
                .findFirst().orElse(null));
    }

    /** Devuelve null si el texto está vacío o no es un número (así falla @NotNull). */
    private Double leerDouble(String texto) {
        try {
            return texto == null || texto.isBlank() ? null : Double.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer leerEntero(String texto) {
        try {
            return texto == null || texto.isBlank() ? null : Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
