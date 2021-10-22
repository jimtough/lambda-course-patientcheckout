package org.jimtough.aws.lambda.s3sns;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientCheckoutLambda {

	private static final String PATIENT_CHECKOUT_TOPIC = System.getenv("PATIENT_CHECKOUT_TOPIC");
	private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public void handler(S3Event event, Context context) {
		//final LambdaLogger logger = context.getLogger();
		Logger logger = LoggerFactory.getLogger(PatientCheckoutLambda.class);

		event.getRecords().forEach(record->{
			try (
				S3ObjectInputStream inputStream = s3
						.getObject(record.getS3().getBucket().getName(), record.getS3().getObject().getKey())
						.getObjectContent()
			) {
				logger.info("Reading data from S3");
				List<PatientCheckoutEvent> patientCheckoutEvents =
						Arrays.asList(objectMapper.readValue(inputStream, PatientCheckoutEvent[].class));
				logger.info(patientCheckoutEvents.toString());
				logger.info("Publishing message to SNS");
				publishMessageToSNS(patientCheckoutEvents);
			} catch (IOException e) {
				logger.error("exception caught", e);
			}
		});
	}

	private void publishMessageToSNS(final List<PatientCheckoutEvent> patientCheckoutEvents) {
		patientCheckoutEvents.forEach(checkoutEvent->{
			try {
				sns.publish(
					PATIENT_CHECKOUT_TOPIC,
					objectMapper.writeValueAsString(checkoutEvent));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}

}
