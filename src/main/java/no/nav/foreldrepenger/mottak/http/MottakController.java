package no.nav.foreldrepenger.mottak.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Soknad;
@RestController
class MottakController {

	@RequestMapping(method = { RequestMethod.POST }, value = "/mottak")
	public ResponseEntity<?> motta(@RequestBody Soknad soknad) {
			return ResponseEntity.status(HttpStatus.OK).body("Takk");

	}
}
