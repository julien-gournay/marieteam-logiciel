module fr.marieteamclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;
    
    requires kernel;
    requires layout;
    requires io;

    opens fr.marieteamclient to javafx.fxml;
    opens fr.marieteamclient.models to javafx.base;
    
    exports fr.marieteamclient;
    exports fr.marieteamclient.models;
}