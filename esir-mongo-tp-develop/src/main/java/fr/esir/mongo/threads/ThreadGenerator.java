package fr.esir.mongo.threads;

import fr.esir.mongo.posts.Post;
import fr.esir.mongo.posts.PostGenerator;
import fr.esir.mongo.text.TextGenerator;
import fr.esir.mongo.users.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
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


  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(generateThread());
  }

  // TODO manage post/thread/user relationship
  private Thread generateThread() {

    String idString = Long.toString(id.getAndIncrement());
    List<String> tags = new ArrayList<String>();
    for (int i = 0; i < 5; i++) {
      tags.add(textGenerator.generateText(2));
    }

    Thread newThread = Thread.builder()
        ._id(idString)
        .title(textGenerator.generateText(1))
        .tags(tags)
        .build();

    knownThreads.put(idString, newThread);

    return newThread;
  }

  public Thread getRandomThread() {
    if (knownThreads.isEmpty()) {
      return null;
    } else {
      return knownThreads.get(Long.toString(RANDOM.nextInt(id.get())));
    }
  }
}
