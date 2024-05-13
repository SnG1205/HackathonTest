package com.example.hackathontest.utils;

import com.example.hackathontest.data.Attorney;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    public Attorney getAttorney(String name) {
        try {
            Attorney attorney = new Attorney(null, 0, 0, null);
            Connection connection = createConnection();
            String attorneyQuery = "SELECT * from attorney where name = ?";
            String casesQuery = "SELECT * from cases where attorney_id = ?";
            PreparedStatement attorneyStatement = connection.prepareStatement(attorneyQuery);
            PreparedStatement casesStatement = connection.prepareStatement(casesQuery);
            attorneyStatement.setString(1, name);
            ResultSet attorneyResultSet = attorneyStatement.executeQuery();
            if (attorneyResultSet.next()) {
                attorney.setName(attorneyResultSet.getString(2));
                attorney.setWonCases(attorneyResultSet.getInt(3));
                attorney.setLostCases(attorneyResultSet.getInt(4));

                casesStatement.setInt(1, attorneyResultSet.getInt(1));
                ResultSet casesResultSet = casesStatement.executeQuery();
                List<String> casesList = new ArrayList<>();
                while (casesResultSet.next()) {
                    casesList.add(casesResultSet.getString(3));
                }
                attorney.setCases(casesList);
            }
            connection.close();
            return attorney;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public void addAttorney(Attorney attorney) {
        try {
            Connection connection = createConnection();
            String insertQuery = "INSERT INTO attorney(name, won_cases, lost_cases) VALUES(?,?,?)";
            String selectQuery = "SELECT id FROM attorney WHERE name = ?";
            String nameOfAttorney = attorney.getName().replace("'", "");

            PreparedStatement preparedInsertStatement = connection.prepareStatement(insertQuery);
            preparedInsertStatement.setString(1, nameOfAttorney);
            preparedInsertStatement.setInt(2, attorney.getWonCases());
            preparedInsertStatement.setInt(3, attorney.getLostCases());
            preparedInsertStatement.execute();

            PreparedStatement preparedSelectStatement = connection.prepareStatement(selectQuery);
            preparedSelectStatement.setString(1, nameOfAttorney);
            ResultSet resultSet = preparedSelectStatement.executeQuery();
            if (resultSet.next()) {
                int attorneyId = resultSet.getInt(1);
                addCases(attorney.getCases(), connection, attorneyId);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void addCases(List<String> cases, Connection connection, int attorneyId) {
        try {
            String query = "INSERT INTO cases(attorney_id, link) VALUES(?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, attorneyId);
            for (String s : cases) {
                try {
                    preparedStatement.setString(2, s);
                    preparedStatement.execute();
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hackathon", "root", "password");
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // Hier die neue Methode hinzufügen
    public List<String> getCaseLinksByAttorney(String attorneyName) {
        List<String> caseLinks = new ArrayList<>();
        try (Connection connection = createConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT link FROM cases JOIN attorney ON cases.attorney_id = attorney.id WHERE attorney.name = ?")) {
            statement.setString(1, attorneyName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                caseLinks.add(resultSet.getString("link"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caseLinks;
    }
}
