package main.java.com.swosh;


import main.java.com.swosh.dao.UserDAO;
import main.java.com.swosh.dao.AccountDAO;
import main.java.com.swosh.dao.TransactionDAO;
import main.java.com.swosh.model.User;
import main.java.com.swosh.model.Account;
import main.java.com.swosh.model.Transaction;
import main.java.com.swosh.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
           
            DatabaseConnection.initializeDatabase();

            UserDAO userDAO = new UserDAO();
            AccountDAO accountDAO = new AccountDAO();
            TransactionDAO transactionDAO = new TransactionDAO();

            Scanner scanner = new Scanner(System.in);
            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.print("Ange personnummer: ");
                String personalNumber = scanner.nextLine();
                System.out.print("Ange lösenord: ");
                String password = scanner.nextLine();
                
                if (userDAO.loginUser(personalNumber, password)) {
                    System.out.println("Inloggning lyckades!");
                    loggedIn = true;
                } else {
                    System.out.println("Fel personnummer eller lösenord. Försök igen.");
                }
            }

            while (true) {
                System.out.println("Välj ett alternativ:");
                System.out.println("1. Lägg till användare");
                System.out.println("2. Ta bort användare");
                System.out.println("3. Uppdatera användare");
                System.out.println("4. Lägg till konto");
                System.out.println("5. Ta bort konto");
                System.out.println("6. Skicka transaktion");
                System.out.println("7. Visa transaktioner");
                System.out.println("8. Visa användarsummering");
                System.out.println("9. Avsluta");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        System.out.print("Ange personnummer: ");
                        String personalNumber = scanner.nextLine();
                        System.out.print("Ange lösenord: ");
                        String password = scanner.nextLine();
                        System.out.print("Ange förnamn: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Ange efternamn: ");
                        String lastName = scanner.nextLine();
                        User user = new User();
                        user.setPersonalNumber(personalNumber);
                        user.setPassword(password);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        userDAO.addUser(user);
                        System.out.println("Användare tillagd!");
                        break;
                    case 2:
                        System.out.print("Ange användar-ID: ");
                        int userIdToDelete = scanner.nextInt();
                        userDAO.deleteUser(userIdToDelete);
                        System.out.println("Användare borttagen!");
                        break;
                    case 3:
                        System.out.print("Ange användar-ID: ");
                        int userIdToUpdate = scanner.nextInt();
                        scanner.nextLine(); 
                        User userToUpdate = userDAO.getUserById(userIdToUpdate);
                        if (userToUpdate != null) {
                            System.out.print("Ange nytt personnummer: ");
                            userToUpdate.setPersonalNumber(scanner.nextLine());
                            System.out.print("Ange nytt lösenord: ");
                            userToUpdate.setPassword(scanner.nextLine());
                            System.out.print("Ange nytt förnamn: ");
                            userToUpdate.setFirstName(scanner.nextLine());
                            System.out.print("Ange nytt efternamn: ");
                            userToUpdate.setLastName(scanner.nextLine());
                            userDAO.updateUser(userToUpdate);
                            System.out.println("Användare uppdaterad!");
                        } else {
                            System.out.println("Användare hittades inte!");
                        }
                        break;
                    case 4:
                        System.out.print("Ange användar-ID: ");
                        int userIdForAccount = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.print("Ange kontonummer: ");
                        String accountNumber = scanner.nextLine();
                        System.out.print("Ange saldo: ");
                        BigDecimal balance = scanner.nextBigDecimal();
                        Account account = new Account();
                        account.setUserId(userIdForAccount);
                        account.setAccountNumber(accountNumber);
                        account.setBalance(balance);
                        accountDAO.addAccount(account);
                        System.out.println("Konto tillagt!");
                        break;
                    case 5:
                        System.out.print("Ange konto-ID: ");
                        int accountIdToDelete = scanner.nextInt();
                        accountDAO.deleteAccount(accountIdToDelete);
                        System.out.println("Konto borttaget!");
                        break;
                    case 6:
                        System.out.print("Ange avsändarkonto-ID: ");
                        int senderAccountId = scanner.nextInt();
                        System.out.print("Ange mottagarkonto-ID: ");
                        int receiverAccountId = scanner.nextInt();
                        System.out.print("Ange belopp: ");
                        BigDecimal amount = scanner.nextBigDecimal();
                        if (accountDAO.checkBalance(senderAccountId, amount)) {
                            if (accountDAO.accountExists(receiverAccountId)) {
                                Transaction transaction = new Transaction();
                                transaction.setSenderAccountId(senderAccountId);
                                transaction.setReceiverAccountId(receiverAccountId);
                                transaction.setAmount(amount);
                                transactionDAO.createTransaction(transaction);
                                System.out.println("Transaktion skapad!");
                            } else {
                                System.out.println("Mottagarkonto hittades inte!");
                            }
                        } else {
                            System.out.println("Otillräckligt saldo!");
                        }
                        break;
                    case 7:
                        System.out.print("Ange konto-ID: ");
                        int accountId = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.print("Ange startdatum (YYYY-MM-DD HH:MM:SS): ");
                        String start = scanner.nextLine();
                        System.out.print("Ange slutdatum (YYYY-MM-DD HH:MM:SS): ");
                        String end = scanner.nextLine();
                        Timestamp startDate = Timestamp.valueOf(start);
                        Timestamp endDate = Timestamp.valueOf(end);
                        List<Transaction> transactions = transactionDAO.getTransactionsByDateRange(accountId, startDate, endDate);
                        for (Transaction tx : transactions) {
                            System.out.println(tx);
                        }
                        break;
                    case 8:
                        System.out.print("Ange användar-ID: ");
                        int userIdForSummary = scanner.nextInt();
                        User summaryUser = userDAO.getUserSummary(userIdForSummary);
                        if (summaryUser != null) {
                            System.out.println("Användaruppgifter:");
                            System.out.println("ID: " + summaryUser.getUserId());
                            System.out.println("Personnummer: " + summaryUser.getPersonalNumber());
                            System.out.println("Förnamn: " + summaryUser.getFirstName());
                            System.out.println("Efternamn: " + summaryUser.getLastName());
                            List<Account> userAccounts = accountDAO.getAccountsByUserId(userIdForSummary);
                            System.out.println("Konton:");
                            for (Account acc : userAccounts) {
                                System.out.println("Konto-ID: " + acc.getAccountId());
                                System.out.println("Kontonummer: " + acc.getAccountNumber());
                                System.out.println("Saldo: " + acc.getBalance());
                            }
                        } else {
                            System.out.println("Användare hittades inte!");
                        }
                        break;
                    case 9:
                        System.out.println("Avslutar...");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Ogiltigt val!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
