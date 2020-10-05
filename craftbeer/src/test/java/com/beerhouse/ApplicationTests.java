package com.beerhouse;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.beerhouse.model.Beer;
import com.beerhouse.repository.CraftbeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ComponentScan(basePackageClasses = Application.class)
public class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	
	@MockBean
	private CraftbeerRepository craftbeerRepository;

	@Before
	public void init() {
		Beer testBeer = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("5,8% ABV"), "IPE");
		when(craftbeerRepository.findOne(3)).thenReturn(testBeer);
	}

	@Test
	public void BeerPorId() throws Exception {
		mockMvc.perform(get("/beers/1")).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("SAISON À TROIS")))
				.andExpect(jsonPath("$.ingredients", is("LÚPULO")))
				.andExpect(jsonPath("$.alcoholContent", is("5,8% ABV")))
				.andExpect(jsonPath("$.price", is(12.69))).andExpect(jsonPath("$.category", is("IPE")));

		verify(craftbeerRepository, times(1)).findOne(1);
	}

	@Test 
	public void listBeers() throws Exception {
		List<Beer> listBeers = new ArrayList<Beer>();

		listBeers.add(new Beer(1, "SAISON À TROIS", "LUPULO", "5,8% ABV", new BigDecimal("12.69"), "IPE"));
		listBeers.add(new Beer(2, "BAMBERG RAUCHBIER", null, "5,2% ABV", new BigDecimal("19.08"), "TYI"));

		when(craftbeerRepository.findAll()).thenReturn(listBeers);

		mockMvc.perform(get("/beers")).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("SAISON À TROIS")))
				.andExpect(jsonPath("$[0].alcoholContent", is("5,8% ABV")))
				.andExpect(jsonPath("$[0].price", is(12.69))).andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("BAMBERG RAUCHBIER")))
				.andExpect(jsonPath("$[1].alcoholContent", is("5,2% ABV"))).andExpect(jsonPath("$[1].price", is(19.08)));
	}

	@Test
	public void createsBeerInvalid() throws Exception {
		String json, expectedMsgRetResponse;

		/*POST com apenas 1 dos campos obrigatórios preenchidos.*/
		json = "{\"name\":\"SAISON À TROIS\"}";
		expectedMsgRetResponse = "1 - O teor/conteúdo é obrigatório!".concat("2- O preço é obrigatório!");
		mockMvc.perform(
				post("/beers").content(json).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.date", is(notNullValue())))
				.andExpect(jsonPath("$.message", containsString(expectedMsgRetResponse)));
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}

	@Test
	public void BeerInvalid() throws Exception {
		Beer beerSave = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.59"), "IPE");
		ObjectMapper objMapper = new ObjectMapper();
		when(craftbeerRepository.save(any(Beer.class))).thenReturn(beerSave);
		mockMvc.perform(post("/beers").content(objMapper.writeValueAsString(beerSave))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(header().stringValues("Location", "http://localhost/beers/1"));
		verify(craftbeerRepository, times(1)).save(any(Beer.class));
	}

	@Test
	public void updateBeerNonexistent() throws Exception {
		Beer beerUpdate = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.59"), "IPE");
		ObjectMapper objMapper = new ObjectMapper();
		/*
		 * cerveja de id igual à 1, então deve ser retornado nulo, o que
		 * acarretará na geração do status 404 para a requisição Put
		 */
		when(craftbeerRepository.findOne(1)).thenReturn(null);
		mockMvc.perform(put("/beers/1").content(objMapper.writeValueAsString(beerUpdate))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}

	@Test
	public void updateBeerValid() throws Exception {
		Beer beerUpdate = new Beer(6, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.69"), "IPE");
		ObjectMapper objMapper = new ObjectMapper();

		when(craftbeerRepository.findOne(1)).thenReturn(beerUpdate);

		mockMvc.perform(put("/beers/1").content(objMapper.writeValueAsString(beerUpdate))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(craftbeerRepository, times(1)).save(any(Beer.class));
	}

	@Test
	public void updatePatchBeerNonexistent() throws Exception {
		Beer beerUpdate = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("24.80"), "IPE");
		ObjectMapper objMapper = new ObjectMapper();
		/*
		 * Ao pesquisar a cerveja de id igual à 1, então deve ser retornado nulo, o que
		 * acarretará na geração do status 404 para a requisição Patch
		 */
		when(craftbeerRepository.findOne(1)).thenReturn(null);
		mockMvc.perform(patch("/beers/1").content(objMapper.writeValueAsString(beerUpdate))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}

	@Test
	public void updatePatchBeer() throws Exception {
		Beer beerToUpdate = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.69"), "ALE");
		ObjectMapper objMapper = new ObjectMapper();

		when(craftbeerRepository.findOne(1)).thenReturn(beerToUpdate);

		mockMvc.perform(patch("/beers/1").content(objMapper.writeValueAsString(beerToUpdate))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(craftbeerRepository, times(1)).save(any(Beer.class));
	}

	@Test
	public void deleteBeerNonexistent() throws Exception {
		when(craftbeerRepository.findOne(1)).thenReturn(null);
		doNothing().when(craftbeerRepository).delete(1);
		mockMvc.perform(delete("/beers/1")).andExpect(status().isNotFound());
		verify(craftbeerRepository, times(0)).delete(1);
	}

	@Test
	public void deleteBeerExisting() throws Exception {
		Beer beerDelete = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.69"), "IPE");
		/*
		 * Retorno do objeto da classe de cerveja preenchido acima.
		 * Isso irá gerar um status 204 (No Content), que é o esperado,
		 * ao invocar o delete
		 */
		when(craftbeerRepository.findOne(1)).thenReturn(beerDelete);
		doNothing().when(craftbeerRepository).delete(beerDelete);
		mockMvc.perform(delete("/beers/1")).andExpect(status().isNoContent());
		verify(craftbeerRepository, times(1)).delete(beerDelete);
	}
}