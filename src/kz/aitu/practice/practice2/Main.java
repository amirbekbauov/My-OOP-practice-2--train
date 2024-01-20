package kz.aitu.practice.practice2;

import kz.aitu.practice.practice2.DatabaseConnector;
import kz.aitu.practice.practice2.Train;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Create a train and add objects
            Train train = new Train();
            train.addLocomotive(new Locomotive(100));
            train.addLocomotive(new Locomotive(150));
            train.addCar(new Car(50, 15));
            train.addCar(new Car(45,85));
            train.addCar(new Car(29,97));
            train.addCar(new Car(38,67));
            train.addCar(new Car(79,46));
            train.addCar(new Car(82,11));


            // Insert locomotives into the database
            insertLocomotives(connection, train.getLocomotives());

            // Insert cars into the database
            insertCars(connection, train.getCars());

            // Insert train data into the database
            insertTrain(connection, train);

            int selectedLocomotiveId = 39; // Replace with the desired locomotive ID
            printCapacityById(connection, "locomotives\n", selectedLocomotiveId);

            calculateAndPrintTotalCapacity(connection);

            // Print the total carrying capacity of the assembled train
            System.out.println("Total Carrying Capacity: " + train.getTotalCapacity() + " passengers\n");
            System.out.println("Total Number of Passengers: " + train.getNumberOfPassengers() + " passengers\n");

            // Delete all inserted data from the tables
            //deleteAllData(connection);

            // Print all data from the database after deletion
            //System.out.println("After Deletion:");

            //printAllData(connection);

            //resetCapacity(connection);




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAndPrintTotalCapacity(Connection connection) throws SQLException {
        // Reset the capacity to 0 before calculating
        resetCapacity(connection);

        // Calculate and print the total capacity of the train along with IDs
        String query = "SELECT GROUP_CONCAT(DISTINCT l.id) AS locomotive_ids, GROUP_CONCAT(DISTINCT c.id) AS car_ids, " +
                "SUM(IFNULL(l.capacity, 0) + IFNULL(c.capacity, 0)) AS total_capacity " +
                "FROM train t " +
                "LEFT JOIN locomotives l ON t.locomotive_id = l.id " +
                "LEFT JOIN cars c ON t.car_id = c.id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String locomotiveIds = resultSet.getString("locomotive_ids");
                String carIds = resultSet.getString("car_ids");
                int totalCapacity = resultSet.getInt("total_capacity");

                System.out.println("Locomotive IDs: " + locomotiveIds);
                System.out.println("Car IDs: " + carIds);
                //System.out.println("Total Carrying Capacity: " + totalCapacity + " passengers");
            }
        }
    }

    private static void printAllData(Connection connection) throws SQLException {
        String query = "SELECT * FROM train";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int locomotiveId = resultSet.getInt("locomotive_id");
                int carId = resultSet.getInt("car_id");

                System.out.println("Train ID: " + id);
                System.out.println("Locomotive ID: " + locomotiveId);
                System.out.println("Car ID: " + carId);
                System.out.println("---------------");
            }
        }
    }

    private static void deleteAllData(Connection connection) throws SQLException {
        // Delete all data from the train table
        String deleteTrain = "DELETE FROM train";
        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteTrain)) {
            preparedStatement1.executeUpdate();
        }

        // Delete all data from the locomotives table
        String deleteLocomotives = "DELETE FROM locomotives";
        try (PreparedStatement preparedStatement2 = connection.prepareStatement(deleteLocomotives)) {
            preparedStatement2.executeUpdate();
        }

        // Delete all data from the cars table
        String deleteCars = "DELETE FROM cars";
        try (PreparedStatement preparedStatement3 = connection.prepareStatement(deleteCars)) {
            preparedStatement3.executeUpdate();
        }
    }


    private static void resetCapacity(Connection connection) throws SQLException {
        // Set the capacity to 0 for all entries in the locomotives and cars tables
        String resetLocomotives = "UPDATE locomotives SET capacity = 0";
        String resetCars = "UPDATE cars SET capacity = 0";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(resetLocomotives);
             PreparedStatement preparedStatement2 = connection.prepareStatement(resetCars)) {
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
        }
    }

    private static void insertLocomotives(Connection connection, List<Locomotive> locomotives) throws SQLException {
        String insertLocomotiveSQL = "INSERT INTO locomotives (capacity) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertLocomotiveSQL)) {
            for (Locomotive locomotive : locomotives) {
                preparedStatement.setInt(1, locomotive.getCapacity());
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void insertCars(Connection connection, List<Car> cars) throws SQLException {
        String insertCarSQL = "INSERT INTO cars (capacity) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCarSQL)) {
            for (Car car : cars) {
                preparedStatement.setInt(1, car.getCapacity());
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void printCapacityById(Connection connection, String tableName, int id) throws SQLException {
        String query = "SELECT capacity FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int capacity = resultSet.getInt("capacity");
                System.out.println("Capacity of " + tableName + " with ID " + id + ": " + capacity);
            } else {
                System.out.println("No record found with ID " + id + " in " + tableName);
            }
        }
    }

    private static void insertTrain(Connection connection, Train train) throws SQLException {
        String insertTrainSQL = "INSERT INTO train (locomotive_id, car_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTrainSQL)) {
            int locomotiveId;
            int carId;

            for (Locomotive locomotive : train.getLocomotives()) {
                locomotiveId = getEntityId(connection, "locomotives", locomotive.getCapacity());
                preparedStatement.setInt(1, locomotiveId);
                preparedStatement.setNull(2, java.sql.Types.INTEGER); // Null for car_id
                preparedStatement.executeUpdate();
            }

            for (Car car : train.getCars()) {
                carId = getEntityId(connection, "cars", car.getCapacity());
                preparedStatement.setNull(1, java.sql.Types.INTEGER); // Null for locomotive_id
                preparedStatement.setInt(2, carId);
                preparedStatement.executeUpdate();
            }
        }
    }

    private static int getEntityId(Connection connection, String tableName, int capacity) throws SQLException {
        String getIdSQL = "SELECT id FROM " + tableName + " WHERE capacity = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getIdSQL)) {
            preparedStatement.setInt(1, capacity);
            preparedStatement.execute();

            return preparedStatement.getResultSet().next() ? preparedStatement.getResultSet().getInt("id") : -1;
        }
    }
}
