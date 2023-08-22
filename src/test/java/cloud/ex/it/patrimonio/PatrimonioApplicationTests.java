package cloud.ex.it.patrimonio;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloud.ex.it.patrimonio.model.Patrimonio;
import cloud.ex.it.patrimonio.repository.PatrimonioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PatrimonioApplicationTests {

	@Autowired
	private PatrimonioRepository patrimonioRepository;

	@Autowired
	private MockMvc mvc;

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		patrimonioRepository.deleteAll();
	}

	@Test
	public void testSavePatrimonio() {
		Patrimonio patrimonio = new Patrimonio("Patrimonio Test", 100000L, 2023);
		Patrimonio savedPatrimonio = patrimonioRepository.save(patrimonio);

		assertNotNull(savedPatrimonio.getId());
		assertEquals("Patrimonio Test", savedPatrimonio.getNome());
		assertEquals(100000L, savedPatrimonio.getValore());
		assertEquals(2023, savedPatrimonio.getAnnoCreazione());
	}

	@Test
	public void testFindPatrimonioById() {
		Patrimonio patrimonio = new Patrimonio("Patrimonio Test", 100000L, 2023);
		Patrimonio savedPatrimonio = patrimonioRepository.save(patrimonio);

		Patrimonio foundPatrimonio = patrimonioRepository.findById(savedPatrimonio.getId()).orElse(null);

		assertNotNull(foundPatrimonio);
		assertEquals(savedPatrimonio.getId(), foundPatrimonio.getId());
		assertEquals("Patrimonio Test", foundPatrimonio.getNome());
		assertEquals(100000L, foundPatrimonio.getValore());
		assertEquals(2023, foundPatrimonio.getAnnoCreazione());
	}

	@Test
	public void testGetAllPatrimoni() throws Exception {
		Patrimonio patrimonio = new Patrimonio("Casa", 100000L, 2020);
		patrimonioRepository.save(patrimonio);

		/* una volta inserito il singolo patrimonio controlla la grandezza dell'array (1 perchè è l'unico inserito nel test)
		 e se array[0].nome e gli altri valori sono uguali a quello inserito allora da ok. $ si riferisce all'oggetto json in se */
		mvc.perform(MockMvcRequestBuilders.get("/api/patrimoni")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nome", is(patrimonio.getNome())))
				.andExpect(jsonPath("$[0].valore", is(patrimonio.getValore().intValue())))
				.andExpect(jsonPath("$[0].annoCreazione", is(patrimonio.getAnnoCreazione())));
	}

	@Test
	public void testAddPatrimonio() {
		// Preparazione dei dati di test
		Patrimonio patrimonioRequest = new Patrimonio("Casa", 100000L, 2000);

		// Esecuzione della richiesta POST
		ResponseEntity<Patrimonio> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/patrimonio",
				patrimonioRequest,
				Patrimonio.class
		);

		// Verifica della risposta
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		// Verifica dei dati nella risposta
		Patrimonio patrimonioResponse = response.getBody();
		assertNotNull(patrimonioResponse);
		assertEquals("Casa", patrimonioResponse.getNome());
		assertEquals(100000L, patrimonioResponse.getValore().longValue());
		assertEquals(2000, patrimonioResponse.getAnnoCreazione().intValue());
	}

	@Test
	public void returnOkToEndpointManagement() {
		ResponseEntity<Map<String, Object>> entity = this.restTemplate.exchange(
				"http://localhost:" + this.mgt + "/actuator",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Map<String, Object>>() {}
		);
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
