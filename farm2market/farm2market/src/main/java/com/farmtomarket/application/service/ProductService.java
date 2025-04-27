package com.farmtomarket.application.service;

import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.ProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PageResponse<ProductDTO> getProducts(Pageable pageable, String searchString, String city);

    String saveProduct(ProductDTO productDTO);

    ProductDTO updateProduct(ProductDTO productDTO);

    String removeOrRecoverProduct(int productId, boolean toDelete);

    List<ProductDTO> ownedProducts(boolean inDraft);
}
