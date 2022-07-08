package natku;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import natku.model.Invoice;
import natku.model.ValidationEntry;

public class ReadInvoices {
	private List<Invoice> invoices = new ArrayList<Invoice>(); 

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public List<ValidationEntry> parseInvoices(InputStream stream) throws IOException {
		PDDocument doc = PDDocument.load(stream);
		List<ValidationEntry> overallValidationEntries = new ArrayList<ValidationEntry>();

		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		PDFTextStripper Tstripper = new PDFTextStripper();
		String st = Tstripper.getText(doc);
		StringBuffer currentPage = new StringBuffer();
		int pageNumber = 0;
		String[] lines = st.split(System.lineSeparator());
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("Начини на плащане:")) {
				currentPage.append(lines[i]);
				currentPage.append(System.lineSeparator());
				Invoice currentInvoice = Invoice.parseInvoiceFromPage(pageNumber, currentPage.toString());
				invoices.add(currentInvoice);
				pageNumber++;
				currentPage = new StringBuffer();
				continue;
			}
			currentPage.append(lines[i]);
			currentPage.append(System.lineSeparator());
		}
		// Process last invoice
		if (currentPage.length() > 0) {
			Invoice currentInvoice = Invoice.parseInvoiceFromPage(pageNumber, currentPage.toString());
			invoices.add(currentInvoice);
		}

		// Control check
		int pdfPages = doc.getDocumentCatalog().getPages().getCount();
		if (invoices.size() != pdfPages) {
			overallValidationEntries.add(ValidationEntry.createOverallError("From " + pdfPages + " pages only " + invoices.size() + " are parsed!"));
		}
		if (invoices.size() > 0) {
			// Process and collect validaiton entries for invoices consistency
			// here...
			for (Invoice invoice : invoices) {
				List<ValidationEntry> invoiceValidationEntries = invoice.getValidationEntries();
				overallValidationEntries.addAll(invoiceValidationEntries);
			}

			// Check all invoices for lines with code CODE_ZAD_TO_COMMUNITY
			// here...
			boolean hasZadToCommunity = invoices.get(0).hasZadToCommunity();
			for (int i = 1; i < invoices.size(); i++) {
				Invoice currentInvoice = invoices.get(i);
				if (hasZadToCommunity != currentInvoice.hasZadToCommunity()) {
					overallValidationEntries.add(ValidationEntry.createOverallError("Документа не е консистентен! Първата фактура " + (hasZadToCommunity ? "има" : "няма") + " поле <Задължения към обществото> а на фактурата на страница " + currentInvoice.getPageNumber() + (currentInvoice.hasZadToCommunity() ? " има" : " няма") + " такова поле."));
				}
			}

			// Sum all entries for <Задължения към обществото> 
			double overallZadToCommunity = 0.0;
			for (Invoice currentInvoice : invoices) {
				overallZadToCommunity += currentInvoice.getZadToCommunity();
			}
			overallZadToCommunity = (double)Math.round(overallZadToCommunity * 1000d) / 1000d;
			overallValidationEntries.add(ValidationEntry.createOverallResult("Общо количество при <Задължения към обществото>:" + overallZadToCommunity));

			// Sum all entries for <Количество> 
			double overallQuantity = 0.0;
			for (Invoice currentInvoice : invoices) {
				overallQuantity += currentInvoice.getElEnergiaPeriodlQuantity();
			}
			overallQuantity = (double)Math.round(overallQuantity * 1000d) / 1000d;
			overallValidationEntries.add(ValidationEntry.createOverallResult("Общо количество при <Ел енергия за периода>:" + overallQuantity));
			
			// Sum all entries for <Мрежови такси и услуги> 
			double overallNetTaxeAndServices = 0.0;
			for (Invoice currentInvoice : invoices) {
				overallNetTaxeAndServices += currentInvoice.getNetTaxAndServices();
			}
			overallNetTaxeAndServices = (double)Math.round(overallNetTaxeAndServices * 100d) / 100d;
			overallValidationEntries.add(ValidationEntry.createOverallResult("Общо <Мрежови такси и услуги>:" + overallNetTaxeAndServices));
			
		} else {
			overallValidationEntries.add(ValidationEntry.createOverallError("Няма фактури за обработка."));
		}

		overallValidationEntries.add(ValidationEntry.createOverallResult("Край! Обработени фактури:" + invoices.size()));

		return overallValidationEntries;
	}
}
