package natku.model;

public class Invoice extends ModelBase {
	private int pageNumber = -1;
	private String number;
	private String date;
	private Participant reciever;
	private Participant issuer;
	private Table table;

	public static Invoice parseInvoiceFromPage(int pageNumber, String page) {
		// System.out.println("------------------------START--------------------");
		// System.out.println(page);
		// System.out.println("------------------------END----------------------");
		String[] lines = page.split(System.lineSeparator());
		String invoiceNumber = null;
		String forDate = null;
		Participant reciever = null;
		Participant issuer = null;
		Table table = null;
		Invoice invoice = new Invoice(pageNumber, invoiceNumber, forDate, reciever, issuer, table);
		StringBuffer currentSection = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			// Parse reciever
			if ("ОРИГИНАЛ".equals(lines[i])) {
				reciever = Participant.parsePerticipant(currentSection.toString(), invoice);
				currentSection = new StringBuffer();
			}

			// Parse invoice data
			if ("Доставчик:".equals(lines[i])) {
				invoiceNumber = parseInvoiceNumber(currentSection.toString());
				forDate = parseInvoiceDate(currentSection.toString());
				currentSection = new StringBuffer();
			}

			// Parse issuer
			if ("Шифър Наименование Мярка Кол-во Ед.Цена ТО Ед.цена-ТО Стойност".equals(lines[i])) {
				issuer = Participant.parsePerticipant(currentSection.toString(), invoice);
				currentSection = new StringBuffer();
			}

			// Parse table
			if ("Начини на плащане:".equals(lines[i])) {
				table = Table.parseTable(currentSection.toString(), invoice);
				currentSection = new StringBuffer();
			}

			currentSection.append(lines[i]);
			currentSection.append(System.lineSeparator());
		}

		// Add fields to invoice
		invoice.setNumber(invoiceNumber);
		invoice.setDate(forDate);
		invoice.setReciever(reciever);
		invoice.setIssuer(issuer);
		invoice.setTable(table);
		
		return invoice;
	}

	private static String parseInvoiceNumber(String invoiceDataSection) {
		String result = "UnknownInvoiceNumber";
		String[] lines = invoiceDataSection.split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if ("ФАКТУРА".equals(lines[i])) {
				if (lines.length > i) {
					result = lines[i + 1];
					break;
				}
			}
		}
		return result;
	}

	private static String parseInvoiceDate(String invoiceDataSection) {
		String result = "UnknownInvoiceDate";
		String[] lines = invoiceDataSection.split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if ("No:".equals(lines[i])) {
				if (lines.length > i) {
					result = lines[i + 1];
					break;
				}
			}
		}
		return result;
	}

	public Invoice(int pageNumber, String number, String date, Participant reciever, Participant issuer, Table table) {
		super();
		this.pageNumber = pageNumber;
		this.number = number;
		this.date = date;
		this.reciever = reciever;
		this.issuer = issuer;
		this.table = table;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Participant getReciever() {
		return reciever;
	}

	public void setReciever(Participant reciever) {
		this.reciever = reciever;
	}

	public Participant getIssuer() {
		return issuer;
	}

	public void setIssuer(Participant issuer) {
		this.issuer = issuer;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
	
	public boolean hasZadToCommunity(){
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_ZAD_TO_COMMUNITY.equals(currentItem.getCipher())) {
				return true;
			}
		}
		return false;
	}

	public double getZadToCommunity(){
		double fullZadToComm = 0;
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_ZAD_TO_COMMUNITY.equals(currentItem.getCipher())) {
				fullZadToComm += currentItem.getlQuantity();
			}
		}
		return fullZadToComm;
	}

	public boolean hasElEnergiaPeriodEdCena(){
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_ELECTRICITY_FOR_PERIOD.equals(currentItem.getCipher())) {
				return true;
			}
		}
		return false;
	}

	public double getElEnergiaPeriodValue(){
		double elEnergiaFullValue = 0;
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_ELECTRICITY_FOR_PERIOD.equals(currentItem.getCipher())) {
				elEnergiaFullValue += currentItem.getlValue();
			}
		}
		return elEnergiaFullValue;
	}

	public double getElEnergiaPeriodEdCena(){
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_ELECTRICITY_FOR_PERIOD.equals(currentItem.getCipher())) {
				return currentItem.getlEdPrize();
			}
		}
		return 0d;
	}

	public double getNetTaxAndServices(){
		for (TableItem currentItem : getTable().getItems()) {
			if (TableItem.CODE_NET_TAX_AND_SERVICES_1.equals(currentItem.getCipher()) || TableItem.CODE_NET_TAX_AND_SERVICES_2.equals(currentItem.getCipher()) || TableItem.CODE_NET_TAX_AND_SERVICES_3.equals(currentItem.getCipher())) {
				return currentItem.getlValue();
			}
		}
		return 0d;
	}

	@Override
	public String toString() {
		return "Фактура [Страница=" + pageNumber + ", No=" + number + ", Дата=" + date + ", Получател=" + reciever + ", Доставчик=" + issuer + ", Таблица=" + table + "]";
	}

	@Override
	protected void validateInternal() {
		if (pageNumber < 0) {
			validationEntries.add(ValidationEntry.createError(this, "Липсва страницата"));
		}
		
		if (number == null || number.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(this, "Липсва номера на фактурата."));
		}
		
		if (date == null || date.isEmpty()) {
			validationEntries.add(ValidationEntry.createWarning(this, "Липсва датата на фактурата."));
		}
		
		if (reciever == null ) {
			validationEntries.add(ValidationEntry.createError(this, "Липсва получател."));
		}else{
			validationEntries.addAll(reciever.getValidationEntries());
		}

		if (issuer == null ) {
			validationEntries.add(ValidationEntry.createError(this, "Липсва таблица с разбивка."));
		}else{
			validationEntries.addAll(issuer.getValidationEntries());
		}
		
		if (table == null ) {
			validationEntries.add(ValidationEntry.createError(this, "Липсва таблица с разбивка."));
		}else{
			validationEntries.addAll(table.getValidationEntries());
		}
	}

}
