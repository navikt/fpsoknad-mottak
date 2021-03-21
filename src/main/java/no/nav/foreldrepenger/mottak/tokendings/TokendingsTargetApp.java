package no.nav.foreldrepenger.mottak.tokendings;

import com.google.common.base.Joiner;

import no.nav.foreldrepenger.boot.conditionals.Cluster;

public record TokendingsTargetApp(Cluster targetCluster, String targetNamespace, String targetNavn) {

    public TokendingsTargetApp(String targetNamespace, String targetNavn) {
        this(Cluster.currentCluster(), targetNamespace, targetNavn);
    }

    public TokendingsTargetApp(String targetNavn) {
        this(Cluster.currentCluster(), Cluster.currentNamespace(), targetNavn);
    }

    public static TokendingsTargetApp of(String targetNavn) {
        return new TokendingsTargetApp(targetNavn);
    }

    String asAudience() {
        return Joiner.on(":").join(targetCluster.clusterName(), targetNamespace, targetNavn);
    }
}
