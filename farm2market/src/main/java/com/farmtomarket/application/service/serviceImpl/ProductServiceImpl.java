package com.farmtomarket.application.service.serviceImpl;

import com.farmtomarket.application.config.UserContextHolder;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.ProductDTO;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.Products;
import com.farmtomarket.application.model.Role;
import com.farmtomarket.application.model.User;
import com.farmtomarket.application.repository.ProductRepository;
import com.farmtomarket.application.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PageResponse<ProductDTO> getProducts(Pageable pageable, String searchString, String city) {
        Page<Products> products;
        products = getProductsPage(pageable, searchString, city);
        PageResponse<ProductDTO> pageResponse = new PageResponse<>();
        pageResponse.setContent(products.getContent().stream()
                .map(product ->modelMapper.map(product,ProductDTO.class))
                .toList());
        pageResponse.setPageNumber(products.getNumber());
        pageResponse.setPageSize(products.getSize());
        pageResponse.setTotalElements(products.getTotalElements());
        pageResponse.setTotalPages(products.getTotalPages());
        return pageResponse;
    }

    @Override
    public String saveProduct(ProductDTO productDTO) {
        UserDTO userDTO = UserContextHolder.getUserDto();
        validateUserAccess(userDTO,false,null);
        Products products = new Products();
        products.setProductName(productDTO.getProductName());
        products.setCity(productDTO.getCity());
        products.setPrice(productDTO.getPrice());
        products.setQuantity(productDTO.getQuantity());
        products.setFarmer(modelMapper.map(userDTO, User.class));
        productRepository.save(products);
        return "Product Saved";
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Products product = productRepository.findById(productDTO.getId())
                .orElseThrow(()->new ApplicationException("Product Not Found"));
        validateUserAccess(UserContextHolder.getUserDto(),true,product);
        product.setProductName(productDTO.getProductName());
        product.setCity(productDTO.getCity());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        productRepository.save(product);
        return productDTO.convertToProduct(product);
    }

    @Override
    public String removeOrRecoverProduct(int productId, boolean toDelete) {
        Products product = productRepository.findById(productId)
                .orElseThrow(()->new ApplicationException("Product Not Found"));
        validateUserAccess(UserContextHolder.getUserDto(),true,product);
        product.setDeleteFlag(toDelete);
        productRepository.save(product);
        return toDelete? "Product Deleted ": "Product Recovered";
    }


    @Override
    public List<ProductDTO> ownedProducts(boolean inDraft) {
        User user = modelMapper.map(UserContextHolder.getUserDto(),User.class);
        if (!user.getRoleName().equals(Role.FARMER)){
            throw new SystemAuthException(user.getRoleName()+" role is not allowed to Access");
        }
        List<Products> products = productRepository.findByFarmerAndDeleteFlag(user,inDraft);
        return products.parallelStream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
    }

    private static void validateUserAccess(UserDTO userDTO, boolean isEdit, Products product){
        if (!userDTO.getRoleName().equals(Role.FARMER.toString())){
            throw new SystemAuthException("Only Farmer Allowed to Add/Edit Products");
        }
        if (isEdit && !product.getFarmer().getUsername().equals(userDTO.getUsername())){
            throw new SystemAuthException("You are not the Owner of the Product");
        }
    }

    private Page<Products> getProductsPage(Pageable pageable, String searchString, String city){
        Page<Products> products;
        if (StringUtils.isNotBlank(city)){
            if (StringUtils.isBlank(searchString)){
                products = productRepository.findByCityContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(city,pageable,0);
            } else {
                products = productRepository.findByCityContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(city,searchString,pageable,0);
            }
        } else {
            if (StringUtils.isBlank(searchString)){
                products = productRepository.findAllOrderByCityFirst(UserContextHolder.getUserDto().getCity(),pageable);
            } else {
                products = productRepository.findByProductNameContainingIgnoreCaseAndQuantityGreaterThanAndDeleteFlagFalse(searchString,pageable,0);
            }
        }
        return products;
    }
}
