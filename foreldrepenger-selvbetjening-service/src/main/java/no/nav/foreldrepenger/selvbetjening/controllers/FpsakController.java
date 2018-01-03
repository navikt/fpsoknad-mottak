package no.nav.foreldrepenger.selvbetjening.controllers;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.FpsakClient;

@RestController
class FpsakController {

   private final FpsakClient fpsakClient;

   @Inject
   public FpsakController(FpsakClient fpsakClient){
      this.fpsakClient = fpsakClient;
   }

   @RequestMapping(method = {RequestMethod.GET}, value = "/fpsak")
   public ResponseEntity<?> incomeForAktor(@RequestParam("aktor") String aktorId) {
      try {
         return ResponseEntity.ok(fpsakClient.hasApplications(aktorId));
      } catch (Exception ex) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
      }

   }
}
