package de.legendlime.departmentService.config.tomcat;

import java.security.KeyStore;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.stereotype.Component;

import de.legendlime.departmentService.config.cert.KeyStoreBean;
import de.legendlime.departmentService.config.cert.TrustStoreBean;

/**
 * Spring bean containing SSL configuration with key store and trust store
 * This bean will be loaded into the containers SSL configuration each
 * time a certificate must be renewed
 * 
 * @author  Thomas Kautenburger
 * @version 1.0
 *
 */

@Component
@ConditionalOnProperty(prefix = "server.ssl", name = "enabled", havingValue = "true")
public class SslStoreProviderBean implements SslStoreProvider {
	
	private KeyStore keyStore, trustStore;
	
	public SslStoreProviderBean(KeyStoreBean keyStoreBean, TrustStoreBean trustStoreBean) {
		this.keyStore = keyStoreBean.getKeyStore();
		this.trustStore = trustStoreBean.getTrustStore();
	}

	@Override
	public KeyStore getKeyStore() throws Exception {
		return keyStore;
	}

	@Override
	public KeyStore getTrustStore() throws Exception {
		return trustStore;
	}	
	
	public void renew(KeyStoreBean keyStoreBean, TrustStoreBean trustStoreBean) {
		this.keyStore = keyStoreBean.getKeyStore();
		this.trustStore = trustStoreBean.getTrustStore();
	}
}
