/** Take in neccessary imports from example */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static java.lang.System.out;


public class LoyaltyPoints extends HttpServlet {
    public LoyaltyPoints() {

    }


    /**
     public static void main(String[] args) {
     }
     */

    /** My database plan
     * Name: loyaltySystem
     * Table: Users
     * Columns: Username , Password , PointsTotal
     * Points Total is automatically set to 100
     * */


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Connection connection = null;
        PrintWriter out = response.getWriter();

        try {
            // Establish database connection
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/loyaltySystem?serverTimezone=UTC", "root", "rootroot1");
            if (connection == null) {
                out.println("Failed to connect to the database.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Database connection error.");
            return;
        }

        /** Register System */
        try {
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            if (password != null && password.equals(confirmPassword)) {
                PreparedStatement createUser = connection.prepareStatement(
                        "INSERT INTO Users (Username, Password, PointsTotal) VALUES (?, ?, ?)");
                createUser.setString(1, request.getParameter("username"));
                createUser.setString(2, password);
                createUser.setInt(3, 100); // sets each new user to 100 points
                createUser.executeUpdate();
                out.println("You have now successfully registered! Please refresh the page and log in.");
            } else {
                out.println("Passwords do not match or password is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error registering user.");
        }

        /** Login System */
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username != null && password != null) {
                PreparedStatement checkUser = connection.prepareStatement(
                        "SELECT * FROM Users WHERE Username = ? AND Password = ?");
                checkUser.setString(1, username);
                checkUser.setString(2, password);
                ResultSet rs = checkUser.executeQuery();

                if (rs.next()) {
                    response.sendRedirect("index2.html");
                } else {
                    out.println("User with that username and password combination doesn't exist. Please try again!");
                }
            } else {
                out.println("Username or password is missing.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /** Add or Spend Points and Update Them */
        try {
            String username = request.getParameter("username");
            String action = request.getParameter("action");
            int points;

            if (username == null || action == null) {
                out.println("Missing username or action.");
                return;
            }

            if ("addPoints".equals(action)) {
                points = Integer.parseInt(request.getParameter("addPoints"));
            } else if ("spendPoints".equals(action)) {
                points = Integer.parseInt(request.getParameter("removePoints"));
            } else {
                out.println("Invalid action. Please select addPoints or spendPoints.");
                return;
            }

            PreparedStatement checkPoints = connection.prepareStatement(
                    "SELECT PointsTotal FROM Users WHERE Username = ?");
            checkPoints.setString(1, username);
            ResultSet rs = checkPoints.executeQuery();

            if (rs.next()) {
                int currentPoints = rs.getInt("PointsTotal");
                int newPoints;

                if ("spendPoints".equals(action)) {
                    if (currentPoints >= points) {
                        newPoints = currentPoints - points;
                    } else {
                        out.println("Insufficient points. You have " + currentPoints + " points available.");
                        return;
                    }
                } else {
                    newPoints = currentPoints + points;
                }

                PreparedStatement updatePoints = connection.prepareStatement(
                        "UPDATE Users SET PointsTotal = ? WHERE Username = ?");
                updatePoints.setInt(1, newPoints);
                updatePoints.setString(2, username);

                int rowsUpdated = updatePoints.executeUpdate();
                if (rowsUpdated > 0) {
                    out.println("Transaction successful. New balance: " + newPoints);
                } else {
                    out.println("Error updating points. Please try again.");
                }
            } else {
                out.println("User not found. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error updating points.");
        } catch (NumberFormatException e) {
            out.println("Invalid points value. Please enter a numeric value.");
        }
    }//end of doGet


}//end of class