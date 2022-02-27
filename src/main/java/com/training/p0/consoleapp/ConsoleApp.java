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
			System.out.println();
			int choice = doMenu("Welcome to Bank of Yugyfoog", startMenu);
			switch (choice) {
			case 1:
				login();
				break;
			case 2: 
				newUser(false);
				break;
			case 3:
				newUser(true);
				break;
			case 4:
				giveHelp();
				break;
			case 9:  
				done = true;
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
		System.out.println("<employServices>");
	}
	
	void customerServices(User user) {
		final String[] customerServicesMenu = {
				"View Accounts Balances",
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
				openAccount(user.getUserId());
				break;
			case 9:
				done = true;
				break;
			}
		} while (!done);
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
				"Balence", "N"
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

