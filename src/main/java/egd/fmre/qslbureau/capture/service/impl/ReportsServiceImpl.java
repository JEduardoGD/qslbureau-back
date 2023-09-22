package egd.fmre.qslbureau.capture.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsServiceImpl implements ReportsService {

	@Autowired
	private SlotLogicService slotLogicService;
	@Autowired
	private QslCaptureService qslCaptureService;


	@Override
	public List<QslsReport> gettingCallsignsMap() {
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		List<QslsReport> list = new ArrayList<>();
		for (Slot slot : openedOrCreatedSlots) {
			try {
				Set<QslDto> qsls = qslCaptureService.qslsBySlot(slot.getId());
				for (QslDto qsl : qsls) {
					processCallsign(list, new QslsReportKey(qsl.getTo(), qsl.getVia()), qsl.getDateTimeCapture());
				}
			} catch (QslcaptureException e) {
				log.error(e.getMessage());
			}
		}
		return list;
	}

	@Override
	public byte[] writeReport(List<QslsReport> list) {
		Workbook workbook = new XSSFWorkbook();
		

		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

		Sheet sheet = workbook.createSheet("indicativos");
		sheet.setColumnWidth(4, 4500);
		sheet.setColumnWidth(5, 4500);

		Row headerRow = sheet.createRow(0);

		CellStyle firstHeaderStyle = workbook.createCellStyle();
		firstHeaderStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		firstHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		firstHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.index);
		headerStyle.setFont(font);
		firstHeaderStyle.setFont(font);

		Cell headerCell;
		
		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("QSLs capturadas sin agrupacion");
		headerCell.setCellStyle(firstHeaderStyle);
		
		headerRow = sheet.createRow(1);

		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("QSL To");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(2);
		headerCell.setCellValue("QSL Via");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(3);
		headerCell.setCellValue("Count");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(4);
		headerCell.setCellValue("Older");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(5);
		headerCell.setCellValue("Newer");
		headerCell.setCellStyle(headerStyle);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		Row row;

		int c = 2;
		for (QslsReport qsl : list) {
			Cell cell;

			row = sheet.createRow(c++);

			cell = row.createCell(1);
			cell.setCellValue(qsl.getQslsReportKey().getTo());

			cell = row.createCell(2);
			cell.setCellValue(qsl.getQslsReportKey().getVia() != null ? qsl.getQslsReportKey().getVia() : null);

			cell = row.createCell(3);
			cell.setCellValue(qsl.getCount());

			cell = row.createCell(4);
			cell.setCellValue(qsl.getOldest());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue(qsl.getNewEst());
			cell.setCellStyle(cellStyle);

		}
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,5));

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			workbook.write(bos);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					log.error("Error closing ByteArrayOutputStream");
					log.error(e.getMessage());
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("Error closing Workbook");
					log.error(e.getMessage());
				}
			}
		}
		return bos.toByteArray();
	}

	private void processCallsign(List<QslsReport> list, QslsReportKey key, Date dateTimeCapture) {
		QslsReport qslsReport;
		int i = list.indexOf(new QslsReport(key));
		if (i == -1) {
			qslsReport = new QslsReport(key);
			qslsReport.setCount(0);
			list.add(qslsReport);
			i = list.indexOf(new QslsReport(key));
		}
		qslsReport = list.get(i);
		Date oldEst = (qslsReport.getOldest() == null || dateTimeCapture.before(qslsReport.getOldest()))
				? dateTimeCapture
				: qslsReport.getOldest();
		Date newEst = (qslsReport.getNewEst() == null || dateTimeCapture.after(qslsReport.getNewEst()))
				? dateTimeCapture
				: qslsReport.getNewEst();
		qslsReport.setCount(qslsReport.getCount() + 1);
		qslsReport.setOldest(oldEst);
		qslsReport.setNewEst(newEst);
		list.set(i, qslsReport);
	}
}