package com.beerhouse.validation;

import java.math.BigDecimal;

import com.beerhouse.model.Beer;

import br.com.beerhouse.exception.ValidationException;

public final class Validation {
	
	public static boolean isValidad(Beer beer) {
		return beer.getName() == null || beer.getName().isEmpty() || beer.getAlcoholContent() == null
				|| beer.getAlcoholContent().isEmpty() || beer.getPrice() == null
				|| beer.getPrice().compareTo(BigDecimal.ZERO) != 1;
	}
	
	public static String getMessageValidad(Beer beer) throws ValidationException {
		StringBuilder validationMessage = new StringBuilder();
		boolean result = Boolean.FALSE;
		int init = 0;

		if (beer.getName() == null || beer.getName().isEmpty()) {
			validationMessage.append(++init).append(" - O nome é obrigatório!");
           result =  Boolean.TRUE;    
		}

		if (beer.getAlcoholContent() == null || beer.getAlcoholContent().isEmpty()) {
			validationMessage.append(++init).append(" - O teor/conteúdo é obrigatório!");
             result =  Boolean.TRUE;
		}

		if (beer.getPrice() == null || beer.getPrice().compareTo(BigDecimal.ZERO) != 1) {
			validationMessage.append(++init).append("- O preço é obrigatório");
		  result =  Boolean.TRUE;
		  
		}
		if (result && validationMessage.toString() != null && !validationMessage.toString().isEmpty()) {
			throw new ValidationException("Não foi possível inserir/atualizar a cerveja! "
					.concat("Inconsistências: ").concat(validationMessage.toString()));
		}

		return validationMessage.toString();
	}
}
