module fr.marieteamclient {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    
    // Java standard modules
    requires java.sql;
    requires java.desktop;
    
    // Third-party UI modules
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    
    // Database module
    requires mysql.connector.j;
    
    // iText modules
    requires kernel;
    requires layout;
    requires io;
    
    // Exports
    exports fr.marieteamclient;
    exports fr.marieteamclient.models;
    exports fr.marieteamclient.database;
    exports fr.marieteamclient.constants;
    exports fr.marieteamclient.utils;
    
    // Opens for JavaFX
    opens fr.marieteamclient to javafx.fxml;
    opens fr.marieteamclient.models to javafx.base;
}