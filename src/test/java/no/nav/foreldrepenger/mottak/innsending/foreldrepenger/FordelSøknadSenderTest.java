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

import no.nav.foreldrepenger.common.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.innsending.pdf.InfoskrivPdfEkstraktor;

@ExtendWith(MockitoExtension.class)
class FordelSøknadSenderTest {

    @Mock
    private TokenUtil tokenHelper;
    @Mock
    private InnsendingHendelseProdusent hendelser;
    @Mock
    private InfoskrivPdfEkstraktor ekstraktor;
    @Mock
    private FordelConnection connection;

    private FordelSøknadSender fordelSøknadSender;

    @BeforeEach
    void before() {
        fordelSøknadSender = new FordelSøknadSender(connection,
            null, ekstraktor, hendelser, tokenHelper);
    }

    @Test
    void returnerKvitteringUtenSaksnummerVedUventetFpFordelResponseException() {
        when(connection.send(any())).thenThrow(new UventetPollingStatusFpFordelException("Feil"));
        var konvolutt = new Konvolutt(SøknadEgenskap.INITIELL_FORELDREPENGER, null, null, null, null);

        var kvittering = fordelSøknadSender.send(konvolutt);

        assertThat(kvittering).isNotNull();
        assertThat(kvittering.getMottattDato()).isNotNull();
        assertThat(kvittering.getSaksNr()).isNull();
    }

    @Test
    void feilHardtVedInnsendingFeiletFpFordelException() {
        when(connection.send(any())).thenThrow(new InnsendingFeiletFpFordelException("Kritisk feil!"));
        var konvolutt = new Konvolutt(SøknadEgenskap.INITIELL_FORELDREPENGER, null, null, null, null);

        assertThatThrownBy(() -> fordelSøknadSender.send(konvolutt))
            .isInstanceOf(InnsendingFeiletFpFordelException.class);
    }

}
