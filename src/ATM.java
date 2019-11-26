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
     public static final int OVERFILL = 3; 
     
	 
   
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
            	long accountNo = 0;
            	System.out.print("Account No.: ");
            	String accountNoTemp = in.nextLine();
            	if (accountNoTemp.isEmpty()) {
            		//System.out.println("1");
            		accountNo = 0;
            		pin = getPin();
            		attemptLogin(accountNo, pin);
            	} else if (accountNoTemp.strip().equals("+")) {
            		//System.out.println("2");
            		accountNo = 0;
            		makeAccount();	
            	} else if(isNumber(accountNoTemp)) {
            		//System.out.println("5");
                	accountNo = Long.valueOf(accountNoTemp);
                	pin = getPin();
                	attemptLogin(accountNo, pin);
            	} else if(accountNoTemp.equals("-1")) {
            		//System.out.println("3");
            		accountNo = -1;
                	pin = getPin();
                	attemptLogin(accountNo, pin);
            	} else {
            		//System.out.println("4");
            		accountNo = 0;
            		pin = getPin();
            		attemptLogin(accountNo, pin);
            	}
               
        }
       }

            public void attemptLogin(long accountNo, int pin) {
            	if (isValidLogin(accountNo, pin)) {	
                	activeAccount = bank.login(accountNo, pin);
                    System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                    boolean validLogin = true;
                    while (validLogin) {
                    	
                        switch (getSelection()) {
                            case VIEW: showBalance(); break;
                            case DEPOSIT: deposit(); break;
                            case WITHDRAW: withdraw(); break;
                            case TRANSFER: transfer(); break;
                            case LOGOUT: validLogin = false; in.nextLine(); break;
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
        
        public boolean isValidLogin(long accountNo, int pin) {
        	boolean valid = false;
        	try {
        		valid = bank.login(accountNo, pin) != null ? true : false;
        	}catch (Exception e) {
        		valid = false;
        	}
        	
            return valid;
        }
        
        public int getPin() {
        	int pin = 0;
        	System.out.print("PIN        : ");
        	String tempPin = in.nextLine();
        	if (tempPin.isEmpty()) {
        		//System.out.println("1");
        		pin = 0;
        	} else if (isNumber(tempPin)) {
        		//System.out.println("2");
        		pin = Integer.valueOf(tempPin);
        	} else {
        		//System.out.println("3");
        		pin = -1;
        	}
        	
        	return pin;
        	
        }
        
        //use to see if an input is a number
        public boolean isNumber(String checkThis) {
        	boolean numeric = true;
        	for (int i = 0; i < checkThis.length(); i++ ) {
                char char1 = checkThis.charAt(i);
                if (!Character.isDigit(char1)) {
                  numeric = false;
        }
      }
        	return numeric;
    }
       
        public int getSelection() {
            System.out.println("[1] View balance");
            System.out.println("[2] Deposit money");
            System.out.println("[3] Withdraw money");
            System.out.println("[4] Transfer money");
            System.out.println("[5] Logout");
            
            
            if(in.hasNextInt()) {
            	return in.nextInt();
            } else {
            	in.nextLine();
            	return 0;
            }
            
        }
        
        public void showBalance() {
            System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        }
        
        public void deposit() {
        	
        	double amount = 0;
			boolean valid = true;
    		System.out.print("\nEnter amount: ");
    		try {
    			amount = in.nextDouble();
    		}catch(Exception e) {
    			valid = false;
    			in.nextLine();
    		}
    		 if (valid) {
    			 int depositStatus = activeAccount.deposit(amount);
    		 if (depositStatus == ATM.INVALID) {
                 System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n"); 
             } else if(depositStatus == ATM.OVERFILL) {
             	System.out.println("\nDeposit rejected. Depositing this amount would cause the balance to exceed $999,999,999,999.99.\n");
             } else if (depositStatus == ATM.SUCCESS) {
                  System.out.println("\nDeposit accepted.\n");
                 bank.update(activeAccount);
                 bank.save();
                 
             }
 		} else {
 			System.out.println("\nDeposit rejected. Enter vaild amount.\n");
 	   }
       
   }
        
        public void withdraw() {
        	double amount = 0;
			boolean valid = true;
    		System.out.print("\nEnter amount: ");
    		try {
    			amount = in.nextDouble();
    		}catch(Exception e) {
    			valid = false;
    			in.nextLine();
    		}
    		if(valid) {
    			int status = activeAccount.withdraw(amount);
                if (status == ATM.INVALID) {
                    System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
                } else if (status == ATM.INSUFFICIENT) {
                    System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
                } else if (status == ATM.SUCCESS) {
                    System.out.println("\nWithdrawal accepted.\n");
                    bank.update(activeAccount);
                    bank.save();
                }
    		} else {
    			System.out.println("\nWithdrawal rejected. Enter vaild amount.\n");
    		}
        }
        
        public void transfer() {
        	long destinationAccount;
        	boolean valid = true;
            System.out.print("\nEnter account: ");
            if (in.hasNextLong()) {
            	destinationAccount = in.nextLong();
            } else {
            	destinationAccount = 0;
            	in.nextLine();
            	in.nextLine();
            }

            System.out.print("Enter amount: ");
            double amount = in.nextDouble();
            if(bank.getAccount(destinationAccount) == null) {
            	valid = false;
            }
            if (valid) {
            	BankAccount transferAccount = bank.getAccount(destinationAccount);
            	int withdrawStatus = activeAccount.withdraw(amount);
            	if (withdrawStatus == ATM.INVALID) {
                    System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
                } else if (withdrawStatus == ATM.INSUFFICIENT) {
                    System.out.println("\nTransfer rejected. Insufficient funds.\n");
                } else if (withdrawStatus == ATM.SUCCESS) {
                	int depositStatus = transferAccount.deposit(amount);
                    if (depositStatus == ATM.OVERFILL) {
                        System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.\n");
                    } else if (depositStatus == ATM.SUCCESS) {
                    	System.out.println("\nTransfer accepted.\n");
                    	bank.update(activeAccount);
                    	bank.save();
                    }
                }
            } else {
            	System.out.println("\nTransfer rejected. Destination account not found.\n");
            }
        	
        }
        
        public void makeAccount() {
           System.out.print("\nFirst name:");
           String fName = in.nextLine();
           if (fName != null && fName.length() <= 20 && fName.length() > 0) {
        	   System.out.print("\nLast name:");
               String lName = in.nextLine();
               if (lName != null && lName.length() <= 30 && lName.length() > 0) {
            	   System.out.print("\nPIN:");
                   String tempPin = in.nextLine();
                   if (tempPin.isEmpty()) {
                	   pin = 0;
                   } else if (isNumber(tempPin)) {
                	   pin = Integer.valueOf(tempPin);
                   } 
                   if(pin >= 1000 && pin <= 9999) {
               		newUser = new User(fName, lName);
                      	
                   BankAccount newAccount = bank.createAccount(pin, newUser);
                   System.out.println("\nThank you. Your account number is " + newAccount.getAccountNo() + ".");
                   System.out.println("\nPlease login to access your newly created account.\n");
                   bank.update(newAccount);
                   bank.save();
                   
               } else {
            	   System.out.println("\nYour PIN must be between 1000 and 9999.\n");
               }
           } else {System.out.println("\nYour last name must be between 1 and 30 characters in length.\n");
           }
           
        } else {System.out.println("\nYour first name must be between 1 and 20 characters in length.\n");
       }
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