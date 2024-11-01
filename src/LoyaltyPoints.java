
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
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/loyaltySystem?serverTimezone=UTC","root", "rootroot1"); //my database is called loyaltySystem
        } catch (SQLException e) {
            e.printStackTrace();
        }//end of catch block

        try {
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            if (password.equals(confirmPassword)) { //check if the 2 passwords match
                PreparedStatement createUser = connection.prepareStatement(
                        "INSERT into Users (Username, Password, PointsTotal) VALUES (?, ?, ?)");
                createUser.setString(1, request.getParameter("username"));
                createUser.setString(2, password);
                createUser.setInt(3, 100); //sets each inputted user to 100 points
                createUser.executeUpdate();
                PrintWriter out = response.getWriter();
                out.println("You have now successfully registered!");
            } else {
                PrintWriter out = response.getWriter();
                out.println("Passwords do not match");
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }//end of doGet method
}//end of class
