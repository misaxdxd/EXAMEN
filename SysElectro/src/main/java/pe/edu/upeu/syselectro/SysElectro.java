package pe.edu.upeu.syselectro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.syselectro.config.AppContext;

import java.io.IOException;

public class SysElectro extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Screen screen = Screen.getPrimary();
        Rectangle2D rectangle2D = screen.getVisualBounds();
        AppContext appContext = AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(SysElectro.class.getResource("/view/maingui.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load(), rectangle2D.getWidth(), rectangle2D.getHeight() - 50);
        scene.getStylesheets().add(SysElectro.class.getResource("/css/style.css").toExternalForm());
        stage.setTitle("SysElectro - Ventas de equipos electrónicos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
