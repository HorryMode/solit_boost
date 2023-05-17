package slcd.boost.boost.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.General.UserAccessCheckService;
import slcd.boost.boost.Syncs.DTOs.InternalProductResponse;
import slcd.boost.boost.Syncs.DTOs.ProductInfo;
import slcd.boost.boost.Users.DTOs.ProductShortResponse;
import slcd.boost.boost.Users.Entities.ProductEntity;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.Repos.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private UserAccessCheckService userAccessCheckService;

    @Autowired
    private ProductRepository productRepository;

    public ProductEntity findByUuid(UUID uuid){
        return productRepository
                .findByUuid(uuid)
                .orElseThrow();
    }

    public List<ProductEntity> findAllNotArchived(){
        return productRepository.findByArchived(false);
    }

    public boolean existsByUuid(UUID uuid){
        return productRepository.existsByUuid(uuid);
    }

    public void saveInternalPost(InternalProductResponse product){
        ProductEntity productEntity = new ProductEntity();

        productEntity.setUuid(
                UUID.fromString(product.getId())
        );
        productEntity.setName(product.getName());
        productEntity.setShortName(product.getShortName());
        productEntity.setArchived(false);

        productRepository.save(productEntity);
    }

    public boolean existsByUuidAndArchived(UUID uuid){
        return productRepository.existsByUuidAndArchived(uuid, true);
    }

    public void setArchivedIsFalse(UUID uuid){
        ProductEntity productEntity = findByUuid(uuid);
        productEntity.setArchived(false);
        productRepository.save(productEntity);
    }

    public void setArchivedTrue(UUID uuid){
        ProductEntity productEntity = findByUuid(uuid);
        productEntity.setArchived(true);
        productRepository.save(productEntity);
    }

    public void comparePostWithInternalInfo(InternalProductResponse internalProduct){
        ProductEntity productEntity = findByUuid(
                UUID.fromString(internalProduct.getId())
        );

        if(!Objects.equals(
                productEntity.getName(), internalProduct.getName()
        )) productEntity.setName(internalProduct.getName());
        if(!Objects.equals(
                productEntity.getShortName(), internalProduct.getShortName()
        )) productEntity.setShortName(internalProduct.getShortName());

        productRepository.save(productEntity);
    }
}
