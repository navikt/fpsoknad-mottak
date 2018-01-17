package no.nav.foreldrepenger.oppslag.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

public class LookupResult<T> {

   private String system;
   private LookupStatus status;
   private String remark;
   private List<T> data;

   public LookupResult(String system, LookupStatus status, List<T> data) {
      this(system, status, data, null);
   }

   public LookupResult(String system, LookupStatus status, List<T> data, String remark) {
      this.system = system;
      this.status = status;
      this.data = data != null ? data : new ArrayList<>();
      this.remark = remark;
   }

   public String getSystem() {
      return system;
   }

   public LookupStatus getStatus() {
      return status;
   }

   @JsonInclude(ALWAYS)
   public List<T> getData() {
      return data;
   }

   public String getRemark() {
      return remark;
   }

   @Override
   public String toString() {
      return "YtelseLookup{" +
         "system='" + system + '\'' +
         ", status=" + status +
         ", data=" + data +
         ", remark=" + remark +
         '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      LookupResult that = (LookupResult) o;
      return Objects.equals(system, that.system) &&
         status == that.status &&
         Objects.equals(data, that.data) &&
         Objects.equals(remark, that.remark);
   }

   @Override
   public int hashCode() {
      return Objects.hash(system, status, data, remark);
   }
}

