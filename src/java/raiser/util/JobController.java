/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.util;

public interface JobController
{
    boolean canContinueRunning();
    void completed();
}
