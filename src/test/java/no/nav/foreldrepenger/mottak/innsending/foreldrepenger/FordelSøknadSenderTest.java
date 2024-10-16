package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;

@ExtendWith(MockitoExtension.class)
class FordelSøknadSenderTest {
    @Mock
    private FordelConnection connection;

    private FordelSøknadSender fordelSøknadSender;

    @BeforeEach
    void before() {
        fordelSøknadSender = new FordelSøknadSender(connection,
            null);
    }

    @Test
    void returnerKvitteringUtenSaksnummerVedUventetFpFordelResponseException() {
        when(connection.send(any())).thenThrow(new UventetPollingStatusFpFordelException("Feil"));
        var konvolutt = new Konvolutt(SøknadEgenskap.INITIELL_FORELDREPENGER, null, null, null, null);

        var kvittering = fordelSøknadSender.send(konvolutt);

        assertThat(kvittering).isNotNull();
        assertThat(kvittering.mottattDato()).isNotNull();
        assertThat(kvittering.saksNr()).isNull();
    }

    @Test
    void feilHardtVedInnsendingFeiletFpFordelException() {
        when(connection.send(any())).thenThrow(new InnsendingFeiletFpFordelException("Kritisk feil!"));
        var konvolutt = new Konvolutt(SøknadEgenskap.INITIELL_FORELDREPENGER, null, null, null, null);

        assertThatThrownBy(() -> fordelSøknadSender.send(konvolutt))
            .isInstanceOf(InnsendingFeiletFpFordelException.class);
    }

}
