/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

/**
 * @author: Costin Emilian GRIGORE
 */
public class Regexp {
	private Regexp() {
	}

	/**
	 * Escapes a char sequence for using as a pattern text into a regular
	 * expression. For example matching for point or dollar sign.
	 * 
	 * @param data
	 * @return
	 */
	public static String escape(String data) {
		return data.replaceAll("(\\p{Punct})", "\\\\$1");
		// result = result.replaceAll("[?*+$^.\\\\,{}|()<>!=:-&\\[\\]]",
		// "\\$1");
		// result = result.replaceAll("\\p{Punct}","\\$1");
		// result = result.replaceAll("(/)", "\\/");
		// return result;
	}

	public static String escapeAll(String data) {
		return data.replaceAll("(.)", "[$1]");
	}

	public static String getEscapeVariable(String command) {
		command = command.replaceAll("[$][{]dollar[}]", "${dollar}{dollar}");
		return command.replaceAll("[$][$][{]", "\\${dollar}\\${dollar}{");
	}

	public static String getEscapeVariableBack(String command) {
		command = command.replaceAll("[$][{]dollar[}]", "\\$");
		command = command.replaceAll("[$][$][{]", "\\${");
		return command;
	}

	public static String escapeVariable(String name) {
		return Regexp.escape("${" + name + "}");
	}

	public static String escapeValue(String value) {
		return value.replaceAll("[$]", "\\\\\\$");
	}

	public static String escapeVariable(String command, String name,
			String value) {
		return command.replaceAll(Regexp.escapeVariable(name), Regexp
				.escapeValue(value));
	}
}
