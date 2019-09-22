package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {

  private final VendorRepository vendorRepository;

  @Autowired
  public VendorController(VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @GetMapping("/api/v1/vendors")
  public Flux<Vendor> list() {
    return vendorRepository.findAll();
  }

  @GetMapping("/api/v1/vendors/{id}")
  Mono<Vendor> getById(@PathVariable String id) {
    return vendorRepository.findById(id);
  }

  // otherwise Junit test will fail because it will return status 200
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/vendors")
  public Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream) {
    return vendorRepository.saveAll(vendorStream).then();
  }

  @PutMapping("/api/v1/vendors/{id}")
  public Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
    vendor.setId(id);
    return vendorRepository.save(vendor);
  }

  @PatchMapping("/api/v1/vendors/{id}")
  public Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {
    // although business logic is WRONG in the controller, since I don't have a service layer here it's fine.... (ish)
    Vendor vend = vendorRepository.findById(id).block();

    // can smell a null pointer here..
    if (vend.getFirstName() != vendor.getFirstName()) {
      vend.setFirstName(vendor.getFirstName());
      return vendorRepository.save(vend);
    }
    return Mono.just(vend);
  }
}
