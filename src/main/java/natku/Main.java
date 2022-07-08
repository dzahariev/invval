/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package natku;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import natku.model.Invoice;
import natku.model.ValidationEntry;

@Controller
@SpringBootApplication
public class Main {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index() {
		return "index";
	}

	@RequestMapping("/home")
	String home() {
		return "index";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	String downloadxlsGet() {
		return "backtoindex";
	}

	@RequestMapping(value = "/download/{fileName:.+}", method = RequestMethod.POST)
	ResponseEntity<Resource> downloadPOST(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, @PathVariable String fileName, HttpServletRequest request) {
		Resource resource = null;
		ParseResult parseResult = null;
		try {
			parseResult = new ParseResult(file.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String contentType = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (fileName.endsWith(".csv")) {
			contentType = "text/csv";

			out.write(239); // 0xEF
			out.write(187); // 0xBB
			out.write(191); // 0xBF
			OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
			PrintWriter opw = new PrintWriter(writer, true);
			for (Invoice invoice : parseResult.getInstance().getInvoices()) {
				String recieverName = invoice.getReciever().getName().replace(',', '.');
				String idNo = invoice.getReciever().getIdNO();
				double zadToCom = invoice.getZadToCommunity();
				double netTaxAndServices = invoice.getNetTaxAndServices();
				double еlEnergiaPeriodEdCena = invoice.getElEnergiaPeriodEdCena();
				double еlEnergiaPeriodQuantity = invoice.getElEnergiaPeriodlQuantity();
				double еlEnergiaPeriodValue = invoice.getElEnergiaPeriodValue();
				opw.println(recieverName + "," + idNo + "," + zadToCom + "," + netTaxAndServices + ","
						+ еlEnergiaPeriodEdCena + "," + еlEnergiaPeriodQuantity + "," + еlEnergiaPeriodValue);
			}
			opw.flush();
			opw.close();
		}

		if (fileName.endsWith(".xls")) {
			contentType = "application/vnd.ms-excel";

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Списък");
			HSSFRow rowhead = sheet.createRow((short) 0);
			rowhead.createCell(0).setCellValue("Име");
			rowhead.createCell(1).setCellValue("Идентиф.No");
			rowhead.createCell(2).setCellValue("Задължения към обществото");
			rowhead.createCell(3).setCellValue("Мрежови такси и услуги");
			rowhead.createCell(4).setCellValue("Ел Енергия за периода Ед. Цена");
			rowhead.createCell(5).setCellValue("Ел Енергия за периода Количество");
			rowhead.createCell(6).setCellValue("Ел Енергия за периода Стойност");

			int count = 1;
			for (Invoice invoice : parseResult.getInstance().getInvoices()) {
				String recieverName = invoice.getReciever().getName().replace(',', '.');
				String idNo = invoice.getReciever().getIdNO();
				double zadToCom = invoice.getZadToCommunity();
				double netTaxAndServices = invoice.getNetTaxAndServices();
				double еlEnergiaPeriodEdCena = invoice.getElEnergiaPeriodEdCena();
				double еlEnergiaPeriodQuantity = invoice.getElEnergiaPeriodlQuantity();
				double еlEnergiaPeriodValue = invoice.getElEnergiaPeriodValue();

				HSSFRow row = sheet.createRow(count++);
				row.createCell(0).setCellValue(recieverName);
				row.createCell(1).setCellValue(idNo);
				row.createCell(2).setCellValue(zadToCom);
				row.createCell(3).setCellValue(netTaxAndServices);
				row.createCell(4).setCellValue(еlEnergiaPeriodEdCena);
				row.createCell(5).setCellValue(еlEnergiaPeriodQuantity);
				row.createCell(6).setCellValue(еlEnergiaPeriodValue);
			}
			try {
				workbook.write(out);
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resource = new ByteArrayResource(out.toByteArray());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);

	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	String uploadGet() {
		return "backtoindex";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	String uploadPOST(@RequestParam("severity") String[] severities, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:/uploadStatus";
		}

		try {

			// Get the file and save it somewhere
			ParseResult parseResult = new ParseResult(file.getInputStream());
			redirectAttributes.addFlashAttribute("valentriesresults",
					parseResult.getValEntries(ValidationEntry.SEVERITY_RESULT));
			if (hasSeverity(ValidationEntry.SEVERITY_ERROR, severities)) {
				redirectAttributes.addFlashAttribute("valentrieserrors",
						parseResult.getValEntries(ValidationEntry.SEVERITY_ERROR));
			}
			if (hasSeverity(ValidationEntry.SEVERITY_WARNING, severities)) {
				redirectAttributes.addFlashAttribute("valentrieswarnings",
						parseResult.getValEntries(ValidationEntry.SEVERITY_WARNING));
			}
			if (hasSeverity(ValidationEntry.SEVERITY_NOTE, severities)) {
				redirectAttributes.addFlashAttribute("valentriesnotes",
						parseResult.getValEntries(ValidationEntry.SEVERITY_NOTE));
			}

			redirectAttributes.addFlashAttribute("message", "Файла е обработен: '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/uploadStatus";
	}

	@RequestMapping("/uploadStatus")
	public String uploadStatus() {
		return "uploadStatus";
	}

	private boolean hasSeverity(int severity, String[] providedSeverities) {
		String severityName = "";
		switch (severity) {
		case 0: // SEVERITY_WARNING = 0;
			severityName = "warnings";
			break;
		case 1: // SEVERITY_ERROR = 1;
			severityName = "errors";
			break;
		case 2: // SEVERITY_NOTE = 2;
			severityName = "notes";
			break;
		default:
			break;
		}
		for (int i = 0; i < providedSeverities.length; i++) {
			if (providedSeverities[i].equals(severityName)) {
				return true;
			}
		}
		return false;
	}

}
