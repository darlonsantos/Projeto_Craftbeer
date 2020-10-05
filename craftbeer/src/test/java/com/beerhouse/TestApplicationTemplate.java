package com.beerhouse;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.beerhouse.model.Beer;
import com.beerhouse.repository.CraftbeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe responsável por testes unitários utilizando o Template Rest.
 * @author lukas
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestApplicationTemplate {

	@Autowired
	private TestRestTemplate restTemplate;

	
	@MockBean
	private CraftbeerRepository craftbeerRepository;

	@Test
	public void createBeerInvalid() {
		String json, expectedResponseRet;
		HttpHeaders headers;
		HttpEntity<String> entity;
		ResponseEntity<String> response;
       
		// JSON para a requisição POST com apenas 1 dos campos obrigatórios preenchidos.
		json = "{\"name\":\"SAISON À TROIS\"}";
		expectedResponseRet = "1 - O teor/conteúdo é obrigatório!"
				.concat("2- O preço é obrigatório!");
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		entity = new HttpEntity<>(json, headers);
		response = restTemplate.postForEntity("/beers", entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertThat(response.getBody(), containsString(expectedResponseRet));
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}

	@Test
	public void updateBeerInvalid() throws Exception{
		String expectedResponseRet;
		HttpHeaders headers;
		HttpEntity<String> entity;
		ResponseEntity<String> response;	
		ObjectMapper objMapper = new ObjectMapper();	
		
		//cerveja que não será atualizada por estar faltando o preço
		Beer beerUpdate = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				null, "IPE");
		when(craftbeerRepository.findOne(1)).thenReturn(beerUpdate);
		expectedResponseRet = "O preço é obrigatório!";
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		entity = new HttpEntity<>(objMapper.writeValueAsString(beerUpdate), headers);
		response = restTemplate.exchange("/beers/1", HttpMethod.PUT, entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertThat(response.getBody(), containsString(expectedResponseRet));
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}
	
	@Test
	public void updatePatchBeerInvalid() throws Exception{
		String expectedResponseRet;
		HttpHeaders headers;
		HttpEntity<String> entity;
		ResponseEntity<String> response;	
		ObjectMapper objMapper = new ObjectMapper();	
		
		//cerveja que não será atualizada por estar faltando o nome
		Beer beerUpdate = new Beer(1, null, "LÚPULO", "5,8% ABV",
				new BigDecimal("12.69"), "IPE");
		
		when(craftbeerRepository.findOne(1)).thenReturn(beerUpdate);
		expectedResponseRet = "O nome é obrigatório!";
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		entity = new HttpEntity<>(objMapper.writeValueAsString(beerUpdate), headers);
		response = restTemplate.exchange("/beers/1", HttpMethod.PATCH, entity, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertThat(response.getBody(), containsString(expectedResponseRet));
		verify(craftbeerRepository, times(0)).save(any(Beer.class));
	}
	
	@Test
	public void deleteBeerNonexistent() {
		HttpEntity<String> entity;
		ResponseEntity<String> response;
		Beer beerdelete = new Beer(1, "SAISON À TROIS", "LÚPULO", "5,8% ABV",
				new BigDecimal("12.59"), "IPE");
		
		/*Força o não retorno de uma cerveja com o Id 1.
		Isso irá gerar um status 404 ao invocar o delete*/
		when(craftbeerRepository.findOne(1)).thenReturn(null);
		entity = new HttpEntity<>(null, new HttpHeaders());
		response = restTemplate.exchange("/beers/1", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(craftbeerRepository, times(0)).delete(beerdelete);
	}
}
