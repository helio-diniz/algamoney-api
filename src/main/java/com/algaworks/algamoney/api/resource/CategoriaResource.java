package com.algaworks.algamoney.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algamoney.api.event.RecursoCriadoEvent;
import com.algaworks.algamoney.api.model.Categoria;
import com.algaworks.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
//@CrossOrigin(maxAge = 10, origins = {"http:/localhost:8000"}) : habilitando CORS na classe
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;
	@Autowired
	private ApplicationEventPublisher publisher;

	//maxAge: depois que a requisição OPTION foi realizada, perdurará por 10 segundos em CACHE
	//@CrossOrigin(maxAge = 10, origins = {"http://localhost:8000"}) : habilitando CORS num método
	//@CrossOrigin(maxAge = 10) : não funcionar para Spring Security OAuth2
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}

	@PostMapping
	// @ResponseStatus(HttpStatus.CREATED)
	// Não precisa mais pois ResponseEntity.created(uri) já diz 201
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		// source é quem gerou o evento
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}

	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> categoriaBuscada = categoriaRepository.findById(codigo);
		return (categoriaBuscada.isPresent()) ? ResponseEntity.ok(categoriaBuscada.get()) : ResponseEntity.notFound().build();
	}

}
