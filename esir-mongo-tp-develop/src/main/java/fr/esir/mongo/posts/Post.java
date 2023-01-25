package fr.esir.mongo.posts;

import fr.esir.mongo.threads.Thread;
import fr.esir.mongo.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author lboutros
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Post {
  @EqualsAndHashCode.Include
  private final String _id;

  private final String title;

  private final String content;

}
