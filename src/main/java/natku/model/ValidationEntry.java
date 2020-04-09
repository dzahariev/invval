package natku.model;

public class ValidationEntry {
	public static int SEVERITY_WARNING = 0;
	public static int SEVERITY_ERROR = 1;
	public static int SEVERITY_NOTE = 2;
	public static int SEVERITY_RESULT = 3;

	private int severity = 2;
	private Invoice forInvoice;
	private String message;

	public static ValidationEntry createNote(Invoice forInvoice, String message) {
		return new ValidationEntry(forInvoice, SEVERITY_NOTE, message);
	}
	public static ValidationEntry createWarning(Invoice forInvoice, String message) {
		return new ValidationEntry(forInvoice, SEVERITY_WARNING, message);
	}
	public static ValidationEntry createError(Invoice forInvoice, String message) {
		return new ValidationEntry(forInvoice, SEVERITY_ERROR, message);
	}

	public static ValidationEntry createOverallNote(String message) {
		return new ValidationEntry(null, SEVERITY_NOTE, message);
	}

	public static ValidationEntry createOverallWarning(String message) {
		return new ValidationEntry(null, SEVERITY_WARNING, message);
	}

	public static ValidationEntry createOverallError(String message) {
		return new ValidationEntry(null, SEVERITY_ERROR, message);
	}
	public static ValidationEntry createOverallResult(String message) {
		return new ValidationEntry(null, SEVERITY_RESULT, message);
	}

	private ValidationEntry(Invoice forInvoice, int severity, String message) {
		super();
		this.forInvoice = forInvoice;
		this.severity = severity;
		this.message = message;
	}

	public boolean isError() {
		return severity == SEVERITY_ERROR;
	}

	public boolean isWarning() {
		return severity == SEVERITY_WARNING;
	}

	public boolean isNote() {
		return severity == SEVERITY_NOTE;
	}

	public boolean isResult() {
		return severity == SEVERITY_RESULT;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public Invoice getForInvoice() {
		return forInvoice;
	}

	public void setForInvoice(Invoice forInvoice) {
		this.forInvoice = forInvoice;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		String[] severityString = {"[Предупреждение]", "[Грешка]", "[Бележка]", "[Резултат]"};

		if (forInvoice == null) {
			return severityString[severity] + message;
		}else{
			return severityString[severity] + "[Страница: " + forInvoice.getPageNumber() + ":Фактура No: " + forInvoice.getNumber() + "] " + message;
		}
	}

}
