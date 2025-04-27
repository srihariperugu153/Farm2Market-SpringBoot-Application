package com.farmtomarket.application.controller;

import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.ProductDTO;
import com.farmtomarket.application.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> saveProduct(@RequestBody @Valid ProductDTO productDTO){
        return ResponseEntity.ok(productService.saveProduct(productDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody @Valid ProductDTO productDTO){
        return ResponseEntity.ok(productService.updateProduct(productDTO));
    }

    @DeleteMapping("/removeOrRecover")
    public ResponseEntity<String> removeProduct(@RequestParam int productId, boolean toDelete){
        return ResponseEntity.ok(productService.removeOrRecoverProduct(productId,toDelete));
    }

    @GetMapping("/products")
    public ResponseEntity<PageResponse<ProductDTO>> getProducts(
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchString){
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        PageResponse<ProductDTO> products = productService.getProducts(pageable,searchString,city);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/myProducts")
    public ResponseEntity<List<ProductDTO>> ownedProducts(@RequestParam boolean inDraft){
        return ResponseEntity.ok(productService.ownedProducts(inDraft));
    }

}
