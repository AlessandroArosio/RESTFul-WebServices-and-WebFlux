package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CategoryController {

  private final CategoryRepository categoryRepository;

  @Autowired
  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @GetMapping("/api/v1/categories")
  public Flux<Category> list() {
    return categoryRepository.findAll();
  }

  @GetMapping("/api/v1/categories/{id}")
  public Mono<Category> getById(@PathVariable String id) {
    return categoryRepository.findById(id);
  }

  // otherwise Junit test will fail because it will return status 200
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/categories")
  public Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
    return categoryRepository.saveAll(categoryStream).then();
  }

  @PutMapping("/api/v1/categories/{id}")
  public Mono<Category> update(@PathVariable String id, @RequestBody Category category) {
    category.setId(id);
    return categoryRepository.save(category);
  }

  @PatchMapping("/api/v1/categories/{id}")
  public Mono<Category> patch(@PathVariable String id, @RequestBody Category category) {
    // although business logic is WRONG in the controller, since I don't have a service layer here it's fine.... (ish)
    Category cat = categoryRepository.findById(id).block();

    // can smell a null pointer here..
    if (cat.getDescription() != category.getDescription()) {
      cat.setDescription(category.getDescription());
      return categoryRepository.save(cat);
    }
    return Mono.just(cat);
  }
}
