package natku.model;

import java.util.ArrayList;
import java.util.List;

import natku.log.Log;

public class Table extends ModelBase {
	private static final String DATA_WUZNIKWANE_DAN_SUBITIE = "Дата на възникване на данъчното събитие:";
	private static final String SUMA_ZA_PLASHTANE_LV = "Сума за плащане в лева:";
	private static final String SLOVOM = "Словом:";
	private List<TableItem> items;
	private String withWords;
	private String danOsnova;
	private double lDanOsnova = -1;
	private String vat;
	private double lVat = -1;
	private String amount;
	private double lAmount = -1;
	private Invoice forInvoice;

	public static Table parseTable(String tableSection, Invoice forInvoice) {
		Log.println("------------------------START--------------------");
		Log.println(tableSection);
		Log.println("------------------------END----------------------");

		String[] lines = tableSection.split(System.lineSeparator());
		String withWords = parseFollowItem(tableSection, SLOVOM, "WithWords");
		String danOsnova = null;
		String vat = null;
		String amount = parseFollowItem(tableSection, SUMA_ZA_PLASHTANE_LV, "Amount");
		List<TableItem> tableItems = new ArrayList<TableItem>();
		StringBuffer currentSection = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			// Parse danOsnova
			if ("Данъчна основа:".equals(lines[i])) {
				danOsnova = lines[i - 2].trim();
			}
			// Parse vat
			if (lines[i].startsWith("ДДС:")) {
				vat = lines[i - 2].trim();
			}
		}
		for (int i = 0; i < lines.length; i++) {
			// Remove lines that are not part of the table content
			if (lines[i].startsWith(DATA_WUZNIKWANE_DAN_SUBITIE)) {
				for (int j = 0; j < i; j++) {
					if (lines[j].equals("Шифър Наименование Мярка Кол-во Ед.Цена ТО Ед.цена-ТО Стойност") || lines[i].trim().length() == 0) {
						continue;
					}
					currentSection.append(lines[j]);
					currentSection.append(System.lineSeparator());
				}
				break;
			}
		}

		Log.println("------------------------START--------------------");
		Log.println(currentSection);
		Log.println("------------------------END----------------------");
		lines = currentSection.toString().split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith(TableItem.CODE_ELECTRICITY_FOR_PERIOD)) {
				StringBuffer tableItemSection = new StringBuffer();
				// Code
				tableItemSection.append(lines[i].trim() + " ");
				for (int j = i + 1; j < lines.length; j++) {
					if (!(lines[j].startsWith(TableItem.CODE_ELECTRICITY_FOR_PERIOD) || (lines[j].startsWith(TableItem.CODE_ACIZ) && (lines[j].length() > 2 && lines[j].charAt(1) == ' ')) || lines[j].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_1) || lines[j].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_2) || lines[j].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_3) || lines[j].startsWith(TableItem.CODE_ZAD_TO_COMMUNITY))) {
						tableItemSection.append(lines[j].trim() + " ");
					} else {
						break;
					}
				}

				Log.println("tableItemSection=" + tableItemSection);
				String[] elements = tableItemSection.toString().split(" ");
				StringBuffer name = new StringBuffer();
				for (int k = 1; k < elements.length - (tableItemSection.indexOf("MWh") > 0 ? 6 : 5); k++) {
					name.append(elements[k].trim() + "_space_");
				}
				String sName = name.toString().substring(0, name.length() - "_space_".length());
				
				Log.println("sName="+sName);
				Log.println("tableItemSection=" + tableItemSection);
				
				String[] elements2 = tableItemSection.toString().split(" ");
				StringBuffer tableItemSection2 = new StringBuffer();
				tableItemSection2.append(elements[0]);
				tableItemSection2.append(" ");
				tableItemSection2.append(sName);
				tableItemSection2.append(" ");
				for (int k = elements2.length - (tableItemSection.indexOf("MWh") > 0 ? 6 : 5); k < elements2.length; k++) {
					tableItemSection2.append(elements[k].trim() + " ");
				}

				Log.println("tableItemSection2="+tableItemSection2);
				
				TableItem currentTableItem = TableItem.parseTableItem(tableItemSection2.toString(), forInvoice);
				Log.println(currentTableItem);
				tableItems.add(currentTableItem);
			}
			if (lines[i].startsWith(TableItem.CODE_ACIZ) && lines[i].length() > 2 && lines[i].charAt(1) == ' ') {
				TableItem currentTableItem = TableItem.parseTableItem(lines[i].trim(), forInvoice);
				tableItems.add(currentTableItem);
			}
			if (lines[i].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_1) || lines[i].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_2) || lines[i].startsWith(TableItem.CODE_NET_TAX_AND_SERVICES_3)) {
				TableItem currentTableItem = TableItem.parseTableItem(lines[i].trim(), forInvoice);
				tableItems.add(currentTableItem);
			}
			if (lines[i].startsWith(TableItem.CODE_ZAD_TO_COMMUNITY)) {
				TableItem currentTableItem = TableItem.parseTableItem(lines[i].trim(), forInvoice);
				tableItems.add(currentTableItem);
			}
		}
		return new Table(tableItems, withWords, danOsnova, vat, amount, forInvoice);
	}

	private static String parseFollowItem(String tableSection, String startsWith, String itemName) {
		String result = "UnknownTable" + itemName;
		String[] lines = tableSection.split(System.lineSeparator());
		lines = splitDoubleLines(lines);
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith(startsWith)) {
				result = lines[i].substring(startsWith.length()).trim();
				break;
			}
		}
		return result;
	}
	
	private static String[] splitDoubleLines(String[] lines){
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			String currentLine = lines[i];
			if (currentLine.startsWith(SUMA_ZA_PLASHTANE_LV) && currentLine.contains(SLOVOM)) {
				String line1 = currentLine.substring(0, currentLine.indexOf(SLOVOM)).trim();
				String line2 = currentLine.substring(currentLine.indexOf(SLOVOM)).trim();
				result.add(line1);
				result.add(line2);
				
			}else{
				result.add(currentLine);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public Table(List<TableItem> items, String withWords, String danOsnova, String vat, String amount, Invoice forInvoice) {
		super();
		this.items = items;
		this.withWords = withWords;
		this.danOsnova = danOsnova;
		this.lDanOsnova = toDouble(danOsnova);
		this.vat = vat;
		this.lVat = toDouble(vat);
		this.amount = amount;
		this.lAmount = toDouble(amount);
		this.forInvoice = forInvoice;
	}

	public String getWithWords() {
		return withWords;
	}

	public void setWithWords(String withWords) {
		this.withWords = withWords;
	}

	public String getDanOsnova() {
		return danOsnova;
	}

	public void setDanOsnova(String danOsnova) {
		this.danOsnova = danOsnova;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getAmount() {
		return amount;
	}

	public double getlAmount() {
		return lAmount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public List<TableItem> getItems() {
		return items;
	}

	public void setItems(List<TableItem> items) {
		this.items = items;
	}

	public Invoice getForInvoice() {
		return forInvoice;
	}

	@Override
	public String toString() {
		return "Таблица [Редове=" + items + ", Словом=" + withWords + ", Данъчна основа=" + danOsnova + ", ДДС=" + vat + ", Сума за плащане в лева=" + amount + "]";
	}

	@Override
	protected void validateInternal() {
		if (withWords == null || withWords.isEmpty()) {
			validationEntries.add(ValidationEntry.createWarning(forInvoice, "Липсва сумата с думи."));
		}
		if (danOsnova == null || danOsnova.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <Данъчна основа>."));
		}
		if (lDanOsnova < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <Данъчна основа> не може да се превърне в число."));
		}
		if (vat == null || vat.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <ДДС>."));
		}
		if (lVat < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <ДДС> не може да се превърне в число."));
		}
		if (amount == null || amount.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <Сума за плащане в лева>."));
		}
		if (lAmount < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <Сума за плащане в лева> не може да се превърне в число."));
		}
		for (TableItem currentItem : items) {
			validationEntries.addAll(currentItem.getValidationEntries());
		}

		// Business validation
		double calculatedDanOsnova = 0;
		for (TableItem currentItem : items) {
			calculatedDanOsnova += currentItem.getlValue();
		}
		calculatedDanOsnova = (double) Math.round(calculatedDanOsnova * 100d) / 100d;
		if (calculatedDanOsnova != lDanOsnova) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <Данъчна основа> е със стойност [" + danOsnova + "] а калкулираната стойност е [" + calculatedDanOsnova + "]"));
		}

		double calculatedVat = rounded(calculatedDanOsnova, 0.2d);
		if (calculatedVat != lVat) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <ДДС> е със стойност [" + vat + "] а калкулираната стойност е [" + calculatedVat + "]"));
		}

		double calculatedAmount = calculatedDanOsnova + calculatedVat;
		calculatedAmount = (double) Math.round(calculatedAmount * 100d) / 100d;
		if (calculatedAmount != lAmount) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <Сума за плащане в лева> е със стойност [" + amount + "] а калкулираната стойност е [" + calculatedAmount + "]"));
		}

	}
}
