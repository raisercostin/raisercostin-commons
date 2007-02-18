/*
 ******************************************************************************
 *    $Logfile: $
 *   $Revision: 1.1 $
 *     $Author: raisercostin $
 *       $Date: 2004/06/10 14:18:37 $
 * $NoKeywords: $
 *****************************************************************************/
package raiser.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author raiser
 */
public class AboutPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7327986676590769464L;

	/**
	 * Makes an about panel like this:
	 * 
	 * <pre>
	 * 	 Eclipse Platform
	 * 	 
	 * 	 Version: 3.0.0
	 * 	 Build id: 200403261517
	 * 	 
	 * 	 (c) Copyright IBM Corp. and others 2000, 2003.  All rights reserved.
	 * 	 Visit http://www.eclipse.org/platform
	 * 	 
	 * 	 This product includes software developed by the
	 * 	 Apache Software Foundation http://www.apache.org/
	 * 	
	 * </pre>
	 */
	String productName;

	String productVersion;

	String productBuild;

	String productDate;

	String productWebsite;

	String copyright;

	String copyrightNotice;

	String licenceType;

	private javax.swing.JPanel jPanel1 = null;

	/**
	 * This is the default constructor
	 */
	public AboutPanel(String productName, String productVersion,
			String productBuild, String productDate, String productWebsite,
			String copyright, String copyrightNotice, String licenceType) {
		super();
		this.productName = productName;
		this.productVersion = productVersion;
		this.productBuild = productBuild;
		this.productDate = productDate;
		this.productWebsite = productWebsite;
		this.copyright = copyright;
		this.copyrightNotice = copyrightNotice;
		this.licenceType = licenceType;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setLayout(new java.awt.BorderLayout()); // Generated
		this.setSize(300, 200);
		this.add(getJPanel(), java.awt.BorderLayout.SOUTH); // Generated
		this.add(getJPanel1(), java.awt.BorderLayout.CENTER); // Generated
		this.add(getPictureLabel(), java.awt.BorderLayout.WEST); // Generated
	}

	/**
	 * 
	 * This method initializes jPanel1
	 * 
	 * 
	 * 
	 * @return javax.swing.JPanel
	 * 
	 */
	private javax.swing.JPanel getJPanel1() {
		if (jPanel1 == null) {
			try {
				/*
				 * Eclipse Platform
				 * 
				 * Version: 3.0.0 Build id: 200403261517
				 * 
				 * (c) Copyright IBM Corp. and others 2000, 2003. All rights
				 * reserved. Visit http://www.eclipse.org/platform
				 * 
				 * This product includes software developed by the Apache
				 * Software Foundation http://www.apache.org/
				 */

				jPanel1 = new javax.swing.JPanel();
				jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1,
						javax.swing.BoxLayout.Y_AXIS)); // Generated
				jPanel1.add(getProductNameLabel(), null); // Generated
				jPanel1.add(getProductVersionLabel(), null); // Generated
				jPanel1.add(getProductBuildLabel(), null); // Generated
				jPanel1.add(getProductDateLabel(), null); // Generated
			} catch (java.lang.Throwable e) {
				// TODO: Something
			}
		}
		return jPanel1;
	}

	JLabel productNameLabel;

	JLabel productVersionLabel;

	JLabel productBuildLabel;

	JLabel productDateLabel;

	JLabel productWebsiteLabel;

	JLabel copyrightLabel;

	JLabel copyrightNoticeLabel;

	JLabel licenceTypeLabel;

	private javax.swing.JLabel pictureLabel = null;

	private javax.swing.JPanel jPanel = null;

	private JLabel getProductNameLabel() {
		if (productNameLabel == null) {
			productNameLabel = new JLabel();
			productNameLabel.setText(productName);
		}
		return productNameLabel;
	}

	private JLabel getProductVersionLabel() {
		if (productVersionLabel == null) {
			productVersionLabel = new JLabel();
			productVersionLabel.setText(productVersion);
		}
		return productVersionLabel;
	}

	private JLabel getProductBuildLabel() {
		if (productBuildLabel == null) {
			productBuildLabel = new JLabel();
			productBuildLabel.setText(productBuild);
		}
		return productBuildLabel;
	}

	private JLabel getProductDateLabel() {
		if (productDateLabel == null) {
			productDateLabel = new JLabel();
			productDateLabel.setText(productDate);
		}
		return productDateLabel;
	}

	private JLabel getProductWebsiteLabel() {
		if (productWebsiteLabel == null) {
			productWebsiteLabel = new JLabel();
			productWebsiteLabel.setText(productWebsite);
		}
		return productWebsiteLabel;
	}

	private JLabel getCopyrightLabel() {
		if (copyrightLabel == null) {
			copyrightLabel = new JLabel();
			copyrightLabel.setText(copyright);
		}
		return copyrightLabel;
	}

	private JLabel getCopyrightNoticeLabel() {
		if (copyrightNoticeLabel == null) {
			copyrightNoticeLabel = new JLabel();
			copyrightNoticeLabel.setText(copyrightNotice);
		}
		return copyrightNoticeLabel;
	}

	private JLabel getLicenceTypeLabel() {
		if (licenceTypeLabel == null) {
			licenceTypeLabel = new JLabel();
			licenceTypeLabel.setText(licenceType);
		}
		return licenceTypeLabel;
	}

	/**
	 * 
	 * This method initializes pictureLabel
	 * 
	 * 
	 * 
	 * @return javax.swing.JLabel
	 * 
	 */
	private javax.swing.JLabel getPictureLabel() {
		if (pictureLabel == null) {
			try {
				pictureLabel = new javax.swing.JLabel();
				pictureLabel.setText(""); // Generated
				pictureLabel.setIcon(UIManager
						.getIcon("OptionPane.warningIcon"));
			} catch (java.lang.Throwable e) {
				// TODO: Something
			}
		}
		return pictureLabel;
	}

	/**
	 * 
	 * This method initializes jPanel
	 * 
	 * 
	 * 
	 * @return javax.swing.JPanel
	 * 
	 */
	private javax.swing.JPanel getJPanel() {
		if (jPanel == null) {
			try {
				jPanel = new javax.swing.JPanel();
				jPanel.setLayout(new javax.swing.BoxLayout(jPanel,
						javax.swing.BoxLayout.Y_AXIS)); // Generated
				jPanel.add(getProductWebsiteLabel(), null); // Generated
				jPanel.add(getCopyrightNoticeLabel(), null); // Generated
				jPanel.add(getCopyrightLabel(), null); // Generated
				jPanel.add(getLicenceTypeLabel(), null); // Generated
			} catch (java.lang.Throwable e) {
				// TODO: Something
			}
		}
		return jPanel;
	}

}