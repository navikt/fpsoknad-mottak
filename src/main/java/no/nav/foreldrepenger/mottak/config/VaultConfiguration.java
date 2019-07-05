package no.nav.foreldrepenger.mottak.config;

import static org.springframework.vault.core.lease.domain.RequestedSecret.renewable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.LeaseEndpoints;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;

@Configuration
@ConditionalOnProperty(value = "spring.cloud.vault.enabled")
public class VaultConfiguration implements InitializingBean {
    private final SecretLeaseContainer container;

    public VaultConfiguration(SecretLeaseContainer container) {
        this.container = container;
    }

    @Override
    public void afterPropertiesSet() {
        container.setLeaseEndpoints(LeaseEndpoints.SysLeases);
        RequestedSecret secret = renewable("kv/preprod/fss/fpsoknad-mottak/default");
        container.addRequestedSecret(secret);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [container=" + container + "]";
    }
}