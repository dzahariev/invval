package natku;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import natku.model.ValidationEntry;

public class ParseResult {
	private List<ValidationEntry> validationEntriesForInstance;
	private ReadInvoices instance; 

	public ParseResult(byte[] fileContent) throws IOException {
		this.instance = new ReadInvoices();
		this.validationEntriesForInstance = instance.parseInvoices(fileContent);
	}


	public ReadInvoices getInstance() {
		return instance;
	}


	public List<String> getValEntries(int severity){
		List<String> valEntries = new ArrayList<String>();
		boolean hasEntries = false;
		for (ValidationEntry valEntry : validationEntriesForInstance) {
			if (valEntry.getSeverity() == severity) {
				valEntries.add(valEntry.toString());
				hasEntries = true;
			}
		}
		if (!hasEntries) {
			valEntries.add("Няма!");
		}

		return valEntries;
	}
}
