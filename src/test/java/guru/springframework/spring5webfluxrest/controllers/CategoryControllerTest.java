package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class CategoryControllerTest {

  WebTestClient webTestClient;
  CategoryRepository categoryRepository;
  CategoryController categoryController;

  @Before
  public void setUp() throws Exception {
    categoryRepository = Mockito.mock(CategoryRepository.class);
    categoryController = new CategoryController(categoryRepository);
    webTestClient = WebTestClient.bindToController(categoryController).build();
  }

  @Test
  public void list() {
    BDDMockito.given(categoryRepository.findAll())
        .willReturn(Flux.just(Category.builder().description("Cat1").build(),
            Category.builder().description("Cat2").build()));

    webTestClient.get()
        .uri("/api/v1/categories")
        .exchange()
        .expectBodyList(Category.class)
        .hasSize(2);
  }

  @Test
  public void getById() {
    BDDMockito.given(categoryRepository.findById("asd"))
        .willReturn(Mono.just(Category.builder().description("Cat").build()));

    webTestClient.get()
        .uri("/api/v1/categories/asd")
        .exchange()
        .expectBodyList(Category.class)
        .hasSize(1);
  }

  @Test
  public void testCreateCategory() {
    BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
        .willReturn(Flux.just(Category.builder().build()));

    Mono<Category> catToSaveMono = Mono.just(Category.builder().description("A category").build());

    webTestClient.post()
        .uri("/api/v1/categories")
        .body(catToSaveMono, Category.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  public void testUpdate() {
    BDDMockito.given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().description("A random category").build()));

    Mono<Category> catToSaveMono = Mono.just(Category.builder().description("A category").build());

    webTestClient.put()
        .uri("/api/v1/categories/asd")
        .body(catToSaveMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testPatchWithChanges() {

    BDDMockito.given(categoryRepository.findById(anyString()))
        .willReturn(Mono.just(Category.builder().build()));

    BDDMockito.given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().build()));

    Mono<Category> catToSaveMono = Mono.just(Category.builder().description("New description").build());

    webTestClient.patch()
        .uri("/api/v1/categories/asd")
        .body(catToSaveMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();

    BDDMockito.verify(categoryRepository).save(any());
  }

  @Test
  public void testPatchWithoutChanges() {

    BDDMockito.given(categoryRepository.findById(anyString()))
        .willReturn(Mono.just(Category.builder().build()));

    BDDMockito.given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().build()));

    Mono<Category> catToSaveMono = Mono.just(Category.builder().build());

    webTestClient.patch()
        .uri("/api/v1/categories/asd")
        .body(catToSaveMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();

    BDDMockito.verify(categoryRepository, Mockito.never()).save(any());
  }
}