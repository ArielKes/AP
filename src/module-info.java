module view {
    requires javafx.controls;
    requires javafx.fxml;
            
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.management;

    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;

    opens view to javafx.fxml;
    exports view;
}