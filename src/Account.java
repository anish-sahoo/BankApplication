public class Account {
    int accountNumber;
    int balance;
    double interest_rate;
    Account(int accountNumber, int balance, double interest_rate){
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.interest_rate = interest_rate;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getBalance() {
        return balance;
    }

    public double getInterest_rate() {
        return interest_rate;
    }

}
