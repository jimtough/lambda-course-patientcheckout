package org.jimtough.aws.lambda.s3sns;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientCheckoutLambda {

	private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public void handler(S3Event event) {
		event.getRecords().forEach(record->{
			S3ObjectInputStream inputStream = s3
					.getObject(record.getS3().getBucket().getName(), record.getS3().getObject().getKey())
					.getObjectContent();
			try {
				List<PatientCheckoutEvent> patientCheckoutEvents =
						Arrays.asList(objectMapper.readValue(inputStream, PatientCheckoutEvent[].class));
				// NOTE: System.out output will be logged in Cloudwatch Logs
				System.out.println(patientCheckoutEvents);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
