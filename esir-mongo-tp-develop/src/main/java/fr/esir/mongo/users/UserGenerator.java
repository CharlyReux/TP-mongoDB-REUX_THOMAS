package fr.esir.mongo.users;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import fr.esir.mongo.posts.Post;
import fr.esir.mongo.posts.PostGenerator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author lboutros
 */
@Component
@RequiredArgsConstructor
public class UserGenerator implements Processor {

  private final static Random RANDOM = new Random(System.currentTimeMillis());

  @Value("${user.max.age}")
  private int userMaxAge;

  @Value("${user.min.age}")
  private int userMinAge;

  @Value("${user.max.id}")
  private int userMaxId;

  @Value("${user.id.mask}")
  private String userIdMask;

  @Value("classpath:super-heroes.txt")
  private Resource heroNameResource;

  private String[] heroNames;

  // This is a dummy example, you should NEVER do that for a production app
  private final ConcurrentHashMap<String, User> knownUsers = new ConcurrentHashMap<>();

  private final PostGenerator postGenerator;

  @Override
  public void process(Exchange exchange) throws Exception {
    exchange.getIn().setBody(generateUser());
  }

  @PostConstruct
  private void initSuperHeroes() throws IOException {
    List<String> heroNameList = IOUtils.readLines(heroNameResource.getInputStream(), StandardCharsets.UTF_8.name());
    heroNames = heroNameList.toArray(new String[heroNameList.size()]);
  }

  private User generateUser() {
    String nickname = heroNames[RANDOM.nextInt(heroNames.length)];
    String id = nickname + "|" + String.format(userIdMask, RANDOM.nextInt(userMaxId));

    List<Post> myPosts = new ArrayList<Post>();

    for (int i = 0; i < (new Random().nextInt(6)); i++) {
      Post randomPost = postGenerator.getRandomKnownPost();
      myPosts.add(randomPost);
    }
    myPosts.removeIf(el -> el == null);
    if (!myPosts.isEmpty()) {

      List<String> myUserTags = new LinkedList<>();
      // getting the tags
      for (Post post : myPosts) {
        myUserTags.addAll(post.getThread().getTags());
      }

      User user = User.builder()
          .age(Math.max(userMinAge, RANDOM.nextInt(userMaxAge)))
          ._id(id)
          .nickname(nickname)
          .posts(myPosts)
          .Usedtags(myUserTags)
          .build();

      User oldUser = knownUsers.putIfAbsent(id, user);
      if (oldUser == null) {
        return user;
      } else {
        return null;
      }
    }
    return null;
  }

  public User getRandomKnownUser() {
    // TODO improve this method (we should use another collection to get random user
    // in a faster way)
    Iterator<User> iterator = knownUsers.values().iterator();

    User retValue = null;
    if (!knownUsers.isEmpty()) {
      int nextPos = RANDOM.nextInt(knownUsers.size());

      for (int i = 0; i <= nextPos; i++) {
        retValue = iterator.next();
      }
    }

    return retValue;
  }
}
