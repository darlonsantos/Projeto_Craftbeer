package com.beerhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beerhouse.model.Beer;

public interface CraftbeerRepository extends JpaRepository<Beer, Integer> {
}
