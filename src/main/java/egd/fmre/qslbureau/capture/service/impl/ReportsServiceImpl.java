package egd.fmre.qslbureau.capture.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import egd.fmre.qslbureau.capture.dto.OrphanCallsignReportObjectDTO;
import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslToViaDTO;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneService;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsServiceImpl extends ReportServiceActions implements ReportsService {

	@Autowired SlotLogicService      slotLogicService;
	@Autowired QslCaptureService     qslCaptureService;
	@Autowired ZoneruleService       zoneruleService;
	@Autowired QslService            qslService;
	@Autowired ZoneService           zoneService;
	@Autowired CallsignRuleService   callsignRuleService;
	@Autowired RepresentativeService representativeService;
	
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
	
    @Override
    public RedirectListObjectRepresentative redirectLists(Representative representative) {

        RedirectListObjectRepresentative rlor = new RedirectListObjectRepresentative();
		String nombre = representative.getName();
		nombre += representative.getLastName() != null && !representative.getLastName().equals("")
				? " " + representative.getLastName()
				: "";
		rlor.setRepresentativeId(representative.getId());
		rlor.setName(nombre);

        List<Zone> zoneList = zoneService.getActiveZonesByRepresentative(representative);
        for (Zone zone : zoneList) {

            RedirectListObjectZone rloz = new RedirectListObjectZone();
            rloz.setName(zone.getName());

			List<Zonerule> zoneruleList = zoneruleService.getAllActivesByZone(zone);
			for (Zonerule zonerule : zoneruleList) {
				String callsign = zonerule.getCallsign();
				
				RedirectListObjecCallsignRule rloo = new RedirectListObjecCallsignRule();

				Integer num = null;
				List<Slot> slots = slotLogicService.getOpenedOrCreatedSlotByCallsign(callsign);
				if (slots == null) {
					rloo.setCallsignTo(callsign);
					rloo.setCallsignRedirect(null);
					rloo.setLocalId(null);
					rloo.setSlotNum(null);
					rloz.getRlocList().add(rloo);
					continue;
				}
				for (Slot slot : slots) {
					if (slot != null) {
						List<Qsl> qsls = qslService.getActiveQslsForSlot(slot);
						num = qsls != null && !qsls.isEmpty() ? qsls.size() : 0;
					}

					List<CallsignRule> callsignRuleList = callsignRuleService.findActiveByCallsignRedirect(callsign);
					rloo.setCallsignTo(callsign);
					rloo.setCallsignRedirect(null);
					if (slot != null) {
						rloo.setLocalId(slot.getLocal().getId());
						rloo.setSlotNum(slot.getSlotNumber());
					}
					rloo.setTotal(num);
					rloz.getRlocList().add(rloo);
					if (callsignRuleList != null && !callsignRuleList.isEmpty()) {
						for (CallsignRule callsignRule : callsignRuleList) {
							rloo = new RedirectListObjecCallsignRule();
							rloo.setCallsignTo(callsignRule.getCallsignTo());
							rloo.setCallsignRedirect(callsignRule.getCallsignRedirect());
							rloz.getRlocList().add(rloo);
						}
					}
				}
			}
            rlor.getRlozList().add(rloz);
        }
        return rlor;
    }
    
    @Override
    public byte[] redirectListObjectRepresentativeToWorkbook(RedirectListObjectRepresentative redirectListObjectRepresentative) {
    	Workbook workbook = new XSSFWorkbook();
		
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
		
		Sheet sheet = workbook.createSheet("indicativos");
		
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 7500);
		sheet.setColumnWidth(3, 7500);
		sheet.setColumnWidth(4, 7500);
		
    	boolean okRepresentative = false;
    	
    	Row row;
    	Cell cell;
    	int c = 3;
    	
    	row = sheet.createRow(2);
    	
    	cell = row.createCell(1);
    	cell.setCellValue("Representante");
    	
    	cell = row.createCell(2);
    	cell.setCellValue("Zona");
    	
    	cell = row.createCell(3);
    	cell.setCellValue("CALLSIGN PARA");
    	
    	cell = row.createCell(4);
    	cell.setCellValue("CALLSIGN REDIRECCION");
    	
    	cell = row.createCell(5);
    	cell.setCellValue("LOCAL ID");
    	
    	cell = row.createCell(6);
    	cell.setCellValue("SLOT NUM");
    	
    	cell = row.createCell(7);
    	cell.setCellValue("CANTIDAD QSLS");
    	
    	int total = 0;
    	
		for (RedirectListObjectZone redirectListObjectZone : redirectListObjectRepresentative.getRlozList()) {
			boolean okZone = false;
			for (RedirectListObjecCallsignRule redirectListObjecCallsignRule : redirectListObjectZone.getRlocList()) {
				row = sheet.createRow(c++);

				cell = row.createCell(1);
				if (!okRepresentative) {
					cell.setCellValue(redirectListObjectRepresentative.getName());
					okRepresentative = true;
				}

				cell = row.createCell(2);
				if (!okZone) {
					cell.setCellValue(redirectListObjectZone.getName());
					okZone = true;
				}

				cell = row.createCell(3);
				cell.setCellValue(redirectListObjecCallsignRule.getCallsignTo());

				cell = row.createCell(4);
				cell.setCellValue(redirectListObjecCallsignRule.getCallsignRedirect());

				if (redirectListObjecCallsignRule.getLocalId() != null) {
					cell = row.createCell(5);
					cell.setCellValue(redirectListObjecCallsignRule.getLocalId());
				}

				if (redirectListObjecCallsignRule.getSlotNum() != null) {
					cell = row.createCell(6);
					cell.setCellValue(redirectListObjecCallsignRule.getSlotNum());
				}
				
				if (redirectListObjecCallsignRule.getTotal() != null) {
					cell = row.createCell(7);
					cell.setCellValue(redirectListObjecCallsignRule.getTotal());
					total += redirectListObjecCallsignRule.getTotal();
				}
			}
		}
		row = sheet.createRow(c++);

		cell = row.createCell(3);
		cell.setCellValue("TOTAL");
		
		cell = row.createCell(7);
		cell.setCellValue(total);

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
	public List<OrphanCallsignReportObjectDTO> getOrphansCallsignsReport() {
		List<Zonerule> zonerules = zoneruleService.getAllActives();

		List<Slot> slots = slotLogicService.getOpenedOrCreatedSlots();
		Set<QslToViaDTO> orphansQslToViaDTOSet = new HashSet<>();
		for (Slot slot : slots) {
			List<Qsl> allQslsInSlot = qslService.getActiveQslsForSlot(slot);
			Set<QslToViaDTO> t = allQslsInSlot.stream().map(c -> new QslToViaDTO(c.getTo(), c.getVia()))
					.collect(Collectors.toSet());
			orphansQslToViaDTOSet.addAll(t);
		}

		List<Representative> representatives = representativeService.findAllActive();
		List<OrphanCallsignReportObjectDTO> orphanCallsignReportObjectDTOList = new ArrayList<>();
		for (Representative representative : representatives) {
			List<Zone> zones = zoneService.getActiveZonesByRepresentative(representative);
			for (Zone zone : zones) {
				List<Zonerule> activeZonerulesForZone = zonerules.stream().filter(zr -> zr.getZone().equals(zone))
						.collect(Collectors.toList());
				for (Zonerule activeZoneruleForZone : activeZonerulesForZone) {
					Set<QslToViaDTO> setsToRemove = orphansQslToViaDTOSet.stream()
							.filter(q -> activeZoneruleForZone.getCallsign().equalsIgnoreCase(q.getTo())
									|| activeZoneruleForZone.getCallsign().equalsIgnoreCase(q.getVia()))
							.collect(Collectors.toSet());
					orphansQslToViaDTOSet.removeAll(setsToRemove);
					
					List<CallsignRule> activeCallsignRules = callsignRuleService.findActiveByCallsignRedirect(activeZoneruleForZone.getCallsign());
					for (CallsignRule callsignRule : activeCallsignRules) {
						Set<QslToViaDTO> setsToRemoveBis = orphansQslToViaDTOSet.stream()
								.filter(q -> q.getTo().equals(callsignRule.getCallsignTo()))
								.collect(Collectors.toSet());
						orphansQslToViaDTOSet.removeAll(setsToRemoveBis);
					}
					
					OrphanCallsignReportObjectDTO ocro = new OrphanCallsignReportObjectDTO();
					ocro.setRepresentativeName(representative.getName()
							+ (representative.getLastName() != null ? " " + representative.getLastName() : ""));
					ocro.setZoneName(zone.getName());
					ocro.setQslTo(activeZoneruleForZone.getCallsign());
					ocro.setQslVia(null);
					orphanCallsignReportObjectDTOList.add(ocro);
				}
			}
		}
		List<OrphanCallsignReportObjectDTO> orphans = orphansQslToViaDTOSet.stream().map(orphanQslToViaDTO -> {
			OrphanCallsignReportObjectDTO ocro = new OrphanCallsignReportObjectDTO();
			ocro.setRepresentativeName(null);
			ocro.setZoneName(null);
			ocro.setQslTo(orphanQslToViaDTO.getTo());
			ocro.setQslVia(orphanQslToViaDTO.getVia());
			return ocro;
		}).collect(Collectors.toList());
		orphanCallsignReportObjectDTOList.addAll(orphans);
		return orphanCallsignReportObjectDTOList;
	}
	
	@Override
	public byte[] generateOrphansReport(
			List<OrphanCallsignReportObjectDTO> orphanCallsignReportObjectDTOList) {
		Workbook workbook = new XSSFWorkbook();

		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

		Sheet sheet = workbook.createSheet("indicativos");

		Row row;
		Cell cell;
		int c = 3;
		
		List<OrphanCallsignReportObjectDTO> orphansOcro = orphanCallsignReportObjectDTOList.stream()
				.filter(ocro -> ocro.getRepresentativeName() == null).collect(Collectors.toList());
		boolean headerAdded = false;
		for (OrphanCallsignReportObjectDTO elementOfZone : orphansOcro) {
			row = sheet.createRow(c++);
			
			if (!headerAdded) {
				cell = row.createCell(3);
				cell.setCellValue("Sin agrupador");
			}
			headerAdded = true;

			cell = row.createCell(5);
			cell.setCellValue(elementOfZone.getQslTo());

			cell = row.createCell(6);
			cell.setCellValue(elementOfZone.getQslVia());
		}

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
}