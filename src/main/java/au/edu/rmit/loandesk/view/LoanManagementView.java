package au.edu.rmit.loandesk.view;

import au.edu.rmit.loandesk.model.Loan;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class LoanManagementView {

    public static final String ALL_STATUSES = "All Statuses";

    private final MainLayout mainLayout;
    private final Scene scene;

    private final TableView<Loan> loanTable = new TableView<>();
    private final ObservableList<Loan> loans =
            FXCollections.observableArrayList();

    private final TextField searchField = new TextField();
    private final ComboBox<String> statusFilter = new ComboBox<>();

    private final Button addButton = new Button("Add Loan");
    private final Button editButton = new Button("Edit Selected");
    private final Button returnButton = new Button("Mark Returned");
    private final Button deleteButton = new Button("Delete Selected");
    private final Button refreshButton = new Button("Refresh");
    private final Button backButton = new Button("Back to Dashboard");

    private final Label messageLabel = new Label();

    public LoanManagementView() {
        Label introLabel = new Label(
                "Track borrowed items, due dates and return status.");
        introLabel.setStyle(
                "-fx-font-size: 13px; -fx-text-fill: #64748b;");

        searchField.setPromptText(
                "Search code, item or borrower...");
        searchField.setPrefWidth(260);

        statusFilter.setItems(FXCollections.observableArrayList(
                ALL_STATUSES, Loan.STATUS_ON_LOAN,
                Loan.STATUS_OVERDUE, Loan.STATUS_RETURNED));
        statusFilter.setValue(ALL_STATUSES);
        statusFilter.setPrefWidth(145);

        HBox filters = new HBox(
                12, searchField, statusFilter, refreshButton);
        filters.setAlignment(Pos.CENTER_LEFT);

        configureTable();
        disableActionsWithoutSelection();

        HBox actions = new HBox(
                10, addButton, editButton, returnButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #b91c1c;");

        VBox content = new VBox(
                16, introLabel, filters, loanTable,
                actions, messageLabel, backButton);
        content.setPadding(new Insets(8, 0, 8, 0));
        VBox.setVgrow(loanTable, Priority.ALWAYS);

        mainLayout = new MainLayout(
                "Manage Loans",
                "View and maintain your loan records.",
                content);

        mainLayout.getProfileButton().setVisible(false);
        mainLayout.getProfileButton().setManaged(false);

        scene = new Scene(mainLayout.getRoot(), 1200, 700);
    }

    private void configureTable() {
        TableColumn<Loan, Integer> idColumn =
                new TableColumn<>("Loan ID");
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(65);

        TableColumn<Loan, String> codeColumn =
                new TableColumn<>("Item Code");
        codeColumn.setCellValueFactory(
                new PropertyValueFactory<>("itemCode"));

        TableColumn<Loan, String> itemColumn =
                new TableColumn<>("Item Name");
        itemColumn.setCellValueFactory(
                new PropertyValueFactory<>("itemName"));

        TableColumn<Loan, String> borrowerColumn =
                new TableColumn<>("Borrower");
        borrowerColumn.setCellValueFactory(
                new PropertyValueFactory<>("borrowerName"));

        TableColumn<Loan, String> categoryColumn =
                new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        TableColumn<Loan, LocalDate> loanDateColumn =
                new TableColumn<>("Loan Date");
        loanDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("loanDate"));

        TableColumn<Loan, LocalDate> dueDateColumn =
                new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("dueDate"));

        TableColumn<Loan, LocalDate> returnDateColumn =
                new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("returnDate"));

        TableColumn<Loan, String> statusColumn =
                new TableColumn<>("Status");
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        TableColumn<Loan, String> noteColumn =
                new TableColumn<>("Notes");
        noteColumn.setCellValueFactory(
                new PropertyValueFactory<>("note"));
        noteColumn.setMinWidth(130);

        loanTable.getColumns().addAll(List.of(
                idColumn,
                codeColumn,
                itemColumn,
                borrowerColumn,
                categoryColumn,
                loanDateColumn,
                dueDateColumn,
                returnDateColumn,
                statusColumn,
                noteColumn
        ));

        loanTable.setItems(loans);
        loanTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        loanTable.setPlaceholder(
                new Label("No loans found. Add a loan to get started."));
        loanTable.setMinHeight(250);
    }

    /** Edit, return and delete only make sense for a selected row. */
    private void disableActionsWithoutSelection() {
        BooleanBinding nothingSelected = loanTable.getSelectionModel()
                .selectedItemProperty().isNull();

        editButton.disableProperty().bind(nothingSelected);
        returnButton.disableProperty().bind(nothingSelected);
        deleteButton.disableProperty().bind(nothingSelected);
    }

    public void show(Stage stage) {
        stage.setTitle("LoanDesk | Manage Loans");
        stage.setMinWidth(950);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public void setLoans(List<Loan> loanList) {
        loans.setAll(loanList);
    }

    public Loan getSelectedLoan() {
        return loanTable.getSelectionModel().getSelectedItem();
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    public TextField getSearchField() {
        return searchField;
    }

    public ComboBox<String> getStatusFilter() {
        return statusFilter;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getReturnButton() {
        return returnButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getRefreshButton() {
        return refreshButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}
