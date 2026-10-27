package au.edu.rmit.loandesk.view;

import au.edu.rmit.loandesk.model.Loan;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class LoanFormView {

    private final Stage dialogStage;

    private final TextField itemCodeField = new TextField();
    private final TextField itemNameField = new TextField();
    private final TextField borrowerNameField = new TextField();

    private final ComboBox<String> categoryComboBox =
            new ComboBox<>(FXCollections.observableArrayList(
                    "Tools", "Electronics", "Outdoor", "Other"));

    private final DatePicker loanDatePicker = new DatePicker();
    private final DatePicker dueDatePicker = new DatePicker();

    private final TextArea noteArea = new TextArea();

    private final Button saveButton = new Button("Save Loan");
    private final Button cancelButton = new Button("Cancel");

    private final Label messageLabel = new Label();

    public LoanFormView(Stage owner, Loan existingLoan) {
        dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        boolean editing = existingLoan != null;

        Label titleLabel = new Label(
                editing ? "Edit Loan" : "Add New Loan");
        titleLabel.setStyle(
                "-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label(
                "Enter the item and borrowing details below.");
        subtitleLabel.setStyle(
                "-fx-text-fill: #64748b; -fx-font-size: 13px;");

        configureFields();

        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(14);
        form.setPadding(new Insets(20, 0, 16, 0));

        addFormRow(form, 0, "Item Code *", itemCodeField);
        addFormRow(form, 1, "Item Name *", itemNameField);
        addFormRow(form, 2, "Borrower Name *", borrowerNameField);
        addFormRow(form, 3, "Category *", categoryComboBox);
        addFormRow(form, 4, "Loan Date", loanDatePicker);
        addFormRow(form, 5, "Due Date *", dueDatePicker);
        addFormRow(form, 6, "Notes", noteArea);

        GridPane.setHgrow(itemCodeField, Priority.ALWAYS);
        GridPane.setHgrow(itemNameField, Priority.ALWAYS);
        GridPane.setHgrow(borrowerNameField, Priority.ALWAYS);
        GridPane.setHgrow(categoryComboBox, Priority.ALWAYS);
        GridPane.setHgrow(loanDatePicker, Priority.ALWAYS);
        GridPane.setHgrow(dueDatePicker, Priority.ALWAYS);
        GridPane.setHgrow(noteArea, Priority.ALWAYS);

        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #b91c1c;");

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(
                8, titleLabel, subtitleLabel, form,
                messageLabel, buttons);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 580, 610);
        dialogStage.setScene(scene);
        dialogStage.setTitle(editing ? "Edit Loan" : "Add Loan");
        dialogStage.setResizable(false);

        if (existingLoan != null) {
            populateFields(existingLoan);

            if (existingLoan.isReturned()) {
                setLoanFieldsDisabled(true);
                saveButton.setDisable(true);
                showMessage("Returned loans cannot be edited.");
            }
        } else {
            loanDatePicker.setValue(LocalDate.now());
            dueDatePicker.setValue(LocalDate.now().plusDays(7));
        }

        // The original loan date is informational, not editable.
        loanDatePicker.setEditable(false);
        loanDatePicker.setDisable(true);
    }

    private void configureFields() {
        itemCodeField.setPromptText("e.g. DRILL-01");
        itemNameField.setPromptText("Enter item name");
        borrowerNameField.setPromptText("Enter borrower's name");

        categoryComboBox.setPromptText("Select category");
        categoryComboBox.setMaxWidth(Double.MAX_VALUE);

        loanDatePicker.setMaxWidth(Double.MAX_VALUE);
        dueDatePicker.setMaxWidth(Double.MAX_VALUE);

        dueDatePicker.setDayCellFactory(picker ->
                new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(
                            LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        LocalDate loanDate = loanDatePicker.getValue();

                        setDisable(empty
                                || (loanDate != null
                                && date.isBefore(loanDate)));
                    }
                });

        loanDatePicker.valueProperty().addListener(
                (observable, oldDate, newDate) -> {
                    if (newDate != null
                            && dueDatePicker.getValue() != null
                            && dueDatePicker.getValue()
                                    .isBefore(newDate)) {
                        dueDatePicker.setValue(newDate);
                    }
                });

        noteArea.setPromptText(
                "Optional notes (maximum 250 characters)");
        noteArea.setPrefRowCount(3);
        noteArea.setWrapText(true);

        // Prevent entering more than 250 characters, including newlines.
        noteArea.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(
                        change -> change.getControlNewText().length() <= 250
                                ? change : null));

        itemCodeField.setMaxWidth(Double.MAX_VALUE);
        itemNameField.setMaxWidth(Double.MAX_VALUE);
        borrowerNameField.setMaxWidth(Double.MAX_VALUE);
    }

    private void addFormRow(
            GridPane grid,
            int row,
            String labelText,
            Node control) {

        Label label = new Label(labelText);
        label.setMinWidth(115);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private void populateFields(Loan loan) {
        itemCodeField.setText(loan.getItemCode());
        itemNameField.setText(loan.getItemName());
        borrowerNameField.setText(loan.getBorrowerName());
        categoryComboBox.setValue(loan.getCategory());
        loanDatePicker.setValue(loan.getLoanDate());
        dueDatePicker.setValue(loan.getDueDate());
        noteArea.setText(loan.getNote());
    }

    private void setLoanFieldsDisabled(boolean disabled) {
        itemCodeField.setDisable(disabled);
        itemNameField.setDisable(disabled);
        borrowerNameField.setDisable(disabled);
        categoryComboBox.setDisable(disabled);
        dueDatePicker.setDisable(disabled);
        noteArea.setDisable(disabled);
    }

    public void showAndWait() {
        dialogStage.showAndWait();
    }

    public void close() {
        dialogStage.close();
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    public String getItemCode() {
        return itemCodeField.getText();
    }

    public String getItemName() {
        return itemNameField.getText();
    }

    public String getBorrowerName() {
        return borrowerNameField.getText();
    }

    public String getCategory() {
        return categoryComboBox.getValue();
    }

    public LocalDate getLoanDate() {
        return loanDatePicker.getValue();
    }

    public LocalDate getDueDate() {
        return dueDatePicker.getValue();
    }

    public String getNote() {
        return noteArea.getText();
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
