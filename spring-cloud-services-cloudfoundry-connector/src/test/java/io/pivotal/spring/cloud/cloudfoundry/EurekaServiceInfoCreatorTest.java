package io.pivotal.spring.cloud.cloudfoundry;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.cloudfoundry.AbstractCloudFoundryConnectorTest;
import org.springframework.cloud.service.ServiceInfo;

import io.pivotal.spring.cloud.service.common.EurekaServiceInfo;

import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Connector tests for Eureka services
 *
 * @author Chris Schaefer
 */
public class EurekaServiceInfoCreatorTest extends AbstractCloudFoundryConnectorTest {
	private static final String EUREKA_SERVICE_TAG_NAME = "myEurekaInstance";
	private static final String VCAP_SERVICES_ENV_KEY = "VCAP_SERVICES";
	private static final String PAYLOAD_FILE_NAME = "test-eureka-info.json";
	private static final String PAYLOAD_TEMPLATE_SERVICE_NAME = "$serviceName";
	private static final String PAYLOAD_TEMPLATE_HOSTNAME = "$hostname";
	private static final String PAYLOAD_TEMPLATE_PORT = "$port";
	private static final String PAYLOAD_TEMPLATE_USER = "$user";
	private static final String PAYLOAD_TEMPLATE_PASS = "$pass";

	@Test
	public void eurekaServiceCreationWithTags() {
		when(mockEnvironment.getEnvValue(VCAP_SERVICES_ENV_KEY))
				.thenReturn(getServicesPayload(getEurekaServicePayload(EUREKA_SERVICE_TAG_NAME, hostname, port, username, password)));

		List<ServiceInfo> serviceInfos = testCloudConnector.getServiceInfos();
		assertServiceFoundOfType(serviceInfos, EUREKA_SERVICE_TAG_NAME, EurekaServiceInfo.class);
		for (ServiceInfo serviceInfo : serviceInfos) {
			if (serviceInfo instanceof EurekaServiceInfo) {
				EurekaServiceInfo eurekaServiceInfo = (EurekaServiceInfo) serviceInfo;
				Assert.assertEquals("theClientId", eurekaServiceInfo.getClientId());
				Assert.assertEquals("theClientSecret", eurekaServiceInfo.getClientSecret());
				Assert.assertEquals("https://p-spring-cloud-services.uaa.my-cf.com/oauth/token", eurekaServiceInfo.getAccessTokenUri());
				return;
			}
		}
		Assert.fail("A EurekaServiceInfo should exist");
	}

	private String getEurekaServicePayload(String serviceName, String hostname, int port, String user, String password) {
		String payload = readTestDataFile(PAYLOAD_FILE_NAME);
		payload = payload.replace(PAYLOAD_TEMPLATE_SERVICE_NAME, serviceName);
		payload = payload.replace(PAYLOAD_TEMPLATE_HOSTNAME, hostname);
		payload = payload.replace(PAYLOAD_TEMPLATE_PORT, Integer.toString(port));
		payload = payload.replace(PAYLOAD_TEMPLATE_USER, user);
		payload = payload.replace(PAYLOAD_TEMPLATE_PASS, password);

		return payload;
	}
}
