package src;

import java.util.*;
import java.sql.*;


interface Rentable {
    void rent() throws VehicleUnavailableException;
    void returnVehicle() throws VehicleUnavailableException;
    double calculatePrice(int days);
}


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

class Car extends Vehicle {

    private String model;

    public Car(String vehicleId, String brand, String model, double basePricePerDay) {
        super(vehicleId, brand, basePricePerDay);
        this.model = model;
    }

    @Override
    public double calculatePrice(int days) {
        return basePricePerDay * days;
    }
}

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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

class RentalService {

    private List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void loadVehiclesFromDB() {

    try {

        Connection con = DBConnection.getConnection();

        String query = "SELECT * FROM vehicles";

        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {

            String id = rs.getString("vehicle_id");
            String brand = rs.getString("brand");
            String model = rs.getString("model");
            String type = rs.getString("type");
            double price = rs.getDouble("price_per_day");

            if (type.equalsIgnoreCase("PREMIUM")) {

                vehicles.add(new PremiumCar(id, brand, model, price));

            } else {

                vehicles.add(new Car(id, brand, model, price));
            }
        }

    } catch (Exception e) {

        System.out.println("Error loading vehicles from DB");
        e.printStackTrace();
    }
}



    public void showAvailableVehicles() {

    for (Vehicle v : vehicles) {

        if (v.isAvailable()) {

            if (v instanceof PremiumCar) {
                System.out.println(v.getVehicleId() + " - " + v.getBrand() + " (Premium)");
            } else {
                System.out.println(v.getVehicleId() + " - " + v.getBrand() + " (Car)");
            }

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

            saveToDatabase(rental);

            System.out.println("Rental Successful!");
            System.out.println("Total Price: $" + price);

        }

        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveToDatabase(Rental rental) {

        try {

            Connection con = DBConnection.getConnection();

            String query = "INSERT INTO rentals(vehicle_id, brand, customer_name, days, total_price) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, rental.getVehicle().getVehicleId());
            ps.setString(2, rental.getVehicle().getBrand());
            ps.setString(3, rental.getCustomer().getName());
            ps.setInt(4, rental.getDays());
            ps.setDouble(5, rental.getTotalPrice());

            ps.executeUpdate();

            System.out.println("Rental stored in database!");

        }

        catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public void returnVehicle(String id) {

        try {

            Vehicle vehicle = findVehicle(id);

            vehicle.returnVehicle();

            System.out.println("Vehicle returned successfully.");

        }

        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

public class Main {

    public static void main(String[] args) {

        RentalService service = new RentalService();
        Scanner sc = new Scanner(System.in);

        service.loadVehiclesFromDB();

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