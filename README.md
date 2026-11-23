# Freelancer Invoice and Payment Desk (JAVA-P048)

A Java desktop application for freelancers to manage clients, billable services, invoices and payments.
Course: Programming in Java (25CSE0202), CA-3 / CA-4 project.

## What it does

1. Maintains freelancer profiles, client records and billable service descriptions.
2. Creates invoices with service quantities, unit rates, a tax percent and a due date.
3. Generates a unique invoice number (for example INV-2026-0007) and calculates the invoice total.
4. Records full or partial payments. The status (Pending, Partially Paid, Paid, Overdue) is worked out automatically.
5. Reports: client-wise outstanding balances, monthly earnings, payment collection and a status summary, with CSV export.

## Tech

Java 17 or newer, Swing (frontend), MySQL, JDBC (MySQL Connector/J).

## How to run

1. Install MySQL Server and run `sql/schema.sql` once (MySQL Workbench: open the file and press the lightning button).
2. Download the Connector/J jar into the `lib` folder and add it to the Eclipse build path (see `lib/README.txt`).
3. Copy `db.properties.example` to `db.properties` and write your MySQL password in it.
4. Run `src/app/Main.java` as a Java Application.

## Project structure

| Package | Purpose |
| --- | --- |
| `app` | `Main` starts the application |
| `model` | Plain classes: Person, Freelancer, Client, BillableService, Invoice, InvoiceItem, Payment, InvoiceStatus |
| `exception` | Custom exceptions for invalid invoices and payments |
| `dao` | All JDBC code (one DAO per table) |
| `service` | Business rules (`InvoiceService`) and Stream API reports (`ReportService`) |
| `util` | CSV export |
| `ui` | Swing screens |

## Java concepts and where they are used

| Concept | Where |
| --- | --- |
| OOP (inheritance, abstraction, polymorphism, interfaces) | `Person` with `Freelancer` and `Client`; `Identifiable`, `Dao` |
| ArrayList | Invoice items, every list returned by a DAO |
| HashMap and TreeMap | `InvoiceDao.loadItems`, report maps in `ReportService` |
| Custom exceptions | `InvalidInvoiceException`, `InvalidPaymentException` |
| JDBC | `dao` package: PreparedStatement, ResultSet mapping, transaction with commit and rollback |
| Lambda and Stream API | `ReportService`, `Invoice` totals, `PaymentPanel` |
| Generics | `Dao<T>`, `CrudDao<T>`, `MasterPanel<T extends Identifiable>` |
| File handling | `CsvExporter`, `DBConnection` reading `db.properties` |
