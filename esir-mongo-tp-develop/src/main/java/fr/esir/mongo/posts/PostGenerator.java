package fr.esir.mongo.posts;

import fr.esir.mongo.text.TextGenerator;
import fr.esir.mongo.users.User;
import fr.esir.mongo.users.UserGenerator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lboutros
 */
@Component
@AllArgsConstructor
public class PostGenerator implements Processor {

  // TODO initialize/read values in mongo
  // This is a dummy example, you should NEVER do that for a production app
  private final AtomicLong id = new AtomicLong(0);

  private final static Random RANDOM = new Random(System.currentTimeMillis());


  private final TextGenerator textGenerator;



  private final ConcurrentHashMap<String, Post> knownPost = new ConcurrentHashMap<>();

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(generatePost());
  }

  // TODO manage post/thread/user relationship
  private Post generatePost() {

      // we need a thread in order to add a post into
      String idString = Long.toString(id.getAndIncrement());
      Post newPost = Post.builder()
              ._id(idString)
              .title(textGenerator.generateText(1))
              .content(textGenerator.generateText(10))
              .build();
              knownPost.put(idString, newPost);

      return newPost;

  }
  public Post getRandomKnownPost() {
    // TODO improve this method (we should use another collection to get random user in a faster way)
    Iterator<Post> iterator = knownPost.values().iterator();

    Post retValue = null;
    if (!knownPost.isEmpty()) {
      int nextPos = RANDOM.nextInt(knownPost.size());

      for (int i = 0; i <= nextPos; i++) {
        retValue = iterator.next();
        iterator.remove();
      }
    }

    return retValue;
  }
}
