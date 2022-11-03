import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankApplication extends JFrame{

    JButton SearchCustomer, AddCustomer, OpenAccount, Deposit, Withdraw, CalculateInterest, Reset, addAccountToCustomer;
    JTextField customer_fname, customer_lname, address, phoneNumber, transactionAmountField, interestMonths, interestAmount, account_number, currentBalance, customerId, interestRate;
    JLabel c_fname, c_lname, c_addr, c_phoneNumber, c_accountNumber, withdraw_deposit, interest_no_of_month, interest_value, current_balance, customer_id;
    JPanel panel1, panel2;
    public static final String DB_URL = "jdbc:sqlite:bank.sqlite";
    Connection c = DriverManager.getConnection(DB_URL);
    CustomerDB c_db = new CustomerDB(c);
    AccountDB a_db = new AccountDB(c);

    public BankApplication() throws SQLException {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println(e);
        }
        initializeComponents();
    }

    void initializeComponents(){
        setTitle("Banking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        BorderLayout b = new BorderLayout();
        setLayout(b);

        createObjects();
        SearchCustomer.addActionListener(l -> {
            try {
                searchCustomer();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        AddCustomer.addActionListener(l -> {
            try {
                addCustomer();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        OpenAccount.addActionListener(l -> {
            try {
                openAccount();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Deposit.addActionListener(l -> {
            try {
                transact(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Withdraw.addActionListener(l -> {
            try {
                transact(-1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        CalculateInterest.addActionListener(l -> calculateInterest());
        Reset.addActionListener(l -> reset());
        addAccountToCustomer.addActionListener(l -> {
            try {
                attachAccountToCustomer();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        pack();
        setVisible(true);
    }

    private void attachAccountToCustomer() throws SQLException {
        int custId = Integer.parseInt(customerId.getText());
        Customer cust = c_db.getCustomer(custId);

        if(cust != null) {
            if(a_db.checkIfAccountExists(Integer.parseInt(account_number.getText()))){
                c_db.updateCustomer(custId, Integer.parseInt(account_number.getText()));
                JOptionPane.showMessageDialog(new JFrame("Success"),"Account attached to Customer!");
            }
            else JOptionPane.showMessageDialog(new JFrame("Error"),"Account does not exist!\nOpen account and try again!");
        }
        else JOptionPane.showMessageDialog(new JFrame("Not Found!"),
                "No Customer found!" );
    }

    private void reset(){
        customer_fname.setText("");
        customer_lname.setText("");
        address.setText("");
        phoneNumber.setText("");
        customerId.setText("");
        account_number.setText("");
        currentBalance.setText("");
        interestRate.setText("");
        transactionAmountField.setText("");
        interestMonths.setText("");
        interestAmount.setText("");

        customerId.setEnabled(true);
        account_number.setEnabled(true);

        customer_fname.setEnabled(true);
        customer_lname.setEnabled(true);
        address.setEnabled(true);
        phoneNumber.setEnabled(true);
        interestRate.setEnabled(true);
        transactionAmountField.setEnabled(false);

        OpenAccount.setVisible(true);
        AddCustomer.setVisible(true);
        addAccountToCustomer.setVisible(false);
        CalculateInterest.setVisible(false);
        Deposit.setVisible(false);
        Withdraw.setVisible(false);
    }

    private void calculateInterest() {
        double interest_rate = Double.parseDouble(interestRate.getText());
        int balance = Integer.parseInt(currentBalance.getText());
        int months = Integer.parseInt(interestMonths.getText());
        interestAmount.setText(((interest_rate*balance*months)/100.0) + "");
    }

    private void transact(int i) throws SQLException {
        int accountNumber = Integer.parseInt(account_number.getText());
        int amount = Integer.parseInt(transactionAmountField.getText());

        if(amount > a_db.getAccount(accountNumber).getBalance() && i==-1){
            JOptionPane.showMessageDialog(new JFrame("Not enough funds!"),"Transaction amount higher than balance!" );
            return;
        }
        currentBalance.setText(a_db.transaction(i,amount,accountNumber)+"");
        transactionAmountField.setText("");
    }

    private void openAccount() throws SQLException {
        int accNo = Integer.parseInt(account_number.getText());
        double intRate = Double.parseDouble(interestRate.getText());

        if(a_db.addAccount(new Account(accNo,0,intRate)))
            JOptionPane.showMessageDialog(new JFrame("Success!"),"Account added successfully,\nAccount Number:"+accNo);
        else
            JOptionPane.showMessageDialog(new JFrame("Failure!"),"Error!");
    }

    private void addCustomer() throws SQLException {
        customerId.setText("");
        customerId.setEnabled(false);

        String fname = customer_fname.getText();
        String lname = customer_lname.getText();
        String add = address.getText();
        int phoneN = Integer.parseInt(phoneNumber.getText());
        int accN; double interestR = -1;
        if(!account_number.getText().isEmpty() && !interestRate.getText().isEmpty()) {
            accN = Integer.parseInt(account_number.getText());
            interestR = Double.parseDouble(interestRate.getText());
        }
        else accN = -1;
        String str = "";
        if(a_db.checkIfAccountExists(accN) && accN != -1){
            account_number.setText(accN+"");
            interestRate.setText(a_db.getAccount(accN).getInterest_rate()+"");
            currentBalance.setText(a_db.getAccount(accN).getBalance()+"");
        }
        else if(accN != -1 && interestR != -1){
            a_db.addAccount(new Account(accN,0,interestR));
            str += "Account has been created!\n\n";
        }
        else {
            account_number.setText("");
            interestRate.setText("");
            currentBalance.setText("");
        }

        Customer newCustomer = new Customer(fname,lname,add,phoneN,accN,-2);
        int customerID = c_db.addCustomer(newCustomer);
        newCustomer.setCustomerId(customerID);

        JOptionPane.showMessageDialog(new JFrame("New Customer Added successfully!"),
                str+"Customer has been added! \nCustomer id = " + customerID + "\nRemember this id!");

        customerId.setText(customerID+"");
    }

    private void searchCustomer() throws SQLException {
        int CustomerID = 0;
        CustomerID = Integer.parseInt(customerId.getText());
        Customer cust = c_db.getCustomer(CustomerID);

        if(cust != null) {
            customer_fname.setText(cust.getFirstName());
            customer_lname.setText(cust.getLastName());
            address.setText(cust.getAddress());
            phoneNumber.setText(cust.getPhoneNumber() + "");

            customer_fname.setEnabled(false);
            customer_lname.setEnabled(false);
            address.setEnabled(false);
            phoneNumber.setEnabled(false);
            transactionAmountField.setEnabled(true);

            OpenAccount.setVisible(false);
            AddCustomer.setVisible(false);

            if(cust.getAccountNumber()!=-1) {
                account_number.setText(cust.getAccountNumber() + "");
                currentBalance.setText(a_db.getAccount(cust.getAccountNumber()).getBalance()+"");
                interestRate.setText(a_db.getAccount(cust.getAccountNumber()).getInterest_rate()+"");
                account_number.setEnabled(false);
                interestRate.setEnabled(false);

                CalculateInterest.setVisible(true);
                Withdraw.setVisible(true);
                Deposit.setVisible(true);
            }
            else {
                account_number.setText("");
                currentBalance.setText("");
                interestRate.setText("");
                addAccountToCustomer.setVisible(true);
            }
        }
        else JOptionPane.showMessageDialog(new JFrame("Not Found!"),
                "No Customer found!" );
    }

    void createObjects(){
        SearchCustomer = new JButton("Search Customer");
        AddCustomer = new JButton("Add New Customer");
        Deposit = new JButton("Deposit");
        Withdraw = new JButton("Withdraw");
        CalculateInterest = new JButton("Calculate Interest");
        OpenAccount = new JButton("Open New Account");
        Reset = new JButton("Reset");
        addAccountToCustomer = new JButton("Attach account to customer");

        addAccountToCustomer.setVisible(false);
        CalculateInterest.setVisible(false);
        Deposit.setVisible(false);
        Withdraw.setVisible(false);

        transactionAmountField = new JTextField();
        interestMonths = new JTextField();
        interestAmount = new JTextField();
        currentBalance = new JTextField();
        customer_fname = new JTextField();
        customer_lname = new JTextField();
        address = new JTextField();
        phoneNumber = new JTextField();
        account_number = new JTextField();
        customerId = new JTextField();
        interestRate = new JTextField();

        currentBalance.setEnabled(false);
        interestAmount.setEnabled(false);
        transactionAmountField.setEnabled(false);

        customer_id = new JLabel("Customer ID:");
        withdraw_deposit = new JLabel("Withdraw/Deposit:");
        interest_no_of_month = new JLabel("Number of Months:");
        interest_value = new JLabel("Interest Amount:");
        current_balance = new JLabel("Current Balance:");
        c_fname = new JLabel("Customer First Name:");
        c_addr = new JLabel("Address:");
        c_phoneNumber = new JLabel("Phone Number:");
        c_accountNumber = new JLabel("Account Number:");
        c_lname = new JLabel("Customer Last Name:");

        panel1 = new JPanel(new GridLayout(9,3,20,8));
        panel1.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel1.add(customer_id); panel1.add(customerId); panel1.add(SearchCustomer);
        panel1.add(c_fname); panel1.add(customer_fname); panel1.add(AddCustomer);
        panel1.add(c_lname); panel1.add(customer_lname); panel1.add(OpenAccount);
        panel1.add(c_addr); panel1.add(address); panel1.add(CalculateInterest);
        panel1.add(c_phoneNumber); panel1.add(phoneNumber); panel1.add(addAccountToCustomer);
        panel1.add(c_accountNumber); panel1.add(account_number); panel1.add(Reset);
        panel1.add(current_balance); panel1.add(currentBalance); panel1.add(new JLabel(""));
        panel1.add(new JLabel("Interest Rate (% per month):")); panel1.add(interestRate); panel1.add(Withdraw);
        panel1.add(withdraw_deposit); panel1.add(transactionAmountField); panel1.add(Deposit);

        add(panel1, BorderLayout.PAGE_START);

        panel2 = new JPanel(new GridLayout(1,4,10,0));
        panel2.add(interest_no_of_month);
        panel2.add(interestMonths);
        panel2.add(interest_value);
        panel2.add(interestAmount);
        panel2.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

        add(panel2, BorderLayout.CENTER);

        JPanel panel3 = new JPanel(new GridLayout(4,1,0,10));
        panel3.add(new JLabel("Recommended steps for creating a new account for a new customer:"));
        panel3.add(new JLabel("1) Add customer, and leave account no. empty if you don't want to associate an account yet. "));
        panel3.add(new JLabel("2) If you left account no. empty, you can search customer by customer ID and attach account later."));
        panel3.add(new JLabel("3) All accounts start with 0 balance, deposit funds!"));
        panel3.setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
        add(panel3,BorderLayout.PAGE_END);
    }

    public static void main(String[] args) throws SQLException {
        new BankApplication();
    }
}
