package au.edu.rmit.loandesk.view;

import javafx.stage.Stage;

/** One window size shared by every screen so the app looks consistent. */
public final class WindowSettings {

    public static final double WIDTH = 1100;
    public static final double HEIGHT = 700;
    public static final double MIN_WIDTH = 900;
    public static final double MIN_HEIGHT = 650;

    private WindowSettings() {
        // Utility class: not instantiated.
    }

    /** Call after stage.setScene(...) and before stage.show(). */
    public static void apply(Stage stage) {
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        if (!stage.isMaximized()) {
            stage.setWidth(WIDTH);
            stage.setHeight(HEIGHT);
        }
    }
}