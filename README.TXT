s4214905, Priya Mukesh Parekh
[Y] Signup and login with validation
[Y] Dashboard and profile editing
[Y] Loan creation and validation
[Y] Duplicate outstanding-loan prevention
[Y] Loan editing, return and deletion
[Y] Viewing loans and calculating status
[Y] User isolation and logout
[Y] JDBC database persistence and automatic setup
[Y] Text-file export
[Y] MVC
[Y] Additional pattern: DAO / Repository - dao/LoanRepository (interface) and dao/LoanDao (JDBC implementation)
[Y] Advanced password update and salted hashing
[Y] Advanced JUnit tests

Setup/run/test instructions:
1. Versions: JDK 17; JavaFX 17.0.16 (javafx-controls, javafx-fxml, via Maven);
   JDBC driver org.xerial:sqlite-jdbc 3.50.3.0 (SQLite); JUnit Jupiter (JUnit 5)
   5.11.0; Maven with javafx-maven-plugin 0.0.8. Views are written in Java code
   (FXML is not used).
2. Install JDK 17 and Maven. Internet access is needed the first time so Maven
   can download the dependencies.
3. Open a terminal in the project root (the folder containing pom.xml).
4. Run the application:   mvn javafx:run
   (main class: au.edu.rmit.loandesk.App)
   In Eclipse: File > Import > Maven > Existing Maven Projects, then
   Run As > Maven build... with goal  javafx:run
5. Run the unit tests:    mvn test      (Eclipse: Run As > JUnit Test)
   The tests need no GUI. They use a fixed clock and an in-memory repository,
   so they never read or change the application's database.
6. Database location: <user home>/.loandesk/loandesk.db. The folder, file and
   tables are created automatically at start-up. No default users or sample
   loans are created and no database is included in this ZIP.
7. Starting with an empty test database without overwriting the real one: set
   the environment variable LOANDESK_DB_PATH to a new file path, for example
   C:/temp/loandesk-test.db (Eclipse: Run Configurations > Maven Build > your
   javafx:run configuration > Environment tab > New). The application then
   creates and uses that file instead. Alternatively use the Java system
   property -Dloandesk.db.path=<path>. To reset the normal database, close the
   application and delete <user home>/.loandesk/loandesk.db.
8. Seeing "Overdue": create a loan whose due date is today. It shows "On loan"
   today and "Overdue" after a refresh on the next day. These status rules are
   also covered by unit tests using fixed dates. No test database is included.

Design notes:
- MVC: model/ (User, Loan, PasswordCredentials), view/ (JavaFX screens built in
  code; no business rules or SQL), controller/ (handle user actions, call
  services, update views), service/ (InputValidator, AuthService,
  PasswordService, LoanService, LoanExportService hold the validation and
  business rules). App.java connects the screens and clears the logged-in user
  on logout.
- Additional pattern - DAO / Repository (dao/): LoanRepository is the
  interface and LoanDao is the JDBC implementation; LoanService depends only on
  the interface (constructor injection). Reasons: SQL stays out of the business
  rules; the rules are unit-tested with an in-memory implementation
  (src/test/.../InMemoryLoanRepository) instead of a database; and ownership is
  enforced in the persistence layer, because every read, update, return and
  delete includes the user's ID. UserDao follows the same DAO approach.
- Other points: encapsulated model classes (loan ID, owner and loan date are
  immutable); a java.time.Clock is injected into LoanService and
  LoanExportService so dates and statuses can be tested with fixed dates; all
  SQL uses PreparedStatement parameters; a partial unique index on
  loans(user_id, item_code COLLATE NOCASE) WHERE return_date IS NULL also
  blocks duplicate active item codes.

Password hashing and update:
- Algorithm: PBKDF2WithHmacSHA256 through javax.crypto.SecretKeyFactory.
- Salt: 16 random bytes from java.security.SecureRandom, generated separately
  for every account and generated again whenever the password changes.
- Iterations: 600,000. Output (derived key) length: 256 bits (32 bytes).
- Stored per account: hash, salt, algorithm, iteration count and key length
  (Base64 text). Plaintext passwords are never stored. Hashing is applied at
  signup and verified at login (constant-time comparison).
- Password update (Edit Profile screen): requires the current password and the
  new password entered twice; applies the signup rules; rejects reuse of the
  current password; saves only after every check passes. After logout or
  restart the new password works and the old one is rejected.

JUnit tests (36, JUnit 5, in src/test/java): InputValidatorTest (usernames,
names, passwords), LoanServiceTest (creation rules, due date before / equal to
the loan date, duplicate active codes ignoring case, ownership checks for view,
edit, return and delete, no edits after return, sort order), LoanTest
(On loan / Overdue / Returned with fixed dates), LoanExportServiceTest (header,
no-records message, field order, no password data) and PasswordServiceTest
(verification, unique salts and hashes, corrupted data).

Known limitations and references:
- Overwrite confirmation when exporting is provided by the operating system's
  file chooser dialog.
- Statuses are recalculated when loans are loaded, after a change, on Refresh
  and before export; there is no timer that updates them at midnight.
- The loan date is shown when editing a loan but cannot be changed (by design).
- AuthService.changePassword has no automated test because it uses the real
  database; hashing and validation are tested separately.
- Optional extra (not required by the specification): search box and status
  filter on the Manage Loans screen.
- References: course lecture, lab and demo material (including the RepairCafe
  demo's controller approach and the JavaFX event-handling demos); official
  documentation for Java, JavaFX, SQLite JDBC and JUnit.