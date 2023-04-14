package no.nav.foreldrepenger.mottak.innsyn;


import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class InnsynConfigTest {


    @Test
    void verifiserKorrektLinkVedServiceDiscovery() {
        var baseUri = URI.create("http://fpinfo/fpinfo");
        var innsynConfig = new InnsynConfig(null, true, baseUri);
        var link = innsynConfig.createLink("/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259");
        assertThat(link).isEqualTo(URI.create("http://fpinfo/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259"));
    }

    @Test
    void verifiserKorrektLinkNårDetIkkeErServiceDiscovery() {
        var baseUri = URI.create("http://fpinfo:8080/fpinfo");
        var innsynConfig = new InnsynConfig(null, true, baseUri);
        var link = innsynConfig.createLink("/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259");
        assertThat(link).isEqualTo(URI.create("http://fpinfo:8080/fpinfo/api/dokumentforsendelse/behandling?behandlingId=1733259"));
    }
}
