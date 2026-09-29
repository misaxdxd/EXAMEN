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
import pe.edu.upeu.syselectro.enums.TipoDocumento;
import pe.edu.upeu.syselectro.model.Cliente;
import pe.edu.upeu.syselectro.service.IClienteService;

import java.util.*;
import java.util.function.Consumer;

/**
 * Controlador del módulo Clientes (Integrante 4).
 * Recibe sus servicios por constructor (los inyecta AppContext).
 */
public class ClienteController {

    private final IClienteService servicio;

    public ClienteController(IClienteService servicio) {
        this.servicio = servicio;
    }

    @FXML TextField txtNumeroDocumento, txtNombres, txtTelefono, txtEmail, txtDireccion, txtFiltroDato;
    @FXML ComboBox<ComboBoxOption> cbxTipoDocumento;
    @FXML TableView<Cliente> tableView;
    @FXML Label lbnMsg;
    @FXML AnchorPane miContenedor;

    private Validator validator;
    private Cliente formulario;
    private Long idEditando = 0L;
    private final ToltipCustom ttc = new ToltipCustom();

    // ------------------------------------------------------------------
    //  INICIALIZACIÓN (se ejecuta al cargar el FXML)
    // ------------------------------------------------------------------
    @FXML
    public void initialize() {
        cbxTipoDocumento.getItems().addAll(servicio.listarTiposDocumento());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Cliente> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("idCliente", 60.0));
        columnas.put("Tipo de documento", new ColumnInfo("tipoDocumento", 130.0));
        columnas.put("N.º de documento", new ColumnInfo("numeroDocumento", 140.0));
        columnas.put("Nombres y apellidos", new ColumnInfo("nombres", 220.0));
        columnas.put("Teléfono", new ColumnInfo("telefono", 110.0));
        columnas.put("Correo", new ColumnInfo("email", 200.0));
        columnas.put("Dirección", new ColumnInfo("direccion", 220.0));

        Consumer<Cliente> accionEditar = x -> editForm(x);
        Consumer<Cliente> accionEliminar = x -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Desea eliminar este registro?", ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText(null);
            if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }
            servicio.delete(x.getIdCliente());
            if (idEditando.equals(x.getIdCliente())) {
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
        List<Cliente> todos = servicio.findAll();
        List<Cliente> resultado = q.isEmpty() ? todos
                : todos.stream().filter(e -> coincide(e, q)).toList();
        tableView.getItems().setAll(resultado);
    }

    private boolean coincide(Cliente e, String q) {
        return contiene(e.getTipoDocumento(), q) ||
                contiene(e.getNumeroDocumento(), q) ||
                contiene(e.getNombres(), q) ||
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
        formulario = new Cliente();
        ComboBoxOption opTipoDocumento = cbxTipoDocumento.getSelectionModel().getSelectedItem();
        formulario.setTipoDocumento(opTipoDocumento == null ? null : TipoDocumento.valueOf(opTipoDocumento.getKey()));
        formulario.setNumeroDocumento(txtNumeroDocumento.getText());
        formulario.setNombres(txtNombres.getText());
        formulario.setTelefono(txtTelefono.getText());
        formulario.setEmail(txtEmail.getText());
        formulario.setDireccion(txtDireccion.getText());

        Set<ConstraintViolation<Cliente>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        boolean esEdicion = idEditando > 0L;
        if (esEdicion) {
            formulario.setIdCliente(idEditando);
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

    private void mostrarErroresValidacion(Set<ConstraintViolation<Cliente>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("tipoDocumento", cbxTipoDocumento);
        campos.put("numeroDocumento", txtNumeroDocumento);
        campos.put("nombres", txtNombres);
        campos.put("telefono", txtTelefono);
        campos.put("email", txtEmail);
        campos.put("direccion", txtDireccion);

        Control primerControl = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> entrada : campos.entrySet()) {
            Optional<ConstraintViolation<Cliente>> violacion = violaciones.stream()
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
    public void editForm(Cliente x) {
        seleccionar(cbxTipoDocumento, x.getTipoDocumento() == null ? null : x.getTipoDocumento().name());
        txtNumeroDocumento.setText(x.getNumeroDocumento() == null ? "" : x.getNumeroDocumento());
        txtNombres.setText(x.getNombres() == null ? "" : x.getNombres());
        txtTelefono.setText(x.getTelefono() == null ? "" : x.getTelefono());
        txtEmail.setText(x.getEmail() == null ? "" : x.getEmail());
        txtDireccion.setText(x.getDireccion() == null ? "" : x.getDireccion());
        idEditando = x.getIdCliente();
        limpiarError();
        lbnMsg.setText("Editando el registro " + idEditando);
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 14px;");
    }

    @FXML
    public void clearForm() {
        cbxTipoDocumento.getSelectionModel().clearSelection();
        txtNumeroDocumento.clear();
        txtNombres.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        idEditando = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void limpiarError() {
        List<Control> controles = List.of(
                cbxTipoDocumento,
                txtNumeroDocumento,
                txtNombres,
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
