package com.training.p0.consoleapp;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.training.p0.dao.DBaseDAO;
import com.training.p0.dao.DBasePostgres;
import com.training.p0.dao.LoginDAO;
import com.training.p0.dao.LoginDAOImpl;

public class ConsoleApp {

	Scanner scanner = new Scanner(System.in);

	DBaseDAO dBase = new DBasePostgres();
	LoginDAO login = new LoginDAOImpl();
	
	public void start() {
		final String[] startMenu = {
			"Log In",
			"New Customer",
			"New Employee",
			"Help"
		};
			
		boolean done = false;
		do {
			int choice = doMenu("Welcome to Bank of Yugyfoog", startMenu);
			switch (choice) {
			case 1:
				login();
				break;
			case 2: 
				newUser(false);   // new Customer
				break;
			case 3:
				newUser(true);    // new Employee
				break;
			case 4:
				giveHelp();
				break;
			case 9:  
				done = true;
				break;
			}
			
		} while (!done);
		
		System.out.println();
		System.out.println("Good-bye");	
	}

	private void newUser(boolean isEmployee) {
		final String[] newCustomerForm = {
				"First Name", "S",
				"Last Name", "S",
				"Username", "S",
				"Password", "S",
		};
		
		while (true) {
			List<String> results = doForm("New Customer Form", newCustomerForm);
		
			// insert new user into password file
		
			int userid = login.newUser(new Credentials(results.get(2), results.get(3)));
		
			if (userid >= 0) {
				// insert new user into user file
				dBase.addUser(new User(userid, results.get(0), results.get(1), isEmployee));
				break;
			}
		
			System.out.println("username " + results.get(2) + " is taken");
		}
		System.out.println();
		System.out.println("new user accepeted");
	}

	private void login() {
		final String[] loginForm = {
				"Username", "S",
				"Password", "S"
		};
			
		// ask user for credentials
	
		Credentials credentials = new Credentials(doForm("Login Form", loginForm));
		
		// validate credentials
		
		int userId = login.validate(credentials);
		if (userId < 0) {
			System.out.println();
			System.out.println("incorect username or password");
			return;
		}

		// get user information
		
		User user = dBase.getUser(userId);	
		
		if (user.getIsEmployee())
			employServices(user.getUserId());
		else
			customerServices(user);
		
	}
	
	void employServices(int userId) {
		final String[] employeeServicesMenu = {
				"View Customer's Account",
				"Approve Accounts",
				"View Transaction Log"
		};
		
		boolean done = false;
		do {
			int choice = doMenu("Employee Services", employeeServicesMenu);
			switch (choice) {
			case 1:
				viewCustomersAccount();
				break;
			case 2:
				approveAccounts();
				break;
			case 3:
				viewTransactions();
				break;
			case 9:
				done = true;
				break;
			}
		} while (!done);
	}
	
	private void viewTransactions() {
		System.out.println();
		System.out.println("Transaction Log");
		System.out.println();
		List<String> transactions = dBase.viewTransactions();
		for (String transaction : transactions) {
			System.out.println(transaction);
		}
	}

	private void viewCustomersAccount() {
		final String[] viewAccountForm = {
				"Account number: ", "N"
		};	
		List<String> results = doForm("View Customer Accounts", viewAccountForm);
		int userid = Integer.parseInt(results.get(0));
		List<AccountInfo> accounts = dBase.viewAccount(userid);
		System.out.println(userid + " " + accounts.get(0).getFullName());
		System.out.println();
		System.out.println(" Account");
		System.out.println("  Number   Balance");
		System.out.println("--------- ---------");
		for (AccountInfo account : accounts) {
			System.out.printf("%9d", account.getAccountNumber());			
			System.out.printf("%9d", account.getBalance());
			if (account.isApproved() == false) {
				System.out.println(" account approval pending");
			}
			System.out.println();
		}
	}	
		
	void approveAccounts() {
		// find all accounts that are pending approval
		
		System.out.println();
		System.out.println("Approve Customer Accounts");
		System.out.println();
		
		List<AccountInfo> accounts = dBase.getAccountsPendingApproval();
		List<AccountInfo> approvedList = new LinkedList<AccountInfo>();
		List<AccountInfo> deniedList = new LinkedList<AccountInfo>();
		
		for (AccountInfo account : accounts) {
			
			// display account information
			
			System.out.println();
			System.out.println("Account number: " + account.getAccountNumber());
			System.out.println("Name: " + account.getFullName());
			System.out.println("Balance: " + account.getBalance());
			
			// Ask what to do?
			
			System.out.println("(A)pprove, (D)eny, (S)kip");
			String reply = scanner.next().toUpperCase();
			do {
				switch (reply) {
				case "A":
					approvedList.add(account);
					break;
				case "D":
					deniedList.add(account);
					break;
				case "S":
					// nothing to do
					break;
				default:
					System.out.println("reply: A, D, or S");
					continue;
				}
			} while (false);
		}
		int approved = dBase.approveAccounts(approvedList);
		int denied = dBase.denyAccounts(deniedList);
		if (approved == 0 && denied == 0)
			System.out.println("no accounts changed");
		else {
			if (approved != 0) {
				if (approved == 1)
					System.out.println("1 account approved");
				else
					System.out.println(approved + " accounts approved");
			}
			if (denied != 0) {
				if (denied == 1)
					System.out.println("1 account denied");
				else
					System.out.println(denied + " accounts denied");
			}
		}
	}
	
	void customerServices(User user) {
		final String[] customerServicesMenu = {
				"View Accounts Balances",
				"Deposit",
				"Withdraw",
				"Money Transfer",
				"Open New Account"
		};
		
		boolean done = false;
		do {
			int choice = doMenu("Customer Services", customerServicesMenu);
			switch (choice) {
			case 1:
				viewAccount(user);
				break;
			case 2:
				deposit(user);
				break;
			case 3:
				withdraw(user);
				break;
			case 4:
				moneyTransfer(user);
				break;
			case 5:
				openAccount(user.getUserId());
				break;
			case 9:
				done = true;
				break;
			}
		} while (!done);
	}

	private void moneyTransfer(User user) {
		final String[] transferForm = {
				"Sending Account", "N",
				"Receiving Account", "N",
				"Amount", "N"
		};
		
		List<String> results = doForm("Money Transfer", transferForm);
		int sendingAccount = Integer.parseInt(results.get(0));
		int receivingAccount = Integer.parseInt(results.get(1));
		int amount = Integer.parseInt(results.get(2));
		
		int balance = dBase.transfer(sendingAccount, receivingAccount, amount);
		
		if (balance < 0)
			System.out.println("money transfer failed");
		else
			System.out.println("money transfer successful");

	}

	private void withdraw(User user) {
		final String[] withdrawForm = {
				"Account", "N",
				"Amount", "N"
		};
		
		List<String> results = doForm("Account Withdraw", withdrawForm);
		int accountNumber = Integer.parseInt(results.get(0));
		int amount = Integer.parseInt(results.get(1));
		if (amount < 0) {
			System.out.println("Can't withdraw negative amount");
		}
		else {
			int balance = dBase.accountUpdate(accountNumber, -amount);
			if (balance < 0)
				System.out.println("no withdraw made");
			else
				System.out.println("new balance: " + balance);
		}
	}

	private void deposit(User user) {
		final String[] depositForm = {
				"Account", "N",
				"Amount", "N"
		};
		
		List<String> results = doForm("Account Deposit", depositForm);
		int account_number = Integer.parseInt(results.get(0));
		int amount = Integer.parseInt(results.get(1));
		
		if (amount < 0) {
			System.out.println("Can't deposit negative amount");
		}
		else {
			int balance = dBase.accountUpdate(account_number, amount);
			if (balance < 0)
				System.out.println("no deposit made");
			else
				System.out.println("new balance: " + balance);
		}
	}

	private void viewAccount(User user) {
		List<AccountInfo> accounts = dBase.viewAccount(user.getUserId());
		System.out.println();
		System.out.println(" Account");
		System.out.println("  Number    Balance");
		System.out.println("--------- ----------");
		for (AccountInfo account : accounts) {
			System.out.printf("%8d", account.getAccountNumber());
			System.out.printf("  %9d", account.getBalance());
			if (account.isApproved() == false) 
				System.out.print("  account approval pending");
			System.out.println();
		}
		
	}

	private void openAccount(int userId) {
		final String[] openAccountForm = {
				"Balance", "N"
		};
		
		List<String> results = doForm("Open Account", openAccountForm);
		int amount = Integer.parseInt(results.get(0));
		if (amount < 0) 
			System.out.println("Can't open an account with negative balence");
		else {
			if (dBase.newAccount(userId, amount))
				System.out.println("Account opened successfully");
			else
				System.out.println("Error opening account");
		}

	}

	private void giveHelp() {
		System.out.println();
		System.out.println("=========================================================");
		System.out.println();
		System.out.println("Bank of Yugyfoog");
		System.out.println("Account Maintance Program");
		System.out.println();
		System.out.println("=========================================================");
		System.out.println();
		// not much help
	}
	
	private int doMenu(String title, String[] menu) {
		
		System.out.println();
		System.out.println(title);
		System.out.println();
		
		while (true) {
			
			// display menu
			
			int count = 1;
			for (String s : menu) {
				System.out.println(" " + count + ". " + s);
				count++;
			}
			System.out.println(" 9. Exit");
			System.out.println();
		
			// get user choice
			
			int choice;
			if (scanner.hasNextInt())
				choice = scanner.nextInt();
			else {
				scanner.nextLine(); // eat bad input
				choice = -1; // force invalid choice
			}
			
			// validate choice, return if good.
			
			if ((1 <= choice && choice <= menu.length) || choice == 9)
				return choice;

			// invalid choice, print message and try again
			
			System.out.println();
			System.out.println("Please enter 1-" + menu.length + ", or 9");
			System.out.println();
		}
	}
	
	private List<String> doForm(String title, String[] form) {
		// "form" should contain pairs of strings.
		// the first string in each pair in the input prompt.
		// the second string is "S" for string input, "N" for integer input, "D" for decimal input
		
		// returns a list of strings.
		//     -- returning numeric data as strings prevents rounding
		//        and overflow errors.
		
		System.out.println();
		System.out.println(title);
		System.out.println();
			
		List<String> entries = new LinkedList<String>(); // values to be returned
		
		for (int i = 0; i < form.length; i += 2) {
				
			// prompt user for data
			
			System.out.println(form[i]);
			
			// get value from user
			
			String type = form[i+1];
			switch (type) {
			case "S":                              // string
				String entry = scanner.next();
				entries.add(entry);
				break;
			case "N":                              // integer
				entry = scanner.next();
				if (!entry.matches("[+-]?\\d+")) {
					System.out.println("please enter a valid number");
					i -= 2; // retry this entry
					continue;
				}
				entries.add(entry);
				break;
			case "D":                              // decimal number
				entry = scanner.next();
				if (!entry.matches("[+-]?((\\d+)|(\\d+\\.\\d*)|(\\.\\d+))")) {
					System.out.println("please enter a valid decimal number");
					i -= 2; // retry current entry
					continue;
				}
				entries.add(entry);
				break;
			default:
				// We should never get here.
				System.err.println("Bad form type specifier: " + type); // throw an Exception?
				System.exit(1);
			}
		}
		return entries;
	}
}

