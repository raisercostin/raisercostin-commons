/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package org.raisercostin.util;

public interface JobController {
	boolean canContinueRunning();

	void completed();
}
