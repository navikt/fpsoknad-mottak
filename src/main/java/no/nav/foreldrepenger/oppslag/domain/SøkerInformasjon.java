package no.nav.foreldrepenger.oppslag.domain;

import java.util.List;

public class SøkerInformasjon {

	private final Person person;
	private final List<LookupResult<Income>> income;
   private final List<LookupResult<Benefit>> benefits;

	public SøkerInformasjon(
	      Person person,
         List<LookupResult<Income>> income,
         List<LookupResult<Benefit>> benefits) {
		this.person = person;
		this.income = income;
		this.benefits = benefits;
	}

	public Person getPerson() {
		return person;
	}

	public List<LookupResult<Income>> getIncome() {
		return income;
	}

   public List<LookupResult<Benefit>> getBenefits() {
      return benefits;
   }

}
