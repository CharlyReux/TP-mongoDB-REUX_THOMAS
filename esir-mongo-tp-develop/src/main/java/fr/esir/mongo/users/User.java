package fr.esir.mongo.users;

import java.util.List;

import fr.esir.mongo.posts.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class User {

  @EqualsAndHashCode.Include
  private final String _id;

  private final String nickname;

  private final int age;

  private final List<Post> posts;

}
