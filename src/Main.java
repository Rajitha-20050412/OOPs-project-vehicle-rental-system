package src;
import java.util.*;
import java.io.*;

// ================= INTERFACE =================
interface Rentable {
    void rent() throws VehicleUnavailableException;
    void returnVehicle() throws VehicleUnavailableException;
    double calculatePrice(int days);
}

// ================= CUSTOM EXCEPTIONS =================
class VehicleNotFoundException extends Exception {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

class VehicleUnavailableException extends Exception {
    public VehicleUnavailableException(String message) {
        super(message);
    }
}

// ================= ABSTRACT CLASS =================
abstract class Vehicle implements Rentable {

    protected String vehicleId;
    protected String brand;
    protected double basePricePerDay;
    protected boolean isAvailable;

    public Vehicle(String vehicleId, String brand, double basePricePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public void rent() throws VehicleUnavailableException {
        if (!isAvailable)
            throw new VehicleUnavailableException("Vehicle already rented.");
        isAvailable = false;
    }

    @Override
    public void returnVehicle() throws VehicleUnavailableException {
        if (isAvailable)
            throw new VehicleUnavailableException("Vehicle was not rented.");
        isAvailable = true;
    }

    public abstract double calculatePrice(int days);
}

// ================= CAR =================
class Car extends Vehicle {

    private String model;

    public Car(String vehicleId, String brand, String model, double basePricePerDay) {
        super(vehicleId, brand, basePricePerDay);
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    @Override
    public double calculatePrice(int days) {
        return basePricePerDay * days;
    }
}

// ================= PREMIUM CAR =================
class PremiumCar extends Car {

    public PremiumCar(String vehicleId, String brand, String model, double basePricePerDay) {
        super(vehicleId, brand, model, basePricePerDay);
    }

    @Override
    public double calculatePrice(int days) {
        double base = super.calculatePrice(days);
        return base + (base * 0.25);
    }
}

// ================= CUSTOMER =================
class Customer {
    private String customerId;
    private String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

// ================= RENTAL =================
class Rental {
    private Vehicle vehicle;
    private Customer customer;
    private int days;
    private double totalPrice;

    public Rental(Vehicle vehicle, Customer customer, int days, double totalPrice) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.days = days;
        this.totalPrice = totalPrice;
    }

    public String toString() {
        return "Customer: " + customer.getName() +
               ", Vehicle ID: " + vehicle.getVehicleId() +
               ", Brand: " + vehicle.getBrand() +
               ", Days: " + days +
               ", Total Price: $" + totalPrice;
    }
}

// ================= SERVICE =================
class RentalService {

    private List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void showAvailableVehicles() {
        for (Vehicle v : vehicles) {
            if (v.isAvailable()) {
                System.out.println(v.getVehicleId() + " - " + v.getBrand());
            }
        }
    }

    public Vehicle findVehicle(String id) throws VehicleNotFoundException {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equals(id)) {
                return v;
            }
        }
        throw new VehicleNotFoundException("Vehicle ID not found.");
    }

    public void rentVehicle(String id, Customer customer, int days) {
        try {
            Vehicle vehicle = findVehicle(id);
            vehicle.rent();
            double price = vehicle.calculatePrice(days);

            Rental rental = new Rental(vehicle, customer, days, price);

            saveToFile(rental);

            System.out.println("Rental Successful!");
            System.out.println("Total Price: $" + price);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveToFile(Rental rental) {
        try {
            FileWriter fw = new FileWriter("rentals.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(rental.toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    public void returnVehicle(String id) {
        try {
            Vehicle vehicle = findVehicle(id);
            vehicle.returnVehicle();
            System.out.println("Vehicle returned successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// ================= MAIN =================
public class Main {

    public static void main(String[] args) {

        RentalService service = new RentalService();
        Scanner sc = new Scanner(System.in);

        service.addVehicle(new Car("C101", "Toyota", "Camry", 60));
        service.addVehicle(new PremiumCar("C202", "BMW", "X5", 120));

        int choice;

        do {
            System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
            System.out.println("1. Show Available Vehicles");
            System.out.println("2. Rent Vehicle");
            System.out.println("3. Return Vehicle");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    service.showAvailableVehicles();
                    break;

                case 2:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    Customer customer = new Customer("CUS" + new Random().nextInt(1000), name);

                    System.out.print("Enter Vehicle ID: ");
                    String id = sc.nextLine();

                    System.out.print("Enter Days: ");
                    int days = sc.nextInt();

                    service.rentVehicle(id, customer, days);
                    break;

                case 3:
                    System.out.print("Enter Vehicle ID: ");
                    String returnId = sc.nextLine();
                    service.returnVehicle(returnId);
                    break;

                case 4:
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 4);

        sc.close();
    }
}