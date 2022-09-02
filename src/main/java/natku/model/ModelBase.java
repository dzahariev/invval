package natku.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelBase {
	protected List<ValidationEntry> validationEntries = new ArrayList<ValidationEntry>();
	private boolean isValidated = false;

	public List<ValidationEntry> getValidationEntries() {
		if (!isValidated) {
			validate();
		}
		return validationEntries;
	}

	public boolean hasErrors() {
		List<ValidationEntry> entries = getValidationEntries();
		for (ValidationEntry valEntry : entries) {
			if (valEntry.isError()) {
				return true;
			}
		}
		return false;
	}

	public List<ValidationEntry> getErrors() {
		List<ValidationEntry> entries = getValidationEntries();
		List<ValidationEntry> result = new ArrayList<ValidationEntry>();
		for (ValidationEntry valEntry : entries) {
			if (valEntry.isError()) {
				result.add(valEntry);
			}
		}
		return result;
	}

	private void validate() {
		validateInternal();
		isValidated = true;
	}

	protected double toDouble(String value) {
		double result = -1;
		try {
			result = Double.parseDouble(value);
		} catch (NumberFormatException nfe) {
			// Initialize with default value
			result = -1;
		}
		return result;
	}

	protected double rounded(double value1, double value2) {
		return roundToMultipleOfFive(value1, value2);
	}

	private double roundToMultipleOfFive(double a, double b) {
		BigDecimal aa = new BigDecimal(String.valueOf(a));
		BigDecimal bd1000 = new BigDecimal(1000);
		aa = aa.multiply(bd1000);
		BigDecimal bb = new BigDecimal(String.valueOf(b));
		BigDecimal x = aa.multiply(bb);
		String str = x.toString(); 
		long wholeMaximized = Long.parseLong(str.substring(0, str.indexOf('.')));
		String strWholeMaximized = String.valueOf(wholeMaximized);
		String strLastDigit = strWholeMaximized.substring(strWholeMaximized.length()-1);
		String strNonLastDigit = strWholeMaximized.substring(0, strWholeMaximized.length()-1);
		if ("".equals(strNonLastDigit)) {
			strNonLastDigit = "0";
		}
		long nonLastDigit = Long.parseLong(strNonLastDigit);
		long lastDigit = Long.parseLong(strLastDigit);
		if (lastDigit >= 5) {
			nonLastDigit++;
		}
		double minimized = nonLastDigit/100.0;
		return minimized;
	}

	protected abstract void validateInternal();
}
