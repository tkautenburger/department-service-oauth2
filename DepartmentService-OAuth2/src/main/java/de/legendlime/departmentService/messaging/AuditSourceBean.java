package de.legendlime.departmentService.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class AuditSourceBean {
	
	private Source source;
	
	@Autowired
	public AuditSourceBean(Source source) {
		this.source = source;
	}
	
	public void publishAuditMessage(String message) {
		source.output().send(MessageBuilder.withPayload(message).build());
	}

}
