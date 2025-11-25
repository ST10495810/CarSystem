/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package iierosebankcollege.carsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Bongumusa Maseko
 */
public class CarSystemIT {
    
    public CarSystemIT() {
    }

     private CarSystem system;

    @BeforeEach
    public void setup() {
        system = new CarSystem();
        // Clear lists so each test starts clean
        system.vehicles.clear();
        system.customers.clear();
        system.rentals.clear();
        system.sales.clear();
        system.maints.clear();

        system.nextVehicleId = 1;
        system.nextCustomerId = 1;
        system.nextRentalId = 1;
        system.nextSaleId = 1;
        system.nextMaintId = 1;
    }

//vihecle test
    @Test
    public void testAddVehicle() {
        system.addVehicle("Toyota", "Corolla", 2020, "White", 150000);

        assertEquals(1, system.vehicles.size());
        assertEquals("Toyota", system.vehicles.get(0).make);
        assertEquals("Available", system.vehicles.get(0).status);
    }

    @Test
    public void testDeleteVehicle() {
        system.addVehicle("BMW", "M4", 2025, "Black", 1600000);
        boolean deleted = system.deleteVehicle(1);

        assertTrue(deleted);
        assertEquals(0, system.vehicles.size());
    }

    @Test
    public void testDeleteVehicleInvalid() {
        boolean deleted = system.deleteVehicle(99);
        assertFalse(deleted);
    }

   // customer test
    @Test
    public void testAddCustomer() {
        system.addCustomer("Bongumusa", "0825253163", "bongumusawellington9@gmail.com", "1200 Street");
        assertEquals(1, system.customers.size());
        assertEquals("Bongumusa", system.customers.get(0).name);
    }

    @Test
    public void testDeleteCustomer() {
        system.addCustomer("Thando", "0795116854", "thando22@gmail.com", "Nelspruit");
        boolean deleted = system.deleteCustomer(1);

        assertTrue(deleted);
        assertEquals(0, system.customers.size());
    }

    // rental test
    @Test
    public void testAddRentalSuccess() {
        system.addVehicle("BMW", "320i", 2022, "Blue", 5000000);
        system.addCustomer("Sam", "0823163452", "sam@mail.com", "Cape Town");

        boolean ok = system.addRental(1, 1, "2025-03-12", "2025-03-18", 4000);

        assertTrue(ok);
        assertEquals("Rented", system.vehicles.get(0).status);
        assertEquals(1, system.rentals.size());
    }

    @Test
    public void testAddRentalVehicleUnavailable() {
        system.addVehicle("Audi", "A5", 2018, "Silver", 250000);
        system.vehicles.get(0).status = "Rented";

        system.addCustomer("STones", "0712781032", "stone12@mail.com", "Free State");

        boolean ok = system.addRental(1, 1, "2025-04-01", "2025-04-05", 3000);

        assertFalse(ok);
        assertEquals(0, system.rentals.size());
    }

    @Test
    public void testCloseRental() {
        system.addVehicle("VW", "Golf 7", 2021, "Red", 300000);
        system.addCustomer("Tinothenda", "0723645123", "tino@mail.com", "Johannesburg");

        system.addRental(1, 1, "2025-02-01", "2025-02-05", 1000);

        boolean closed = system.closeRental(1);

        assertTrue(closed);
        assertEquals("Available", system.vehicles.get(0).status);
        assertEquals(0, system.rentals.size());
    }

    // sales test
    @Test
    public void testAddSaleSuccess() {
        system.addVehicle("Mercedes", "C63", 2022, "Black", 6000000);
        system.addCustomer("Watts", "0602654378", "Watts@mail.com", "Petoria");

        boolean ok = system.addSale(1, 1, "2025-03-15", 580000);

        assertTrue(ok);
        assertEquals("Sold", system.vehicles.get(0).status);
        assertEquals(1, system.sales.size());
    }

    @Test
    public void testAddSaleVehicleUnavailable() {
        system.addVehicle("Ford", "Ranger", 2020, "Grey", 400000);
        system.vehicles.get(0).status = "Rented";

        system.addCustomer("Malwande", "0712324556", "jake@mail.com", "Nelspruit");

        boolean ok = system.addSale(1, 1, "2025-05-10", 380000);

        assertFalse(ok);
        assertEquals(0, system.sales.size());
    }

    //maintanence test
    @Test
    public void testAddMaintenance() {
        system.addVehicle("Nissan", "NP200", 2017, "White", 120000);

        boolean ok = system.addMaint(1, "Engine Service", "2025-05-13", 1500, "Joy");

        assertTrue(ok);
        assertEquals("Maintenance", system.vehicles.get(0).status);
        assertEquals(1, system.maints.size());
    }

    @Test
    public void testAddMaintenanceInvalidVehicle() {
        boolean ok = system.addMaint(10, "Oil Change", "2025-05-20", 550, "Nduiso");

        assertFalse(ok);
        assertEquals(0, system.maints.size());
    }
}

