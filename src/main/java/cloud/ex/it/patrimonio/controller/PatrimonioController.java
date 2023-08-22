package cloud.ex.it.patrimonio.controller;

import cloud.ex.it.patrimonio.model.Patrimonio;
import cloud.ex.it.patrimonio.repository.PatrimonioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/api")
public class PatrimonioController {
    @Autowired
    private PatrimonioRepository patrimonioRepository;

    Logger log = LoggerFactory.getLogger(PatrimonioController.class);

    @GetMapping("/patrimoni")
    public ResponseEntity<List<Patrimonio>> getAllPatrimoni() {
        log.info("GET /patrimoni");
        List<Patrimonio> patrimoni = patrimonioRepository.findAll();

        if (patrimoni.isEmpty()) {
            // Nessun contenuto da ritornare
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(patrimoni, HttpStatus.OK);
    }

    @GetMapping("/patrimonio/{id}")
    public ResponseEntity<Patrimonio> getPatrimonioById(@PathVariable("id") long id) {
        log.info("Request GET /patrimonio/" + id);
        Patrimonio patrimonio = patrimonioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patrimonio Not Found With Id = " + id));
        return new ResponseEntity<>(patrimonio, HttpStatus.OK);
    }

    @PostMapping(path = "/patrimonio")
    public ResponseEntity<Patrimonio> addPatrimonio(@RequestBody Patrimonio patrimonio) {
        log.info("Request POST /patrimonio/");

        String nome = validaNome(patrimonio.getNome());
        Long valore = validaValore(patrimonio.getValore());
        Integer annoCreazione = validaAnnoCreazione(patrimonio.getAnnoCreazione());

        Patrimonio patrimonioCreated = patrimonioRepository.save(new Patrimonio(nome, valore, annoCreazione));

        return new ResponseEntity<>(patrimonioCreated, HttpStatus.CREATED);
    }

    @PutMapping("/patrimonio/{id}")
    public ResponseEntity<Patrimonio> updatePatrimonio(@PathVariable("id") Long id, @RequestBody Patrimonio patrimonioDaAggiornare) {
        log.info("Request PUT /patrimonio/" + id);
        Patrimonio patrimonio = patrimonioRepository.findById(id)
                .orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrimonio non trovato con id " + id));

        String nome = validaNome(patrimonioDaAggiornare.getNome());
        Long valore = validaValore(patrimonioDaAggiornare.getValore());
        Integer annoCreazione = validaAnnoCreazione(patrimonioDaAggiornare.getAnnoCreazione());

        // Aggiorna le proprietà del Patrimonio con i valori del nuovo Patrimonio ricevuto
        patrimonio.setNome(nome);
        patrimonio.setValore(valore);
        patrimonio.setAnnoCreazione(annoCreazione);

        // Salva il Patrimonio aggiornato nel repository
        return new ResponseEntity<>(patrimonioRepository.save(patrimonio),HttpStatus.OK);
    }

    @DeleteMapping(path="/patrimonio/{id}")
    public ResponseEntity<HttpStatus> deletePatrimonioById(@PathVariable("id") Long id) {
        log.info("Request DELETE /patrimonio/" + id);

        try {
            patrimonioRepository.deleteById(id);

        } catch (IllegalArgumentException e) {
            log.error("Errore durante la cancellazione del patrimonio con ID: " + id, e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/patrimoni")
    public ResponseEntity<HttpStatus> deleteAllPatrimoni() {
        log.info("Request DELETE /patrimoni/");
        patrimonioRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String validaNome(String nome) {
        return Optional.ofNullable(nome).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome non trovato"));
    }

    private Long validaValore(Long valore) {
        return Optional.ofNullable(valore).orElse(0L);
    }

    private Integer validaAnnoCreazione(Integer annoCreazione) {
        return Optional.ofNullable(annoCreazione).orElse(0);
    }
}
