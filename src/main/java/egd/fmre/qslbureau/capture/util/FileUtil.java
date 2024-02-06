package egd.fmre.qslbureau.capture.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;

import egd.fmre.qslbureau.capture.exception.FileException;

public abstract class FileUtil {

	public FileUtil() {
		// not called
	}

	protected static byte[] inputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[4096];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	protected static String byteArrayToBase64(byte[] byteArray) {
		byte[] encoded = Base64.getEncoder().encode(byteArray);
		return new String(encoded);
	}

	protected static String base64ToString(String s) {
		byte[] decoded = Base64.getDecoder().decode(s);
		return new String(decoded);
	}

	protected static String mimeBase64ToString(String s) {
		return new String(org.apache.commons.codec.binary.Base64.decodeBase64(s.getBytes()));
	}

	protected static InputStream byteArrayToInputStream(byte[] byteArray) {
		return new ByteArrayInputStream(byteArray);
	}

	protected static String getMd5Hash(byte[] byteArray) throws IOException {
		return DigestUtils.md5DigestAsHex(byteArray);
	}

	protected static List<String> pathToListString(Path filePath) throws IOException {
		Charset charset = Charset.forName("UTF8");
		return Files.readAllLines(filePath, charset);
	}

	protected static byte[] pathToArrayBytes(Path filePath) throws IOException {
		return Files.readAllBytes(filePath);
	}

	protected static String inputStreamToString(InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
	}

	protected static void write(File outputFile, byte[] dataForWriting) throws FileException {
		File parentFile = outputFile.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!parentFile.exists()) {
			throw new FileException("Parent path does not exists");
		}
		try {
			FileUtils.writeByteArrayToFile(outputFile, dataForWriting);
		} catch (IOException e) {
			throw new FileException(e);
		}
	}
}
