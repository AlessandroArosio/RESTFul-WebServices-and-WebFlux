package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class VendorControllerTest {

  WebTestClient webTestClient;
  VendorRepository vendorRepository;
  VendorController vendorController;

  @Before
  public void setUp() throws Exception {
    vendorRepository = Mockito.mock(VendorRepository.class);
    vendorController = new VendorController(vendorRepository);
    webTestClient = WebTestClient.bindToController(vendorController).build();
  }

  @Test
  public void list() {
    BDDMockito.given(vendorRepository.findAll())
        .willReturn(Flux.just(Vendor.builder().firstName("Alessandro").lastName("Arosio").build(),
            Vendor.builder().firstName("Thomas").lastName("Shelby").build()));

    webTestClient.get()
        .uri("/api/v1/vendors")
        .exchange()
        .expectBodyList(Vendor.class)
        .hasSize(2);
  }

  @Test
  public void getById() {
    BDDMockito.given(vendorRepository.findById("asd"))
        .willReturn(Mono.just(Vendor.builder().firstName("Alessandro").build()));

    webTestClient.get()
        .uri("/api/v1/vendors/asd")
        .exchange()
        .expectBodyList(Vendor.class)
        .hasSize(1);
  }

  @Test
  public void testCreateCategory() {
    BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
        .willReturn(Flux.just(Vendor.builder().build()));

    Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("A name").build());

    webTestClient.post()
        .uri("/api/v1/vendors")
        .body(vendorToSaveMono, Vendor.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  public void testUpdate() {
    BDDMockito.given(vendorRepository.save(any(Vendor.class)))
        .willReturn(Mono.just(Vendor.builder().firstName("A random name").build()));

    Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("A name").build());

    webTestClient.put()
        .uri("/api/v1/vendors/asd")
        .body(vendorToSaveMono, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testPatchWithChanges() {

    BDDMockito.given(vendorRepository.findById(anyString()))
        .willReturn(Mono.just(Vendor.builder().build()));

    BDDMockito.given(vendorRepository.save(any(Vendor.class)))
        .willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("New name").build());

    webTestClient.patch()
        .uri("/api/v1/vendors/asd")
        .body(vendorToSaveMono, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();

    BDDMockito.verify(vendorRepository).save(any());
  }

  @Test
  public void testPatchWithoutChanges() {

    BDDMockito.given(vendorRepository.findById(anyString()))
        .willReturn(Mono.just(Vendor.builder().build()));

    BDDMockito.given(vendorRepository.save(any(Vendor.class)))
        .willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().build());

    webTestClient.patch()
        .uri("/api/v1/vendors/asd")
        .body(vendorToSaveMono, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();

    BDDMockito.verify(vendorRepository, Mockito.never()).save(any());
  }
}