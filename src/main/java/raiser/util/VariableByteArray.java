/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

import java.util.*;
import java.util.List;

/**
 * An growing array with int values. 
 * Used for buffers.
 * @author: Costin Emilian GRIGORE
 */
public class VariableByteArray extends ArrayList implements List
{
    public void removeRange(int fromIndex, int toIndex)
    {
        super.removeRange(fromIndex, toIndex);
    }
    public void addAll(byte[] values)
    {
        for (int i = 0; i < values.length; i++)
        {
            add(new Byte(values[i]));
        }
    }
    public void copyTo(int off, int len, byte[] b)
    {
        for (int i = 0; i < len; i++)
        {
            b[off + i] = ((Number) get(i)).byteValue();
        }
    }
}
