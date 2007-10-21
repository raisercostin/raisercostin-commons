/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util.properties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author: Costin Emilian GRIGORE
 */
public class ExtendedProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5759852499244438960L;

	public void store(final OutputStream out, final String header,
			final Valueable valueable, final Descriptable descriptable,
			final DefaultValue defaultValue) throws IOException {
		BufferedWriter awriter;
		awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
		if (header != null) {
			writeln(awriter, "#" + header);
		}
		writeln(awriter, "#" + new Date().toString());
		for (final Enumeration<Object> e = keys(); e.hasMoreElements();) {
			String key = (String)e.nextElement();
			final Object value = get(key);

			writeln(awriter, "#" + descriptable.getDescription(value));
			String val = valueable.getValue(value);
			key = saveConvert(key, true);

			/*
			 * No need to escape embedded and trailing spaces for value, hence
			 * pass false to flag.
			 */
			if ((val == null) || (val.equals(""))) {
				if (defaultValue.getDefaultValue(value) != null) {
					val = defaultValue.getDefaultValue(value).toString();
				}
			}
			val = saveConvert(val, false);
			writeln(awriter, key + "=" + val);
		}
		awriter.flush();
	}

	private static void writeln(final BufferedWriter bw, final String s)
			throws IOException {
		bw.write(s);
		bw.newLine();
	}

	/*
	 * Converts unicodes to encoded &#92;uxxxx and writes out any of the
	 * characters in specialSaveChars with a preceding slash
	 */
	private String saveConvert(final String theString, final boolean escapeSpace) {
		if (theString == null) {
			return "";
		}
		final int len = theString.length();
		final StringBuffer outBuffer = new StringBuffer(len * 2);

		for (int x = 0; x < len; x++) {
			final char aChar = theString.charAt(x);
			switch (aChar) {
			case ' ':
				if ((x == 0) || escapeSpace) {
					outBuffer.append('\\');
				}

				outBuffer.append(' ');
				break;
			case '\\':
				outBuffer.append('\\');
				outBuffer.append('\\');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			default:
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					if (specialSaveChars.indexOf(aChar) != -1) {
						outBuffer.append('\\');
					}
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Convert a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 */
	private static char toHex(final int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	private static final String specialSaveChars = "=: \t\r\n\f#!";

	/** A table of hex digits */
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
