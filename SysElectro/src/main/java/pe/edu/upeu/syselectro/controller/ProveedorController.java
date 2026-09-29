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
import pe.edu.upeu.syselectro.model.Proveedor;
import pe.edu.upeu.syselectro.service.IProveedorService;

import java.util.*;
import java.util.function.Consumer;

/**
 * Controlador del módulo Proveedores (Integrante 5).
 * Recibe sus servicios por constructor (los inyecta AppContext).
 */
public class ProveedorController {

    private final IProveedorService servicio;

    public ProveedorController(IProveedorService servicio) {
        this.servicio = servicio;
    }

    @FXML TextField txtRuc, txtRazonSocial, txtTelefono, txtEmail, txtDireccion, txtFiltroDato;
    @FXML TableView<Proveedor> tableView;
    @FXML Label lbnMsg;
    @FXML AnchorPane miContenedor;

    private Validator validator;
    private Proveedor formulario;
    private Long idEditando = 0L;
    private final ToltipCustom ttc = new ToltipCustom();

    // ------------------------------------------------------------------
    //  INICIALIZACIÓN (se ejecuta al cargar el FXML)
    // ------------------------------------------------------------------
    @FXML
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Proveedor> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("idProveedor", 60.0));
        columnas.put("RUC", new ColumnInfo("ruc", 120.0));
        columnas.put("Razón social", new ColumnInfo("razonSocial", 250.0));
        columnas.put("Teléfono", new ColumnInfo("telefono", 110.0));
        columnas.put("Correo", new ColumnInfo("email", 200.0));
        columnas.put("Dirección", new ColumnInfo("direccion", 220.0));

        Consumer<Proveedor> accionEditar = x -> editForm(x);
        Consumer<Proveedor> accionEliminar = x -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Desea eliminar este registro?", ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText(null);
            if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }
            servicio.delete(x.getIdProveedor());
            if (idEditando.equals(x.getIdProveedor())) {
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
        List<Proveedor> todos = servicio.findAll();
        List<Proveedor> resultado = q.isEmpty() ? todos
                : todos.stream().filter(e -> coincide(e, q)).toList();
        tableView.getItems().setAll(resultado);
    }

    private boolean coincide(Proveedor e, String q) {
        return contiene(e.getRuc(), q) ||
                contiene(e.getRazonSocial(), q) ||
                contiene(e.getTelefono(), q) ||
                contiene(e.getEmail(), q) ||
                contiene(e.getDireccion(), q);
    }

    private boolean contiene(Object valor, String q) {
        return valor != null && valor.toString().toLowerCase().contains(q);
    }

    // ------------------------------------------------------------------
    //  FORMULARIO: leer -> validar -> guardar
    // ------------------------------------------------------------------
    @FXML
    public void validarFormulario() {
        formulario = new Proveedor();
        formulario.setRuc(txtRuc.getText());
        formulario.setRazonSocial(txtRazonSocial.getText());
        formulario.setTelefono(txtTelefono.getText());
        formulario.setEmail(txtEmail.getText());
        formulario.setDireccion(txtDireccion.getText());

        Set<ConstraintViolation<Proveedor>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        boolean esEdicion = idEditando > 0L;
        if (esEdicion) {
            formulario.setIdProveedor(idEditando);
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

    private void mostrarErroresValidacion(Set<ConstraintViolation<Proveedor>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("ruc", txtRuc);
        campos.put("razonSocial", txtRazonSocial);
        campos.put("telefono", txtTelefono);
        campos.put("email", txtEmail);
        campos.put("direccion", txtDireccion);

        Control primerControl = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> entrada : campos.entrySet()) {
            Optional<ConstraintViolation<Proveedor>> violacion = violaciones.stream()
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
    public void editForm(Proveedor x) {
        txtRuc.setText(x.getRuc() == null ? "" : x.getRuc());
        txtRazonSocial.setText(x.getRazonSocial() == null ? "" : x.getRazonSocial());
        txtTelefono.setText(x.getTelefono() == null ? "" : x.getTelefono());
        txtEmail.setText(x.getEmail() == null ? "" : x.getEmail());
        txtDireccion.setText(x.getDireccion() == null ? "" : x.getDireccion());
        idEditando = x.getIdProveedor();
        limpiarError();
        lbnMsg.setText("Editando el registro " + idEditando);
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 14px;");
    }

    @FXML
    public void clearForm() {
        txtRuc.clear();
        txtRazonSocial.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        idEditando = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void limpiarError() {
        List<Control> controles = List.of(
                txtRuc,
                txtRazonSocial,
                txtTelefono,
                txtEmail,
                txtDireccion);
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
