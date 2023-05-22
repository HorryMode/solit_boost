package slcd.boost.boost.Syncs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import slcd.boost.boost.General.Entities.PostEntity;
import slcd.boost.boost.General.Entities.SubdivisionEntity;
import slcd.boost.boost.General.Repos.SubdivisionRepository;
import slcd.boost.boost.General.PostService;
import slcd.boost.boost.Syncs.DTOs.InternalPostResponse;
import slcd.boost.boost.Syncs.DTOs.InternalProductResponse;
import slcd.boost.boost.Syncs.DTOs.InternalUserResponse;
import slcd.boost.boost.Users.Entities.ProductEntity;
import slcd.boost.boost.Users.Entities.UserEntity;
import slcd.boost.boost.Users.ProductService;
import slcd.boost.boost.Users.Repos.UserRepository;
import slcd.boost.boost.Users.UserService;

import java.util.*;
import java.util.logging.Level;

@Service
public class SyncService {

    @Autowired
    private SubdivisionRepository subdivisionRepository;

    @Autowired
    private InternalApiService internalApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ProductService productService;

    private final static Logger LOGGER = LoggerFactory.getLogger(SyncService.class);

    public boolean syncUsersWithInternal(){
        try {
            //Получение всех актвных отделов
            List<SubdivisionEntity> subdivisions = subdivisionRepository.findByArchived(false);
            Set<String> userUUIDs = new HashSet<>();

            for (SubdivisionEntity subdivision : subdivisions) {
                //Проучение списка пользователей отдела
                List<String> users =
                        internalApiService
                                .getInternalUsersBySubdivision(subdivision.getUuid().toString())
                                .getSubdivision()
                                .getEmployees();

                for (String userUUID : users) {
                    syncUser(userUUID);
                    userUUIDs.add(userUUID);
                }
            }

            //Получение всего списка пользователей
            List<UserEntity> userEntities = userService.findAllNotArchived();

            //Если пользователь не был получен, то архивировать его
            for (UserEntity userEntity :
                    userEntities) {
                if (!userUUIDs.contains(
                        userEntity.getUuid().toString()
                )) userService.setArchivedTrue(userEntity.getUuid());
            }

            return true;
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            return false;
        }
    }

    public boolean syncUser(String stringUuid){
        try {
            UUID uuid = UUID.fromString(stringUuid);
            InternalUserResponse internalUserResponse = internalApiService.getInternalUser(stringUuid);

            if(!userService.existsByUuid(uuid)) {
                userService.saveInternalUser(internalUserResponse.getUser());
            }
            else if(userService.existsByUuidAndArchived(uuid)) {
                userService.setArchivedFalse(uuid);
                userService.compareWithInternalInfo(internalUserResponse.getUser());
            }
            else {
                userService.compareWithInternalInfo(internalUserResponse.getUser());
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean syncPosts(){
        try {
            List<InternalPostResponse> posts = internalApiService.getInternalPosts();
            Set<String> postUuids = new HashSet<>();

            for (InternalPostResponse post : posts) {
                syncPost(post);
                postUuids.add(post.getId());
            }

            List<PostEntity> postEntities = postService.findAllNotArchived();
            for (PostEntity postEntity : postEntities){
                if(!postUuids.contains(
                        postEntity.getUuid().toString()
                )) postService.setArchivedTrue(postEntity.getUuid());
            }

            return true;
        } catch (Exception e){
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            return false;
        }
    }

    public boolean syncPost(InternalPostResponse post){
        try {
            UUID uuid = UUID.fromString(post.getId());

            if(!postService.existsByUuid(uuid)) {
                postService.saveInternalPost(post);
            }
            else if (postService.existsByUuidAndArchived(uuid)) {
                postService.setArchivedIsFalse(uuid);
                postService.comparePostWithInternalInfo(post);
            }
            else {
                postService.comparePostWithInternalInfo(post);
            }

            return true;
        } catch (Exception e){
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            return false;
        }
    }

    public boolean syncProducts(){
        try {
            List<InternalProductResponse> products = internalApiService.getInternalProducts();
            Set<String> productUuids = new HashSet<>();

            for (InternalProductResponse product : products) {
                syncProduct(product);
                productUuids.add(product.getId());
            }

            List<ProductEntity> productEntities = productService.findAllNotArchived();
            for (ProductEntity productEntity : productEntities){
                if(!productUuids.contains(
                        productEntity.getUuid().toString()
                )) productService.setArchivedTrue(productEntity.getUuid());
            }

            return true;
        } catch (Exception e){
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            return false;
        }
    }

    public boolean syncProduct(InternalProductResponse product){
        try {
            UUID uuid = UUID.fromString(product.getId());

            if(!productService.existsByUuid(uuid)) {
                productService.saveInternalPost(product);
            }
            else if (productService.existsByUuidAndArchived(uuid)) {
                productService.setArchivedIsFalse(uuid);
                productService.comparePostWithInternalInfo(product);
            }
            else {
                productService.comparePostWithInternalInfo(product);
            }

            return true;
        } catch (Exception e){
            LOGGER.error(Level.SEVERE.getResourceBundleName(), e);
            return false;
        }
    }
}
