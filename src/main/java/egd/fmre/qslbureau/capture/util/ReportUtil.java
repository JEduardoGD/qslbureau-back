package egd.fmre.qslbureau.capture.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.SlotReport;
import egd.fmre.qslbureau.capture.dto.imageutil.Dimensions;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ReportUtil {
	private ReportUtil() {
		// no call
	}

	public static byte[] writeSlotsReport(List<SlotReport> list) {
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
		headerCell.setCellValue("QSLs capturadas agrupadas");
		headerCell.setCellStyle(firstHeaderStyle);

		headerRow = sheet.createRow(1);

		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("Slot Numero");
		headerCell.setCellStyle(headerStyle);

		headerCell = headerRow.createCell(2);
		headerCell.setCellValue("Callsign to");
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
		for (SlotReport sr : list) {
			Cell cell;

			row = sheet.createRow(c++);

			cell = row.createCell(1);
			cell.setCellValue(sr.getSlot().getSlotNumber());

			cell = row.createCell(1);
			cell.setCellValue(sr.getSlot().getSlotNumber());

			cell = row.createCell(2);
			cell.setCellValue(sr.getSlot().getCallsignto());

			cell = row.createCell(3);
			cell.setCellValue(sr.getQslsQuantity());
		}

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

	public static byte[] writeReport(List<QslsReport> list) {
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

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));

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

	public static void createShipLabel(Slot slot) throws InvalidFormatException, IOException, URISyntaxException {
		XWPFDocument document = new XWPFDocument();

		// XWPFStyles styles = document.createStyles();
		document.createStyles();

		// there must be section properties for the page having at least the page size
		// set
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		CTPageSz pageSz = sectPr.addNewPgSz();
		pageSz.setW(BigInteger.valueOf(StaticsShipLabelUtil.LETTER_DIMENSION.getWidth()));
		pageSz.setH(BigInteger.valueOf(StaticsShipLabelUtil.LETTER_DIMENSION.getHeight()));

		createImage(document, "FMRE.png", 0.15);

		createTitle(document, StaticsShipLabelUtil.HEADER);

		createHorizontalRect(document);

		createParagrap(document, StaticsShipLabelUtil.PARAGRAPH_1);

		createParagrap(document, StaticsShipLabelUtil.PARAGRAPH_2);

		String confirmUrl = StaticsShipLabelUtil.CONFIRM_URL + StaticValuesHelper.SLASH + slot.getConfirmCode();

		createParagrap(document, String.format(StaticsShipLabelUtil.PARAGRAPH_3, confirmUrl));

		createParagrap(document, StaticsShipLabelUtil.PARAGRAPH_4);

		byte[] ba = FileUtil.generateQRCodeImage(
				StaticsShipLabelUtil.CONFIRM_URL + StaticValuesHelper.SLASH + slot.getConfirmCode());
		createImage(document, ba, 01.0);

		// document must be written so underlaaying objects will be committed
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		document.close();

		document = new XWPFDocument(new ByteArrayInputStream(out.toByteArray()));
		PdfOptions options = PdfOptions.create();
		PdfConverter converter = (PdfConverter) PdfConverter.getInstance();
		converter.convert(document, new FileOutputStream("XWPFToPDFConverterSampleMin.pdf"), options);

		document.close();
	}

	private static void createTitle(XWPFDocument document, String textOfTitle) {
		XWPFParagraph title = document.createParagraph();
		title.setBorderBottom(Borders.SINGLE);
		title.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun titleRun = title.createRun();
		titleRun.setText(StaticsShipLabelUtil.HEADER);
		titleRun.setColor(StaticsShipLabelUtil.NORMAL_COLOR);
		titleRun.setBold(true);
		titleRun.setFontFamily("Courier");
		titleRun.setFontSize(13);
	}

	private static void createImage(XWPFDocument document, byte[] byteArray, double scale) {
		XWPFParagraph image = document.createParagraph();
		image.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun imageRun = image.createRun();
		imageRun.setTextPosition(20);

		try (InputStream targetStream = new ByteArrayInputStream(byteArray)) {
			imageRun.addPicture(targetStream, XWPFDocument.PICTURE_TYPE_PNG, "qrcode.png",
					Units.toEMU(StaticsShipLabelUtil.QRCODE_DIMENSION.getWidth() * scale),
					Units.toEMU(StaticsShipLabelUtil.QRCODE_DIMENSION.getHeight() * scale));
		} catch (IOException | InvalidFormatException e) {
			log.error(e.getMessage());
		}
	}

	private static void createImage(XWPFDocument document, String fileImagen, double scale) {
		XWPFParagraph image = document.createParagraph();
		image.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun imageRun = image.createRun();
		imageRun.setTextPosition(20);
		Path imagePath = FileUtil.loadFileFromResources(fileImagen);
		if (imagePath != null) {
			Dimensions dimensions = FileUtil.getDimensionsOfImage(imagePath.toFile());
			try {
				imageRun.addPicture(Files.newInputStream(imagePath), XWPFDocument.PICTURE_TYPE_PNG,
						imagePath.getFileName().toString(), Units.toEMU(dimensions.getWidth() * scale),
						Units.toEMU(dimensions.getHeight() * scale));
			} catch (InvalidFormatException | IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	private static void createHorizontalRect(XWPFDocument document) {
		XWPFTable table = document.createTable(1, 1);
		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(10000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(10000));
		// XWPFTableRow newrow = table.createRow();
		table.getRow(0).getCell(0).setText(StaticValuesHelper.EMPTY_STRING);
		table.removeBorders();
		table.setTopBorder(XWPFBorderType.SINGLE, 10, 0, "000000");
	}

	private static void createParagrap(XWPFDocument document, String paragraph) {
		XWPFParagraph para1 = document.createParagraph();
		para1.setAlignment(ParagraphAlignment.BOTH);
		XWPFRun para1Run = para1.createRun();
		para1Run.setText(paragraph);
	}
}
