# 🍽️ Cafeteria Preorder System (SQLite Edition)

A **Cafeteria Preorder System** built with **Java + Swing + JDBC/SQLite**, demonstrating core **Object-Oriented Programming (OOP)** concepts. No database server to install — the database is a single file created automatically on first run.

---

## ✨ Features
- **Role-based login:** Admin, Staff, Customer
- **Dashboard** (Admin/Staff): live counts + revenue
- **Menu Management** (Admin): add items, toggle availability, delete
- **Place Order** (Customer): browse menu, build cart, pick a slot, order
- **Order tracking:** Staff/Admin update status; Customers track their own

---

## 🎯 OOP Concepts Demonstrated
| Concept | Implementation |
|---------|----------------|
| **Abstraction** | Abstract `Person` class with abstract `getRole()` |
| **Inheritance** | `Admin`, `Staff`, `Customer` extend `Person` |
| **Encapsulation** | Private fields with getters/setters |
| **Polymorphism** | `getRole()` differs per subclass; login builds the right one |
| **Singleton** | `CafeteriaManager` coordinates all data access |
| **Composition** | `Order` is composed of `OrderItem` objects |

Plus a JDBC **transaction** in `placeOrder()` (order + items commit together or roll back).

---

## 📁 Project Structure
```
Cafeteria_Preorder_System/
├── src/
│   ├── Main.java
│   ├── models/     # Person, Admin, Staff, Customer, MenuItem, Order, OrderItem
│   ├── managers/   # CafeteriaManager (Singleton + JDBC)
│   ├── utils/      # DBConnection (auto-creates SQLite DB)
│   └── gui/        # LoginFrame, MainFrame, components/UITheme
├── database/
│   └── schema.sql  # Reference only — the app builds the DB itself
├── lib/            # Put sqlite-jdbc-x.x.x.jar here (one download)
├── run.bat / run.sh
└── README.md
```
> A `cafeteria.db` file appears automatically the first time you run it. That's your database. Delete it to reset everything.

---

## 🚀 Run in VS Code (3 small steps)

### 1. Get the SQLite JDBC driver (one download)
Easiest — paste this into the VS Code PowerShell terminal from the project folder:
```powershell
Invoke-WebRequest -Uri "https://github.com/xerial/sqlite-jdbc/releases/download/3.46.1.3/sqlite-jdbc-3.46.1.3.jar" -OutFile "lib\sqlite-jdbc-3.46.1.3.jar"
```
(Or download it manually from https://github.com/xerial/sqlite-jdbc/releases and drop the `.jar` into `lib\`.)

Verify it landed:
```powershell
Get-ChildItem lib -Filter *.jar | Select-Object Name
```

### 2. (For the Run button) add the jar to Referenced Libraries
In VS Code's **Java Projects** panel → **Referenced Libraries** → **+** → pick the jar in `lib\`.
This makes the ▶ Run button above `main()` in `Main.java` work.

### 3. Run
**Run button:** open `src/Main.java`, click ▶ Run.

**Or the script:**
```powershell
.\run.bat
```

**Or manually** (match the jar version):
```powershell
javac -d out -cp "lib/sqlite-jdbc-3.46.1.3.jar" -sourcepath src src/Main.java src/models/*.java src/managers/*.java src/utils/*.java src/gui/*.java src/gui/components/*.java
java -cp "out;lib/sqlite-jdbc-3.46.1.3.jar" Main
```
On macOS/Linux use `:` instead of `;` in the second command.

That's it — no MySQL, no server, no password, no schema step.

---

## 🔑 Default Logins
| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Administrator |
| `staff` | `staff123` | Staff |
| `shreyo` | `pass123` | Customer |
| `rohit` | `pass123` | Customer |

---

## 🧪 Demo Flow
1. Log in as **shreyo** → *Place Order* → add items → pick slot → **Place Order**.
2. Log in as **staff** → *Kitchen Orders* → set status to PREPARING / READY.
3. Log in as **admin** → mark order COMPLETED → *Dashboard* revenue updates.

---

## 🛠️ Troubleshooting
| Problem | Fix |
|---------|-----|
| "Database error" dialog | The sqlite-jdbc jar isn't in `lib/`. Add it (step 1). |
| `ClassNotFoundException: org.sqlite.JDBC` | Same — jar not on classpath / not in Referenced Libraries. |
| Run button greyed out | Add the jar to Referenced Libraries (step 2). |
| Want a fresh database | Close the app and delete `cafeteria.db`. |

## 📄 License
Educational use (OOP / DBMS mini-project).
