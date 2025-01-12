package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Subscription;
import database.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class SubscriptionController {

    @FXML
    private TableView<Subscription> subscriptionTable;
    @FXML
    private TableColumn<Subscription, Integer> colId;
    @FXML
    private TableColumn<Subscription, Integer> colCustomerId;
    @FXML
    private TableColumn<Subscription, String> colPlanName;
    @FXML
    private TableColumn<Subscription, Double> colPrice;
    @FXML
    private TableColumn<Subscription, String> colStartDate;
    @FXML
    private TableColumn<Subscription, String> colEndDate;

    @FXML
    private ComboBox<Integer> customerIdField; // Changed to ComboBox for customer selection
    @FXML
    private ComboBox<String> planNameField; // Changed to ComboBox for plan selection
    @FXML
    private TextField priceField; // Keeping TextField for numeric input
    @FXML
    private DatePicker startDateField; // Changed to DatePicker for date selection
    @FXML
    private DatePicker endDateField; // Changed to DatePicker for date selection

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Subscription> subscriptionList = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colPlanName.setCellValueFactory(new PropertyValueFactory<>("planName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        loadSubscriptions();
        loadCustomerIds(); // Load customer IDs for ComboBox
        loadPlanNames();    // Load plan names for ComboBox
    }

    private void loadSubscriptions() {
        subscriptionList.clear();
        try (Connection conn = DBHelper.getConnection()) {
            String sql = "SELECT * FROM subscriptions";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subscriptionList.add(new Subscription(
                        rs.getInt("subscription_id"),
                        rs.getInt("customer_id"),
                        rs.getString("plan_name"),
                        rs.getDouble("price"),
                        rs.getString("start_date"),
                        rs.getString("end_date")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        subscriptionTable.setItems(subscriptionList);
    }

    private void loadCustomerIds() {
        try (Connection conn = DBHelper.getConnection()) {
            String sql = "SELECT customer_id FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ObservableList<Integer> customerIds = FXCollections.observableArrayList();
            while (rs.next()) {
                customerIds.add(rs.getInt("customer_id"));
            }
            customerIdField.setItems(customerIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlanNames() {
        ObservableList<String> planNames = FXCollections.observableArrayList("Basic Plan", "Premium Plan", "Enterprise Plan");
        planNameField.setItems(planNames);
    }

    @FXML
    private void addSubscription() {
        Integer customerId = customerIdField.getValue();
        String planName = planNameField.getValue();
        double price = Double.parseDouble(priceField.getText());
        String startDate = (startDateField.getValue() != null) ? startDateField.getValue().format(DATE_FORMATTER) : null;
        String endDate = (endDateField.getValue() != null) ? endDateField.getValue().format(DATE_FORMATTER) : null;

        try (Connection conn = DBHelper.getConnection()) {
            String sql = "INSERT INTO subscriptions (customer_id, plan_name, price, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setString(2, planName);
            stmt.setDouble(3, price);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            stmt.executeUpdate();
            loadSubscriptions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateSubscription() {
        Subscription selectedSubscription = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selectedSubscription == null) {
            return;
        }

        try (Connection conn = DBHelper.getConnection()) {
            String sql = "UPDATE subscriptions SET customer_id = ?, plan_name = ?, price = ?, start_date = ?, end_date = ? WHERE subscription_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerIdField.getValue());
            stmt.setString(2, planNameField.getValue());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setString(4, startDateField.getValue().format(DATE_FORMATTER));
            stmt.setString(5, endDateField.getValue().format(DATE_FORMATTER));
            stmt.setInt(6, selectedSubscription.getId());
            stmt.executeUpdate();
            loadSubscriptions();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteSubscription() {
        Subscription selectedSubscription = subscriptionTable.getSelectionModel().getSelectedItem();
        if (selectedSubscription == null) {
            return;
        }

        try (Connection conn = DBHelper.getConnection()) {
            String sql = "DELETE FROM subscriptions WHERE subscription_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedSubscription.getId());
            stmt.executeUpdate();
            loadSubscriptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        customerIdField.getSelectionModel().clearSelection();
        planNameField.getSelectionModel().clearSelection();
        priceField.clear();
        startDateField.setValue(null);
        endDateField.setValue(null);
    }
}
