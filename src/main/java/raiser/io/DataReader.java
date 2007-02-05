/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.io;

import java.io.IOException;

/**
 * @author: Costin Emilian GRIGORE
 */
public interface DataReader
{
    public abstract void close() throws IOException;
    public abstract Integer readInteger() throws IOException;
    public abstract String readString() throws IOException;
    public abstract char readCharacter(String accepted) throws IOException;
}