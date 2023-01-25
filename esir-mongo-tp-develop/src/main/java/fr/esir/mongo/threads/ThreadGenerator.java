package fr.esir.mongo.threads;

import fr.esir.mongo.posts.Post;
import fr.esir.mongo.posts.PostGenerator;
import fr.esir.mongo.text.TextGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lboutros
 */
@Component
@AllArgsConstructor
@Slf4j
public class ThreadGenerator implements Processor {

  private final static Random RANDOM = new Random(System.currentTimeMillis());

  // TODO initialize/read values in mongo
  // This is a dummy example, you should NEVER do that for a production app
  private final AtomicInteger id = new AtomicInteger(0);
  private final ConcurrentHashMap<String, Thread> knownThreads = new ConcurrentHashMap<>();

  private final TextGenerator textGenerator;

  private final PostGenerator postGenerator;

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(generateThread());
  }

  // TODO manage post/thread/user relationship
  private Thread generateThread() {
    List<Post> myPosts = new ArrayList<Post>();
    for(int i = 0; i< (new Random().nextInt(6));i++){
      Post randomPost = postGenerator.getRandomKnownPost();
      myPosts.add(randomPost);
    }
    myPosts.removeIf(el -> el == null);
    if (!myPosts.isEmpty()) {
      String idString = Long.toString(id.getAndIncrement());
      Thread newThread = Thread.builder()
              ._id(idString)
              .title(textGenerator.generateText(1))
              .posts(myPosts)
              .build();

      knownThreads.put(idString, newThread);

      return newThread;
    } else {
      log.warn("Cannot create thread, no user created yet.");
      return null;
    }
  }

  public Thread getRandomThread() {
    if (knownThreads.isEmpty()) {
      return null;
    } else {
      return knownThreads.get(Long.toString(RANDOM.nextInt(id.get())));
    }
  }
}
