import model.Discount;
import model.Inventory;
import model.RegisterLogger;

import java.io.*;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AutomatedCheckout {

	private static Map<Integer, Discount> discountMap = readDiscountsFile();
	private static List<Inventory> inventoryList = readInventoryFile();

	private static final String HEADER = "Item               Price       Quantity    Discount   FinalPrice";
	private static final String HEADER_LINE = "----               -----       --------    --------   ----------";
	private static final String SINGLE_SPACE = " ";
	private static final String BOTTOM_LINE = "                                                      ----------";
	private static final String INVENTORY_FILE = "inventory.txt";
	private static final String DISCOUNT_FILE = "discounts.txt";
	private static final String RECEIPT_FILE = "receipts.txt";
	private static final String NEW_LINE = System.lineSeparator();

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		List<Integer> upcCodesList = new LinkedList<>();
		List<RegisterLogger> registerLoggerList =  new LinkedList<>();
		while (true) {

			System.out.print(" please enter item upc code : ");
			int upcCode = scanner.nextInt();
			if (!isValidUpc(upcCode)) {
				System.out.println("entered upc code is not in our current inventory, please try again with correct upc code");
				continue;
			}
			if(!doWeHaveItemInInventory(upcCode)) {
				System.out.println("entered item is not available in our current inventory ");
				System.out.println("we will add it to our inventory sooner");
				continue;
			}
			System.out.println("please enter quantity of item : ");
			Double quantity = 0.0;
			quantity = scanner.nextDouble();
			while (!doWeHaveEnoughQuantityInInventory(quantity, upcCode)) {
				System.out.println("please enter quantity of item again : ");
				quantity = scanner.nextDouble();
			}
			RegisterLogger registerLogger = calculatePrice(upcCode, quantity);
			registerLoggerList.add(registerLogger);
			upcCodesList.add(upcCode);
			System.out.print ("do you have more items  :  ");
			String userResponse = scanner.next();
			if(userResponse.equalsIgnoreCase("NO")) {
				System.out.println("closing the register !!!! ");
				System.out.println();
				printReceipt(registerLoggerList);
				updateInventory(registerLoggerList);
				break;
			}

		}


	}


	private static boolean doWeHaveItemInInventory(int upcCode) {
		Inventory inventory = inventoryList.stream().filter(inv -> inv.getUpcCode() == upcCode).findFirst().get();
		return inventory.getQuantity() > 0;
	}

	private static boolean doWeHaveEnoughQuantityInInventory(Double quantity, int upcCode) {
		Inventory invItem = inventoryList.stream().filter(inventory -> inventory.getUpcCode() == upcCode).findFirst().get();
		for (Inventory inventory : inventoryList) {
			if (inventory.getUpcCode() == upcCode) {
				if (inventory.getQuantity() >= quantity) {
					return true;
				}
			}
		}
		if (invItem.getWeightMeasure().equalsIgnoreCase("U")) {
			System.out.println("we have only "+invItem.getQuantity() + " units of "+invItem.getName()+ " please reduce quantity");
		}else {
			System.out.println("we have only "+invItem.getQuantity() + " pounds of "+invItem.getName()+ " please reduce quantity");
		}
		return false;
	}


	private static void updateInventory(List<RegisterLogger> registerLoggerList) {
		for (RegisterLogger registerLogger : registerLoggerList) {
			for(Inventory inventory: inventoryList) {
				if(registerLogger.getUpcCode() ==  inventory.getUpcCode()) {
					inventory.setQuantity(inventory.getQuantity() - Double.parseDouble(registerLogger.getOrderedQuantity()));
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Inventory inventory : inventoryList) {
			sb.append(inventory.getUpcCode());
			sb.append(",");
			sb.append(inventory.getWeightMeasure());
			sb.append(",");
			sb.append(inventory.getName());
			sb.append(",");
			sb.append(inventory.getPrice());
			sb.append(",");
			sb.append(inventory.getQuantity());
			sb.append(NEW_LINE);
		}
		File file = Paths.get(INVENTORY_FILE).toFile();
		if (file.exists()) {
			file.delete();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
			writer.write(sb.toString());
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void printReceipt(List<RegisterLogger> registerLoggerList) {

		String itemSpace = "                 ";
		String originalPriceSpace = "          ";
		String quantitySpace = "          ";
		String disCountSpace = "          ";

		StringBuilder logEntry = new StringBuilder();

		logEntry.append("CUSTOMER RECEIPT ");
		logEntry.append("    ");
		logEntry.append(" TIME : ");
		logEntry.append(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
		logEntry.append(NEW_LINE);
		logEntry.append("==========================================");
		logEntry.append(NEW_LINE);
		logEntry.append(HEADER);
		logEntry.append(NEW_LINE);
		logEntry.append(HEADER_LINE);
		logEntry.append(NEW_LINE);

		registerLoggerList.stream().forEach(registerLogger -> {
			logEntry.append(appendExtraSpace(registerLogger.getItemName(), itemSpace));
			logEntry.append("  ");
			logEntry.append(appendExtraSpace(registerLogger.getItemPrice(), originalPriceSpace));
			logEntry.append("  ");
			logEntry.append(appendExtraSpace(registerLogger.getOrderedQuantity(), quantitySpace));
			logEntry.append("  ");
			logEntry.append(appendExtraSpace(registerLogger.getDiscount(), disCountSpace));
			logEntry.append("  ");
			logEntry.append(registerLogger.getPriceAfterDiscount());
			logEntry.append("\r\n");
		});

		logEntry.append(BOTTOM_LINE);
		logEntry.append(NEW_LINE);
			Double totalPrice = registerLoggerList.stream().mapToDouble(register -> {
				Double finalPrice = 0.0;
				try {
					finalPrice = NumberFormat.getCurrencyInstance(Locale.US).parse(register.getPriceAfterDiscount()).doubleValue();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return finalPrice;
			}).sum();
		String finalPrice = "                                                      "+NumberFormat.getCurrencyInstance(Locale.US).format(totalPrice);
		logEntry.append(finalPrice);
		System.out.println(logEntry.toString());

		//write receipts to a file
		File receiptFile = Paths.get(RECEIPT_FILE).toFile();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile, true))) {
			writer.write(logEntry.toString());
			writer.write(NEW_LINE);
			writer.write(NEW_LINE);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private static String appendExtraSpace(String entry, String neededSpace) {
		int diff =  neededSpace.length() - entry.length();
		StringBuilder spaceBuilder = new StringBuilder(entry);
		if(diff > 0) {
			for (int i = 0; i< diff; i++) {
				spaceBuilder.append(SINGLE_SPACE);
			}
		}else {
			spaceBuilder.append(spaceBuilder.substring(neededSpace.length()));
		}
		return spaceBuilder.toString();
	}

	private static RegisterLogger calculatePrice(int upcCode, Double quantity){
		Inventory invItem = inventoryList.stream().filter(inv -> inv.getUpcCode() == upcCode).findFirst().get();
		Discount discount = discountMap.get(upcCode);
		Double price = invItem.getPrice()*quantity;
		if (doesInvItemHasDiscount(upcCode)) {
			System.out.println(invItem.getName() + " has eligible discount");
			if (discount.isPercentage()) {
				price = price - (price * discount.getDiscountValue());
			}else {
				price = price - discount.getDiscountValue();
			}
			String itemString = "price of each " + invItem.getName();
			System.out.println(itemString + "                          = "+ invItem.getPrice());
			System.out.println("ordered quantity                            = "+ quantity);
			System.out.println("applied discount                            = " + printDiscountValue(discount) + " (-)") ;
			System.out.println("                                              " + "--------------------------");
			System.out.println("total price of the item after discount      = " + NumberFormat.getCurrencyInstance(Locale.US).format(price));
		}else {
			System.out.println(invItem.getName() + " has no discount");
			System.out.println("price of the the "+ invItem.getName() + " with quantity " + quantity + " is " + NumberFormat.getCurrencyInstance(Locale.US).format(price) );
		}
		return new RegisterLogger(upcCode, invItem.getName(), quantity.toString(), convertIntoUSCurrency(invItem.getPrice()), printDiscountValue(discount), convertIntoUSCurrency(price)) ;
	}

	private static String convertIntoUSCurrency(Double value) {
		return  NumberFormat.getCurrencyInstance(Locale.US).format(value);
	}

	private static String printDiscountValue(Discount discount) {
		if(discount != null) {
			try {
				if(discount.isPercentage()) {
					return NumberFormat.getPercentInstance(Locale.US).format(discount.getDiscountValue());
				}else {
					return convertIntoUSCurrency(discount.getDiscountValue());
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return "N/A";
	}


	private static boolean doesInvItemHasDiscount(int upcCode) {
		return discountMap.get(upcCode) != null;
	}

	private static boolean isValidUpc(int upc) {
		List<Inventory> inventoryList = readInventoryFile();
		if (inventoryList != null) {
			return inventoryList.stream().anyMatch(inventory -> inventory.getUpcCode() == upc);
		}
		return false;
	}

	private static Map<Integer, Discount> readDiscountsFile(){
		File file = Paths.get(DISCOUNT_FILE).toFile();
		String discountLine = "";
		Map<Integer, Discount> discountsMap = new HashMap<>();
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((discountLine= reader.readLine())!=null) {
				String[] discItems = discountLine.split("\\,");
				String discValue = discItems[1].trim();
				if (discValue.endsWith("%")) {
					Double discNumber = NumberFormat.getPercentInstance(Locale.US).parse(discValue).doubleValue();
					discountsMap.put(Integer.parseInt(discItems[0].trim()), new Discount(discNumber, true));
				}else {
					Double discNumber = NumberFormat.getCurrencyInstance(Locale.US).parse(discValue).doubleValue();
					discountsMap.put(Integer.parseInt(discItems[0].trim()), new Discount(discNumber, false));
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return discountsMap;
	}

	private static List<Inventory> readInventoryFile(){
		File file = Paths.get(INVENTORY_FILE).toFile();
		List<Inventory> inventoryList =  new ArrayList<Inventory>();
		String invLine = "";
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((invLine = reader.readLine()) != null) {
				String[] invItems = invLine.split("\\,");
				inventoryList.add(new Inventory(Integer.parseInt(invItems[0].trim()), invItems[1].trim(), invItems[2].trim(), Double.parseDouble(invItems[3].trim()), Double.parseDouble(invItems[4].trim())));
			}
		}catch (IOException ex) {
			System.out.println("dev is needed not able to parse the inventory file");
			return null;
		}
		return inventoryList;
	}

}