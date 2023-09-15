package com.user1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EmployeeSearchApp extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultTextArea;

    public EmployeeSearchApp() {
        setTitle("Employee Search");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        resultTextArea = new JTextArea(10, 30);

        panel.add(new JLabel("Enter Employee Name, City, or Years of Exp:"));
        panel.add(searchField);
        panel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
    }

    private void performSearch() {
        String searchQuery = searchField.getText();
        String sql = "SELECT emp_name, years_of_exp, city FROM employee e " +
                     "LEFT JOIN address a ON e.address_id = a.address_id " +
                     "WHERE emp_name LIKE ? OR city LIKE ? OR years_of_exp = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Employee-Database", "root", "Sree27@532");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, "%" + searchQuery + "%");
            try {
                int yearsExp = Integer.parseInt(searchQuery);
                preparedStatement.setInt(3, yearsExp);
            } catch (NumberFormatException e) {
                preparedStatement.setInt(3, -1); // Set to -1 if it's not a valid number.
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder resultText = new StringBuilder();
            while (resultSet.next()) {
                String empName = resultSet.getString("emp_name");
                int yearsExp = resultSet.getInt("years_of_exp");
                String city = resultSet.getString("city");

                resultText.append("Employee Name: ").append(empName)
                        .append(", Years of Exp: ").append(yearsExp)
                        .append(", City: ").append(city).append("\n");
            }

            resultTextArea.setText(resultText.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EmployeeSearchApp().setVisible(true);
            }
        });
    }

}
