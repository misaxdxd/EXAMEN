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
import pe.edu.upeu.syselectro.enums.EstadoEquipo;
import pe.edu.upeu.syselectro.model.Equipo;
import pe.edu.upeu.syselectro.service.ICategoriaService;
import pe.edu.upeu.syselectro.service.IEquipoService;
import pe.edu.upeu.syselectro.service.IMarcaService;

import java.util.*;
import java.util.function.Consumer;

/**
 * Controlador del módulo Equipos electrónicos (Integrante 3).
 * Recibe sus servicios por constructor (los inyecta AppContext).
 */
public class EquipoController {

    private final IEquipoService servicio;
    private final ICategoriaService categoriaServicio;
    private final IMarcaService marcaServicio;

    public EquipoController(IEquipoService servicio, ICategoriaService categoriaServicio, IMarcaService marcaServicio) {
        this.servicio = servicio;
        this.categoriaServicio = categoriaServicio;
        this.marcaServicio = marcaServicio;
    }

    @FXML TextField txtNombre, txtModelo, txtPrecio, txtStock, txtGarantiaMeses, txtFiltroDato;
    @FXML ComboBox<ComboBoxOption> cbxEstado;
    @FXML ComboBox<ComboBoxOption> cbxCategoria;
    @FXML ComboBox<ComboBoxOption> cbxMarca;
    @FXML TableView<Equipo> tableView;
    @FXML Label lbnMsg;
    @FXML AnchorPane miContenedor;

    private Validator validator;
    private Equipo formulario;
    private Long idEditando = 0L;
    private final ToltipCustom ttc = new ToltipCustom();

    // ------------------------------------------------------------------
    //  INICIALIZACIÓN (se ejecuta al cargar el FXML)
    // ------------------------------------------------------------------
    @FXML
    public void initialize() {
        cbxEstado.getItems().addAll(servicio.listarEstados());
        cbxCategoria.getItems().addAll(categoriaServicio.listarCombobox());
        cbxMarca.getItems().addAll(marcaServicio.listarCombobox());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Equipo> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columnas = new LinkedHashMap<>();
        columnas.put("ID", new ColumnInfo("idEquipo", 60.0));
        columnas.put("Nombre del equipo", new ColumnInfo("nombre", 220.0));
        columnas.put("Modelo", new ColumnInfo("modelo", 140.0));
        columnas.put("Estado", new ColumnInfo("estado", 120.0));
        columnas.put("Precio (S/)", new ColumnInfo("precio", 100.0));
        columnas.put("Stock", new ColumnInfo("stock", 80.0));
        columnas.put("Garantía (meses)", new ColumnInfo("garantiaMeses", 110.0));
        columnas.put("Categoría", new ColumnInfo("categoria.nombre", 150.0));
        columnas.put("Marca", new ColumnInfo("marca.nombre", 150.0));

        Consumer<Equipo> accionEditar = x -> editForm(x);
        Consumer<Equipo> accionEliminar = x -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Desea eliminar este registro?", ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText(null);
            if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }
            servicio.delete(x.getIdEquipo());
            if (idEditando.equals(x.getIdEquipo())) {
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
        List<Equipo> todos = servicio.findAll();
        List<Equipo> resultado = q.isEmpty() ? todos
                : todos.stream().filter(e -> coincide(e, q)).toList();
        tableView.getItems().setAll(resultado);
    }

    private boolean coincide(Equipo e, String q) {
        return contiene(e.getNombre(), q) ||
                contiene(e.getModelo(), q) ||
                contiene(e.getEstado(), q) ||
                contiene(e.getPrecio(), q) ||
                contiene(e.getStock(), q) ||
                contiene(e.getGarantiaMeses(), q) ||
                contiene(e.getCategoria() == null ? null : e.getCategoria().getNombre(), q) ||
                contiene(e.getMarca() == null ? null : e.getMarca().getNombre(), q);
    }

    private boolean contiene(Object valor, String q) {
        return valor != null && valor.toString().toLowerCase().contains(q);
    }

    // ------------------------------------------------------------------
    //  FORMULARIO: leer -> validar -> guardar
    // ------------------------------------------------------------------
    @FXML
    public void validarFormulario() {
        formulario = new Equipo();
        formulario.setNombre(txtNombre.getText());
        formulario.setModelo(txtModelo.getText());
        ComboBoxOption opEstado = cbxEstado.getSelectionModel().getSelectedItem();
        formulario.setEstado(opEstado == null ? null : EstadoEquipo.valueOf(opEstado.getKey()));
        formulario.setPrecio(leerDouble(txtPrecio.getText()));
        formulario.setStock(leerEntero(txtStock.getText()));
        formulario.setGarantiaMeses(leerEntero(txtGarantiaMeses.getText()));
        ComboBoxOption opCategoria = cbxCategoria.getSelectionModel().getSelectedItem();
        formulario.setCategoria(opCategoria == null ? null
                : categoriaServicio.findById(Long.parseLong(opCategoria.getKey())));
        ComboBoxOption opMarca = cbxMarca.getSelectionModel().getSelectedItem();
        formulario.setMarca(opMarca == null ? null
                : marcaServicio.findById(Long.parseLong(opMarca.getKey())));

        Set<ConstraintViolation<Equipo>> violaciones = validator.validate(formulario);
        if (violaciones.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violaciones);
        }
    }

    private void procesarFormulario() {
        boolean esEdicion = idEditando > 0L;
        if (esEdicion) {
            formulario.setIdEquipo(idEditando);
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

    private void mostrarErroresValidacion(Set<ConstraintViolation<Equipo>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("modelo", txtModelo);
        campos.put("estado", cbxEstado);
        campos.put("precio", txtPrecio);
        campos.put("stock", txtStock);
        campos.put("garantiaMeses", txtGarantiaMeses);
        campos.put("categoria", cbxCategoria);
        campos.put("marca", cbxMarca);

        Control primerControl = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> entrada : campos.entrySet()) {
            Optional<ConstraintViolation<Equipo>> violacion = violaciones.stream()
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
    public void editForm(Equipo x) {
        txtNombre.setText(x.getNombre() == null ? "" : x.getNombre());
        txtModelo.setText(x.getModelo() == null ? "" : x.getModelo());
        seleccionar(cbxEstado, x.getEstado() == null ? null : x.getEstado().name());
        txtPrecio.setText(x.getPrecio() == null ? "" : x.getPrecio().toString());
        txtStock.setText(x.getStock() == null ? "" : x.getStock().toString());
        txtGarantiaMeses.setText(x.getGarantiaMeses() == null ? "" : x.getGarantiaMeses().toString());
        seleccionar(cbxCategoria, x.getCategoria() == null ? null : String.valueOf(x.getCategoria().getIdCategoria()));
        seleccionar(cbxMarca, x.getMarca() == null ? null : String.valueOf(x.getMarca().getIdMarca()));
        idEditando = x.getIdEquipo();
        limpiarError();
        lbnMsg.setText("Editando el registro " + idEditando);
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 14px;");
    }

    @FXML
    public void clearForm() {
        txtNombre.clear();
        txtModelo.clear();
        cbxEstado.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtStock.clear();
        txtGarantiaMeses.clear();
        cbxCategoria.getSelectionModel().clearSelection();
        cbxMarca.getSelectionModel().clearSelection();
        idEditando = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void limpiarError() {
        List<Control> controles = List.of(
                txtNombre,
                txtModelo,
                cbxEstado,
                txtPrecio,
                txtStock,
                txtGarantiaMeses,
                cbxCategoria,
                cbxMarca);
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
