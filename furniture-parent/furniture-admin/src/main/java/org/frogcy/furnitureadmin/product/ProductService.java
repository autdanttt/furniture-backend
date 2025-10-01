package org.frogcy.furnitureadmin.product;


import org.frogcy.furnitureadmin.product.dto.ProductCreateDTO;
import org.frogcy.furnitureadmin.product.dto.ProductResponseDTO;
import org.frogcy.furnitureadmin.product.dto.ProductUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductCreateDTO dto, List<MultipartFile> images);

    ProductResponseDTO get(Integer id);

    ProductResponseDTO update(ProductUpdateDTO productDto, List<MultipartFile> images);

    void delete(Integer id, Integer userLoginId);

    void changeEnabled(Integer id, boolean enabled);
}
