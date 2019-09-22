package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repository.CategoryRepository;
import guru.springframework.spring5webfluxrest.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

  private final CategoryRepository categoryRepository;
  private final VendorRepository vendorRepository;

  @Autowired
  public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
    this.categoryRepository = categoryRepository;
    this.vendorRepository = vendorRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    if (categoryRepository.count().block() == 0) {
      // load data
      System.out.println("### Loading data on bootstrap ###");

      categoryRepository.save(Category.builder()
          .description("Fruits").build()).block();

      categoryRepository.save(Category.builder()
          .description("Nuts").build()).block();

      categoryRepository.save(Category.builder()
          .description("Breads").build()).block();

      categoryRepository.save(Category.builder()
          .description("Eggs").build()).block();

      System.out.println("Loaded categories: " + categoryRepository.count().block());

      vendorRepository.save(Vendor.builder()
          .firstName("Micheal")
          .lastName("Buck")
          .build()).block();

      vendorRepository.save(Vendor.builder()
          .firstName("David")
          .lastName("White")
          .build()).block();

      vendorRepository.save(Vendor.builder()
          .firstName("Joe")
          .lastName("Jonson")
          .build()).block();

      vendorRepository.save(Vendor.builder()
          .firstName("Donald")
          .lastName("Strump")
          .build()).block();

      System.out.println("Loaded vendord: " + vendorRepository.count().block());
    } else {
      System.out.println("repository not empty");
    }
  }
}
