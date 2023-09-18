package contacts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Utils {
    // Class with methods to verify input
    public static boolean isNumberValid(String phoneNumber) {
        if (!hasNumber(phoneNumber)) return false;

        String regex = "^\\+?(\\(\\w+\\)|\\w+[ -]\\(\\w{2,}\\)|\\w+)([ -]\\w{2,})*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean hasNumber(String phoneNumber) {
        return phoneNumber != null;
    }

    public static boolean isBirthDateValid(String birthDate) {
        // I don't yet know the format of the birthdate
        return !birthDate.equals("") && birthDate != null;
    }

    public static boolean isGenderValid(String gender) {
        return gender.equals("M") || gender.equals("F");
    }
}

class PhoneBook extends ArrayList<Record> {
    Scanner scanner = new Scanner(System.in);

    public void enterAction() {


        String command = "";
        while (!command.equals("exit")) {
            System.out.println("\n[menu] Enter action (add, list, search, count, exit):");
            command = scanner.nextLine();
            switch (command) {
                case "count":
                    count();
                    break;
                case "add":
                    add();
                    break;
                case "list":
                    list();
                    break;
                case "search":
                    search();
                    break;
                case "exit":
                    break;
                default:
                    System.out.println("Enter valid action!\n");
                    break;
            }

        }
    }

    public void search() {
        System.out.println("Enter search query: ");
        String string = scanner.nextLine();

        Pattern pattern = Pattern.compile(string, Pattern.CASE_INSENSITIVE);
        int index = 0;
        String result = "";
        for (Record record : this) {
            Matcher matcher = pattern.matcher(record.toString());
            if (matcher.find()) {
                index++;
                result = result.concat(index + "." + record.getFullName());
            }
        }
        System.out.println("Found " + index + " results: " + result);
        if (index == 0) {
            return;
        }
        String next = scanner.nextLine();
        if (next.equals("back")) return;
        else if (next.equals("again"))
            search();
        else {
            index = Integer.parseInt(next);
            record(this, index);
        }
    }

    public void record(ArrayList<Record> phoneBook, int index) {
        System.out.println(phoneBook.get(index - 1).toString());
        while (true) {
            System.out.println("[record] Enter action (edit, delete, menu): ");
            String command = scanner.nextLine();

            if (command.equals("menu")) {
                return;
            } else if (command.equals("edit")) {
                edit(phoneBook, index);
            } else {
                delete(phoneBook, index);
            }
        }
    }

    public void info() {
        int index = 0;
        for (Record record : this) {
            index++;
            String string;
            if (record instanceof Person) string = ((Person) record).getLastName();
            else string = "";
            System.out.println(index + ". " + record.getName() + " " + string);
        }
        System.out.println("Enter index to show info: ");
        index = Integer.parseInt(scanner.nextLine()) - 1;

        System.out.println(get(index).toString());

    }

    private void list() {
        int index = 0;
        for (Record record : this) {
            index++;
            String string;
            if (record instanceof Person) string = ((Person) record).getLastName();
            else string = "";
            System.out.println(index + ". " + record.getName() + " " + string);
        }
        System.out.println("[list] Enter action ([number], back): ");
        String value = scanner.nextLine();
        if (value.matches("\\d+")) {
            // it's a number
            record(this, Integer.parseInt(value));
        } else {
            return;
        }
    }

    public void edit(ArrayList<Record> phoneBook, int index) {

        index--;
        Record record = phoneBook.get(index);


        System.out.println("Select a field" + record.getFields() + ":");
        String field = scanner.nextLine();
        System.out.println("Enter " + field + ":");
        String value = scanner.nextLine();
        record.setField(field, value);
        System.out.println("Saved");
        System.out.println(record);

        set(index, record);


    }

    public void delete(ArrayList<Record> phoneBook, int index) {

        index--;
        remove(index);
        System.out.println("The record removed!");

    }

    public void count() {
        System.out.println("The Phone Book has " + size() + " records.\n");
    }

    public void add() {
        System.out.println("Enter the type (person, organization): ");
        String command = scanner.nextLine();
        if (command.equals("person")) {
            addPerson();
        } else {
            addOrganization();
        }
    }

    public void addOrganization() {
        System.out.print("Enter the organization name:");
        String name = scanner.nextLine();

        System.out.print("Enter the address:");
        String address = scanner.nextLine();

        System.out.print("Enter the number:");
        String number = scanner.nextLine();

        Organization organization = new Organization(name, address, number);
        this.add(organization);
        System.out.println("The record added.");
    }

    public void addPerson() {
        System.out.print("Enter the name:");
        String name = scanner.nextLine();

        System.out.print("Enter the surname:");
        String surname = scanner.nextLine();

        System.out.println("Enter the birth date:");
        String birthDate = scanner.nextLine();
        if (!Utils.isBirthDateValid(birthDate)) {
            System.out.println("Bad birth date!");
        }

        System.out.println("Enter the gender (M, F):");
        String gender = scanner.nextLine();
        if (!Utils.isGenderValid(gender)) {
            System.out.println("Bad gender!");
        }

        System.out.print("Enter the number:");
        String number = scanner.nextLine();
        if (!Utils.isNumberValid(number)) {
            System.out.println("Wrong number format!");
        }


        Person person = new Person(name, surname, number, birthDate, gender);
        this.add(person);
        System.out.println("The record added.");
    }

}

abstract class Record {
    protected String phoneNumber = null;
    protected LocalDateTime created;
    protected LocalDateTime lastEdit;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (!Utils.isNumberValid(phoneNumber)) {
            this.phoneNumber = "[no number]";
        }
    }

    public void updateLastEdit() {
        lastEdit = LocalDateTime.now();
    }

    public abstract String getName();

    public abstract String getFields();

    public abstract void setField(String field, String value);

    public abstract String getField(String field);

    public abstract String getFullName();
}

class Organization extends Record {

    private String name;
    private String address;

    public Organization(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        if (!Utils.isNumberValid(phoneNumber)) {
            this.phoneNumber = "[no number]";
        }
        created = LocalDateTime.now();
        lastEdit = LocalDateTime.now();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getFields() {
        return " (name, address, number)";
    }

    @Override
    public void setField(String field, String value) {
        switch (field) {
            case "name":
                this.name = value;
                break;
            case "address":
                address = value;
                break;
        }
    }

    @Override
    public String getField(String field) {
        return switch (field) {
            case "name" -> name;
            case "address" -> address;
            default -> null;
        };
    }

    @Override
    public String getFullName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        return String.format("Organization name: %s\nAddress: %s\nNumber: %s\nTime created: %s\nTime last edit: %s\n", name, address, phoneNumber, created, lastEdit);
    }
}

class Person extends Record {

    private String name;
    private String lastName;

    private String gender;
    private String birthDate;

    public Person(String firstName, String lastName, String phoneNumber, String gender, String birthDate) {
        this.name = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        if (!Utils.isNumberValid(phoneNumber)) {
            this.phoneNumber = "[no number]";
        }

        this.gender = gender;
        if (!Utils.isGenderValid(gender)) {
            this.gender = "[no data]";
        }

        this.birthDate = birthDate;
        if (!Utils.isBirthDateValid(birthDate)) {
            this.birthDate = "[no data]";
        }
        created = LocalDateTime.now();
        lastEdit = LocalDateTime.now();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        if (!Utils.isGenderValid(gender)) {
            this.gender = "[no data]";
        }
    }

    public String toString() {
        return String.format("Name: %s\nSurname: %s\nBirth date: %s\nGender: %s\nNumber: %s\nTime created: %s\nTime last edit: %s\n", name, lastName, birthDate, gender, phoneNumber, created, lastEdit);
    }


    public String getName() {
        return name;
    }

    @Override
    public String getFields() {
        return " (name, surname, birth, gender, number)";
    }

    @Override
    public void setField(String field, String value) {
        switch (field) {
            case "name":
                name = value;
                break;
            case "birth":
                birthDate = value;
                break;
            case "surname":
                lastName = value;
                break;
            case "gender":
                gender = value;
                break;
        }
    }

    @Override
    public String getField(String field) {
        switch (field) {
            case "name":
                return name;
            case "birthday":
                return birthDate;
            case "lastName":
                return lastName;
            case "gender":
                return gender;
            default:
                return null;
        }
    }

    @Override
    public String getFullName() {
        return name + " " + lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        if (!Utils.isBirthDateValid(birthDate)) {
            this.birthDate = "[no data]";
        }
    }
}

public class Main {
    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        phoneBook.enterAction();
    }
}
