package slcd.boost.boost.General;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import slcd.boost.boost.General.Entities.PostEntity;
import slcd.boost.boost.General.Repos.PostRepository;
import slcd.boost.boost.Syncs.DTOs.InternalPostResponse;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostEntity findByUuid(UUID uuid){
        return postRepository
                .findByUuid(uuid)
                .orElseThrow();
    }

    public List<PostEntity> findAllNotArchived(){
        return postRepository.findByArchived(false);
    }

    public boolean existsByUuid(UUID uuid){
        return postRepository.existsByUuid(uuid);
    }

    public void saveInternalPost(InternalPostResponse post){
        PostEntity postEntity = new PostEntity();

        postEntity.setUuid(
                UUID.fromString(post.getId())
        );
        postEntity.setName(post.getName());
        postEntity.setArchived(false);

        postRepository.save(postEntity);
    }

    public boolean existsByUuidAndArchived(UUID uuid){
        return postRepository.existsByUuidAndArchived(uuid, true);
    }

    public void setArchivedIsFalse(UUID uuid){
        PostEntity postEntity = findByUuid(uuid);
        postEntity.setArchived(false);
        postRepository.save(postEntity);
    }

    public void setArchivedTrue(UUID uuid){
        PostEntity postEntity = findByUuid(uuid);
        postEntity.setArchived(true);
        postRepository.save(postEntity);
    }

    public void comparePostWithInternalInfo(InternalPostResponse internalPost){
        PostEntity postEntity = findByUuid(
                UUID.fromString(internalPost.getId())
        );

        if(!Objects.equals(
                postEntity.getName(), internalPost.getName()
        )) postEntity.setName(internalPost.getName());

        postRepository.save(postEntity);
    }
}
