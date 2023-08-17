package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryStubImpl implements PostRepository {
  // Открытое хранилище постов, доступное для просмотра и редактирования
  private final ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();
  // Хранилище удалённых постов, недоступное пользователю
  private final ConcurrentMap<Long, Post> removedPostsStorage = new ConcurrentHashMap<>();
  private static final AtomicLong countPostID = new AtomicLong(0);
  public List<Post> all() {
    return new ArrayList<>(new ArrayList<>(posts.values()));
  }

  public Optional<Post> getById(long id) {
    if(posts.containsKey(id)) {
      return Optional.ofNullable(posts.get(id));
    } else {
      throw new NotFoundException();
    }
  }

  public Post save(Post post) {
    long id = post.getId();
    if (removedPostsStorage.containsKey(id)) {
      throw new NotFoundException();
    }
    else if (posts.containsKey(id)) {
      posts.put(id, post);
    } else {
      post.setId(countPostID.incrementAndGet());
      posts.put(countPostID.get(), post);
    }
    return post;
  }

  public void removeById(long id) {
    // Если пост удаляется, то он помещается в закрытое хранилище
    if (posts.containsKey(id)) {
      Post delPost = new Post(id, posts.get(id).getContent());
      removedPostsStorage.put(id, delPost);
      posts.remove(id);
    } else {
      throw new NotFoundException();
    }
  }
}