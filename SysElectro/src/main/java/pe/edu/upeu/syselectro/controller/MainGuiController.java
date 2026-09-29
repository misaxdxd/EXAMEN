package pe.edu.upeu.syselectro.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import pe.edu.upeu.syselectro.config.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainGuiController {

    @FXML
    TabPane tabPane;

    @FXML
    MenuItem menuCategoria, menuMarca, menuEquipo, menuCliente, menuProveedor, menuUsuario, menuSalir;

    /** id del menú -> {ruta del FXML, título de la pestaña} */
    private final Map<String, String[]> menuConfig = Map.of(
            "menuCategoria", new String[]{"/view/main_categoria.fxml", "Categorías"},
            "menuMarca", new String[]{"/view/main_marca.fxml", "Marcas"},
            "menuEquipo", new String[]{"/view/main_equipo.fxml", "Equipos electrónicos"},
            "menuCliente", new String[]{"/view/main_cliente.fxml", "Clientes"},
            "menuProveedor", new String[]{"/view/main_proveedor.fxml", "Proveedores"},
            "menuUsuario", new String[]{"/view/main_usuario.fxml", "Usuarios (vendedores)"});

    @FXML
    public void initialize() {
        List.of(menuCategoria, menuMarca, menuEquipo, menuCliente, menuProveedor, menuUsuario).forEach(item -> item.setOnAction(this::abrirModulo));
        menuSalir.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void abrirModulo(ActionEvent e) {
        String id = ((MenuItem) e.getSource()).getId();
        String[] config = menuConfig.get(id);
        if (config != null) {
            abrirTabPaneFXML(config[0], config[1]);
        }
    }

    private void abrirTabPaneFXML(String fxmlPath, String titulo) {
        // Si la pestaña ya está abierta, solo se selecciona
        for (Tab tab : tabPane.getTabs()) {
            if (titulo.equals(tab.getText())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        try {
            AppContext context = AppContext.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            Tab nuevaTab = new Tab(titulo, scrollPane);
            tabPane.getTabs().add(nuevaTab);
            tabPane.getSelectionModel().select(nuevaTab);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
