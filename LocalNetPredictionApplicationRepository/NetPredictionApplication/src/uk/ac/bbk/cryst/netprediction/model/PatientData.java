package uk.ac.bbk.cryst.netprediction.model;

import uk.ac.bbk.cryst.netprediction.common.InhibitorStatus;

public class PatientData {

	private String variant;
	private int position;
	private String severity;
	private InhibitorStatus inhibitorStatus;
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
	public InhibitorStatus getInhibitorStatus() {
		return inhibitorStatus;
	}
	public void setInhibitorStatus(InhibitorStatus inhibitorStatus) {
		this.inhibitorStatus = inhibitorStatus;
	}
	
	public PatientData(String variant, int position, String severity, InhibitorStatus inhibitorStatus) {
		super();
		this.variant = variant;
		this.position = position;
		this.severity = severity;
		this.inhibitorStatus = inhibitorStatus;
	}
	@Override
	public String toString() {
		return "PatientData [variant=" + variant + ", position=" + position + ", severity=" + severity
				+ ", inhibitorFormation=" + inhibitorStatus + "]";
	}
	
	
}
