package com.beerhouse.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.beerhouse.model.Beer;
import com.beerhouse.repository.CraftbeerRepository;
import com.beerhouse.validation.Validation;

import br.com.beerhouse.exception.ValidationException;

@RestController
@RequestMapping("/beers")
public class CraftbeerController {

	  private final CraftbeerRepository craftbeerRepository;
	   
	   public CraftbeerController(CraftbeerRepository craftbeerRepository) {
		   this.craftbeerRepository = craftbeerRepository;
	   }

	@PostMapping("/")
	public ResponseEntity<Beer> getSavePost(@RequestBody Beer beer) {
		Beer savedBeer;
		if (Validation.isValidad(beer)) {
			throw new ValidationException(Validation.getMessageValidad(beer));
		}
		savedBeer = craftbeerRepository.save(beer);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedBeer.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/")
	public List<Beer> getlistAll() {
		return craftbeerRepository.findAll();
	}
	@GetMapping("/{id}")
	public Beer retornaPorId(@PathVariable Integer id) {
		return craftbeerRepository.findOne(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> getUpdatePut(@PathVariable Integer id, @RequestBody Beer beer) {
		Beer beerUpdate = craftbeerRepository.findOne(id);
		if (beerUpdate == null) {
			return ResponseEntity.notFound().build();
		}
		if (Validation.isValidad(beer)) {
			throw new ValidationException(Validation.getMessageValidad(beer));
		}
		BeanUtils.copyProperties(beer, beerUpdate, "id");
		beerUpdate = craftbeerRepository.save(beerUpdate);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> getUpdatePatch(@PathVariable Integer id, @RequestBody Beer beer) {
		Beer beerUpdate = craftbeerRepository.findOne(id);
		if (beerUpdate == null) {
			return ResponseEntity.notFound().build();
		}
		if (Validation.isValidad(beer)) {
			throw new ValidationException(Validation.getMessageValidad(beer));
		}
		BeanUtils.copyProperties(beer, beerUpdate, "id");
		beerUpdate = craftbeerRepository.save(beerUpdate);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleta(@PathVariable Integer id) {
		Beer beerToDelete = craftbeerRepository.findOne(id);
		if (beerToDelete == null) {
			return ResponseEntity.notFound().build();
		}
		craftbeerRepository.delete(beerToDelete);
		return ResponseEntity.noContent().build();
	}
}
