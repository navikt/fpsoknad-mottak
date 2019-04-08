package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@JsonInclude(NON_NULL)
public class Person {

    public Fødselsnummer fnr;
    public String fornavn;
    public String mellomnavn;
    public String etternavn;
    public String kjønn;
    public LocalDate fødselsdato;
    public String målform;
    public CountryCode land;
    public Boolean ikkeNordiskEøsLand;
    public Bankkonto bankkonto;
    public AktørId aktørId;

    @Override
    public int hashCode() {
        return Objects.hashCode(aktørId, bankkonto, fornavn, mellomnavn, etternavn, fnr, fødselsdato,
                ikkeNordiskEøsLand, kjønn, land, målform);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Person other = (Person) obj;
        if (aktørId == null) {
            if (other.aktørId != null) {
                return false;
            }
        }
        else if (!aktørId.equals(other.aktørId)) {
            return false;
        }
        if (bankkonto == null) {
            if (other.bankkonto != null) {
                return false;
            }
        }
        else if (!bankkonto.equals(other.bankkonto)) {
            return false;
        }
        if (etternavn == null) {
            if (other.etternavn != null) {
                return false;
            }
        }
        else if (!etternavn.equals(other.etternavn)) {
            return false;
        }
        if (fnr == null) {
            if (other.fnr != null) {
                return false;
            }
        }
        else if (!fnr.equals(other.fnr)) {
            return false;
        }
        if (fornavn == null) {
            if (other.fornavn != null) {
                return false;
            }
        }
        else if (!fornavn.equals(other.fornavn)) {
            return false;
        }
        if (fødselsdato == null) {
            if (other.fødselsdato != null) {
                return false;
            }
        }
        else if (!fødselsdato.equals(other.fødselsdato)) {
            return false;
        }
        if (ikkeNordiskEøsLand == null) {
            if (other.ikkeNordiskEøsLand != null) {
                return false;
            }
        }
        else if (!ikkeNordiskEøsLand.equals(other.ikkeNordiskEøsLand)) {
            return false;
        }
        if (kjønn == null) {
            if (other.kjønn != null) {
                return false;
            }
        }
        else if (!kjønn.equals(other.kjønn)) {
            return false;
        }
        if (land != other.land) {
            return false;
        }
        if (mellomnavn == null) {
            if (other.mellomnavn != null) {
                return false;
            }
        }
        else if (!mellomnavn.equals(other.mellomnavn)) {
            return false;
        }
        if (målform == null) {
            if (other.målform != null) {
                return false;
            }
        }
        else if (!målform.equals(other.målform)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnr + ", fornavn=" + fornavn + ", mellomnavn=" + mellomnavn
                + ", etternavn=" + etternavn
                + ", kjønn=" + kjønn + ", fødselsdato=" + fødselsdato + ", målform=" + målform + ", land=" + land
                + ", ikkeNordiskEøsLand=" + ikkeNordiskEøsLand + ", bankkonto=" + bankkonto + ", aktørId=" + aktørId
                + "]";
    }
}