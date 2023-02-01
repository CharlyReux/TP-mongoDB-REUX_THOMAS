package fr.esir.mongo.threads;

import java.util.List;

import fr.esir.mongo.posts.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author lboutros
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Thread {
  @EqualsAndHashCode.Include
  private final String _id;
  
  private final String title;

  private final List<String> tags;

}
