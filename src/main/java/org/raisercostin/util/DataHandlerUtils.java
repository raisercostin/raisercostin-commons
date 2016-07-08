package org.raisercostin.util;

import java.io.*;

import javax.activation.DataHandler;

import org.apache.commons.io.IOUtils;

public class DataHandlerUtils {

	private static final String BYTES_CONTENT_TYPE = "application/bytes";
	private static final String STRING_ENCODING = "UTF-8";

	public static DataHandler fromString(String string) {
		try {
			return fromBytes(string.getBytes(STRING_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toString(DataHandler dh) {
		try {
			return new String(toBytes(dh), STRING_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static DataHandler fromBytes(byte[] content) {
		return fromBytes(BYTES_CONTENT_TYPE, content, content.length);
	}

	public static DataHandler fromBytes(byte[] content, int length) {
		return fromBytes(BYTES_CONTENT_TYPE, content, length);
	}

	public static DataHandler fromBytes(String contentType, byte[] content, int length) {
		return new DataHandler(new ByteArrayDataSource(contentType, content, length));
	}

	public static byte[] toBytes(DataHandler handler) {
		try {
			byte[] byteArray = IOUtils.toByteArray(handler.getInputStream());
			return byteArray;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class ByteArrayDataSource implements javax.activation.DataSource {
		private final byte[] bytes;
		private final String contentType;
		private int length;

		public ByteArrayDataSource(String contentType, byte[] bytes, int length) {
			this.bytes = bytes;
			this.contentType = contentType;
			this.length = length;
		}
		@Override
		public String getContentType() {
			return contentType;
		}
		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(bytes, 0, length);
		}
		/**
		 * for completeness, here's how to implement the outputstream. this is unnecessary for what you're doing, you
		 * can just throw an UnsupportedOperationException.
		 */
		@Override
		public OutputStream getOutputStream() {
			throw new UnsupportedOperationException();
			// final ByteArrayDataSource bads = this;
			// final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// // return an outputstream that sets my byte array
			// // when it is closed.
			// return new FilterOutputStream(baos) {
			// @Override
			// public void close() throws IOException {
			// baos.close();
			// bads.setBytes(baos.toByteArray());
			// }
			// };
		}
		@Override
		public String getName() {
			return "ByteArrayDataSource";
		}
	}

	public static DataHandler fromFile(File file) {
		try {
			return fromBytes(IOUtils.toByteArray(new BufferedInputStream(new FileInputStream(file))));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// try {
		// return new DataHandler(new URL("file:///" + file.getAbsolutePath()),"whatever");
		// } catch (MalformedURLException e) {
		// throw new RuntimeException(e);
		// }
	}
}