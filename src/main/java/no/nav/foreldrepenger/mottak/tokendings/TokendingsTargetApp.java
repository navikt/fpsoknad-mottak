package no.nav.foreldrepenger.mottak.tokendings;

import com.google.common.base.Joiner;

import no.nav.foreldrepenger.boot.conditionals.Cluster;

record TokendingsTargetApp(Cluster targetCluster, String targetNamespace, String targetNavn) {

    TokendingsTargetApp(String targetNamespace, String targetNavn) {
        this(Cluster.currentCluster(), targetNamespace, targetNavn);
    }

    TokendingsTargetApp(String targetNavn) {
        this(Cluster.currentCluster(), Cluster.currentNamespace(), targetNavn);
    }

    String asAudience() {
        return Joiner.on(":").join(targetCluster.clusterName(), targetNamespace, targetNavn);
    }
}
