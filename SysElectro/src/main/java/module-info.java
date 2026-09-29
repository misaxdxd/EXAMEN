module pe.edu.upeu.syselectro {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.syselectro to javafx.fxml;
    opens pe.edu.upeu.syselectro.controller to javafx.fxml;
    opens pe.edu.upeu.syselectro.model;
    exports pe.edu.upeu.syselectro;
}
