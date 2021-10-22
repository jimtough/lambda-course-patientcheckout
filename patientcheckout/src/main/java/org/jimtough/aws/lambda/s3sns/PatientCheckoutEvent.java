package org.jimtough.aws.lambda.s3sns;

public class PatientCheckoutEvent {

	public String firstName;
	public String middleName;
	public String lastName;
	public String ssn;

	public PatientCheckoutEvent() {}

	public PatientCheckoutEvent(
			final String firstName,
			final String middleName,
			final String lastName,
			final String ssn) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.ssn = ssn;
	}

	@Override public String toString() {
		return "PatientCheckoutEvent{" +
				"firstName='" + firstName + '\'' +
				", middleName='" + middleName + '\'' +
				", lastName='" + lastName + '\'' +
				", ssn='" + ssn + '\'' +
				'}';
	}

}
