package egd.fmre.qslbureau.capture.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

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

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.dto.RepresentativeInfo;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsServiceImpl extends ReportServiceActions implements ReportsService {

	@Autowired
	private SlotLogicService slotLogicService;
	@Autowired
	private QslCaptureService qslCaptureService;
	@Autowired
	private ZoneruleService zoneruleService;
	@Autowired
	private QslService qslService;
	@Autowired
	private RepresentativeService representativeService;
	
	private Status statusQslVigente;
	
	@PostConstruct
	private void init() {
		statusQslVigente = qslCaptureService.getStatusQslVigente();
	}


	@Override
	public List<QslsReport> gettingCallsignsMap() {
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		List<QslsReport> list = new ArrayList<>();
		for (Slot slot : openedOrCreatedSlots) {
			try {
				List<QslDto> qsls = qslCaptureService.qslsBySlot(slot.getId()).stream()
						.filter(q -> q.getStatus() == statusQslVigente.getId().intValue())
						.collect(Collectors.toList());
				for (QslDto qsl : qsls) {
					String efectiveCallsign = qsl.getVia() != null ? qsl.getVia() : qsl.getTo();
					Zonerule zonerule = zoneruleService.findActiveByCallsign(efectiveCallsign);
					if (zonerule == null) {
						processCallsign(list, new QslsReportKey(qsl.getTo(), qsl.getVia()), qsl.getDateTimeCapture());
					}
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
			return bos.toByteArray();
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
	}
	
	@Override
	public List<CapturedCallsign> getReportOfCapturedCallsigns() {
		return qslService.getCapturedCallsigns();
	}

	@Override
	public byte[] writeReportOfCapturedCallsigns(List<CapturedCallsign> captiredCallsigns) {
		Workbook workbook = new XSSFWorkbook();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
		//SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		

		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
		

		Sheet sheet = workbook.createSheet("indicativos");
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 7500);

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
		
		Row headerRow = sheet.createRow(0);
		
		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("QSL's en poder del QSL Bureau de la FMRE");
		headerCell.setCellStyle(firstHeaderStyle);
		
		Row dateRow = sheet.createRow(1);
		
		Cell dateCell = dateRow.createCell(1);
		dateCell.setCellValue(String.format("Fecha de generacion: %s", sdf.format(new Date())));
		dateCell.setCellStyle(firstHeaderStyle);
		
		headerRow = sheet.createRow(2);

		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("callsign");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(2);
		headerCell.setCellValue("Fecha de captura");
		headerCell.setCellStyle(headerStyle);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		Row row;
		
		TimeZone timeZoneMexico = TimeZone.getTimeZone("America/Mexico_City");
		TimeZone timeZoneUtc = TimeZone.getTimeZone("Etc/UTC");
		
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZoneUtc);

		int c = 3;
		for (CapturedCallsign capturedCallsign : captiredCallsigns) {
	        calendar.setTime(capturedCallsign.getOldestdatetime());
	        calendar.setTimeZone(timeZoneMexico);
	        
			Cell cell;

			row = sheet.createRow(c++);

			cell = row.createCell(1);
			cell.setCellValue(capturedCallsign.getCallsign());

			cell = row.createCell(2);
			cell.setCellValue(calendar.getTime());
			cell.setCellStyle(cellStyle);
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,2));
		sheet.addMergedRegion(new CellRangeAddress(1,1,1,2));

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			workbook.write(bos);
			return bos.toByteArray();
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
	}
	
	/*
	@Override
	public List<CapturedCallsign> getOrphansCallsigns() {
		// 1. obtener lista de callsigns para sin via vigentes que se encuentren en slots abiertos o creados
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		Set<String> hashSetCallsigns = new HashSet<>();
		for (Slot openedOrCreatedSlot : openedOrCreatedSlots) {
			List<Qsl> qsls = qslService.getBySlotAndStatus(openedOrCreatedSlot,
					Arrays.asList(QslstatusEnum.QSL_VIGENTE));
			//tomar el callsign, para los que tienen via tomar el via y para los que tienen to y no via tomar el to
			List<String> allCallsigns = qsls.stream().map(qsl -> qsl.getVia() !=null ? qsl.getVia() : qsl.getTo() ).collect(Collectors.toList());
			//se agregan
			hashSetCallsigns.addAll(allCallsigns);
		}
		
		List<Zonerule> activeZoneRules = zoneruleService.getAllActives();
		// List<RepresentativeInfo> representativeInfoList = new ArrayList<String>();
		for (Zonerule activeZoneRule : activeZoneRules) {
			// representativeInfoList.stream().map(r->r.getRepresentativeId().equals(activeZoneRule.get))
			Zone zone = activeZoneRule.getZone();
			List<Representative> representatives = representativeService.getRepresentativesByZone(zone);
			// System.out.println(representatives);
			RepresentativeInfo representativeInfo = new RepresentativeInfo();
			for(String )
			representativeInfo.setRepresentativeId(activeZoneRule.g);
			
		}
		List<String> activeZoneRulesCallsigns = activeZoneRules.stream().map(zr -> zr.getCallsign())
				.collect(Collectors.toList());
		hashSetCallsigns.removeAll(activeZoneRulesCallsigns);

		return null;
	}
	*/
}