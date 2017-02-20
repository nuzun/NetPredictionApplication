package uk.ac.bbk.cryst.netprediction.model;

public class PatientData {

	private String variant;
	private int position;
	private String severity;
	private boolean inhibitorFormation;
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
	public boolean isInhibitorFormation() {
		return inhibitorFormation;
	}
	public void setInhibitorFormation(boolean inhibitorFormation) {
		this.inhibitorFormation = inhibitorFormation;
	}
	public PatientData(String variant, int position, String severity, boolean inhibitorFormation) {
		super();
		this.variant = variant;
		this.position = position;
		this.severity = severity;
		this.inhibitorFormation = inhibitorFormation;
	}
	@Override
	public String toString() {
		return "PatientData [variant=" + variant + ", position=" + position + ", severity=" + severity
				+ ", inhibitorFormation=" + inhibitorFormation + "]";
	}
	
	
}
