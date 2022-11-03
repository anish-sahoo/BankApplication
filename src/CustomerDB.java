import java.sql.*;

public class CustomerDB {
    Statement statement;

    private static final String TABLE_NAME = "customerTable";
    private static final String ID_COL = "id";
    private static final String FIRST_NAME_COL = "FName";
    public static final String LAST_NAME_COL = "LName";
    public static final String ADDRESS_COL = "address";
    public static final String PHONE_NUM_COL = "phoneNum";
    public static final String ACCOUNT_NO_COL = "accNum";

    CustomerDB(Connection connection) throws SQLException {
        try {
            statement = connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIRST_NAME_COL + " TEXT,"
                    + LAST_NAME_COL + " TEXT,"
                    + ADDRESS_COL + " TEXT,"
                    + PHONE_NUM_COL + " INTEGER,"
                    + ACCOUNT_NO_COL + " INTEGER)";

            statement.execute(query);
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    int addCustomer(Customer c){
        try {
            String query = "INSERT INTO "+ TABLE_NAME + " (" + FIRST_NAME_COL + "," + LAST_NAME_COL + "," + ADDRESS_COL + "," + PHONE_NUM_COL + "," + ACCOUNT_NO_COL + ") " +
                    "VALUES ('" + c.getFirstName() + "','" + c.getLastName() + "','" + c.getAddress() + "'," + c.getPhoneNumber() + ","+ c.getAccountNumber()  +");";
            statement.execute(query);

            ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME + ";");

            int id = -1;
            String fname = "", lname = "";

            while (rs.next()){
                id = rs.getInt(ID_COL);
                fname = rs.getString(FIRST_NAME_COL);
                lname = rs.getString(LAST_NAME_COL);
            }

            System.out.println("New Customer added, details:");
            System.out.print("ID = " + id + "\t");
            System.out.print("First name = " + fname + "\t");
            System.out.print("Last name = " + lname + "\t");
            return id;
        }
        catch (Exception e){
            System.err.println(e);
        }
        return -1;
    }

    Customer getCustomer(int customerId) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM customerTable where id = "+customerId+";");
        Customer c = null;
        while (rs.next()){
            c = new Customer(rs.getString(2),rs.getString(3),rs.getString(4),
                    rs.getInt(5),rs.getInt(6),rs.getInt(1));
        }
        return c;
    }

    void updateCustomer(int custId, int accNo) throws SQLException {
        statement.execute("UPDATE customerTable SET "+ACCOUNT_NO_COL+"="+accNo+" WHERE id ="+custId);
    }
}
