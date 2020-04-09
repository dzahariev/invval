package natku.model;

public class Participant extends ModelBase {
	private static final String PREFIX_ACCOUNT = "Сметка:";
	private static final String PREFIX_CODE = "Код:";
	private static final String PREFIX_BANK = "Банка:";
	private static final String PREFIX_ID_NO = "Идентиф.No:";
	private static final String PREFIX_ID_VAT = "Идентиф по ДДС:";
	private static final String PREFIX_ADDRESS = "Адрес:";
	private static final String PREFIX_CITY = "гр./с./";
	private static final String PREFIX_RECIEVER = "Получател:";
	private static final String PREFIX_ISSUER = "Доставчик:";

	private String name;
	private String city;
	private String address;
	private String idVAT;
	private String idNO;
	private String bank;
	private String bankCode;
	private String account;
	private Invoice forInvoice;

	public static Participant parsePerticipant(String participantSection, Invoice forInvoice) {
		// System.out.println("------------------------START--------------------");
		// System.out.println(participantSection);
		// System.out.println("------------------------END----------------------");

		String name = parseName(participantSection.toString());
		String city = parseCity(participantSection.toString());
		String address = parseAddress(participantSection.toString());
		String idVAT = parseIdVAT(participantSection.toString());
		String idNO = parseIdNO(participantSection.toString());
		String bank = parseBank(participantSection.toString());
		String bankCode = parseBankCode(participantSection.toString());
		String account = parseAccount(participantSection.toString());
		return new Participant(name, city, address, idVAT, idNO, bank, bankCode, account, forInvoice);
	}

	private static String parseName(String participantSection) {
		String result = "UnknownParticipantName";
		String[] lines = participantSection.split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if (!(lines[i].startsWith(PREFIX_CITY) || lines[i].startsWith(PREFIX_ADDRESS) || lines[i].startsWith(PREFIX_ID_VAT) || lines[i].startsWith(PREFIX_ID_NO) || lines[i].startsWith(PREFIX_BANK) || lines[i].startsWith(PREFIX_CODE) || lines[i].trim().length() == 0 || lines[i].startsWith(PREFIX_RECIEVER) || lines[i].startsWith(PREFIX_ISSUER))) {
				result = lines[i].trim();
			}
		}
		return result;
	}

	private static String parseCity(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_CITY, "City");
	}

	private static String parseAddress(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_ADDRESS, "Address");
	}

	private static String parseIdVAT(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_ID_VAT, "IdVAT");
	}

	private static String parseIdNO(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_ID_NO, "IdNO");
	}

	private static String parseBank(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_BANK, "Bank");
	}

	private static String parseBankCode(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_CODE, "BankCode");
	}

	private static String parseAccount(String participantSection) {
		return parseFollowItem(participantSection, PREFIX_ACCOUNT, "Account");
	}

	private static String parseFollowItem(String participantSection, String startsWith, String itemName) {
		String result = "UnknownParticipant" + itemName;
		String[] lines = participantSection.split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith(startsWith)) {
				result = lines[i].substring(startsWith.length()).trim();
				break;
			}
		}
		return result;
	}

	public Participant(String name, String city, String address, String idVAT, String idNO, String bank, String bankCode, String account, Invoice forInvoice) {
		super();
		this.name = name;
		this.city = city;
		this.address = address;
		this.idVAT = idVAT;
		this.idNO = idNO;
		this.bank = bank;
		this.bankCode = bankCode;
		this.account = account;
		this.forInvoice = forInvoice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIdVAT() {
		return idVAT;
	}

	public void setIdVAT(String idVAT) {
		this.idVAT = idVAT;
	}

	public String getIdNO() {
		return idNO;
	}

	public void setIdNO(String idNO) {
		this.idNO = idNO;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Invoice getForInvoice() {
		return forInvoice;
	}

	@Override
	public String toString() {
		return "[Име=" + name + ", Град/Село=" + city + ", Адрес=" + address + ", Идентиф по ДДС=" + idVAT + ", Идентиф.No=" + idNO + ", Банка=" + bank + ", Код=" + bankCode + ", Сметка=" + account + "]";
	}
	
	@Override
	protected void validateInternal() {
		if (name == null || name.isEmpty()) {
			validationEntries.add(ValidationEntry.createError(forInvoice, "Липсва <Име> на Получател/Доставчик."));
		}
		if (city == null || city.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Град/Село>."));
		}
		if (address == null || address.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Адрес>."));
		}
		if (idVAT == null || idVAT.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Идентиф по ДДС>."));
		}
		if (idNO == null || idNO.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Идентиф.No>."));
		}
		if (bank == null || bank.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Банка>."));
		}
		if (bankCode == null || bankCode.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Код>."));
		}
		if (account == null || account.isEmpty()) {
			validationEntries.add(ValidationEntry.createNote(forInvoice, "Липсва <Сметка>."));
		}
	}

}
