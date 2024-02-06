package egd.fmre.qslbureau.capture.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import egd.fmre.qslbureau.capture.dto.imageutil.Dimensions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FileUtil {

	private FileUtil() {
		// not called
	}

	public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[4096];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	public static String byteArrayToBase64(byte[] byteArray) {
		byte[] encoded = Base64.getEncoder().encode(byteArray);
		return new String(encoded);
	}

	public static String base64ToString(String s) {
		byte[] decoded = Base64.getDecoder().decode(s);
		return new String(decoded);
	}

	public static String mimeBase64ToString(String s) {
		return new String(org.apache.commons.codec.binary.Base64.decodeBase64(s.getBytes()));
	}

	public static InputStream byteArrayToInputStream(byte[] byteArray) {
		return new ByteArrayInputStream(byteArray);
	}

	public static String getMd5Hash(byte[] byteArray) throws IOException {
		return DigestUtils.md5DigestAsHex(byteArray);
	}

	public static List<String> pathToListString(Path filePath) throws IOException {
		Charset charset = Charset.forName("UTF8");
		return Files.readAllLines(filePath, charset);
	}

	public static byte[] pathToArrayBytes(Path filePath) throws IOException {
		return Files.readAllBytes(filePath);
	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
	}

	public static Path loadFileFromResources(String fileOnResources) {
		Path path = null;
		try {
			path = Paths.get(ClassLoader.getSystemResource(fileOnResources).toURI());
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		return path;
	}

	public static Dimensions getDimensionsOfImage(File file) {
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(file);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		if (bimg != null) {
			return new Dimensions(bimg.getWidth(), bimg.getHeight());
		}
		return null;
	}
	
	public static byte[] generateQRCodeImage(String barcodeText) {
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix;
		try {
			byteMatrix = qrCodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE,
					StaticsShipLabelUtil.QRCODE_DIMENSION.getWidth(), StaticsShipLabelUtil.QRCODE_DIMENSION.getHeight(),
					hintMap);
		} catch (WriterException e) {
			log.error(e.getMessage());
			return null;
		}
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}

		try (OutputStream os = new ByteArrayOutputStream()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public static String convertTextFileToString(String fileName) {
	    try (Stream<String> stream 
	      = Files.lines(Paths.get(ClassLoader.getSystemResource(fileName).toURI()))) {
	        
	        return stream.collect(Collectors.joining(" "));
	    } catch (IOException | URISyntaxException e) {
	        return null;
	    }
	}
}
