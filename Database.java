import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;

public class Database {
    private String name; // Database name
    private ArrayList<Physician> physicians; // list of Physicians in Database
    private ArrayList<Patient> patients; // list of Patients in Database
    private Admin admin; // Admin object for Database

    // solo constructor
    public Database(String name, String adminID) {
        this.name = name;
        this.physicians = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.admin = new Admin(adminID, this);
    }

    // Database start procedure
    public void start() {
        // welcome prompt + query for ID
        String input = query(String.format("Welcome to %s! Please enter your ID.", this.name));
        // check if ID exists in Database and start relevant session (physician, patient, admin)
        int valid = this.checkExistsAndStart(input);
        while(valid == 0) {
            input = query("I'm sorry, that was not a valid ID. Please enter your ID.");
            valid = this.checkExistsAndStart(input);
        }
    }

    // helper method to handle user querying
    public static String query(String prompt) {
        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    // helper method to handle user querying (in bullets)
    public static String bulletQuery(ArrayList<String> prompt) {
        for(String item : prompt) {System.out.println(item);}
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    // helper method to check if ID exists in Database and start relevant session (physician, patient, admin)
    private int checkExistsAndStart(String input) {
        if(this.admin.getID().equals(input)) {
            this.admin.start();
            return 1;
        }
        ArrayList<String> physicianIDs = getPhysicianIDs();
        if(physicianIDs.contains(input)) {
            this.physicians.get(physicianIDs.indexOf(input)).start();
            return 1;
        }
        ArrayList<String> patientIDs = getPatientIDs();
        if(patientIDs.contains(input)) {
            this.patients.get(patientIDs.indexOf(input)).start();
            return 1;
        }
        return 0;
    }

    // getter for this.physicians IDs
    ArrayList<String> getPhysicianIDs() {
        ArrayList<String> ids = new ArrayList<>();
        for(Physician item : this.physicians) {ids.add(item.getID());}
        return ids;
    }

    // getter for this.patients IDs
    ArrayList<String> getPatientIDs() {
        ArrayList<String> ids = new ArrayList<>();
        for(Patient item : this.patients) {ids.add(item.getID());}
        return ids;
    }

    // helper method to access specific Patient in Database
    Patient getPatient(String id) {
        ArrayList<String> patientIDs = this.getPatientIDs();
        return this.patients.get(patientIDs.indexOf(id));
    }

    // helper methods to add and remove Patients and Physicians from the Database
    void addPatient(String id) {this.patients.add(new Patient(id, this));}
    void removePatient(String id) {
        ArrayList<String> patientIDs = this.getPatientIDs();
        this.patients.remove(patientIDs.indexOf(id));
    }
    void addPhysician(String id) {this.physicians.add(new Physician(id, this));}
    void removePhysician(String id) {
        ArrayList<String> physicianIDs = this.getPhysicianIDs();
        this.physicians.remove(physicianIDs.indexOf(id));
    }
}

class Patient {
    String id; // unique Patient ID
    Database database; // Database that Patient belongs to
    HashMap<String, Chron> chronicle; // HashMap with key : String representation of when Chron was input, value : the Chron object itself
    ArrayList<String> contents; // list of keys for each Chron stored in this.chronicle

    // solo constructor
    Patient(String id, Database database) {
        this.id = id;
        this.database = database;
        chronicle = new HashMap<>();
        contents = new ArrayList<>();
    }

    // Patient start procedure
    void start() {
        // welcome message
        System.out.println(String.format("Welcome, User %s", this.id));
        // presentation of action options
        String input = "";
        // loop until logout
        while(!input.equals("4")) {
            input = Database.bulletQuery(new ArrayList<>(Arrays.asList("Enter '1' to add a Chron", "Enter '2' to delete a Chron", "Enter '3' to view all Chrons", "Enter '4' to log out")));
            // checking for valid input
            ArrayList<String> options = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
            if(options.contains(input)) {
                // actions
                if(input.equals("1")) {
                    // adds a new Chron with text entry
                    this.addChron();
                    System.out.println("Success.");
                } else if(input.equals("2")) {
                    // deletes an existing Chron from this.chronicle
                    if(this.contents.size() == 0) {
                        System.out.println("-Empty-");
                    } else {
                        for(String item : this.contents) {
                            System.out.println(item);
                        }
                    }
                    input = Database.query("Which Chron would you like to delete? Please enter the identifying text shown above exactly.");
                    if(this.contents.contains(input)) {
                        this.chronicle.remove(input);
                        this.contents.remove(this.contents.indexOf(input));
                        System.out.println("Success.");
                    } else {
                        System.out.println("Delete unsuccessful.");
                    }
                } else if(input.equals("3")) {
                    // display all Chrons for Patient
                    this.viewChronicle();
                } else {
                    System.out.println("Logging out. Thank you for using our systems.");
                }
            } else {
                System.out.println("I'm sorry, that was not a valid input.");
            }
        }
    }

    // getter for Patient ID
    String getID() {return this.id;}

    // helper method to add Chron to chronicle
    void addChron() {
        String entry = Database.query("Please enter your Chron entry, then press enter/return.");
        Chron chron = new Chron(entry, this.id);
        String chronWhen = chron.getWhenEntry();
        this.chronicle.put(chronWhen, chron);
        this.contents.add(chronWhen);
    }

    // overloaded helper method to add Chron to chronicle (if added by Physician, not Patient)
    void addChron(String physicianID) {
        String entry = Database.query("Please enter your Chron entry, then press enter/return.");
        Chron chron = new Chron(entry, physicianID);
        String chronWhen = chron.getWhenEntry();
        this.chronicle.put(chronWhen, chron);
        this.contents.add(chronWhen);
    }

    // helper method to display chronicle
    void viewChronicle() {
        if(this.contents.size() == 0) {
            System.out.println("-Empty-");
        } else {
            for(String item : this.contents) {
                System.out.println(item + " // " + this.chronicle.get(item).getEntry());
            }
        }
    }
}

class Physician {
    private String id; // unique Physician ID
    private Database database; // Database that Physician belongs to
    private ArrayList<String> roster; // list of Patient IDs on Physician's roster

    // solo constructor
    Physician(String id, Database database) {
        this.id = id;
        this.database = database;
        roster = new ArrayList<>();
    }

    // Physician start procedure
    void start() {
        // welcome message
        System.out.println(String.format("Welcome, User %s", this.id));
        // presentation of action options
        String input = "";
        // loop until logout
        while(!input.equals("6")) {
            input = Database.bulletQuery(new ArrayList<>(Arrays.asList("Enter '1' to view your roster", "Enter '2' to add an existing patient to your roster", "Enter '3' to delete a patient from your roster", "Enter '4' to add a Chron for a patient", "Enter '5' to view a patient's chronicle", "Enter '6' to log out")));
            // checking for valid input
            ArrayList<String> options = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));
            if(options.contains(input)) {
                // actions
                if(input.equals("2")) {
                    // adds an existing Patient to this.roster
                    input = Database.query("Please enter the ID of the patient to add to your roster.");
                    ArrayList<String> patientIDs = database.getPatientIDs();
                    if(patientIDs.contains(input) && !this.roster.contains(input)) {
                        this.roster.add(input);
                        System.out.println("Success.");
                    } else {System.out.println("Add unsuccessful.");}
                } else if(input.equals("3")) {
                    // deletes a Patient from this.roster
                    input = Database.query("Please enter the ID of the patient to delete from your roster.");
                    if(this.roster.contains(input)) {
                        this.roster.remove(this.roster.indexOf(input));
                        System.out.println("Success.");
                    } else {System.out.println("Delete unsuccessful.");}
                } else if(input.equals("4")) {
                    // adds a Chron for a specified Patient
                    input = Database.query("Please enter the ID of the patient for which you would like to add a Chron.");
                    if(this.roster.contains(input)) {
                        Patient patient = database.getPatient(input);
                        patient.addChron(this.id);
                        System.out.println("Success.");
                    } else {
                        System.out.println("Unsuccessful.");
                    }
                } else if(input.equals("5")) {
                    // displays chronicle for a specified Patient
                    input = Database.query("Please enter the ID of the patient whose chronicle to display.");
                    if(this.roster.contains(input)) {
                        Patient patient = database.getPatient(input);
                        patient.viewChronicle();
                    } else {
                        System.out.println("Unsuccessful.");
                    }
                } else if(input.equals("1")) {
                    // displays Physician's roster
                    if(this.roster.size() == 0) {
                        System.out.println("-Empty-");
                    } else {
                        for(String item : this.roster) {System.out.println(item);}
                    }
                } else {
                    System.out.println("Logging out. Thank you for using our systems.");
                }
            } else {System.out.println("I'm sorry, that was not a valid input.");}
        }
    }

    // getter for Physician ID
    String getID() {return this.id;}
}

class Admin {
    private String id; // unique Admin ID
    private Database database; // Database that Admin belongs to

    // solo constructor
    Admin(String id, Database database) {
        this.id = id;
        this.database = database;
    }

    void start() {
        // welcome message
        System.out.println(String.format("Welcome, User %s", this.id));
        // presentation of action options
        String input = "";
        // loop until logout
        while(!input.equals("5")) {
            input = Database.bulletQuery(new ArrayList<>(Arrays.asList("Enter '1' to add a physician to the system", "Enter '2' to remove a physician from the system", "Enter '3' to add a patient to the system", "Enter '4' to remove a patient from the system", "Enter '5' to log out")));
            // checking for valid input
            ArrayList<String> options = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
            if(options.contains(input)) {
                // actions
                if(input.equals("3")) {
                    // adds a new Patient to the system
                    input = Database.query("Please enter the new patient's ID.");
                    ArrayList<String> patientIDs = this.database.getPatientIDs();
                    ArrayList<String> physicianIDs = this.database.getPhysicianIDs();
                    if(!patientIDs.contains(input) && !physicianIDs.contains(input)) {
                        this.database.addPatient(input);
                        System.out.println("Success.");
                    } else {
                        System.out.println("Add unsuccessful.");
                    }
                } else if(input.equals("4")) {
                    // deletes a Patient from the system
                    input = Database.query("Please enter the ID of the patient to remove.");
                    ArrayList<String> patientIDs = this.database.getPatientIDs();
                    if(patientIDs.contains(input)) {
                        this.database.removePatient(input);
                        System.out.println("Success.");
                    } else {
                        System.out.println("Remove unsuccessful.");
                    }
                } else if(input.equals("1")) {
                    // adds a new Physician to the system
                    input = Database.query("Please enter the new physician's ID.");
                    ArrayList<String> physicianIDs = this.database.getPhysicianIDs();
                    ArrayList<String> patientIDs = this.database.getPatientIDs();
                    if(!physicianIDs.contains(input) && !patientIDs.contains(input)) {
                        this.database.addPhysician(input);
                        System.out.println("Success.");
                    } else {
                        System.out.println("Add unsuccessful.");
                    }
                } else if(input.equals("2")) {
                    // deletes a Physician from the system
                    input = Database.query("Please enter the ID of the physician to remove.");
                    ArrayList<String> physicianIDs = this.database.getPhysicianIDs();
                    if(physicianIDs.contains(input)) {
                        this.database.removePhysician(input);
                        System.out.println("Success.");
                    } else {
                        System.out.println("Remove unsuccessful.");
                    }
                } else {
                    System.out.println("Logging out. Thank you for using our systems.");
                }
            } else {System.out.println("I'm sorry, that was not a valid input.");}
        }
    }

    // getter for Admin ID
    String getID() {return this.id;}
}