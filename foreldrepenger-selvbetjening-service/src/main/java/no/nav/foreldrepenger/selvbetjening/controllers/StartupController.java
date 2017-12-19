package no.nav.foreldrepenger.selvbetjening.controllers;


import java.util.Optional;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.AktorIdKlient;
import no.nav.foreldrepenger.selvbetjening.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.domain.BrukerInformasjon;
import no.nav.foreldrepenger.selvbetjening.domain.Fodselsnummer;

@RestController
@Validated
@RequestMapping("/startup")
public class StartupController {	

   private final AktorIdKlient aktorClient;

   @Inject
   public  StartupController(AktorIdKlient aktorClient) {
      this.aktorClient = aktorClient;
   }

   @GetMapping(value = "/")
   public ResponseEntity<BrukerInformasjon> startup(@Valid @RequestParam(value="fnr", required=true) Fodselsnummer fnr) {
	  Optional<AktorId> aktorId = aktorClient.aktorIdForFnr(fnr);
	  if (aktorId.isPresent()) {
          return new ResponseEntity<BrukerInformasjon>(new BrukerInformasjon(aktorId.get(),fnr), HttpStatus.OK);
      }
	  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }
   
   @ExceptionHandler({ConstraintViolationException.class})
   public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
       return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
   }
  
   
   @Override
	public String toString() {
		return getClass().getSimpleName() + " [aktorClient=" + aktorClient + "]";
	}

}
