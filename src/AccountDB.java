import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDB {
    Statement statement;

    private static final String TABLE_NAME = "accountsTable";
    private static final String ID_COL = "id";
    private static final String ACCOUNT_COL = "accNo";
    public static final String BALANCE_COL = "balance";
    public static final String INTEREST_RATE_COL = "interestRate";

    AccountDB(Connection connection) {
        try {
            statement = connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ACCOUNT_COL + " INTEGER,"
                    + INTEREST_RATE_COL + " REAL,"
                    + BALANCE_COL + " INTEGER)";

            statement.execute(query);
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    boolean addAccount(Account ac) throws SQLException {
        String query = "INSERT INTO "+ TABLE_NAME + " (" + ACCOUNT_COL + "," + BALANCE_COL + "," + INTEREST_RATE_COL + ") " +
                "VALUES (" + ac.getAccountNumber() + "," + 0 + "," + ac.getInterest_rate()+");";

        if(!checkIfAccountExists(ac.getAccountNumber())) {
            statement.execute(query);
            return true;
        }
        else return false;
    }

    boolean checkIfAccountExists(int accountNumber) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME + ";");

        int acc;

        while (rs.next()){
            acc = rs.getInt(ACCOUNT_COL);
            if(accountNumber == acc)
                return true;
        }
        return false;
    }

    Account getAccount(int accountNumber) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME + ";");

        while (rs.next()){
            int acc = rs.getInt(ACCOUNT_COL);
            if(accountNumber == acc){
                return new Account(accountNumber,rs.getInt(BALANCE_COL),rs.getDouble(INTEREST_RATE_COL));
            }
        }
        return null;
    }

    int transaction(int type, int amount, int accountNumber) throws SQLException {
        Account acc = getAccount(accountNumber);
        int newBalance;
        if(type == 1){
            newBalance = acc.getBalance() + amount;
        }
        else newBalance = acc.getBalance() - amount;

        statement.execute("UPDATE accountsTable SET "+BALANCE_COL+"="+newBalance+" WHERE "+ ACCOUNT_COL +" ="+accountNumber);

        return newBalance;
    }
}
