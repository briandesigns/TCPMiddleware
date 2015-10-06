


import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/**
 * Class that implements a client connection to the Middleware TCP server
 */
public class MiddlewareRunnable implements Runnable, ResourceManager {
    Socket clientSocket = null;
    Socket carSocket = null;
    Socket flightSocket = null;
    Socket roomSocket = null;
    String[] rmAddresses;
    PrintWriter toCar, toFlight, toRoom, toClient;
    BufferedReader fromCar, fromFlight, fromRoom, fromClient;

    public MiddlewareRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
        readRmAddresses();
        connectRM();
        setComms();
    }

    private void setComms() {
        try {
            toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            toCar = new PrintWriter(carSocket.getOutputStream(), true);
            toFlight = new PrintWriter(flightSocket.getOutputStream(), true);
            toRoom = new PrintWriter(roomSocket.getOutputStream(), true);
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            fromCar = new BufferedReader(new InputStreamReader(carSocket.getInputStream()));
            fromFlight = new BufferedReader(new InputStreamReader(flightSocket.getInputStream()));
            fromRoom = new BufferedReader(new InputStreamReader(flightSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectRM() {
        try {
            this.flightSocket = new Socket(rmAddresses[0], Integer.parseInt(rmAddresses[1]));
            this.carSocket = new Socket(rmAddresses[2], Integer.parseInt(rmAddresses[3]));
            this.roomSocket = new Socket(rmAddresses[4], Integer.parseInt(rmAddresses[5]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRmAddresses() {
        String line;
        BufferedReader br = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("RMList.txt").getFile());
            rmAddresses = new String[6];
            br = new BufferedReader(new FileReader(file));

            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                rmAddresses[i] = tokens[0];
                rmAddresses[i + 1] = tokens[1];
                i = i + 2;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {

            String inputLine;
            toClient.println("Middleware> ");

            //keep on prompting user for input until disconnect
            outerloop:
            while ((inputLine = fromClient.readLine()) != null) {
                //read a line from user if we used the scanner instaed
                //inputLine = in.nextLine();

                //split the line into tokens and save them into an array
                String[] cmdWords = inputLine.split(",");

                int choice = findChoice(cmdWords);

                boolean success;
                int value;
                String stringValue;
                //switch statement based on 1st keyword of user input
                switch (choice) {
                    case 2:
                        if(cmdWords.length<4) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = addFlight(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]), Integer.parseInt(cmdWords[3]), Integer.parseInt(cmdWords[4]));
                        toClient.println(success);
                        break;
                    case 3:
                        if(cmdWords.length<4) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = addCars(Integer.parseInt(cmdWords[1]), cmdWords[2], Integer.parseInt(cmdWords[3]), Integer.parseInt(cmdWords[4]));
                        toClient.println(success);
                        break;
                    case 4:
                        if(cmdWords.length<4) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = addRooms(Integer.parseInt(cmdWords[1]), cmdWords[2], Integer.parseInt(cmdWords[3]), Integer.parseInt(cmdWords[4]));
                        toClient.println(success);
                        break;
                    case 5:
                        if(cmdWords.length<1) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value = newCustomer(Integer.parseInt(cmdWords[1]));
                        toClient.println(value);
                        break;
                    case 6:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = deleteFlight(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(success);
                        break;
                    case 7:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = deleteCars(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(success);
                        break;
                    case 8:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = deleteRooms(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(success);
                        break;
                    case 9:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = deleteCustomer(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(success);
                        break;
                    case 10:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryFlight(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(value);
                        break;
                    case 11:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryCars(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(value);
                        break;
                    case 12:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryRooms(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(value);
                        break;
                    case 13:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        stringValue = queryCustomerInfo(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(stringValue);
                        break;
                    case 14:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryFlight(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(value);
                        break;
                    case 15:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryCarsPrice(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(value);
                        break;
                    case 16:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        value= queryRoomsPrice(Integer.parseInt(cmdWords[1]), cmdWords[2]);
                        toClient.println(value);
                        break;
                    case 17:
                        if(cmdWords.length<3) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = reserveFlight(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]), Integer.parseInt(cmdWords[3]));
                        toClient.println(success);
                        break;
                    case 18:
                        if(cmdWords.length<3) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = reserveCar(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]), cmdWords[3]);
                        toClient.println(success);
                        break;
                    case 19:
                        if(cmdWords.length<3) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = reserveRoom(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]), cmdWords[3]);
                        toClient.println(success);
                        break;
                    case 20:
                        int numFlights = Integer.parseInt(cmdWords[1]);
                        int id = Integer.parseInt(cmdWords[2]);
                        int customerId = Integer.parseInt(cmdWords[3]);
                        Vector flightNumbers = new Vector();
                        for (int i = 0; i<numFlights; i++) {
                            flightNumbers.addElement(cmdWords[i+4]);
                        }
                        String location = cmdWords[3 + numFlights + 1];
                        boolean car = cmdWords[3+numFlights+2].contains("true");
                        boolean room = cmdWords[3 + numFlights + 3].contains("true");
                        reserveItinerary(id,customerId,flightNumbers,location,car,room);
                        break;
                    case -1:
                        toClient.println("ERROR :  Command " + cmdWords[0] + " not supported");
                        break;
                    
                    case 21:
                        System.exit(0);
                        break outerloop;
                    case 22:
                        if(cmdWords.length<2) {
                            toClient.println("ERROR : wrong arguments");
                            break;
                        }
                        success = newCustomerId(Integer.parseInt(cmdWords[1]), Integer.parseInt(cmdWords[2]));
                        toClient.println(success);
                        break;
                    default:
                        toClient.println("ERROR :  Command " + cmdWords[0] + " not supported");
                        break;
                }
                toClient.println("Middleware> ");
            }

            fromClient.close();
            fromCar.close();
            fromRoom.close();
            fromFlight.close();
            toClient.close();
            toCar.close();
            toRoom.close();
            toFlight.close();
        } catch (IOException e) {
            System.out.println("exception IO");
        }
    }

    private int findChoice(String[] cmdWords) {
        int choice =-1;

        if (cmdWords[0].compareToIgnoreCase("help") == 0)
            choice = 1;
        else if (cmdWords[0].compareToIgnoreCase("newflight") == 0)
            choice =2;
        else if (cmdWords[0].compareToIgnoreCase("newcar") == 0)
            choice =3;
        else if (cmdWords[0].compareToIgnoreCase("newroom") == 0)
            choice =4;
        else if (cmdWords[0].compareToIgnoreCase("newcustomer") == 0)
            choice =5;
        else if (cmdWords[0].compareToIgnoreCase("deleteflight") == 0)
            choice =6;
        else if (cmdWords[0].compareToIgnoreCase("deletecar") == 0)
            choice =7;
        else if (cmdWords[0].compareToIgnoreCase("deleteroom") == 0)
            choice=8;
        else if (cmdWords[0].compareToIgnoreCase("deletecustomer") == 0)
            choice=9;
        else if (cmdWords[0].compareToIgnoreCase("queryflight") == 0)
            choice=10;
        else if (cmdWords[0].compareToIgnoreCase("querycar") == 0)
            choice= 11;
        else if (cmdWords[0].compareToIgnoreCase("queryroom") == 0)
            choice= 12;
        else if (cmdWords[0].compareToIgnoreCase("querycustomer") == 0)
            choice= 13;
        else if (cmdWords[0].compareToIgnoreCase("queryflightprice") == 0)
            choice= 14;
        else if (cmdWords[0].compareToIgnoreCase("querycarprice") == 0)
            choice= 15;
        else if (cmdWords[0].compareToIgnoreCase("queryroomprice") == 0)
            choice= 16;
        else if (cmdWords[0].compareToIgnoreCase("reserveflight") == 0)
            choice= 17;
        else if (cmdWords[0].compareToIgnoreCase("reservecar") == 0)
            choice= 18;
        else if (cmdWords[0].compareToIgnoreCase("reserveroom") == 0)
            choice= 19;
        else if (cmdWords[0].compareToIgnoreCase("itinerary") == 0)
            choice= 20;
        else if (cmdWords[0].compareToIgnoreCase("quit") == 0)
            choice= 21;
        else if (cmdWords[0].compareToIgnoreCase("newcustomerid") == 0)
            choice=22;
        else
            choice=-1;
        return choice;
    }


    // Basic operations on RMItem //

    // Read a data item.
    private RMItem readData(int id, String key) {
        synchronized (TCPServer.m_itemHT_customer) {
            return (RMItem) TCPServer.m_itemHT_customer.get(key);
        }
    }

    // Write a data item.
    private void writeData(int id, String key, RMItem value) {
        synchronized (TCPServer.m_itemHT_customer) {
            TCPServer.m_itemHT_customer.put(key, value);
        }
    }

    // Remove the item out of storage.
    protected RMItem removeData(int id, String key) {
        synchronized (TCPServer.m_itemHT_customer) {
            return (RMItem) TCPServer.m_itemHT_customer.remove(key);
        }
    }


    // Basic operations on ReservableItem //

    // Delete the entire item.
    protected boolean deleteItem(int id, String key) {
        Trace.info("RM::deleteItem(" + id + ", " + key + ") called.");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        // Check if there is such an item in the storage.
        if (curObj == null) {
            Trace.warn("RM::deleteItem(" + id + ", " + key + ") failed: "
                    + " item doesn't exist.");
            return false;
        } else {
            if (curObj.getReserved() == 0) {
                removeData(id, curObj.getKey());
                Trace.info("RM::deleteItem(" + id + ", " + key + ") OK.");
                return true;
            } else {
                Trace.info("RM::deleteItem(" + id + ", " + key + ") failed: "
                        + "some customers have reserved it.");
                return false;
            }
        }
    }

    // Query the number of available seats/rooms/cars.
    protected int queryNum(int id, String key) {
        Trace.info("RM::queryNum(" + id + ", " + key + ") called.");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        int value = 0;
        if (curObj != null) {
            value = curObj.getCount();
        }
        Trace.info("RM::queryNum(" + id + ", " + key + ") OK: " + value);
        return value;
    }

    // Query the price of an item.
    protected int queryPrice(int id, String key) {
        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") called.");
        ReservableItem curObj = (ReservableItem) readData(id, key);
        int value = 0;
        if (curObj != null) {
            value = curObj.getPrice();
        }
        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") OK: $" + value);
        return value;
    }

    // Reserve an item.
    protected boolean reserveItem(int id, int customerId,
                                  String key, String location) throws Exception {
        Trace.info("RM::reserveItem(" + id + ", " + customerId + ", "
                + key + ", " + location + ") called.");
        // Read customer object if it exists (and read lock it).
        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
        if (cust == null) {
            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
                    + key + ", " + location + ") failed: customer doesn't exist.");
            return false;
        }
        //Check for item availability and getting price
        boolean isSuccessfulReservation = false;
        int itemPrice = -1;
        if (key.contains("car-")) {
            toCar.println("reserveCar," + id + "," + customerId + "," + location);
            if (fromCar.readLine().contains("true")) {
                isSuccessfulReservation = true;
                toCar.println("queryCarsPrice," + id + "," + location);
                itemPrice = Integer.parseInt(fromCar.readLine());
            }
        } else if (key.contains("flight-")) {
            toFlight.println("reserveFlight," + id + "," + customerId + "," + location);
            if (fromFlight.readLine().contains("true")) {
                isSuccessfulReservation = true;
                toFlight.println("queryFlightPrice," + id + "," + location);
                itemPrice = Integer.parseInt(fromFlight.readLine());
            }
        } else if (key.contains("room-")) {
            toRoom.println("reserveRoom," + id + "," + customerId + "," + location);
            if (fromRoom.readLine().contains("true")) {
                isSuccessfulReservation = true;
                toRoom.println("queryRoomsPrice," + id + "," + location);
                itemPrice = Integer.parseInt(fromRoom.readLine());
            }
        } else {
            throw new Exception("can't reserve this");
        }
        // Check if the item is available.
        if (!isSuccessfulReservation) {
            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
                    + key + ", " + location + ") failed: item doesn't exist or no more items.");
            return false;
        } else {
            // Do reservation.

            cust.reserve(key, location, itemPrice);
            writeData(id, cust.getKey(), cust);

            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
                    + key + ", " + location + ") OK.");
            return true;
        }
    }


    // Flight operations //

    // Create a new flight, or add seats to existing flight.
    // Note: if flightPrice <= 0 and the flight already exists, it maintains
    // its current price.
    @Override
    public boolean addFlight(int id, int flightNumber,
                             int numSeats, int flightPrice) {
        toFlight.println("addFlight" + "," + id + "," + numSeats + "," + flightPrice);
        String line = null;
        try {
            line = fromFlight.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        toFlight.println("deleteFlight" + "," + id + "," + flightNumber);
        String line = null;
        try {
            line = fromFlight.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    // Returns the number of empty seats on this flight.
    @Override
    public int queryFlight(int id, int flightNumber) {
        toFlight.println("queryFlight" + "," + id + "," + flightNumber);
        String line = null;
        try {
            line = fromFlight.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }

    // Returns price of this flight.
    public int queryFlightPrice(int id, int flightNumber) {
        toFlight.println("queryFlightPrice" + "," + id + "," + flightNumber);
        String line = null;
        try {
            line = fromFlight.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }

    /*
    // Returns the number of reservations for this flight.
    public int queryFlightReservations(int id, int flightNumber) {
        Trace.info("RM::queryFlightReservations(" + id
                + ", #" + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id,
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations == null) {
            numReservations = new RMInteger(0);
       }
        Trace.info("RM::queryFlightReservations(" + id +
                ", #" + flightNumber + ") = " + numReservations);
        return numReservations.getValue();
    }
    */

    /*
    // Frees flight reservation record. Flight reservation records help us
    // make sure we don't delete a flight if one or more customers are
    // holding reservations.
    public boolean freeFlightReservation(int id, int flightNumber) {
        Trace.info("RM::freeFlightReservations(" + id + ", "
                + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id,
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations != null) {
            numReservations = new RMInteger(
                    Math.max(0, numReservations.getValue() - 1));
        }
        writeData(id, Flight.getNumReservationsKey(flightNumber), numReservations);
        Trace.info("RM::freeFlightReservations(" + id + ", "
                + flightNumber + ") OK: reservations = " + numReservations);
        return true;
    }
    */


    // Car operations //

    // Create a new car location or add cars to an existing location.
    // Note: if price <= 0 and the car location already exists, it maintains
    // its current price.
    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        toCar.println("addCars" + "," + id + "," + location + "," + numCars + "," + carPrice);
        String line = null;
        try {
            line = fromCar.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    // Delete cars from a location.
    @Override
    public boolean deleteCars(int id, String location) {
        toCar.println("deleteCars" + "," + id + "," + location);
        String line = null;
        try {
            line = fromCar.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    // Returns the number of cars available at a location.
    @Override
    public int queryCars(int id, String location) {
        toCar.println("queryCars" + "," + id + "," + location);
        String line = null;
        try {
            line = fromCar.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }

    // Returns price of cars at this location.
    @Override
    public int queryCarsPrice(int id, String location) {
        toCar.println("queryCarsPrice" + "," + id + "," + location);
        String line = null;
        try {
            line = fromCar.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }


    // Room operations //

    // Create a new room location or add rooms to an existing location.
    // Note: if price <= 0 and the room location already exists, it maintains
    // its current price.
    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        toRoom.println("addRooms" + "," + id + "," + location + "," + numRooms + "," + roomPrice);
        String line = null;
        try {
            line = fromRoom.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    // Delete rooms from a location.
    @Override
    public boolean deleteRooms(int id, String location) {
        toRoom.println("deleteRooms" + "," + id + "," + location);
        String line = null;
        try {
            line = fromRoom.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equalsIgnoreCase("true")) return true;
        else return false;
    }

    // Returns the number of rooms available at a location.
    @Override
    public int queryRooms(int id, String location) {
        toRoom.println("queryRooms" + "," + id + "," + location);
        String line = null;
        try {
            line = fromRoom.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }

    // Returns room price at this location.
    @Override
    public int queryRoomsPrice(int id, String location) {
        toRoom.println("queryRoomsPrice" + "," + id + "," + location);
        String line = null;
        try {
            line = fromRoom.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(line);
    }


    // Customer operations //

    @Override
    public int newCustomer(int id) {
        Trace.info("INFO: RM::newCustomer(" + id + ") called.");
        // Generate a globally unique Id for the new customer.
        int customerId = Integer.parseInt(String.valueOf(id) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer cust = new Customer(customerId);
        writeData(id, cust.getKey(), cust);
        Trace.info("RM::newCustomer(" + id + ") OK: " + customerId);
        return customerId;
    }

    // This method makes testing easier.
    @Override
    public boolean newCustomerId(int id, int customerId) {
        Trace.info("INFO: RM::newCustomer(" + id + ", " + customerId + ") called.");
        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
        if (cust == null) {
            cust = new Customer(customerId);
            writeData(id, cust.getKey(), cust);
            Trace.info("INFO: RM::newCustomer(" + id + ", " + customerId + ") OK.");
            return true;
        } else {
            Trace.info("INFO: RM::newCustomer(" + id + ", " +
                    customerId + ") failed: customer already exists.");
            return false;
        }
    }

    // Delete customer from the database.
    @Override
    public boolean deleteCustomer(int id, int customerId) {
        Trace.info("RM::deleteCustomer(" + id + ", " + customerId + ") called.");
        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
        if (cust == null) {
            Trace.warn("RM::deleteCustomer(" + id + ", "
                    + customerId + ") failed: customer doesn't exist.");
            return false;
        } else {
            // Increase the reserved numbers of all reservable items that
            // the customer reserved.
            RMHashtable reservationHT = cust.getReservations();
            for (Enumeration e = reservationHT.keys(); e.hasMoreElements(); ) {
                String reservedKey = (String) (e.nextElement());
                ReservedItem reservedItem = cust.getReservedItem(reservedKey);
                Trace.info("RM::deleteCustomer(" + id + ", " + customerId + "): "
                        + "deleting " + reservedItem.getCount() + " reservations "
                        + "for item " + reservedItem.getKey());
                ReservableItem item =
                        (ReservableItem) readData(id, reservedItem.getKey());
                item.setReserved(item.getReserved() - reservedItem.getCount());
                item.setCount(item.getCount() + reservedItem.getCount());
                Trace.info("RM::deleteCustomer(" + id + ", " + customerId + "): "
                        + reservedItem.getKey() + " reserved/available = "
                        + item.getReserved() + "/" + item.getCount());
            }
            // Remove the customer from the storage.
            removeData(id, cust.getKey());
            Trace.info("RM::deleteCustomer(" + id + ", " + customerId + ") OK.");
            return true;
        }
    }

    // Return data structure containing customer reservation info.
    // Returns null if the customer doesn't exist.
    // Returns empty RMHashtable if customer exists but has no reservations.
    public RMHashtable getCustomerReservations(int id, int customerId) {
        Trace.info("RM::getCustomerReservations(" + id + ", "
                + customerId + ") called.");
        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
        if (cust == null) {
            Trace.info("RM::getCustomerReservations(" + id + ", "
                    + customerId + ") failed: customer doesn't exist.");
            return null;
        } else {
            return cust.getReservations();
        }
    }

    // Return a bill.
    @Override
    public String queryCustomerInfo(int id, int customerId) {
        Trace.info("RM::queryCustomerInfo(" + id + ", " + customerId + ") called.");
        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
        if (cust == null) {
            Trace.warn("RM::queryCustomerInfo(" + id + ", "
                    + customerId + ") failed: customer doesn't exist.");
            // Returning an empty bill means that the customer doesn't exist.
            return "";
        } else {
            String s = cust.printBill();
            Trace.info("RM::queryCustomerInfo(" + id + ", " + customerId + "): \n");
            System.out.println(s);
            return s;
        }
    }

    // Add flight reservation to this customer.
    @Override
    public boolean reserveFlight(int id, int customerId, int flightNumber) {
        try {
            return reserveItem(id, customerId,
                    Flight.getKey(flightNumber), String.valueOf(flightNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add car reservation to this customer.
    @Override
    public boolean reserveCar(int id, int customerId, String location) {
        try {
            return reserveItem(id, customerId, Car.getKey(location), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add room reservation to this customer.
    @Override
    public boolean reserveRoom(int id, int customerId, String location) {
        try {
            return reserveItem(id, customerId, Room.getKey(location), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // Reserve an itinerary.
    @Override
    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers,
                                    String location, boolean car, boolean room) {
        Iterator it = flightNumbers.iterator();

        boolean isSuccessfulReservation = false;
        while (it.hasNext()) {
            isSuccessfulReservation = reserveFlight(id, customerId, (Integer) it.next());
        }
        if (car) isSuccessfulReservation = reserveCar(id, customerId, location);
        if (room) isSuccessfulReservation = reserveRoom(id, customerId, location);
        return isSuccessfulReservation;
    }

}