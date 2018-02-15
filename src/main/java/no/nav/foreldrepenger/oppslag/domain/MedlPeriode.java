package no.nav.foreldrepenger.oppslag.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class MedlPeriode extends TidsAvgrensetBrukerInfo {

    private String status;
    private String type;
    private String grunnlagstype;
    private String land;

   @JsonCreator
   public MedlPeriode(
         @JsonProperty("from") LocalDate from,
         @JsonProperty("to") Optional<LocalDate> to,
         @JsonProperty("status") String status,
         @JsonProperty("type") String type,
         @JsonProperty("grunnlagstype") String grunnlagstype,
         @JsonProperty("land") String land) {
      super(from, to);
      this.status = status;
      this.type = type;
      this.grunnlagstype = grunnlagstype;
      this.land = land;
   }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getGrunnlagstype() {
        return grunnlagstype;
    }

    public String getLand() {
        return land;
    }

    @Override
    public String toString() {
        return "MedlPeriode{" +
                "status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", grunnlagstype='" + grunnlagstype + '\'' +
                ", land='" + land + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MedlPeriode that = (MedlPeriode) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(type, that.type) &&
                Objects.equals(grunnlagstype, that.grunnlagstype) &&
                Objects.equals(land, that.land);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status, type, grunnlagstype, land);
    }
}
