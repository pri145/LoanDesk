LoanDesk - COSC1295 Assignment 2
Priya Mukesh Parekh, s4214905

VERSIONS
- JDK 17 (maven.compiler.release = 17)
- JavaFX 17.0.16 (javafx-controls, javafx-fxml via Maven; views are built in code, FXML is not used)
- JDBC: org.xerial:sqlite-jdbc 3.50.3.0 (SQLite)
- JUnit Jupiter (JUnit 5) 5.11.0
- Maven with javafx-maven-plugin 0.0.8

SETUP / RUN / TEST
1. Install JDK 17 and Maven. Dependencies download automatically.
2. Run the application:   mvn javafx:run      (main class: au.edu.rmit.loandesk.App)
3. Run the unit tests:    mvn test
   Tests use a fixed clock and an in-memory repository, so they need no GUI
   and never touch the application database.
Start with an empty database: delete ~/.loandesk/loandesk.db, or run with
   mvn javafx:run -Dexec.jvmArgs=-Dloandesk.db.path=/tmp/test-loandesk.db
(or add -Dloandesk.db.path=... to the JVM arguments of your IDE run configuration).
No default users or sample loans are created. The database and tables are
created automatically on start-up.

DATABASE LOCATION
~/.loandesk/loandesk.db (user home directory; no absolute path is hard-coded).
Override with the system property loandesk.db.path.

FEATURE STATUS
[Y] Signup and login with validation
[Y] Dashboard and profile editing (first/last name)
[Y] Loan creation and validation (loan date fixed to today; item code
    normalised to upper case; duplicate active code per user rejected both
    in LoanService and by a partial unique index)
[Y] Duplicate outstanding-loan prevention
[Y] Loan editing, return and deletion (edit/return/delete buttons are disabled
    until a row is selected; returned loans cannot be edited)
[Y] Viewing loans and calculating status (newest loan date first, then highest
    ID; due today = On loan, day after = Overdue; Refresh button recomputes)
[Y] User isolation and logout (every loan query/update/delete carries user_id;
    logout clears the session)
[Y] JDBC database persistence and automatic setup
[Y] Text-file export (all of the user's loans, UTF-8, FileChooser, header
    with username/full name/export date, "No loan records found." if empty;
    passwords and hashes are never exported)
[Y] Advanced password update
[Y] Advanced salted hashing (PBKDF2WithHmacSHA256, 600000 iterations, random
    16-byte salt per account, applied from signup)
[Y] Advanced JUnit tests (validator, loan service rules incl. ownership,
    return and edit rules, status with fixed dates, export, password hashing)
Optional features (not marked): search box and status filter on Manage Loans.

DESIGN
MVC: model (User, Loan), view (JavaFX classes in view/, no business rules),
controller (coordinates user actions), service (validation and business rules).
Additional pattern - DAO / Repository: dao/LoanRepository is the interface;
dao/LoanDao is the JDBC implementation. LoanService depends only on the
interface (constructor injection), which keeps SQL out of the business rules,
lets tests use InMemoryLoanRepository instead of a database, and enforces
ownership in the persistence layer. UserDao follows the same approach.
Other OO ideas: encapsulation (private fields, immutable id/loan date),
dependency injection of a java.time.Clock so status and dates are testable.

KNOWN LIMITATIONS
- Overwrite confirmation is provided by the operating system's file chooser.
- Status is calculated when loans are loaded/refreshed, not by a timer.

ACKNOWLEDGEMENTS
Course lecture/lab/demo code (RepairCafe demo patterns) was used as reference.
Declare any AI or other assistance used here, as required by the specification.
