package natku.model;

import natku.log.Log;

public class TableItem extends ModelBase {
	private String cipher;
	private String name;
	private String measure;
	private String quantity;
	private double lQuantity = -1;
	private String edPrize;
	private double lEdPrize = -1;
	private String to;
	private double lTo = -1;
	private String edPrizeTo;
	private double lEdPrizeTo = -1;
	private String value;
	private double lValue = -1;
	private Invoice forInvoice;

	public static String CODE_ELECTRICITY_FOR_PERIOD = "100000005";
	public static String CODE_ACIZ = "1";
	public static String CODE_ZAD_TO_COMMUNITY = "100000006";
	public static String CODE_NET_TAX_AND_SERVICES_1 = "170000";
	public static String CODE_NET_TAX_AND_SERVICES_2 = "180000";
	public static String CODE_NET_TAX_AND_SERVICES_3 = "190000";

	public static TableItem parseTableItem(String tableItemSection, Invoice forInvoice) {
		Log.println("------------------------START--------------------");
		Log.println(tableItemSection);
		Log.println("------------------------END----------------------");
		String[] elements = tableItemSection.split(" ");
		String cipher = elements[0].trim();
		String name = "";
		String measure = "";
		String quantity = "";
		String edPrize = "";
		String to = "";
		String edPrizeTo = "";
		String value = "";

		if (cipher.equals(CODE_ELECTRICITY_FOR_PERIOD)) {
			int counter = 1;
			name = elements[counter++].replace("_space_", " ");
			if ("MWh".equals(elements[counter])) {
				measure = elements[counter++];
			}
			quantity = elements[counter++];
			edPrize = elements[counter++];
			to = elements[counter++];
			edPrizeTo = elements[counter++];
			value = elements[counter++];
		}
		if (cipher.equals(CODE_ACIZ)) {
			int counter = 1;
			name = elements[counter++];
			quantity = elements[counter++];
			edPrize = elements[counter++];
			to = elements[counter++];
			edPrizeTo = elements[counter++];
			value = elements[counter++];
		}
		if (cipher.equals(CODE_ZAD_TO_COMMUNITY)) {
			int counter = 1;
			name = elements[counter++] + " " + elements[counter++] + " " + elements[counter++];
			quantity = elements[counter++];
			edPrize = elements[counter++];
			to = elements[counter++];
			edPrizeTo = elements[counter++];
			value = elements[counter++];
		}
		if (cipher.equals(CODE_NET_TAX_AND_SERVICES_1) || cipher.equals(CODE_NET_TAX_AND_SERVICES_2)
				|| cipher.equals(CODE_NET_TAX_AND_SERVICES_3)) {
			int counter = 1;
			name = elements[counter++] + " " + elements[counter++] + " " + elements[counter++] + " "
					+ elements[counter++];
			quantity = elements[counter++];
			to = elements[counter++];
			value = elements[counter++];
		}

		return new TableItem(cipher, name, measure, quantity, edPrize, to, edPrizeTo, value, forInvoice);
	}

	public TableItem(String cipher, String name, String measure, String quantity, String edPrize, String to,
			String edPrizeTo, String value, Invoice forInvoice) {
		super();
		this.cipher = cipher;
		this.name = name;
		this.measure = measure;
		this.quantity = quantity;
		this.lQuantity = toDouble(quantity);
		this.edPrize = edPrize;
		this.lEdPrize = toDouble(edPrize);
		this.to = to;
		this.lTo = toDouble(to);
		this.edPrizeTo = edPrizeTo;
		this.lEdPrizeTo = toDouble(edPrizeTo);
		this.value = value;
		this.lValue = toDouble(value);
		this.forInvoice = forInvoice;
	}

	public double getlQuantity() {
		return lQuantity;
	}

	public void setlQuantity(double lQuantity) {
		this.lQuantity = lQuantity;
	}

	public double getlEdPrize() {
		return lEdPrize;
	}

	public void setlEdPrize(double lEdPrize) {
		this.lEdPrize = lEdPrize;
	}

	public double getlTo() {
		return lTo;
	}

	public void setlTo(double lTo) {
		this.lTo = lTo;
	}

	public double getlEdPrizeTo() {
		return lEdPrizeTo;
	}

	public void setlEdPrizeTo(double lEdPrizeTo) {
		this.lEdPrizeTo = lEdPrizeTo;
	}

	public double getlValue() {
		return lValue;
	}

	public void setlValue(double lValue) {
		this.lValue = lValue;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getEdPrize() {
		return edPrize;
	}

	public void setEdPrize(String edPrize) {
		this.edPrize = edPrize;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getEdPrizeTo() {
		return edPrizeTo;
	}

	public void setEdPrizeTo(String edPrizeTo) {
		this.edPrizeTo = edPrizeTo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Invoice getForInvoice() {
		return forInvoice;
	}

	@Override
	public String toString() {
		return "Ред [Шифър:" + cipher + ", Наименование:" + name + ", Мярка=" + measure + ", Кол-во=" + quantity
				+ ", Ед.Цена=" + edPrize + ", ТО=" + to + ", Ед.цена-ТО=" + edPrizeTo + ", Стойност=" + value + "]";
	}

	@Override
	protected void validateInternal() {
		if (cipher == null || cipher.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <Шифър> на ред:" + this.toString()));
		}

		if (name == null || name.isEmpty()) {
			validationEntries
					.add(ValidationEntry.createError(forInvoice, "Липсва <Наименование> на ред:" + this.toString()));
		}

		if (measure == null || measure.isEmpty()) {
			if (CODE_ELECTRICITY_FOR_PERIOD.equals(cipher)) {
				validationEntries
						.add(ValidationEntry.createNote(forInvoice, "Липсва <Мярка> на ред:" + this.toString()));
			}
		}

		if (quantity == null || quantity.isEmpty()) {
			validationEntries
					.add(ValidationEntry.createError(forInvoice, "Липсва <Количество> на ред:" + this.toString()));
		}
		if (lQuantity < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice,
					"Полето <Количество> не може да се превърне в число. Ред:" + this.toString()));
		}

		if (edPrize == null || edPrize.isEmpty()) {
			if (CODE_NET_TAX_AND_SERVICES_1.equals(cipher) || CODE_NET_TAX_AND_SERVICES_2.equals(cipher)
					|| CODE_NET_TAX_AND_SERVICES_3.equals(cipher)) {
				validationEntries
						.add(ValidationEntry.createNote(forInvoice, "Липсва <Ед.Цена> на ред:" + this.toString()));
			} else {
				validationEntries
						.add(ValidationEntry.createError(forInvoice, "Липсва <Ед.Цена> на ред:" + this.toString()));
			}
		}
		if (lEdPrize < 0) {
			if (CODE_NET_TAX_AND_SERVICES_1.equals(cipher) || CODE_NET_TAX_AND_SERVICES_2.equals(cipher)
					|| CODE_NET_TAX_AND_SERVICES_3.equals(cipher)) {
				validationEntries.add(ValidationEntry.createNote(forInvoice,
						"Полето <Ед.Цена> не може да се превърне в число. Ред:" + this.toString()));
			} else {
				validationEntries.add(ValidationEntry.createError(forInvoice,
						"Полето <Ед.Цена> не може да се превърне в число. Ред:" + this.toString()));
			}
		}

		if (to == null || to.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <ТО> на ред:" + this.toString()));
		}
		if (lTo < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice,
					"Полето <ТО> не може да се превърне в число. Ред:" + this.toString()));
		}

		if (edPrizeTo == null || edPrizeTo.isEmpty()) {
			if (CODE_NET_TAX_AND_SERVICES_1.equals(cipher) || CODE_NET_TAX_AND_SERVICES_2.equals(cipher)
					|| CODE_NET_TAX_AND_SERVICES_3.equals(cipher)) {
				validationEntries
						.add(ValidationEntry.createNote(forInvoice, "Липсва <Ед.цена-ТО> на ред:" + this.toString()));
			} else {
				validationEntries
						.add(ValidationEntry.createError(forInvoice, "Липсва <Ед.цена-ТО> на ред:" + this.toString()));
			}
		}
		if (lEdPrizeTo < 0) {
			if (CODE_NET_TAX_AND_SERVICES_1.equals(cipher) || CODE_NET_TAX_AND_SERVICES_2.equals(cipher)
					|| CODE_NET_TAX_AND_SERVICES_3.equals(cipher)) {
				validationEntries.add(ValidationEntry.createNote(forInvoice,
						"Полето <Ед.цена-ТО> не може да се превърне в число. Ред:" + this.toString()));
			} else {
				validationEntries.add(ValidationEntry.createError(forInvoice,
						"Полето <Ед.цена-ТО> не може да се превърне в число. Ред:" + this.toString()));
			}
		}

		if (value == null || value.isEmpty()) {
			validationEntries
					.add(ValidationEntry.createError(forInvoice, "Липсва <Стойност> на ред:" + this.toString()));
		}
		if (lValue < 0) {
			validationEntries.add(ValidationEntry.createError(forInvoice,
					"Полето <Стойност> не може да се превърне в число. Ред:" + this.toString()));
		}

		// Business validation
		if (CODE_ELECTRICITY_FOR_PERIOD.equals(cipher) || CODE_ACIZ.equals(cipher)
				|| CODE_ZAD_TO_COMMUNITY.equals(cipher)) {
			// Check calculation of value
			double calculatedValue = rounded(lQuantity, lEdPrizeTo);
			if (calculatedValue != lValue) {
				validationEntries.add(ValidationEntry.createError(forInvoice, "Полето <Стойност> е със стойност ["
						+ value + "] а калкулираната стойност е [" + calculatedValue + "] на ред:" + this.toString()));
			}
		}

		// Business validation
		if (CODE_ZAD_TO_COMMUNITY.equals(cipher)) {
			// Check is not 0 for all invoices that are non 0 valued.
			if (lValue == 0.0 && forInvoice.getTable().getlAmount() != 0.0) {
				validationEntries.add(ValidationEntry.createError(forInvoice,
						"Полето <Стойност> при <Задължения към обществото> е със стойност [" + value + "]"));
			}
		}

		if (CODE_NET_TAX_AND_SERVICES_1.equals(cipher) || CODE_NET_TAX_AND_SERVICES_2.equals(cipher)
				|| CODE_NET_TAX_AND_SERVICES_3.equals(cipher)) {
			// Check is not 0
			if (lValue == 0.0 && forInvoice.getTable().getlAmount() != 0.0) {
				validationEntries.add(ValidationEntry.createError(forInvoice,
						"Полето <Стойност> при <Мрежови такси и услуги> е със стойност [" + value + "]"));
			}
		}

	}

}
