import java.io.*;
import java.util.*;

// Customer Class
class Customer {
    String name;

    Customer(String name) {
        this.name = name;
    }
}

// Bill Item Class
class BillItem {
    String itemName;
    int quantity;
    int price;

    BillItem(String itemName, int quantity, int price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    int getTotal() {
        return quantity * price;
    }
}

public class market {

    // Queue for customers
    static Queue<Customer> queue = new LinkedList<>();

    // HashMap for product prices
    static HashMap<String, Integer> priceList = new HashMap<>();

    // Bill history
    static ArrayList<String> billHistory = new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "bills.txt";

    public static void main(String[] args) {

        // Adding products and prices
        priceList.put("Rice", 60);
        priceList.put("Milk", 30);
        priceList.put("Bread", 40);

        // Load previous bills
        loadBills();

        System.out.println("================================");
        System.out.println("   SUPERMARKET BILLING SYSTEM");
        System.out.println("================================");

        while (true) {

            System.out.println("\n1. Add Customer");
            System.out.println("2. Bill Next Customer");
            System.out.println("3. View Queue");
            System.out.println("4. Exit");

            System.out.print("\nEnter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    addCustomer();
                    break;

                case "2":
                    billCustomer();
                    break;

                case "3":
                    viewQueue();
                    break;

                case "4":
                    saveBills();
                    System.out.println("Bills saved successfully!");
                    System.out.println("Thank you for using the system.");
                    return;

                default:
                    System.out.println("Invalid menu option!");
            }
        }
    }

    // Method 1: Add Customer
    static void addCustomer() {

        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        queue.add(new Customer(name));

        System.out.println(name +
                " added to billing queue. Position: "
                + queue.size());
    }

    // Method 2: Bill Next Customer
    static void billCustomer() {

        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No customer to bill.");
            return;
        }

        Customer customer = queue.peek();

        System.out.println("\nBilling Customer: " + customer.name);

        System.out.println("\nAvailable Items:");
        System.out.println("Rice  = Rs.60");
        System.out.println("Milk  = Rs.30");
        System.out.println("Bread = Rs.40");

        // Stack for undo operation
        Stack<BillItem> stack = new Stack<>();

        while (true) {

            System.out.print("\nEnter item name (undo/done): ");
            String item = sc.nextLine().trim();

            // Finish billing
            if (item.equalsIgnoreCase("done")) {
                break;
            }

            // Undo last scanned item
            if (item.equalsIgnoreCase("undo")) {

                if (stack.isEmpty()) {
                    System.out.println("No item available to undo.");
                } else {
                    BillItem removed = stack.pop();

                    System.out.println(
                            "Last item " + removed.itemName +
                            " removed.");
                }

                continue;
            }

            // Check product in HashMap
            String key = item.toLowerCase();

            Integer price = null;

            for (String product : priceList.keySet()) {
                if (product.toLowerCase().equals(key)) {
                    price = priceList.get(product);
                    item = product;
                    break;
                }
            }

            if (price == null) {
                System.out.println(
                        "Item " + item + " not found in price list.");
                continue;
            }

            System.out.print("Enter quantity: ");

            String quantityInput = sc.nextLine();

            int quantity;

            try {
                quantity = Integer.parseInt(quantityInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity!");
                continue;
            }

            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                continue;
            }

            // Push item into Stack
            stack.push(new BillItem(item, quantity, price));

            System.out.println(item + " added successfully.");
        }

        // Do not remove customer if no items were billed
        if (stack.isEmpty()) {
            System.out.println("No items added. Customer remains in queue.");
            return;
        }

        // Remove billed customer from Queue
        queue.poll();

        // Convert Stack data into Array
        BillItem[] billItems = stack.toArray(new BillItem[0]);

        int grandTotal = 0;

        StringBuilder bill = new StringBuilder();

        bill.append("Bill for ").append(customer.name).append(": ");

        // Calculate bill using Array
        for (int i = 0; i < billItems.length; i++) {

            BillItem b = billItems[i];

            int itemTotal = b.getTotal();

            grandTotal += itemTotal;

            if (i > 0) {
                bill.append(" | ");
            }

            bill.append(b.itemName)
                .append(" x")
                .append(b.quantity)
                .append(" = ")
                .append(itemTotal);
        }

        bill.append(" | Total ").append(grandTotal);

        // Display bill
        System.out.println("\n========== FINAL BILL ==========");
        System.out.println(bill);
        System.out.println("================================");

        // Store bill in history
        billHistory.add(bill.toString());

        // Check queue
        if (queue.isEmpty()) {
            System.out.println("Queue is now empty.");
        }
    }

    // Method 3: View Queue
    static void viewQueue() {

        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        System.out.println("\nCurrent Billing Queue:");

        int position = 1;

        for (Customer customer : queue) {

            System.out.println(
                    position + ". " + customer.name);

            position++;
        }
    }

    // Method 4: Load bills from file
    static void loadBills() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No previous bill file found.");
            return;
        }

        try (BufferedReader br =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                billHistory.add(line);
            }

            System.out.println(
                    "Previous bills loaded: " + billHistory.size());

        } catch (IOException e) {
            System.out.println("Error loading bills.");
        }
    }

    // Method 5: Save bills to file
    static void saveBills() {

        try (PrintWriter writer =
                new PrintWriter(new FileWriter(FILE_NAME))) {

            for (String bill : billHistory) {
                writer.println(bill);
            }

        } catch (IOException e) {
            System.out.println("Error saving bills.");
        }
    }
}