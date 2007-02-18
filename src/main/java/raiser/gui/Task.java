/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: $
 *     $Author: $
 *       $Date: $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.gui;

/**
 * @author raiser
 */
public class Task {
	/**
	 * @param completed
	 * @param description
	 * @param owner
	 * @param percenteComplete
	 */
	public Task(boolean completed, String description, String owner,
			int percenteComplete) {
		super();
		this.completed = completed;
		this.description = description;
		this.owner = owner;
		this.percenteComplete = percenteComplete;
	}

	private boolean completed;

	private String description;

	private String owner;

	private int percenteComplete;

	private String audit;

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getPercenteComplete() {
		return percenteComplete;
	}

	public void setPercenteComplete(int percenteComplete) {
		this.percenteComplete = percenteComplete;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public String getAuditDerivated() {
		return audit + "/" + audit;
	}
}
