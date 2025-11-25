/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package iierosebankcollege.carsystem;

/**
 *
 * @author Bongumusa Maseko
 */
import javax.swing.*; 
import javax.swing.table.DefaultTableModel; 
import java.awt.*; 
import java.awt.event.*; 
import java.io.*; 
import java.util.ArrayList; 
import java.util.Date; 
import java.text.SimpleDateFormat;

public class CarSystem extends JFrame implements Serializable{

    // Vehicle model: holds vehicle-related fields
    static class Vehicle implements Serializable {
        private static final long serialVersionUID = 1L; // version for serialization
        int id;                          // unique numeric ID for the vehicle
        String make, model, color;       // make (brand), model name, and color
        int year;                        // manufacture year
        double price;                    // price for sale or rental base
        String status;                   // status: Available / Rented / Sold / Maintenance

        // Constructor: set default status to "Available"
        Vehicle(int id, String make, String model, int year, String color, double price) {
            this.id = id;               // assign ID
            this.make = make;           // assign make
            this.model = model;         // assign model
            this.year = year;           // assign year
            this.color = color;         // assign color
            this.price = price;         // assign price
            this.status = "Available"; // default status when created
        }
    }

    // Customer model
    static class Customer implements Serializable {
        private static final long serialVersionUID = 1L; // serialization version
        int id;                 // unique customer ID
        String name, phone, email, address; // contact details

        // Constructor: set customer properties
        Customer(int id, String name, String phone, String email, String address) {
            this.id = id;         // set ID
            this.name = name;     // set name
            this.phone = phone;   // set phone
            this.email = email;   // set email
            this.address = address; // set address
        }
    }

    // Rental model
    static class Rental implements Serializable {
        private static final long serialVersionUID = 1L; // serialization version
        int id;                 // unique rental ID
        int vehicleId;          // ID of rented vehicle
        int customerId;         // ID of renting customer
        String rentalDate;      // rental start date as string
        String returnDate;      // expected return date as string
        double totalCost;       // total cost charged for the rental

        // Constructor: create a rental record with provided values
        Rental(int id, int vehicleId, int customerId, String rentalDate, String returnDate, double totalCost) {
            this.id = id;                 // set rental ID
            this.vehicleId = vehicleId;   // set vehicle reference
            this.customerId = customerId; // set customer reference
            this.rentalDate = rentalDate; // set rental date
            this.returnDate = returnDate; // set return date
            this.totalCost = totalCost;   // set cost
        }
    }

    // Sale model: represents a sales transaction
    static class Sale implements Serializable {
        private static final long serialVersionUID = 1L; // serialization version
        int id;                 // unique sale ID
        int vehicleId;          // vehicle sold
        int customerId;         // buyer
        String dateSold;        // sale date string
        double sellingPrice;    // price sold for

        // Constructor: set sale fields
        Sale(int id, int vehicleId, int customerId, String dateSold, double sellingPrice) {
            this.id = id;               // set sale ID
            this.vehicleId = vehicleId; // set vehicle reference
            this.customerId = customerId; // set customer reference
            this.dateSold = dateSold;   // set date
            this.sellingPrice = sellingPrice; // set price
        }
    }

    // Maintenance model: represents vehicle servicing record
    static class Maintenance implements Serializable {
        private static final long serialVersionUID = 1L; // serialization version
        int id;                 // unique maintenance ID
        int vehicleId;          // vehicle under maintenance
        String serviceType;     // description of service (e.g., Oil change)
        String serviceDate;     // date of service
        double cost;            // cost of service
        String mechanic;        // who performed the service

        // Constructor: initialize maintenance record
        Maintenance(int id, int vehicleId, String serviceType, String serviceDate, double cost, String mechanic) {
            this.id = id;               // set maintenance ID
            this.vehicleId = vehicleId; // set vehicle reference
            this.serviceType = serviceType; // set service type
            this.serviceDate = serviceDate; // set date
            this.cost = cost;           // set cost
            this.mechanic = mechanic;   // set mechanic name
        }
    }

    // memeory stores thta acts like a database
    ArrayList<Vehicle> vehicles = new ArrayList<>();      // list of vehicles
    ArrayList<Customer> customers = new ArrayList<>();    // list of customers
    ArrayList<Rental> rentals = new ArrayList<>();        // list of active rentals
    ArrayList<Sale> sales = new ArrayList<>();            // list of sales records
    ArrayList<Maintenance> maints = new ArrayList<>();    // list of maintenance records

    // ID counter
    int nextVehicleId = 1;   // next ID to assign to a new Vehicle
    int nextCustomerId = 1;  // next ID for Customer
    int nextRentalId = 1;    // next ID for Rental
    int nextSaleId = 1;      // next ID for Sale
    int nextMaintId = 1;     // next ID for Maintenance

    //GUI components
    private DefaultTableModel vehicleTableModel;   // model for vehicle table
    private DefaultTableModel customerTableModel;  // model for customer table
    private DefaultTableModel rentalTableModel;    // model for rental table
    private DefaultTableModel saleTableModel;      // model for sales table
    private DefaultTableModel maintTableModel;     // model for maintenance table

    // Serialization filename 
    private static final String DATA_FILE = "car_system_data.ser";

    // Constructor: build the main window and initialize state
    public CarSystem() {
        super("Car System — All In One (Java Only)"); // set window title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // close program when window is closed
        setSize(950, 600); // window size width=950 height=600
        setLocationRelativeTo(null); // center window on screen

        initGUI();      // setup GUI components and layout
        loadData();     // attempt to load persisted data from disk
        refreshAllTables(); // update table views with current in-memory data
    }

    // Initialize the GUI: tabs, tables and control panels
    private void initGUI() {
        JTabbedPane tabs = new JTabbedPane(); // tabbed pane to hold modules

        // Vehicles Tab 
        JPanel vehiclePanel = new JPanel(new BorderLayout()); // panel with BorderLayout
        vehicleTableModel = new DefaultTableModel(new Object[]{"ID", "Make", "Model", "Year", "Color", "Price", "Status"}, 0);
        JTable vehicleTable = new JTable(vehicleTableModel); // create table using model
        vehiclePanel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER); // add table inside a scroll pane to center
        vehiclePanel.add(vehicleControlsPanel(), BorderLayout.SOUTH); // add controls at bottom
        tabs.addTab("Vehicles", vehiclePanel); // add tab labeled "Vehicles"

        // Customers Tab 
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email", "Address"}, 0);
        JTable customerTable = new JTable(customerTableModel); // table to display customers
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER); // center table
        customerPanel.add(customerControlsPanel(), BorderLayout.SOUTH); // controls below
        tabs.addTab("Customers", customerPanel); // add customer tab

        // Rentals Tab
        JPanel rentalPanel = new JPanel(new BorderLayout());
        rentalTableModel = new DefaultTableModel(new Object[]{"ID", "VehicleID", "CustomerID", "RentalDate", "ReturnDate", "TotalCost"}, 0);
        JTable rentalTable = new JTable(rentalTableModel); // create rental table
        rentalPanel.add(new JScrollPane(rentalTable), BorderLayout.CENTER); // center rental table
        rentalPanel.add(rentalControlsPanel(), BorderLayout.SOUTH); // add rental controls
        tabs.addTab("Rentals", rentalPanel); // add rentals tab

        // Sales Tab
        JPanel salesPanel = new JPanel(new BorderLayout());
        saleTableModel = new DefaultTableModel(new Object[]{"ID", "VehicleID", "CustomerID", "DateSold", "SellingPrice"}, 0);
        JTable saleTable = new JTable(saleTableModel); // create sales table
        salesPanel.add(new JScrollPane(saleTable), BorderLayout.CENTER); // add scrollable table to center
        salesPanel.add(salesControlsPanel(), BorderLayout.SOUTH); // add sales controls at bottom
        tabs.addTab("Sales", salesPanel); // add sales tab

        // Maintenance Tab
        JPanel maintPanel = new JPanel(new BorderLayout());
        maintTableModel = new DefaultTableModel(new Object[]{"ID", "VehicleID", "ServiceType", "ServiceDate", "Cost", "Mechanic"}, 0);
        JTable maintTable = new JTable(maintTableModel); // table for maintenance records
        maintPanel.add(new JScrollPane(maintTable), BorderLayout.CENTER); // center table
        maintPanel.add(maintenanceControlsPanel(), BorderLayout.SOUTH); // add maintenance controls
        tabs.addTab("Maintenance", maintPanel); // add maintenance tab

        // Top menu
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); // top panel with left-aligned flow layout
        JButton saveBtn = new JButton("Save Data"); // button to save data
        JButton loadBtn = new JButton("Load Data"); // button to load data from disk
        JButton clearBtn = new JButton("Clear All (reset)"); // clear/reset stored data

        // wire button actions to methods
        saveBtn.addActionListener(e -> saveData()); // when clicked, call saveData()
        loadBtn.addActionListener(e -> { loadData(); refreshAllTables(); }); // load then refresh tables
        clearBtn.addActionListener(e -> { if (confirm("Clear ALL data?")) { clearAllData(); refreshAllTables(); } });

        // add buttons to top panel
        top.add(saveBtn);
        top.add(loadBtn);
        top.add(clearBtn);

        // attach top and tabs to frame content
        getContentPane().setLayout(new BorderLayout()); // set frame layout
        getContentPane().add(top, BorderLayout.NORTH); // top panel at north
        getContentPane().add(tabs, BorderLayout.CENTER); // tabs in center
    }

    //Panels for controls 

    // Controls panel for Vehicles
    private JPanel vehicleControlsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        JTextField makeF = new JTextField(8); // text field for make
        JTextField modelF = new JTextField(8); // model field
        JTextField yearF = new JTextField(4); // year field 
        JTextField colorF = new JTextField(6); // color field
        JTextField priceF = new JTextField(7); // price field

        JButton add = new JButton("Add Vehicle"); // add button
        JButton delete = new JButton("Delete by ID"); // delete button
        JTextField deleteId = new JTextField(4); // field for id to delete

        // Add button action
        add.addActionListener(e -> {
            try {
                String make = makeF.getText().trim(); // get make text
                String model = modelF.getText().trim(); // get model text
                int year = Integer.parseInt(yearF.getText().trim()); // parse year integer
                String color = colorF.getText().trim(); // get color
                double price = Double.parseDouble(priceF.getText().trim()); // parse price double
                addVehicle(make, model, year, color, price); // add vehicle to list
                // clear input fields after successful add
                makeF.setText(""); modelF.setText(""); yearF.setText(""); colorF.setText(""); priceF.setText("");
                refreshAllTables(); // refresh table views to show new vehicle
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage()); // show error if parsing fails
            }
        });

        // Delete button action
        delete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteId.getText().trim()); // parse id
                boolean ok = deleteVehicle(id); // attempt to delete
                if (!ok) showError("No vehicle with ID " + id); // show error if not found
                deleteId.setText(""); // clear delete field
                refreshAllTables(); // refresh table after deletion
            } catch (Exception ex) { showError("Invalid ID"); } // show error if parse fails
        });

        // add labeled fields and buttons to panel
        p.add(new JLabel("Make:")); p.add(makeF);
        p.add(new JLabel("Model:")); p.add(modelF);
        p.add(new JLabel("Year:")); p.add(yearF);
        p.add(new JLabel("Color:")); p.add(colorF);
        p.add(new JLabel("Price:")); p.add(priceF);
        p.add(add);
        p.add(new JLabel("ID:")); p.add(deleteId); p.add(delete);
        return p; // return constructed panel
    }

    // Controls panel for Customers
    private JPanel customerControlsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameF = new JTextField(10); // name field
        JTextField phoneF = new JTextField(8); // phone field
        JTextField emailF = new JTextField(10); // email field
        JTextField addressF = new JTextField(12); // address field
        JButton add = new JButton("Add Customer"); // add button
        JButton delete = new JButton("Delete by ID"); // delete button
        JTextField deleteId = new JTextField(4); // id field for delete

        // Add customer action
        add.addActionListener(e -> {
            try {
                String name = nameF.getText().trim(); // get name
                String phone = phoneF.getText().trim(); // get phone
                String email = emailF.getText().trim(); // get email
                String address = addressF.getText().trim(); // get address
                addCustomer(name, phone, email, address); // add customer to list
                nameF.setText(""); phoneF.setText(""); emailF.setText(""); addressF.setText(""); // clear fields
                refreshAllTables(); // refresh tables to show new customer
            } catch (Exception ex) { showError("Invalid input"); } // generic error
        });

        // Delete customer by ID action
        delete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(deleteId.getText().trim()); // parse id
                boolean ok = deleteCustomer(id); // attempt delete
                if (!ok) showError("No customer with ID " + id); // notify if not found
                deleteId.setText("");
                refreshAllTables();
            } catch (Exception ex) { showError("Invalid ID"); }
        });

        // add controls and labels to panel
        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Phone:")); p.add(phoneF);
        p.add(new JLabel("Email:")); p.add(emailF);
        p.add(new JLabel("Address:")); p.add(addressF);
        p.add(add);
        p.add(new JLabel("ID:")); p.add(deleteId); p.add(delete);
        return p;
    }

    // Controls panel for Rentals
    private JPanel rentalControlsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField vehicleIdF = new JTextField(4); // vehicle id input
        JTextField customerIdF = new JTextField(4); // customer id input
        JTextField returnDateF = new JTextField(8); // return date input (yyyy-MM-dd expected)
        JTextField costF = new JTextField(6); // total cost input
        JButton add = new JButton("Add Rental"); // add rental button
        JButton close = new JButton("Close Rental (by ID)"); // close rental button
        JTextField closeId = new JTextField(4); // rental id to close

        // Add rental action
        add.addActionListener(e -> {
            try {
                int vid = Integer.parseInt(vehicleIdF.getText().trim()); // vehicle id
                int cid = Integer.parseInt(customerIdF.getText().trim()); // customer id
                String rDate = today(); // rental date is today's date
                String ret = returnDateF.getText().trim(); // return date string
                double cost = Double.parseDouble(costF.getText().trim()); // total cost
                boolean ok = addRental(vid, cid, rDate, ret, cost); // attempt to add rental
                if (!ok) showError("Vehicle not available or IDs invalid"); // error if not valid
                vehicleIdF.setText(""); customerIdF.setText(""); returnDateF.setText(""); costF.setText(""); // clear
                refreshAllTables(); // refresh UI
            } catch (Exception ex) { showError("Invalid input: " + ex.getMessage()); }
        });

        // Close rental action
        close.addActionListener(e -> {
            try {
                int id = Integer.parseInt(closeId.getText().trim()); // rental id to close
                boolean ok = closeRental(id); // attempt to close rental
                if (!ok) showError("No rental with ID " + id); // show error if not found
                closeId.setText("");
                refreshAllTables();
            } catch (Exception ex) { showError("Invalid ID"); }
        });

        // add labels and inputs to panel
        p.add(new JLabel("VehicleID:")); p.add(vehicleIdF);
        p.add(new JLabel("CustomerID:")); p.add(customerIdF);
        p.add(new JLabel("ReturnDate (yyyy-MM-dd):")); p.add(returnDateF);
        p.add(new JLabel("TotalCost:")); p.add(costF);
        p.add(add);
        p.add(new JLabel("RentalID:")); p.add(closeId); p.add(close);
        return p;
    }

    // Controls panel for Sales
    private JPanel salesControlsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField vehicleIdF = new JTextField(4); // vehicle id input
        JTextField customerIdF = new JTextField(4); // customer id input
        JTextField sellingPriceF = new JTextField(7); // price input
        JButton add = new JButton("Record Sale"); // record sale button

        // Add sale action
        add.addActionListener(e -> {
            try {
                int vid = Integer.parseInt(vehicleIdF.getText().trim()); // vehicle id
                int cid = Integer.parseInt(customerIdF.getText().trim()); // customer id
                double sp = Double.parseDouble(sellingPriceF.getText().trim()); // selling price
                boolean ok = addSale(vid, cid, today(), sp); // attempt to add sale
                if (!ok) showError("Vehicle not available or IDs invalid"); // show error otherwise
                vehicleIdF.setText(""); customerIdF.setText(""); sellingPriceF.setText(""); // clear
                refreshAllTables(); // refresh UI
            } catch (Exception ex) { showError("Invalid input"); }
        });

        // add components to panel
        p.add(new JLabel("VehicleID:")); p.add(vehicleIdF);
        p.add(new JLabel("CustomerID:")); p.add(customerIdF);
        p.add(new JLabel("Price:")); p.add(sellingPriceF);
        p.add(add);
        return p;
    }

    // Controls panel for Maintenance
    private JPanel maintenanceControlsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField vehicleIdF = new JTextField(4); // vehicle id
        JTextField serviceTypeF = new JTextField(8); // service type description
        JTextField dateF = new JTextField(8); // service date
        JTextField costF = new JTextField(6); // cost input
        JTextField mechF = new JTextField(8); // mechanic name
        JButton add = new JButton("Add Maintenance"); // add maintenance button

        // Add maintenance action
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int vid = Integer.parseInt(vehicleIdF.getText().trim()); // parse vehicle id
                    String type = serviceTypeF.getText().trim(); // service type
                    String date = dateF.getText().trim(); // date string
                    double c = Double.parseDouble(costF.getText().trim()); // parse cost
                    String m = mechF.getText().trim(); // mechanic name
                    boolean ok = addMaint(vid, type, date, c, m); // attempt to add maintenance
                    if (!ok) showError("Vehicle ID invalid"); // show error if vehicle not found
                    // clear input fields
                    vehicleIdF.setText(""); serviceTypeF.setText(""); dateF.setText(""); costF.setText(""); mechF.setText("");
                    refreshAllTables(); // refresh tables
                } catch (Exception ex) { showError("Invalid input: " + ex.getMessage()); }
            }
        });

        // add components to panel
        p.add(new JLabel("VehicleID:")); p.add(vehicleIdF);
        p.add(new JLabel("Service:")); p.add(serviceTypeF);
        p.add(new JLabel("Date (yyyy-MM-dd):")); p.add(dateF);
        p.add(new JLabel("Cost:")); p.add(costF);
        p.add(new JLabel("Mechanic:")); p.add(mechF);
        p.add(add);
        return p;
    }

    // Data operations 

    // Add a vehicle to the vehicles list with next numeric ID
    void addVehicle(String make, String model, int year, String color, double price) {
        vehicles.add(new Vehicle(nextVehicleId++, make, model, year, color, price)); // create and append new Vehicle; increment nextVehicleId
    }

    // Delete a vehicle by numeric ID; return true if found and removed, false otherwise
    boolean deleteVehicle(int id) {
        for (Vehicle v : vehicles) {
            if (v.id == id) {
                vehicles.remove(v); // remove the matching vehicle from the list
                return true; // success
            }
        }
        return false; // not found
    }

    // Add a customer to the customers list with next numeric ID
    void addCustomer(String name, String phone, String email, String address) {
        customers.add(new Customer(nextCustomerId++, name, phone, email, address)); // create and append, increment ID
    }

    // Delete a customer by numeric ID; return true if found and removed, false otherwise
    boolean deleteCustomer(int id) {
        for (Customer c : customers) {
            if (c.id == id) {
                customers.remove(c); // remove matching customer
                return true; // success
            }
        }
        return false; // not found
    }

    // Add a rental if vehicle and customer exist and vehicle is available. Return true on success.
    boolean addRental(int vehicleId, int customerId, String rentalDate, String returnDate, double totalCost) {
        Vehicle v = findVehicleById(vehicleId); // find vehicle by id
        Customer c = findCustomerById(customerId); // find customer by id
        if (v == null || c == null || !v.status.equals("Available")) return false; // fail if invalid or not available
        rentals.add(new Rental(nextRentalId++, vehicleId, customerId, rentalDate, returnDate, totalCost)); // create rental record
        v.status = "Rented"; // mark vehicle as rented
        return true; // success
    }

    // Close rental by ID: set vehicle status back to Available and remove rental record; return true on success
    boolean closeRental(int rentalId) {
        for (Rental r : rentals) {
            if (r.id == rentalId) {
                Vehicle v = findVehicleById(r.vehicleId); // find associated vehicle
                if (v != null) v.status = "Available"; // set vehicle available
                rentals.remove(r); // remove rental record
                return true; // success
            }
        }
        return false; // rental not found
    }

    // Add a sale record if vehicle/customer valid and vehicle is available; set vehicle status to Sold
    boolean addSale(int vehicleId, int customerId, String dateSold, double sellingPrice) {
        Vehicle v = findVehicleById(vehicleId); // lookup
        Customer c = findCustomerById(customerId); // lookup
        if (v == null || c == null || !v.status.equals("Available")) return false; // fail if invalid
        sales.add(new Sale(nextSaleId++, vehicleId, customerId, dateSold, sellingPrice)); // create sale
        v.status = "Sold"; // mark vehicle sold
        return true; // success
    }

    // Add maintenance record and set vehicle status to Maintenance; return false if vehicle not found
    boolean addMaint(int vehicleId, String serviceType, String serviceDate, double cost, String mechanic) {
        Vehicle v = findVehicleById(vehicleId); // find vehicle
        if (v == null) return false; // invalid vehicle
        maints.add(new Maintenance(nextMaintId++, vehicleId, serviceType, serviceDate, cost, mechanic)); // add record
        v.status = "Maintenance"; // set status
        return true; // success
    }

    // Helper: find vehicle by ID 
    private Vehicle findVehicleById(int id) {
        for (Vehicle v : vehicles) if (v.id == id) return v; // return first match
        return null; // not found
    }

    // Helper: find customer by ID
    private Customer findCustomerById(int id) {
        for (Customer c : customers) if (c.id == id) return c; // return match
        return null; // not found
    }

    // UI helpers

    // Refresh all JTable models from the in-memory lists
    private void refreshAllTables() {
        // Vehicles table: clear rows then append all vehicle data rows
        vehicleTableModel.setRowCount(0); // remove existing rows
        for (Vehicle v : vehicles) {
            vehicleTableModel.addRow(new Object[]{v.id, v.make, v.model, v.year, v.color, v.price, v.status}); // add row
        }

        // Customers table
        customerTableModel.setRowCount(0);
        for (Customer c : customers) {
            customerTableModel.addRow(new Object[]{c.id, c.name, c.phone, c.email, c.address});
        }

        // Rentals table 
        rentalTableModel.setRowCount(0);
        for (Rental r : rentals) {
            rentalTableModel.addRow(new Object[]{r.id, r.vehicleId, r.customerId, r.rentalDate, r.returnDate, r.totalCost});
        }

        // Sales table
        saleTableModel.setRowCount(0);
        for (Sale s : sales) {
            saleTableModel.addRow(new Object[]{s.id, s.vehicleId, s.customerId, s.dateSold, s.sellingPrice});
        }

        // Maintenance table
        maintTableModel.setRowCount(0);
        for (Maintenance m : maints) {
            maintTableModel.addRow(new Object[]{m.id, m.vehicleId, m.serviceType, m.serviceDate, m.cost, m.mechanic});
        }
    }

    // Save all in-memory lists and ID counters to disk via serialization
    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(vehicles); // write vehicles list
            out.writeObject(customers); // write customers list
            out.writeObject(rentals); // write rentals list
            out.writeObject(sales); // write sales list
            out.writeObject(maints); // write maints list
            // write next ID counters so IDs continue after reload
            out.writeInt(nextVehicleId);
            out.writeInt(nextCustomerId);
            out.writeInt(nextRentalId);
            out.writeInt(nextSaleId);
            out.writeInt(nextMaintId);
            showInfo("Data saved to " + DATA_FILE); // inform user of success
        } catch (Exception ex) {
            showError("Save failed: " + ex.getMessage()); // show error message on exception
        }
    }

    // Load data from disk if file exists and restore lists and counters
    @SuppressWarnings("unchecked")
    private void loadData() {
        File f = new File(DATA_FILE); // create File object for data file
        if (!f.exists()) return; // if no file, nothing to load
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            vehicles = (ArrayList<Vehicle>) in.readObject(); // read vehicles list
            customers = (ArrayList<Customer>) in.readObject(); // read customers
            rentals = (ArrayList<Rental>) in.readObject(); // read rentals
            sales = (ArrayList<Sale>) in.readObject(); // read sales
            maints = (ArrayList<Maintenance>) in.readObject(); // read maintenance
            // restore ID counters
            nextVehicleId = in.readInt();
            nextCustomerId = in.readInt();
            nextRentalId = in.readInt();
            nextSaleId = in.readInt();
            nextMaintId = in.readInt();
            showInfo("Data loaded from " + DATA_FILE); // inform user
        } catch (Exception ex) {
            showError("Load failed: " + ex.getMessage()); // show error if load fails
        }
    }

    // Clear all in-memory data and delete the data file if exists
    private void clearAllData() {
        vehicles.clear(); customers.clear(); rentals.clear(); sales.clear(); maints.clear(); // clear lists
        // reset ID counters
        nextVehicleId = 1; nextCustomerId = 1; nextRentalId = 1; nextSaleId = 1; nextMaintId = 1;
        File f = new File(DATA_FILE); if (f.exists()) f.delete(); // delete persisted file
    }

    // Helper that returns today's date formatted as yyyy-MM-dd
    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // format current date
    }

    // Helper: show confirm dialog and return true if user clicks YES
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Helper: show an error dialog 
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Helper: show an information dialOG
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarSystem app = new CarSystem(); // create app instance
            app.setVisible(true); // show the main window
        });
    }
}
