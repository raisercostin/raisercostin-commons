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
	public Task(final boolean completed, final String description,
			final String owner, final int percenteComplete) {
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

	public void setCompleted(final boolean completed) {
		this.completed = completed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(final String owner) {
		this.owner = owner;
	}

	public int getPercenteComplete() {
		return percenteComplete;
	}

	public void setPercenteComplete(final int percenteComplete) {
		this.percenteComplete = percenteComplete;
	}

	public void setAudit(final String audit) {
		this.audit = audit;
	}

	public String getAuditDerivated() {
		return audit + "/" + audit;
	}
}
