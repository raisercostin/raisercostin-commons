/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.ArrayList;
import java.util.List;

/**
 * An growing array with int values. Used for buffers.
 * 
 * @author: Costin Emilian GRIGORE
 */
public class VariableByteArray extends ArrayList<Number> implements
		List<Number> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7603888715774526663L;

	@Override
	public void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
	}

	public void addAll(byte[] values) {
		for (byte element : values) {
			add(new Byte(element));
		}
	}

	public void copyTo(int off, int len, byte[] b) {
		for (int i = 0; i < len; i++) {
			b[off + i] = ((Number) get(i)).byteValue();
		}
	}
}
