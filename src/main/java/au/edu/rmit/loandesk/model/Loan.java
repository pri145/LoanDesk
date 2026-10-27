package au.edu.rmit.loandesk.model;

import java.time.LocalDate;

public class Loan {

    public static final String STATUS_ON_LOAN = "On loan";
    public static final String STATUS_OVERDUE = "Overdue";
    public static final String STATUS_RETURNED = "Returned";

    private final int id;
    private final int userId;
    private final LocalDate loanDate;

    private String itemCode;
    private String itemName;
    private String borrowerName;
    private String category;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String note;

    public Loan(
            int id,
            int userId,
            String itemCode,
            String itemName,
            String borrowerName,
            String category,
            LocalDate loanDate,
            LocalDate dueDate,
            LocalDate returnDate,
            String note) {

        this.id = id;
        this.userId = userId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.borrowerName = borrowerName;
        this.category = category;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.note = note == null ? "" : note;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? "" : note;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    /** Status relative to today's date. */
    public String getStatus() {
        return getStatus(LocalDate.now());
    }

    /**
     * Status relative to a given reference date, so that tests and exports
     * can use a fixed date. A loan due on the reference date is still
     * "On loan"; it becomes "Overdue" the day after.
     */
    public String getStatus(LocalDate today) {
        if (isReturned()) {
            return STATUS_RETURNED;
        }

        if (dueDate != null && today.isAfter(dueDate)) {
            return STATUS_OVERDUE;
        }

        return STATUS_ON_LOAN;
    }
}
