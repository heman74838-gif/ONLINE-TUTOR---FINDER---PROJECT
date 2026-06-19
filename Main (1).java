import java.util.*;
import java.io.*;

// ===================== INTERFACES =====================
interface Rateable {
    void addRating(int rating);
    double getAverageRating();
}

// ===================== ABSTRACT CLASS =====================
abstract class User {
    private int id;
    private String name;
    private String email;
    private String password;

    public User() {                                  // default constructor
        this.id = 0; this.name = ""; this.email = ""; this.password = "";
    }
    public User(int id, String name, String email, String password) {  // parameterized
        this.id = id; this.name = name; this.email = email; this.password = password;
    }

    // Encapsulation: getters & setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setName(String name) { this.name = name; }

    public abstract String getRole();   // Abstraction

    public void displayProfile() {      // will be overridden -> Polymorphism
        System.out.println("Name: " + name + " | Email: " + email + " | Role: " + getRole());
    }
}

// ===================== TUTOR (Inheritance) =====================
class Tutor extends User implements Rateable {
    private String subject;
    private String area;
    private double feePerHour;
    private ArrayList<Integer> ratings = new ArrayList<>();

    public Tutor(int id, String name, String email, String password,
                 String subject, String area, double feePerHour) {
        super(id, name, email, password);
        this.subject = subject; this.area = area; this.feePerHour = feePerHour;
    }

    public String getSubject() { return subject; }
    public String getArea() { return area; }
    public double getFeePerHour() { return feePerHour; }
    public void setFeePerHour(double f) { this.feePerHour = f; }

    @Override
    public String getRole() { return "Tutor"; }

    @Override
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) ratings.add(rating);
        else System.out.println("Rating must be between 1-5.");
    }

    @Override
    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        int sum = 0;
        for (int r : ratings) sum += r;        // for loop
        return (double) sum / ratings.size();
    }

    @Override
    public void displayProfile() {             // Method Overriding (Polymorphism)
        super.displayProfile();
        System.out.printf("Subject: %s | Area: %s | Fee/hr: Rs.%.0f | Rating: %.1f%n",
            subject, area, feePerHour, getAverageRating());
    }

    public String toFileString() {
        return getId() + "," + getName() + "," + getEmail() + "," + getPassword()
               + "," + subject + "," + area + "," + feePerHour;
    }
}

// ===================== STUDENT (Inheritance) =====================
class Student extends User {
    private String area;

    public Student(int id, String name, String email, String password, String area) {
        super(id, name, email, password);
        this.area = area;
    }

    public String getArea() { return area; }

    @Override
    public String getRole() { return "Student"; }

    public String toFileString() {
        return getId() + "," + getName() + "," + getEmail() + "," + getPassword() + "," + area;
    }
}

// ===================== FEE CALCULATOR (Overloading + Recursion) =====================
class FeeCalculator {
    // Method Overloading
    public static double calculate(double feePerHour, int hours) {
        return feePerHour * hours;
    }
    public static double calculate(double feePerHour, int hours, double discountPercent) {
        double total = feePerHour * hours;
        return total - (discountPercent / 100) * total;
    }

    // Recursive function
    public static double projectFeeGrowth(double fee, int months) {
        if (months == 0) return fee;                       // base case
        return projectFeeGrowth(fee * 1.05, months - 1);    // recursive call
    }
}

// ===================== CUSTOM EXCEPTION =====================
class ValidationException extends Exception {
    public ValidationException(String msg) { super(msg); }
}

// ===================== FILE HANDLER =====================
class FileHandler {
    static final String TUTORS_FILE = "tutors.txt";
    static final String STUDENTS_FILE = "students.txt";

    public static void saveTutor(Tutor t) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TUTORS_FILE, true))) {
            pw.println(t.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving tutor: " + e.getMessage());
        }
    }

    public static void saveStudent(Student s) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENTS_FILE, true))) {
            pw.println(s.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }

    public static ArrayList<Tutor> loadTutors() {
        ArrayList<Tutor> list = new ArrayList<>();
        File f = new File(TUTORS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {        // while loop
                if (line.trim().isEmpty()) continue;          // continue
                String[] p = line.split(",");
                list.add(new Tutor(Integer.parseInt(p[0]), p[1], p[2], p[3],
                                   p[4], p[5], Double.parseDouble(p[6])));
            }
        } catch (IOException e) {
            System.out.println("Error loading tutors: " + e.getMessage());
        }
        return list;
    }
}

// ===================== MAIN CLASS =====================
public class Main {
    static ArrayList<Tutor> tutors = new ArrayList<>();
    static ArrayList<Student> students = new ArrayList<>();
    static int tutorId = 1, studentId = 1;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        tutors = FileHandler.loadTutors();
        if (tutors.isEmpty()) seedData();
        else tutorId = tutors.size() + 1;

        System.out.println("=== Tuition / Home Tutor Finder System ===");
        boolean running = true;

        while (running) {                       // main menu loop
            System.out.println("\n1. Register Tutor\n2. Register Student\n3. Browse Tutors"
                + "\n4. Search by Subject\n5. Sort by Fee\n6. Rate a Tutor"
                + "\n7. Fee Calculator\n0. Exit");
            System.out.print("Choice: ");
            int choice = readInt();

            switch (choice) {                   // switch statement
                case 1: registerTutor(); break;
                case 2: registerStudent(); break;
                case 3: browseTutors(); break;
                case 4: searchBySubject(); break;
                case 5: sortByFee(); break;
                case 6: rateTutor(); break;
                case 7: feeCalculatorMenu(); break;
                case 0: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }

    // ---------- Seed demo data ----------
    static void seedData() {
        Tutor t1 = new Tutor(tutorId++, "Ali Hassan", "ali@email.com", "pass123", "Mathematics", "Gulshan", 500);
        Tutor t2 = new Tutor(tutorId++, "Sara Khan", "sara@email.com", "pass123", "Physics", "DHA", 600);
        Tutor t3 = new Tutor(tutorId++, "Bilal Ahmed", "bilal@email.com", "pass123", "English", "Nazimabad", 400);
        t1.addRating(5); t1.addRating(4);
        t2.addRating(4); t2.addRating(5);
        t3.addRating(3);
        tutors.add(t1); tutors.add(t2); tutors.add(t3);
        for (Tutor t : tutors) FileHandler.saveTutor(t);
    }

    // ---------- Register Tutor ----------
    static void registerTutor() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        System.out.print("Subject: "); String subject = sc.nextLine();
        System.out.print("Area: "); String area = sc.nextLine();
        System.out.print("Fee per hour: ");
        double fee = 0;

        try {
            fee = Double.parseDouble(sc.nextLine());
            if (!email.contains("@")) throw new ValidationException("Invalid email format.");
            if (fee < 0) throw new ValidationException("Fee cannot be negative.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage()); return;
        } catch (NumberFormatException e) {
            System.out.println("Fee must be a number."); return;
        } finally {
            System.out.println("Registration attempt finished.");
        }

        Tutor t = new Tutor(tutorId++, name, email, pass, subject, area, fee);
        tutors.add(t);
        FileHandler.saveTutor(t);
        System.out.println("Tutor registered successfully! ID: " + t.getId());
    }

    // ---------- Register Student ----------
    static void registerStudent() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        System.out.print("Area: "); String area = sc.nextLine();

        Student s = new Student(studentId++, name, email, pass, area);
        students.add(s);
        FileHandler.saveStudent(s);
        System.out.println("Student registered successfully! ID: " + s.getId());
    }

    // ---------- Browse Tutors ----------
    static void browseTutors() {
        if (tutors.isEmpty()) { System.out.println("No tutors available."); return; }
        int i = 1;
        for (Tutor t : tutors) {                // for-each loop
            System.out.print(i++ + ". ");
            t.displayProfile();                 // polymorphic call
        }
    }

    // ---------- Search by Subject (String handling) ----------
    static void searchBySubject() {
        System.out.print("Enter subject: ");
        String sub = sc.nextLine();
        boolean found = false;

        int i = 0;
        do {                                     // do-while loop
            Tutor t = tutors.get(i);
            if (t.getSubject().toLowerCase().contains(sub.toLowerCase())) {
                t.displayProfile();
                found = true;
            }
            i++;
        } while (i < tutors.size());

        if (!found) System.out.println("No tutors found for: " + sub);
    }

    // ---------- Sort by Fee (Bubble Sort - nested loop) ----------
    static void sortByFee() {
        int n = tutors.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (tutors.get(j).getFeePerHour() > tutors.get(j + 1).getFeePerHour()) {
                    Tutor temp = tutors.get(j);
                    tutors.set(j, tutors.get(j + 1));
                    tutors.set(j + 1, temp);
                }
            }
        }
        System.out.println("Tutors sorted by fee (low to high):");
        browseTutors();
    }

    // ---------- Rate a Tutor ----------
    static void rateTutor() {
        browseTutors();
        System.out.print("Enter Tutor ID to rate: ");
        int id = readInt();

        for (Tutor t : tutors) {
            if (t.getId() == id) {
                System.out.print("Rating (1-5): ");
                int rating = readInt();
                t.addRating(rating);
                System.out.println("Rating added!");
                return;
            }
        }
        System.out.println("Tutor not found.");
    }

    // ---------- Fee Calculator (overloading + recursion demo) ----------
    static void feeCalculatorMenu() {
        System.out.print("Fee per hour: "); double fee = Double.parseDouble(sc.nextLine());
        System.out.print("Hours: "); int hours = readInt();
        System.out.print("Apply discount? (y/n): ");
        String ans = sc.nextLine();

        if (ans.equalsIgnoreCase("y")) {
            System.out.print("Discount %: "); double disc = Double.parseDouble(sc.nextLine());
            System.out.printf("Total: Rs. %.2f%n", FeeCalculator.calculate(fee, hours, disc));
        } else {
            System.out.printf("Total: Rs. %.2f%n", FeeCalculator.calculate(fee, hours));
        }

        System.out.print("Project fee growth for how many months? ");
        int months = readInt();
        double projected = FeeCalculator.projectFeeGrowth(fee, months);
        System.out.printf("Projected fee after %d months: Rs. %.2f%n", months, projected);
    }

    // ---------- Helper: safe integer input ----------
    static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, using 0.");
            return 0;
        }
    }
}
