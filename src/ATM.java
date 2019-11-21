import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
	private Scanner in;
	private BankAccount activeAccount;
	private Bank bank;
	private User newUser;
	
    long accountNo = 0;
    int pin = 0;
	    
	 public static final int VIEW = 1;
	 public static final int DEPOSIT = 2;
	 public static final int WITHDRAW = 3;
	 public static final int LOGOUT = 5;
	 public static final int TRANSFER = 4;
	 public static final int FIRST_NAME_WIDTH = 20;
	 public static final int LAST_NAME_WIDTH = 20;	
	 
	 public static final int INVALID = 0;
     public static final int INSUFFICIENT = 1;
     public static final int SUCCESS = 2;
	 
   
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new instance of the ATM class.
     */
    	 
        public ATM() {
            in = new Scanner(System.in);
            
            activeAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));
            
            try {
    			this.bank = new Bank();
    		} catch (IOException e) {
    			// cleanup any resources (i.e., the Scanner) and exit
    		}
        }
        
        public void startup() {
            System.out.println("Welcome to the AIT ATM!\n");

            while (true) {
             login();
                
                if (isValidLogin(accountNo, pin)) {
                	activeAccount = bank.login(accountNo, pin);
                    System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                    boolean validLogin = true;
                    System.out.println("test4");
                    while (validLogin) {
                        switch (getSelection()) {
                            case VIEW: showBalance(); break;
                            case DEPOSIT: deposit(); break;
                            case WITHDRAW: withdraw(); break;
                            case TRANSFER: transfer(); break;
                            case LOGOUT: validLogin = false; break;
                            default: System.out.println("\nInvalid selection.\n"); break;
                        }
                    }
                } else {
                    if (accountNo == -1 && pin == -1) {
                        shutdown();
                    } else {
                        System.out.println("\nInvalid account number and/or PIN.\n");
                    }
                }
            }
        }

    
        
        public boolean isValidLogin(long accountNo, int pin) {
        	boolean valid = false;
        	try {
        		valid = bank.login(accountNo, pin) != null ? true : false;
        	}catch (Exception e) {
        		valid = false;
        	}
        	
            return valid;
        }
        
        
        public void login() {
        	accountNo = 0;
        	pin = 0;
        	System.out.println("Account No.: ");
        	
        	
        	System.out.println("test");
           if(in.hasNextLong()) {
            	accountNo = in.nextLong();
            } else if(in.nextLine().equals("+")) {
        		makeAccount();
        	}else {
            	System.out.println(accountNo);
            }
            System.out.print("PIN        : ");
            if(in.hasNextInt()) {
            	pin = in.nextInt();
            } else {
            	pin = 0;
            	in.nextLine();
            }
        }
        
       
        public int getSelection() {
            System.out.println("[1] View balance");
            System.out.println("[2] Deposit money");
            System.out.println("[3] Withdraw money");
            System.out.println("[4] Transfer money");
            System.out.println("[5] Logout");
            
            return in.nextInt();
        }
        
        public void showBalance() {
            System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        }
        
        public void deposit() {
            System.out.print("\nEnter amount: ");
            double amount = in.nextDouble();
            
            activeAccount.deposit(amount);
            System.out.println();
        }
        
        public void withdraw() {
            System.out.print("\nEnter amount: ");
            double amount = in.nextDouble();
            
            activeAccount.withdraw(amount);
            System.out.println();
        }
        
        public void transfer() {
        	System.out.print("\nEnter account:");
        	long otherAccount = in.nextLong();
        	
        	System.out.print("\nEnter amount:");
        	double transferAmount = in.nextDouble();
        	
        	BankAccount transferAccount = bank.getAccount(otherAccount);
        	int depositStatus = transferAccount.deposit(transferAmount);
        	
        }
        
        public void makeAccount() {
           System.out.print("\nFirst name:");
           String fName = in.nextLine();
           System.out.print("\nLast name:");
           String lName = in.nextLine();
           System.out.print("\nPIN:");
           int newPIN = in.nextInt();
           in.nextLine();
           
           newUser = new User(fName, lName);
           
           bank.createAccount(newPIN, newUser);
           
           bank.save();
           login();
        }
        
        public void shutdown() {
            if (in != null) {
                in.close();
            }
            
            System.out.println("\nGoodbye!");
            
            System.exit(0);
        }
        
    
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.startup();
    }
}