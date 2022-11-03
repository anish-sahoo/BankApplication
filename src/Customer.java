public class Customer {
    String firstName, lastName, address;
    int phoneNumber, accountNumber, customerId;

    Customer(String Fname, String Lname, String address, int phoneNumber, int account_number, int customer_id){
        firstName = Fname;
        lastName = Lname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        accountNumber = account_number;
        customerId = customer_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
