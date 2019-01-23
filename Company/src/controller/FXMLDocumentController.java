package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.sql.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Employees;

public class FXMLDocumentController {

    @FXML
    private TableView table;
    @FXML
    private JFXTextField nameTxtInput, addrsTxtInput, ageTxtField, incomeTxtInput, findByTxtInput;
    @FXML
    private JFXTextField ageFromTxt, ageBellowTxt, fromAndAge, bellowAndAge,idFromTxt,idBellowTxt,fromAndId,bellowAndId;
    @FXML
    private Label ageError, incomeError;
    @FXML
    private ToggleGroup findByGroup;
    @FXML
    private JFXRadioButton nameToggle, ageToggle, idToggle, addressToggle;
    @FXML
    private JFXButton fromAge, bellowAge, fromAndBellowAge,fromId,bellowId,fromAndBellowId;
    
    private Stage stage;
    public static final String USERNAME = "root";
    public static final String PASSWORD = "klisa021";
    private static final String CONNECTION = "jdbc:mysql://localhost:3306/company";
    Connection conn;
    String query;
    StringBuilder value;
    int age, age2;
    Employees employee;
    ObservableList<Employees> employeesList = FXCollections.<Employees>observableArrayList();

    public void initialize() throws SQLException {
        try {
            conn = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Employees empl = (Employees) table.getSelectionModel().getSelectedItem();
                nameTxtInput.setText(empl.getName());
                addrsTxtInput.setText(empl.getAddress());
                ageTxtField.setText(String.valueOf(empl.getAge()));
                incomeTxtInput.setText(String.valueOf(empl.getIncome()));
            }
        });

        nameToggle.setUserData("Name");
        idToggle.setUserData("ID");
        addressToggle.setUserData("Address");
        ageToggle.setUserData("Age");

    }

    //METODE ZA DODAVANJE,IZMENU I BRISANJE ZAPOSLENIH
    //KREIRANJE OBJEKATA I SMESTANJE U BAZU
    @FXML
    private void insertEmp() throws SQLException {
        PreparedStatement st = conn.prepareStatement("insert into employees(name,address,age,income) values (?,?,?,?)");

        //KREIRANJE OBJEKTA I SMESTANJE U TABELU
        employee = new Employees();

        employee.setName(nameTxtInput.getText());
        employee.setAddress(addrsTxtInput.getText());
        try {
            employee.setAge(Integer.parseInt(ageTxtField.getText()));
        } catch (NumberFormatException e) {
            ageError.setVisible(true);
        }
        try {
            employee.setIncome(Double.parseDouble(incomeTxtInput.getText()));
        } catch (NumberFormatException e) {
            incomeError.setVisible(true);
        }

        if (employee.inputIsValid()) {
            //UBACIVANJE ZAPOSLENOG U BAZU PODATAKA
            st.setString(1, employee.getName());
            st.setString(2, employee.getAddress());
            st.setString(3, String.valueOf(employee.getAge()));
            st.setString(4, String.valueOf(employee.getIncome()));
            st.execute();

            //POSTAVLJANJE ID-A ZAPOSLENOG
            ResultSet rs = st.executeQuery("select last_insert_id() as id from employees");

            rs.next();
            employee.setId(Integer.valueOf(rs.getString("id")));

            //UBACIVANJE ZAPOSLENOG U LISTU I U TABELU
            employeesList.add(new Employees(employee.getId(), employee.getName(), employee.getAddress(), employee.getAge(), employee.getIncome()));
            table.setItems(employeesList);
            Alert alert;
            alert = new Alert(Alert.AlertType.INFORMATION, employee.saveEmployee(), ButtonType.OK);
            alert.setHeaderText("Employee created successfuly!");
            alert.show();
            ageError.setVisible(false);
            incomeError.setVisible(false);
        } else {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR, employee.saveEmployee(), ButtonType.OK);
            alert.setHeaderText("Error");
            alert.show();
        }

    }

    //IZMENA ZAPOSLENOG
    @FXML
    private void updateEmployee() throws SQLException {
        //PREUZIMANJE SELEKTOVANOG ITEMA I KREIRANJE NOVOG OBJEKTA
        Employees empl = (Employees) table.getSelectionModel().getSelectedItem();
        Employees updtEmpl = new Employees(empl.getId(), empl.getName(), empl.getAddress(), empl.getAge(), empl.getIncome());
        //IZMENA VREDNOSTI
        updtEmpl.setName(nameTxtInput.getText());
        updtEmpl.setAddress(addrsTxtInput.getText());
        try {
            updtEmpl.setAge(Integer.parseInt(ageTxtField.getText()));
        } catch (NumberFormatException e) {
            ageError.setVisible(true);
        }
        try {
            updtEmpl.setIncome(Double.parseDouble(incomeTxtInput.getText()));
        } catch (NumberFormatException e) {
            incomeError.setVisible(true);
        }

        //PROVERA ISPRAVNOSTI UNETIH PODATAKA I UBACIVANJE U BAZU I IZMENA OBJEKTA IZ TABELE
        if (updtEmpl.inputIsValid()) {
            Statement st = conn.createStatement();
            st.execute("update employees set name='" + updtEmpl.getName() + "',address='" + updtEmpl.getAddress() + "',age='" + updtEmpl.getAge() + "',income='"
                    + updtEmpl.getIncome() + "' where id=" + updtEmpl.getId());

            table.getItems().remove(empl);
            table.getItems().add(updtEmpl);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Employee:\n" + empl + "has been updated.", ButtonType.OK);
            alert.setHeaderText("Employee updated successfuly");
            alert.show();
            ageError.setVisible(false);
            incomeError.setVisible(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!", ButtonType.OK);
            alert.setHeaderText("Someting went wrong");
            alert.show();
        }
    }

    //BRISANJE ZAPOSLENOG
    @FXML
    private void deleteEmployee() {
        Employees empl = (Employees) table.getSelectionModel().getSelectedItem();
        Statement st;
        try {
            st = conn.createStatement();
            st.execute("delete from employees where id = " + empl.getId());
            table.getItems().remove(empl);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Employee\n" + empl + "\ndeleted successfuly!", ButtonType.OK);
            alert.setHeaderText("Employee deleted");
            alert.show();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.CLOSE);
            alert.setHeaderText("Error");
            alert.show();
        }

    }

    @FXML
    private void clearInputs() {
        nameTxtInput.clear();
        addrsTxtInput.clear();
        ageTxtField.clear();
        incomeTxtInput.clear();
    }

    //METODE ZA PRETRAGU
    //izlistavanje svih zaposlenih
    @FXML
    private void listAll() {
        employee = null;

        //Ciscenje tabele
        table.getItems().clear();

        //Listanje podataka iz baze i ubacivanje u tabelu   
        try {
            Statement st = conn.createStatement();
            st.execute("select * from employees");
            ResultSet rs = st.getResultSet();
            ObservableList<Employees> allEmp = FXCollections.observableArrayList();

            while (rs.next()) {
                employee = new Employees(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getInt("age"), rs.getDouble("income"));
                allEmp.add(employee);
            }
            table.getItems().addAll(allEmp);

        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.CLOSE);
            alert.setHeaderText("Something went wrong");
            alert.show();
        }

    }

    //pretraga po kolonama
    @FXML
    private void findBy() {
        query = "";
        String toggle;

        if (findByGroup.getSelectedToggle() != null) {
            toggle = findByGroup.getSelectedToggle().getUserData().toString();

            switch (toggle) {
                case "Name":
                    query = "name";
                    break;
                case "Address":
                    query = "address";
                    break;
                case "Age":
                    query = "age";
                    break;
                case "ID":
                    query = "id";
                    break;
            }
            value = new StringBuilder();
            value.append("%");
            String str = findByTxtInput.getText();
            value.append(str);
            value.append("%");

            try {
                Statement st = conn.createStatement();
                st.execute("select * from employees where " + query + " LIKE '" + value + "'");
                ResultSet rs = st.getResultSet();
                ObservableList<Employees> findEmpl = FXCollections.observableArrayList();
                while (rs.next()) {
                    employee = new Employees(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getInt("age"), rs.getDouble("income"));
                    findEmpl.add(employee);
                }
                table.getItems().clear();
                table.setItems(findEmpl);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    //pretraga po godinama
    @FXML
    private void findByAge(ActionEvent event) throws SQLException {
        value = new StringBuilder();
        query = "";
        if (event.getSource() == fromAge) {
            query = ageFromTxt.getText();

            value.append("select * from employees where age > ");
            value.append(query);
        }
        if (event.getSource() == bellowAge) {
            query = ageBellowTxt.getText();

            value.append("select * from employees where age < ");
            value.append(query);
        }
        if (event.getSource() == fromAndBellowAge) {
            query = fromAndAge.getText();
            value.append("select * from employees where age BETWEEN  ").append(query).append(" AND ").append(bellowAndAge.getText());
            System.out.println(value.toString());
        }
        try {
            Statement st = conn.createStatement();
            st.execute(value.toString());
            ResultSet rs = st.getResultSet();
            ObservableList<Employees> findEmpl = FXCollections.observableArrayList();
            while (rs.next()) {
                employee = new Employees(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getInt("age"), rs.getDouble("income"));
                findEmpl.add(employee);
            }
            table.getItems().clear();
            table.setItems(findEmpl);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    @FXML
    private void findByID(ActionEvent event) throws SQLException {
        value = new StringBuilder();
        query = "";
        if (event.getSource() == fromId) {
            query = idFromTxt.getText();

            value.append("select * from employees where id > ");
            value.append(query);
        }
        if (event.getSource() == bellowId) {
            query = idBellowTxt.getText();

            value.append("select * from employees where id < ");
            value.append(query);
        }
        if (event.getSource() == fromAndBellowId) {
            query = fromAndId.getText();
            value.append("select * from employees where id BETWEEN  ").append(query).append(" AND ").append(bellowAndId.getText());
            System.out.println(value.toString());
        }
        try {
            Statement st = conn.createStatement();
            st.execute(value.toString());
            ResultSet rs = st.getResultSet();
            ObservableList<Employees> findEmpl = FXCollections.observableArrayList();
            while (rs.next()) {
                employee = new Employees(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getInt("age"), rs.getDouble("income"));
                findEmpl.add(employee);
            }
            table.getItems().clear();
            table.setItems(findEmpl);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    //METODE ZA WINODW
    @FXML
    private void closeWindow() throws SQLException {
        conn.close();
        Platform.exit();
    }

    @FXML
    private void minimizeWindow() throws IOException {
        stage = (Stage) ageError.getScene().getWindow();
        stage.setIconified(true);
    }

}
