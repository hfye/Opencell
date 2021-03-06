package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class Sequence {
	
	@Column(name = "PREFIX_EL", length = 2000)
	@Size(max = 2000)
	private String prefixEL = "";
	
	@Column(name = "SEQUENCE_SIZE")
	private Integer sequenceSize= 9;
	
    @Column(name = "CURRENT_INVOICE_NB")
    private Long currentInvoiceNb = 0L;

	public Sequence() {
	}

	/**
	 * @return the prefixEL
	 */
	public String getPrefixEL() {
		
		return prefixEL==null?"":prefixEL;
	}

	/**
	 * @param prefixEL the prefixEL to set
	 */
	public void setPrefixEL(String prefixEL) {
		this.prefixEL = prefixEL;
	}

	/**
	 * @return the sequenceSize
	 */
	public Integer getSequenceSize() {
		return sequenceSize;
	}

	/**
	 * @param sequenceSize the sequenceSize to set
	 */
	public void setSequenceSize(Integer sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	/**
	 * @return the currentInvoiceNb
	 */
	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	/**
	 * @param currentInvoiceNb the currentInvoiceNb to set
	 */
	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}


	@Override
	public String toString() {
		return "Sequence [prefixEL=" + prefixEL + ", sequenceSize=" + sequenceSize + ", currentInvoiceNb=" + currentInvoiceNb + "]";
	}
}