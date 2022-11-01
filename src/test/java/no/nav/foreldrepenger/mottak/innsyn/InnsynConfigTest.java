package no.nav.foreldrepenger.mottak.innsyn;


import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class InnsynConfigTest {


    @Test
    void verifiserKorrektLinkVedServiceDiscovery() {
        URI baseUri = URI.create("http://fpinfo/fpinfo");
        InnsynConfig innsynConfig = new InnsynConfig(null, true, baseUri);
        URI link = innsynConfig.createLink("/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259");
        assertThat(link).isEqualTo(URI.create("http://fpinfo/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259"));
    }

    @Test
    void verifiserKorrektLinkNårDetIkkeErServiceDiscovery() {
        URI baseUri = URI.create("http://fpinfo:8080/fpinfo");
        InnsynConfig innsynConfig = new InnsynConfig(null, true, baseUri);
        URI link = innsynConfig.createLink("/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259");
        assertThat(link).isEqualTo(URI.create("http://fpinfo:8080/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259"));
    }
}
