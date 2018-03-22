package no.nav.foreldrepenger.mottak.config;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

import com.ibm.msg.client.jms.DetailedJMSException;

public class NotDevModeFailureAnalyzer extends AbstractFailureAnalyzer<UnsatisfiedDependencyException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, UnsatisfiedDependencyException cause) {
        Throwable most = cause.getMostSpecificCause();
        if (most instanceof DetailedJMSException) {
            return new FailureAnalysis(
                    "Spring profile 'dev' must be active when running locally. This sets dummy values for the dokmot configuration so that your application starts. It will also make your logging output look nicer (no JSON)",
                    "Set -Dspring.profiles.active=dev in your run configuration to activate", most);
        }
        return new FailureAnalysis(rootFailure.getMessage(), "Fix your setup ", cause);
    }

}
