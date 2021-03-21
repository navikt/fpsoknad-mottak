package no.nav.foreldrepenger.mottak.tokendings;

import com.google.common.base.Joiner;

import no.nav.foreldrepenger.boot.conditionals.Cluster;

record TargetApp(Cluster cluster, String namespace, String navn) {

    String asString() {

        return Joiner.on(":").join(cluster.clusterName(), namespace, navn);
    }

}
