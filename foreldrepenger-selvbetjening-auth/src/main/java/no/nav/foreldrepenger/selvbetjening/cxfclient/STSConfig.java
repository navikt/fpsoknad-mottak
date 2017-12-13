package no.nav.foreldrepenger.selvbetjening.cxfclient;

public class STSConfig {

	private final String stsUrl;
	private final String systemUserName;
	private final String systemUserPassword;

	public String getStsUrl() {
		return stsUrl;
	}

	public String getSystemUserName() {
		return systemUserName;
	}

	public String getSystemUserPassword() {
		return systemUserPassword;
	}

	public STSConfig(String stsUrl, String systemUserName, String systemUserPassword) {
		this.stsUrl = stsUrl;
		this.systemUserName = systemUserName;
		this.systemUserPassword = systemUserPassword;
				
	}

}
