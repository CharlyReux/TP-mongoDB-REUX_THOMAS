# TP-mongoDB-REUX_THOMAS

## Commands:
Démarrer l'instance docker:
```sh
docker-compose up
```
Se connecter au shell
```sh
docker exec -it mongo bash
mongo admin -u root -p password
```

## Introduction

Dans ce TP, nous allons découvrir l'utilisation et les particularités des bases de données mongoDB, d'abord en apprenant les bases, puis en abordant la génération de données ainsi que la modélisation. Enfin, nous nous focaliserons sur l'amélioration de la disponibité ainsi que de la scalabilité horizontale.

## Pour débuter 1
### 3. Insertion utilisateur
```sh
> use forum
```
 2./3. insertion utilisateur
```sh
> db.collection.insert({"nickname":"jean","age":20})
```
### 4. Vérification du résultat
```sh
> db.collection.find()
{ "_id" : ObjectId("63cfe4054a461e15c728b3bc"), "nickname" : "jean", "age" : 20 }
```
### 5. Recherche par champ
par le champ age
```sh
> db.collection.find({"age":20})
{ "_id" : ObjectId("63cfe540d4286c9a88c98c00"), "nickname" : "jean", "age" : 20 }
```
Recherche par l'ID
```sh
> db.collection.find({},{"_id":0})
{ "nickname" : "jean", "age" : 20 }
```
### 6. Ajout d'un attribut interest et recherche
```sh
> db.collection.update({ "nickname" : "jean" }, { $set : { "interests" : ["sucre","poe"] }})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```
Recherche par cet attribut
```sh
> db.collection.find({"interests":{$all: ["sucre"]}})
{ "_id" : ObjectId("63cfe4054a461e15c728b3bc"), "nickname" : "jean", "age" : 20, "interests" : [ "sucre", "poe" ] }
```
### 7. Champ _id
Le champ _id est un champ généré automatiquement par mongodb si non spécifié, il contient un timestamp, une adresse MAC, un Process Identifier, ainsi qu'un counter.
### 8. Essai de modification de l'ID
```sh
> db.collection.update({ "nickname" : "jean" }, { $set : { "_id" : "test" }})
WriteResult({
        "nMatched" : 0,
        "nUpserted" : 0,
        "nModified" : 0,
        "writeError" : {
                "code" : 66,
                "errmsg" : "Performing an update on the path '_id' would modify the immutable field '_id'"
        }
})
```
Il est impossible de changer l'id d'une entité car cette variable est immuable. Il faut donc passer outre cette contrainte en créant une nouvelle entité avec les mêmes paramètres mais un id différent.


### 9. Méthode pour mettre à jour l'id de l'utilisateur
```sh
doc = db.collection.findOne({_id: OAbjectId("63cfe4054a461e15c728b3bc")})

doc._id = ObjectId("000123454a461e15c728b3bc")

db.collection.insert(doc)   

db.collection.remove({_id: ObjectId("63cfe4054a461e15c728b3bc")})
```

## Pour débuter 2
### 1. Insertion deux utilisateurs en une commande
```sh
> db.collection.insertMany( [{ "nickname": "pierre", "age" : 22, "interests" : [ "vélo", "natation" ] },{ "nickname": "jacque", "age" : 31, "interests" : [ "cuisine" ] }] );
```
### 2. Vérifier l'insertion dans la base
```sh
> db.collection.find()
{ "_id" : ObjectId("000123454a461e15c728b3bc"), "nickname" : "jean", "age" : 20, "interests" : [ "sucre", "poe" ] }
{ "_id" : ObjectId("63cfedbeb7c2dd93d791d101"), "nickname" : "pierre", "age" : 22, "interests" : [ "vélo", "natation" ] }
{ "_id" : ObjectId("63cfedbeb7c2dd93d791d102"), "nickname" : "jacque", "age" : 31, "interests" : [ "cuisine" ] }
```
### 3. insertion utilisateur avec un ID déjà existant
```sh
> db.collection.insert({"nickname":"patrick","age":42,"_id" : ObjectId("000123454a461e15c728b3bc")})
WriteResult({
        "nInserted" : 0,
        "writeError" : {
                "code" : 11000,
                "errmsg" : "E11000 duplicate key error collection: forum.collection index: _id_ dup key: { _id: ObjectId('000123454a461e15c728b3bc') }"
        }
})
```
Cette opération est mpossible car l'id est unique.
### 4. Supprimer un utilisateur grâce à son identifiant
```sh
> db.collection.remove({_id: ObjectId("000123454a461e15c728b3bc")})
WriteResult({ "nRemoved" : 1 })
```

### 5. Suppression de plusieurs utilisateurs en même temps
```sh
> db.collection.remove({"age":20})
```
5.1. Suppression de tous les documents en gardant la base
```sh
> db.collection.remove({})
```
ou 
```sh
> db.collection.deletemany({})
```
La différence est que deletemany va retourner un booléen alors que remove va retourner object WriteResult.<br>
On peut aussi utiliser `db.collection.drop()` qui va supprimer l'ensemble d'une collection

## Générons des données
### 1. Accès à la base
On ajoute dans la ligne 13 de application.propertie:
```yaml
mongo.cnx.string=mongodb://root:password@localhost:27017
```
### 3. Observation et recherche sur les données générées
On retrouve bien les trois collections générées
```sh
> show collections
posts
threads
users
```

Les données contenues dans ces collections sont les suivantes:
```sh
> db.posts.find().limit(5)
{ "_id" : "0", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }
{ "_id" : "1", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }
{ "_id" : "2", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }
{ "_id" : "3", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }
{ "_id" : "4", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }
```
```sh
> db.threads.find().limit(5)
{ "_id" : "0", "title" : "blah. " }
{ "_id" : "1", "title" : "blah. " }
{ "_id" : "2", "title" : "blah. " }
{ "_id" : "3", "title" : "blah. " }
{ "_id" : "4", "title" : "blah. " }
```
```sh
> db.users.find().limit(5)
{ "_id" : "Nina Theroux|60", "nickname" : "Nina Theroux", "age" : 43 }
{ "_id" : "Hawkgirl|80", "nickname" : "Hawkgirl", "age" : 13 }
{ "_id" : "Cy-Gor|56", "nickname" : "Cy-Gor", "age" : 25 }
{ "_id" : "Ego|65", "nickname" : "Ego", "age" : 13 }
{ "_id" : "Bushido|08", "nickname" : "Bushido", "age" : 13 }
```

Pour avoir plus d'informations sur les collections on peut utiliser les méthodes suivantes:
```sh
> db.getCollectionInfos()
> db.users.stats()
```

### 4. Différetes valeurs du champ age
On peut utiliser la methode distinct en spécifiant l'attribut pour avoir la liste des valeurs distinctes
```sh
> db.users.distinct("age")
[
        13,
        15,
        16,
        ...
]
```

### 5. Recherche d'utilisateur avec 3 valeurs différentes pour le champ age
On utilise l'opérateur logique or pour spécifier les différents ages possibles.
```sh
> db.users.find({"$or":[{"age":20},{"age":25},{"age":35}]})
{ "_id" : "Cy-Gor|56", "nickname" : "Cy-Gor", "age" : 25 }
{ "_id" : "Abe Sapien|07", "nickname" : "Abe Sapien", "age" : 25 }
{ "_id" : "Scarecrow|85", "nickname" : "Scarecrow", "age" : 35 }
{ "_id" : "Indigo|01", "nickname" : "Indigo", "age" : 20 }
{ "_id" : "Metamorpho|29", "nickname" : "Metamorpho", "age" : 20 }
```

### 6. Recherche des utilisateurs de plus de 30 ans et tri par l'age
On utilise l'opérateur de comparaison $gt pour "existe et est suppérieur" avec la méthode .sort("..") en spécifiant l'attribut et le sens du tri
```sh
> db.users.find({"age":{"$gt":30}}).sort({"age":1}) 
{ "_id" : "Wonder Woman|85", "nickname" : "Wonder Woman", "age" : 32 }
{ "_id" : "Captain Cold|88", "nickname" : "Captain Cold", "age" : 33 }
{ "_id" : "Big Man|93", "nickname" : "Big Man", "age" : 33 }
{ "_id" : "Crimson Dynamo|84", "nickname" : "Crimson Dynamo", "age" : 34 }
{ "_id" : "Scarecrow|85", "nickname" : "Scarecrow", "age" : 35 }
{ "_id" : "Amazo|11", "nickname" : "Amazo", "age" : 36 }
{ "_id" : "Gladiator|42", "nickname" : "Gladiator", "age" : 36 }
...
```

## Explain
### 1. Execution de la requête explain
```sh
> db.users.find({"age":{"$gt":30}}).sort({"age":1}).explain()
```
### 2. Résultat
```json
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "forum.users",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "age" : {
                                "$gt" : 30
                        }
                },
                "queryHash" : "F760A7CC",
                "planCacheKey" : "F760A7CC",
                "winningPlan" : {
                        "stage" : "SORT",
                        "sortPattern" : {
                                "age" : 1
                        },
                        "memLimit" : 104857600,
                        "type" : "simple",
                        "inputStage" : {
                                "stage" : "COLLSCAN",
                                "filter" : {
                                        "age" : {
                                                "$gt" : 30
                                        }
                                },
                                "direction" : "forward"
                        }
                },
                "rejectedPlans" : [ ]
        },
        "serverInfo" : {
                "host" : "6066384c07db",
                "port" : 27017,
                "version" : "4.4.18",
                "gitVersion" : "8ed32b5c2c68ebe7f8ae2ebe8d23f36037a17dea"
        },
        "ok" : 1
}
```

La méthode explain nous donne (en plus de données générales sur la requête), le nombre d'indexes utilisés.<br> Un index est une structure de données qui stocke une partie des données d'une collection pour améliorer la performance d'accès à ces données.<br>
Dans notre requête actuelle, aucun index n'est utilisé.
### 3. Création d'un index sur le champ age
```sh
> db.users.createIndex({"age":1})
```
On obtient la réponse suivante:
```json
{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}
```
En renouvelant la recherche 
```sh
> db.users.find({"age":{"$gt":30}}).sort({"age":1}).explain()
```
On obtient le résultat suivant:
```json
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "forum.users",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "age" : {
                                "$gt" : 30
                        }
                },
                "queryHash" : "F760A7CC",
                "planCacheKey" : "45A19300",
                "winningPlan" : {
                        "stage" : "FETCH",
                        "inputStage" : {
                                "stage" : "IXSCAN",
                                "keyPattern" : {
                                        "age" : 1
                                },
                                "indexName" : "age_1",
                                "isMultiKey" : false,
                                "multiKeyPaths" : {
                                        "age" : [ ]
                                },
                                "isUnique" : false,
                                "isSparse" : false,
                                "isPartial" : false,
                                "indexVersion" : 2,
                                "direction" : "forward",
                                "indexBounds" : {
                                        "age" : [
                                                "(30.0, inf.0]"
                                        ]
                                }
                        }
                },
                "rejectedPlans" : [ ]
        },
        "serverInfo" : {
                "host" : "6066384c07db",
                "port" : 27017,
                "version" : "4.4.18",
                "gitVersion" : "8ed32b5c2c68ebe7f8ae2ebe8d23f36037a17dea"
        },
        "ok" : 1
}
```
On voit que l'index apparaît `"indexName" : "age_1"`

### 4. Plans de requêtes utilisés par mongoDB

Lors de la première requête l'optimizer de mongodb a utilisé comme "winning plan" un SORT car aucun index n'avait été créé. Lors de la seconde requête, l'optimizer a trouvé qu'un index existait et a donc utilisé un FETCH, ce qui améliore les performances.

### 5. Utilisation d'un index "Hashed"
On récupère et on supprime d'abord l'index existant avec
```sh
> db.users.getIndexes()
> db.users.dropIndex("age_1")
```

Puis on ajoute un index haché:
```sh
> db.users.ensureIndex({ "age" : "hashed" })
{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}
```

On recommence la requête:
```sh
> db.users.find({"age":{"$gt":30}}).sort({"age":1}).explain()
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "forum.users",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "age" : {
                                "$gt" : 30
                        }
                },
                "queryHash" : "F760A7CC",
                "planCacheKey" : "45A19300",
                "winningPlan" : {
                        "stage" : "SORT",
                        "sortPattern" : {
                                "age" : 1
                        },
                        "memLimit" : 104857600,
                        "type" : "simple",
                        "inputStage" : {
                                "stage" : "COLLSCAN",
                                "filter" : {
                                        "age" : {
                                                "$gt" : 30
                                        }
                                },
                                "direction" : "forward"
                        }
                },
                "rejectedPlans" : [ ]
        },
        "serverInfo" : {
                "host" : "6066384c07db",
                "port" : 27017,
                "version" : "4.4.18",
                "gitVersion" : "8ed32b5c2c68ebe7f8ae2ebe8d23f36037a17dea"
        },
        "ok" : 1
}

```
On peut voir que l'index haché n'a pas été utilisé cette fois-ci, l'optimizer à préféré refaire un SORT sur les données.

#### 7. Intérêt d'un index haché
L'utilisation d'un index haché est moins intéressant dans ce cadre car la requête qu'on utilise à un .sort(). L'optimizer de mongoDB va donc refuser de prendre cet index haché car il ne donne aucun avantage sur la rapidité de la requête par rapport à un sort classique.


## Aggrégation
On utilise $group pour grouper par nickname avec les opérateurs $avg pour la moyenne d'age et $sum pour le nombre d'utilisateurs avec le même nickname. On utilise aussi $first pour récupérer le nickname dans un attribut.<br>
Ensuite, pour supprimer l'id on utilise $project avec _id:0, et pour finir on utilise $sort pour trier, d'abord par l'age:
```sh
> db.users.aggregate( [ { $group : { "_id":"$nickname", "age":{$avg:"$age"}, count: { $sum: 1.0 }, "nickname":{$first:"$nickname"}  } }, {$project:{_id:0}},{$sort:{"age":-1}} ] )

{ "age" : 69, "count" : 1, "nickname" : "Yellowjacket" }
{ "age" : 68, "count" : 1, "nickname" : "Atom" }
{ "age" : 68, "count" : 1, "nickname" : "He-Man" }
{ "age" : 68, "count" : 1, "nickname" : "Mister Knife" }
{ "age" : 68, "count" : 1, "nickname" : "Mimic" }
{ "age" : 67, "count" : 1, "nickname" : "Batman II" }
{ "age" : 67, "count" : 1, "nickname" : "Shatterstar" }
```
puis par le nombre d'utilisateurs:
```sh
> db.users.aggregate( [ { $group : { "_id":"$nickname", "age":{$avg:"$age"}, count: { $sum: 1.0 }, "nickname":{$first:"$nickname"}  } }, {$project:{_id:0}},{$sort:{"count":-1}} ] )

{ "age" : 19, "count" : 2, "nickname" : "Armor" }
{ "age" : 41.5, "count" : 2, "nickname" : "Cy-Gor" }
{ "age" : 30, "count" : 2, "nickname" : "Big Man" }
{ "age" : 52.5, "count" : 2, "nickname" : "Donatello" }
{ "age" : 38, "count" : 2, "nickname" : "Proto-Goblin" }
{ "age" : 26.5, "count" : 2, "nickname" : "Crimson Dynamo" }
{ "age" : 13, "count" : 1, "nickname" : "Man-Bat" }
{ "age" : 24, "count" : 1, "nickname" : "Spock" }
{ "age" : 63, "count" : 1, "nickname" : "Space Ghost" }
```

## Modélisation(1)

### 1. Manière de modéliser des relations
Il existe plusieurs manières de modéliser des relations avec mongodb, soit avec des sous-documents, soit avec des documents liés.

### 2. Implémentations

#### Embedded document
- Version 1 

Ajout d'un attribut user et un attribut thread dans post.<br>
On va donc ajouter un user et un thread dans la classe Post
```java
  private final User user;
```
Ensuite on modifie ThreadGenerator et PostGenerator pour prendre un utilisateur aléatoire.
```java
Thread randomThread = threadGenerator.getRandomThread();
User randomKnownUser = userGenerator.getRandomKnownUser();

Post newPost = Post.builder()
      ._id(idString)
      .title(textGenerator.generateText(1))
      .user(randomKnownUser)
      .thread(randomThread)
      .content(textGenerator.generateText(10))
      .build();

```

On génère ensuite les données et on trouve bien des users et des threads dans chaque post
```sh
> db.posts.find()
{ "_id" : "0", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Elongated Man|71", "nickname" : "Elongated Man", "age" : 14 }, "thread" : { "_id" : "0", "title" : "blah. " } }
{ "_id" : "1", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Elongated Man|71", "nickname" : "Elongated Man", "age" : 14 }, "thread" : { "_id" : "0", "title" : "blah. " } }
{ "_id" : "2", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Elongated Man|71", "nickname" : "Elongated Man", "age" : 14 }, "thread" : { "_id" : "0", "title" : "blah. " } }
```

On peut trouver tous les posts d'un utilisateur avec la commande : 
```sh
> db.posts.find({"user.nickname":"Thor Girl"})
{ "_id" : "492", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Thor Girl|08", "nickname" : "Thor Girl", "age" : 26 }, "thread" : { "_id" : "149", "title" : "blah. " } }
{ "_id" : "500", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Thor Girl|08", "nickname" : "Thor Girl", "age" : 26 }, "thread" : { "_id" : "127", "title" : "blah. " } }
```
et avec le .explain("executionStats") on récupère les statistiques suivantes:
```json
        "executionStats" : {
                "executionSuccess" : true,
                "nReturned" : 22,
                "executionTimeMillis" : 1,
                "totalKeysExamined" : 0,
                "totalDocsExamined" : 796,
                "executionStages" : {
                        "stage" : "COLLSCAN",
                        "filter" : {
                                "user.nickname" : {
                                        "$eq" : "Thor Girl"
                                }
                        },
                        "nReturned" : 22,
                        "executionTimeMillisEstimate" : 0,
                        "works" : 798,
                        "advanced" : 22,
                        "needTime" : 775,
                        "needYield" : 0,
                        "saveState" : 0,
                        "restoreState" : 0,
                        "isEOF" : 1,
                        "direction" : "forward",
                        "docsExamined" : 796
                }
        },
```

On peut trouver tous les posts d'un thread :
```sh
> db.posts.find({"thread._id":"23"})
{ "_id" : "84", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Flash III|47", "nickname" : "Flash III", "age" : 34 }, "thread" : { "_id" : "23", "title" : "blah. " } }
{ "_id" : "115", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "John Wraith|82", "nickname" : "John Wraith", "age" : 68 }, "thread" : { "_id" : "23", "title" : "blah. " } }
{ "_id" : "176", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Donna Troy|35", "nickname" : "Donna Troy", "age" : 25 }, "thread" : { "_id" : "23", "title" : "blah. " } }
{ "_id" : "201", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Big Barda|75", "nickname" : "Big Barda", "age" : 35 }, "thread" : { "_id" : "23", "title" : "blah. " } }
```
Et on obtient les statistiques suivantes:
```json
"executionStats" : {
                "executionSuccess" : true,
                "nReturned" : 5,
                "executionTimeMillis" : 1,
                "totalKeysExamined" : 0,
                "totalDocsExamined" : 796,
                "executionStages" : {
                        "stage" : "COLLSCAN",
                        "filter" : {
                                "thread._id" : {
                                        "$eq" : "23"
                                }
                        },
                        "nReturned" : 5,
                        "executionTimeMillisEstimate" : 0,
                        "works" : 798,
                        "advanced" : 5,
                        "needTime" : 792,
                        "needYield" : 0,
                        "saveState" : 0,
                        "restoreState" : 0,
                        "isEOF" : 1,
                        "direction" : "forward",
                        "docsExamined" : 796
                }
        }
```
- Version 2

On ajoute un tableau de post dans user et un tableau de post dans thread.
on ajoute une map dans postGenerator
```java
  private final ConcurrentHashMap<String, Post> knownPost = new ConcurrentHashMap<>();
  ```
Dans la méthode generatePost on ajoute les posts dans la map
```java
knownPost.put(idString, newPost);
```
On ajoute ensuite une méthode getRandomPost
```java
  public Post getRandomKnownPost() {

    Iterator<Post> iterator = knownPost.values().iterator();

    Post retValue = null;
    if (!knownPost.isEmpty()) {
      int nextPos = RANDOM.nextInt(knownPost.size());

      for (int i = 0; i <= nextPos; i++) {
        retValue = iterator.next();
      }
    }

    return retValue;
  }
```
On rajoute en attribut dans user et dans thread une liste de post
```java
  private final List<Post> posts;

```

On va ensuite rajouter dans ThreadGenerator et dans UserGenerator les lignes de code suivante:

```java
    List<Post> myPosts = new ArrayList<Post>();
    for(int i = 0; i< (new Random().nextInt(6));i++){
      Post randomPost = postGenerator.getRandomKnownPost();
      myPosts.add(randomPost);
    }

    ...
  private final List<Post> posts;
      Thread newThread = Thread.builder()
              ._id(idString)
              .title(textGenerator.generateText(1))
              .posts(myPosts)
              .build();
```
On trouve maintenant dans thread une liste de posts
```sh
> db.threads.find()
{ "_id" : "0", "title" : "blah. ", "posts" : [ { "_id" : "1", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "1", "title" : "blah. ", "posts" : [ { "_id" : "4", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "2", "title" : "blah. ", "posts" : [ { "_id" : "5", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "3", "title" : "blah. ", "posts" : [ { "_id" : "9", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "10", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
```

```sh
> db.users.find()
{ "_id" : "Blob|69", "nickname" : "Blob", "age" : 20, "posts" : [ { "_id" : "880", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "885", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "Atlas|54", "nickname" : "Atlas", "age" : 13, "posts" : [ { "_id" : "889", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "890", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "Blackwing|41", "nickname" : "Blackwing", "age" : 53, "posts" : [ { "_id" : "895", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "Goliath IV|89", "nickname" : "Goliath IV", "age" : 21, "posts" : [ { "_id" : "902", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "903", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
{ "_id" : "Tracy Strauss|20", "nickname" : "Tracy Strauss", "age" : 27, "posts" : [ { "_id" : "908", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ] }
```

Pour trouver tous les posts d'un utilisateur on peut faire :

```sh
db.users.find({"nickname":"Deadpool"},{"posts":1})
```

```sh
db.threads.find({"_id":"6"},{"posts":1})
```

Ensuite avec explain on trouve les données suivantes
```json
 "executionStats" : {
                "executionSuccess" : true,
                "nReturned" : 1,
                "executionTimeMillis" : 0,
                "totalKeysExamined" : 1,
                "totalDocsExamined" : 1,
                "executionStages" : {
                        "stage" : "PROJECTION_SIMPLE",
                        "nReturned" : 1,
                        "executionTimeMillisEstimate" : 0,
                        "works" : 2,
                        "advanced" : 1,
                        "needTime" : 0,
                        "needYield" : 0,
                        "saveState" : 0,
                        "restoreState" : 0,
                        "isEOF" : 1,
                        "transformBy" : {
                                "posts" : 1
                        },
                        "inputStage" : {
                                "stage" : "IDHACK",
                                "nReturned" : 1,
                                "executionTimeMillisEstimate" : 0,
                                "works" : 2,
                                "advanced" : 1,
                                "needTime" : 0,
                                "needYield" : 0,
                                "saveState" : 0,
                                "restoreState" : 0,
                                "isEOF" : 1,
                                "keysExamined" : 1,
                                "docsExamined" : 1
                        }
                }
        }
```

On observe que l'étape stage n'est pas la même puisque la requête n'a pas eu besoin de scanner l'entièreté des utilisateurs. En théorie, cette modélisation est plus optimale pour les requêtes.

Pour la partie suivante, nous utiliserons cette version pour effectuer nos requêtes.


## Modélisation (2)

1. Ajout d'une notion de tag dans les threads.
- Pour implémenter ce modèle, nous allons rajouter une liste de string dans la classe thread.
- Une autre méthode serait d'utiliser une classe Tag et de mettre une liste de tag dans les thread
- On pourrait aussi utiliser un ENUM et lister des tags possible, puis faire une liste de tag dans thread.
2. Nous avons décidé de selectionner la liste de string, qui permet plus de liberté quant au contenu du tag, et qui reste plus facile à implémenter. Pour faire ceci, nous avons rajouté un attribut tags dans la class thread comme ceci:
```java
  private final List<String> tags;
```
Nous n'avons pas généré automatiquement de tags, mais on peut en rajouter à partir de notre console avec la commande:
```sh
>db.threads.update({"_id":"2"},{$push:{"tags":"not found"}})
{ "_id" : "2", "title" : "blah. ", "posts" : [ { "_id" : "6", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "9", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " }, { "_id" : "10", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. " } ], "tags" : [ "not found" ] }
```

## Modélisation(3)
1. Effectuer des statistiques sur l'âge moyen
- La première modélisation à laquelle nous avons pensé serait de ne plus avoir une liste de posts dans user, mais avoir dans post un user, ce qui permettrait de simplifier la requête à la base de données. En plus de cela on peut directement rajouter dans threads un double moyenne contenant la moyenne de l'âge des utilisateurs.
- La deuxième possibilité serait d'utiliser notre architecture actuelle (avec une liste de post dans user et une liste de post dans thread) et de faire une fonction aggregate complexe.
2. Nous allons utiliser la première méthode et modifier notre architecture.
Premièrement, on va ajouter un attribut averageAge dans la classe thread:
```java
  private final double averageAge;
```
Ensuite, il faut supprimer la liste contenant les posts dans les users.
Lors de la génération d'un thread, on va maintenant calculer la moyenne sur les utilisateurs des posts pris aléatoirement, sans oublier de ne pas prendre plusieurs fois le même user.
```java
    double myAverageAge = 0;
    Set<User> myUserInThread = new HashSet<User>();
    for (Post myPost : myPosts) {
      if (myPost == null)
        continue;
      myUserInThread.add(myPost.getUser());
    }

    for (User user : myUserInThread) {
      myAverageAge += user.getAge();
    }
    myAverageAge /= myUserInThread.size();

```
Ici myPost contient la liste des posts du thread actuel.

Maintenant lorsqu'on requête la base de données on a bien un attribut averageAge qui a été ajouté:
```sh
> db.threads.find()
{ "_id" : "0", "title" : "blah. ", "posts" : [ { "_id" : "1", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Han Solo|82", "nickname" : "Han Solo", "age" : 13 } } ], "tags" : [ ], "averageAge" : 13 }
{ "_id" : "3", "title" : "blah. ", "posts" : [ { "_id" : "9", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Han Solo|82", "nickname" : "Han Solo", "age" : 13 } }, { "_id" : "10", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Klaw|77", "nickname" : "Klaw", "age" : 66 } } ], "tags" : [ ], "averageAge" : 39.5 }
{ "_id" : "4", "title" : "blah. ", "posts" : [ { "_id" : "14", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "user" : { "_id" : "Bloodwraith|85", "nickname" : "Bloodwraith", "age" : 63 } } ], "tags" : [ ], "averageAge" : 63 }
```
## Modélisation(4)

1. Effectuer des statistiques sur les tags des threads par utilisateur.
- Encore une fois on pourrait utiliser l'architecture actuelle, et effectuer une méthode aggregate complexe.
- Ou on peut changer notre architecture pour répondre aux besoins. On peut cette fois-ci remettre dans user une liste de posts et dans chaque poste mettre un thread. Ensuite on va rajouter dans user une liste de tags qu'on aura récupéré depuis les posts lors de la génération d'un user.
2. On va donc implémenter la deuxième modélisation.
Premièrement, on va rajouter dans User un attribut tagList et un attribut post
```java
private final List<String> Usedtags;

private final List<Post> posts;
```

Ensuite on va générer des tags aléatoires dans les threads:
```java
    for (int i = 0; i < 5; i++) {
      tags.add(textGenerator.generateText(2));
    }
```
Et finalement, dans thread on va ajouter un attribut thread
```java
  private final Thread thread;
```
Et on va l'initialiser aléatoirement lors de la création d'un post.

Ensuite, lors de la génération des users, on va itérer dans la liste de ses posts et accéder aux threads correspondant pour trouver la liste des tags correspondant.
```java
  List<String> myUserTags = new LinkedList<>();
      // getting the tags
      for (Post post : myPosts) {
        myUserTags.addAll(post.getThread().getTags());
      }
```
Mainenant, en faisant des requêtes, on peut trouver dans chaque user une liste de tag correspondant au tag des threads dans lequel il a posté, et donc effectuer des statistiques sur ceux-ci.<br>
Voici un exemple d'utilisateur.
```sh
{ "_id" : "Yellowjacket II|65", "nickname" : "Yellowjacket II", "age" : 13, "posts" : [ { "_id" : "121", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "7", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "108", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "14", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "109", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "9", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } } ], "usedtags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] }
```
Ici en l'occurence les tags sont générés "aléatoirement" avec textGenerator, donc tous égaux à "blah blah".

## Réplication et haute disponibilité

1. On utilise les commandes suivante:
Pour pouvoir continuer de lire dans les bases de données secondaires, on va ajouter l'option `rs.secondaryOK()`.
```sh
$ mongo --nodb
var replicaSet = new ReplSetTest({"nodes" : 3})
replicaSet.startSet()
replicaSet.initiate()
```

2. on se connecte ensuite au docker depuis une autre fenêtre de commande:
```sh
docker exec -it mongo bash
```

On va ensuite se connecter à un replicaSet secondaire:
```sh
mongo --host localhost:20002
```

3. On effectue maintenant la commande status:
```sh
> rs.status()
```
On obtient comme réponse une liste des membres ainsi qu'un attribut "ok" à 1.

4. On va maintenant changer le string de connexion dans le application.properties, mais avant cela, il faut trouver l'ip du conteneur mongo:
```sh
> docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mongo
172.19.0.2
```
On peut ensuite rajouter ce qui suit dans le fichier application.properties du java :
```properties
mongo.cnx.string=mongodb://172.19.0.2:20000
```

5. On utilise ces commandes pour interroger la base:
```sh
>use forum
>db.users.find()
...,
{ "_id" : "Hawkwoman III|78", "nickname" : "Hawkwoman III", "age" : 13, "posts" : [ { "_id" : "126", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "13", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "127", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "34", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } } ], "usedtags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] }
```
Les données ont bien été ajoutées.

6. On va stopper une des replica avec la commande kill. Pour cela, on trouve d'abord le pid des instances :
```sh
>ps -aux
USER         PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
...
root         898  1.2  1.2 1766632 95908 pts/0   Sl+  16:25   0:06 /usr/bin/mongod --oplogSize 40 --port 20000 --replSet __unknown_name__ --dbpath /data/db/__u
root         900  1.3  1.2 1779572 98500 pts/0   Sl+  16:25   0:06 /usr/bin/mongod --oplogSize 40 --port 20001 --replSet __unknown_name__ --dbpath /data/db/__u
root         902  1.3  1.2 1742708 95736 pts/0   Sl+  16:25   0:06 /usr/bin/mongod --oplogSize 40 --port 20002 --replSet __unknown_name__ --dbpath /data/db/__u
```
Puis on kill l'instance correspondante(sur le port 20001):

```sh
kill 900
```
7. Et on regarde le statut:

```sh
replicaset.status()
```
dans le noeud avec l'id 2, on observe les lignes suivantes:
```json
...,
 "health" : 0,
 "state" : 8,
 "stateStr" : "(not reachable/healthy)",
 ...
 ```

8. On va ré-interroger la base de données avec la commande suivante:
```sh
> db.users.find()
{ "_id" : "Ultron|19", "nickname" : "Ultron", "age" : 13, "posts" : [ { "_id" : "8", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "0", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "9", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "1", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "10", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "1", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } } ], "usedtags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] }
```

Même si un noeud secondaire est arreté, la base est toujours fonctionnelle.

## Réplication et haute disponibilité(2)

1. On va kill le noeud primaire:
```sh
kill 898
```

2. Voici ce qu'on obtient avec la commande status du réplicaset
```json
{
        "set" : "__unknown_name__",
        "date" : ISODate("2023-02-01T16:46:05.200Z"),
        "myState" : 2,
        "term" : NumberLong(2),
        "syncSourceHost" : "",
        "syncSourceId" : -1,
        "heartbeatIntervalMillis" : NumberLong(2000),
        "majorityVoteCount" : 2,
        "writeMajorityCount" : 2,
        "votingMembersCount" : 3,
        "writableVotingMembersCount" : 3,
        "optimes" : {
                "lastCommittedOpTime" : {
                        "ts" : Timestamp(1675269837, 14),
                        "t" : NumberLong(1)
                },
                "lastCommittedWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                "readConcernMajorityOpTime" : {
                        "ts" : Timestamp(1675269837, 14),
                        "t" : NumberLong(1)
                },
                "readConcernMajorityWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                "appliedOpTime" : {
                        "ts" : Timestamp(1675269837, 14),
                        "t" : NumberLong(1)
                },
                "durableOpTime" : {
                        "ts" : Timestamp(1675269837, 14),
                        "t" : NumberLong(1)
                },
                "lastAppliedWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                "lastDurableWallTime" : ISODate("2023-02-01T16:43:57.920Z")
        },
        "lastStableRecoveryTimestamp" : Timestamp(1675269837, 14),
        "members" : [
                {
                        "_id" : 0,
                        "name" : "df397d65d352:20000",
                        "health" : 0,
                        "state" : 8,
                        "stateStr" : "(not reachable/healthy)",
                        "uptime" : 0,
                        "optime" : {
                                "ts" : Timestamp(0, 0),
                                "t" : NumberLong(-1)
                        },
                        "optimeDurable" : {
                                "ts" : Timestamp(0, 0),
                                "t" : NumberLong(-1)
                        },
                        "optimeDate" : ISODate("1970-01-01T00:00:00Z"),
                        "optimeDurableDate" : ISODate("1970-01-01T00:00:00Z"),
                        "lastAppliedWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                        "lastDurableWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                        "lastHeartbeat" : ISODate("2023-02-01T16:46:05.029Z"),
                        "lastHeartbeatRecv" : ISODate("2023-02-01T16:45:58.130Z"),
                        "pingMs" : NumberLong(0),
                        "lastHeartbeatMessage" : "Error connecting to df397d65d352:20000 (172.19.0.2:20000) :: caused by :: Connection refused",
                        "syncSourceHost" : "",
                        "syncSourceId" : -1,
                        "infoMessage" : "",
                        "configVersion" : 3,
                        "configTerm" : 1
                },
                {
                        "_id" : 1,
                        "name" : "df397d65d352:20001",
                        "health" : 0,
                        "state" : 8,
                        "stateStr" : "(not reachable/healthy)",
                        "uptime" : 0,
                        "optime" : {
                                "ts" : Timestamp(0, 0),
                                "t" : NumberLong(-1)
                        },
                        "optimeDurable" : {
                                "ts" : Timestamp(0, 0),
                                "t" : NumberLong(-1)
                        },
                        "optimeDate" : ISODate("1970-01-01T00:00:00Z"),
                        "optimeDurableDate" : ISODate("1970-01-01T00:00:00Z"),
                        "lastAppliedWallTime" : ISODate("2023-02-01T16:29:56.581Z"),
                        "lastDurableWallTime" : ISODate("2023-02-01T16:29:56.581Z"),
                        "lastHeartbeat" : ISODate("2023-02-01T16:46:05.028Z"),
                        "lastHeartbeatRecv" : ISODate("2023-02-01T16:37:36.630Z"),
                        "pingMs" : NumberLong(0),
                        "lastHeartbeatMessage" : "Error connecting to df397d65d352:20001 (172.19.0.2:20001) :: caused by :: Connection refused",
                        "syncSourceHost" : "",
                        "syncSourceId" : -1,
                        "infoMessage" : "",
                        "configVersion" : 3,
                        "configTerm" : 1
                },
                {
                        "_id" : 2,
                        "name" : "df397d65d352:20002",
                        "health" : 1,
                        "state" : 2,
                        "stateStr" : "SECONDARY",
                        "uptime" : 1210,
                        "optime" : {
                                "ts" : Timestamp(1675269837, 14),
                                "t" : NumberLong(1)
                        },
                        "optimeDate" : ISODate("2023-02-01T16:43:57Z"),
                        "lastAppliedWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                        "lastDurableWallTime" : ISODate("2023-02-01T16:43:57.920Z"),
                        "syncSourceHost" : "",
                        "syncSourceId" : -1,
                        "infoMessage" : "",
                        "configVersion" : 3,
                        "configTerm" : 1,
                        "self" : true,
                        "lastHeartbeatMessage" : ""
                }
        ],
        "ok" : 1,
        "$clusterTime" : {
                "clusterTime" : Timestamp(1675269838, 1),
                "signature" : {
                        "hash" : BinData(0,"AAAAAAAAAAAAAAAAAAAAAAAAAAA="),
                        "keyId" : NumberLong(0)
                }
        },
        "operationTime" : Timestamp(1675269837, 14)
}
```

On voit bien que le noeud primaire n'est plus disponible et comme il ne reste qu'un noeud secondaire, il n'a pas la majorité sur les trois noeuds pour passer en primaire.
3. On va ré-interroger la base de données
```sh
>  db.users.find()
{ "_id" : "Ultron|19", "nickname" : "Ultron", "age" : 13, "posts" : [ { "_id" : "8", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "0", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "9", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "1", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } }, { "_id" : "10", "title" : "blah. ", "content" : "blah. blah. blah. blah. blah. blah. blah. blah. blah. blah. ", "thread" : { "_id" : "1", "title" : "blah. ", "tags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] } } ], "usedtags" : [ "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. ", "blah. blah. " ] }
```
5. On observe que les données sont toujours disponibles dans la base, le dernier replica secondaire permet toujours de lire les données grâce à l'option que nous avons rajouté au début.

## Sharding
1. On commence par initialiser le cluster shardé:
```sh
> cluster = new ShardingTest({"shards" : 3, "chunkSize" : 1})
```
2. On peut ensuite se connecter au cluster shardé avec la commande suivante:
```sh
> mongo --host localhost:20006
```

3. Pour vérifier l'état du cluster, on lance la commande suivante:
```sh
> sh.status(true) 
--- Sharding Status --- 
  sharding version: {
        "_id" : 1,
        "minCompatibleVersion" : 5,
        "currentVersion" : 6,
        "clusterId" : ObjectId("63e3a45c9818f73ef8f45af5")
  }
  shards:
        {  "_id" : "__unknown_name__-rs0",  "host" : "__unknown_name__-rs0/342c6c438131:20000",  "state" : 1 }
        {  "_id" : "__unknown_name__-rs1",  "host" : "__unknown_name__-rs1/342c6c438131:20001",  "state" : 1 }
        {  "_id" : "__unknown_name__-rs2",  "host" : "__unknown_name__-rs2/342c6c438131:20002",  "state" : 1 }
  active mongoses:
        {  "_id" : "342c6c438131:20006",  "advisoryHostFQDNs" : [ ],  "mongoVersion" : "4.4.18",  "ping" : ISODate("2023-02-08T13:33:10.428Z"),  "up" : NumberLong(50),  "waiting" : true }
  autosplit:
        Currently enabled: no
  balancer:
        Currently enabled:  no
        Currently running:  no
        Failed balancer rounds in last 5 attempts:  0
        Migration Results for the last 24 hours: 
                No recent migrations
  databases:
        {  "_id" : "config",  "primary" : "config",  "partitioned" : true }
```

        On peut observer que les 3 shards et le mongos sont up, que le balancer n'est pas activé et que la base de données utilisée est pour l'instant la base config.

4. On crée notre base de données forum et on active le sharding avec les commandes suivantes:
```sh
use forum
sh.enableSharding("forum")
```

En revérifiant le statut du cluster shardé, on observe l'apparition de la base de données:      
```sh
{  "_id" : "forum",  "primary" : "__unknown_name__-rs0",  "partitioned" : true,  "version" : {  "uuid" : UUID("b2f172a6-3a3d-43ce-8710-0052fb6da9fc"),  "lastMod" : 1 } }
```

5. On lance les commandes suivantes pour définir les clés de sharding:
```sh
sh.shardCollection("forum.posts", { _id : 1})
sh.shardCollection("forum.threads", { _id : 1})
sh.shardCollection("forum.users", { _id : 1})
```

6. Le balancer est déjà stoppé par défaut.
7. On va ensuite changer le paramètre de connection dans le programme java pour se connecter à notre base de données:
```yaml
mongo.cnx.string=mongodb://172.18.0.2:20006
```
On a récupéré l'addresse du conteneur mongo avec la même commande utilisé plus haut pour le réplicaset.
Et on va maintenant lancer le programme de génération de données(`mvn spring-boot:run`).

Pour vérifier la répartition des données sur les shards, on lance la commande suivante:
```sh
db.getCollection('collName').getShardDistribution()
```
Pour toutes les collections, on observe qu'elles ne sont que sur le shard 1:
```sh
Shard __unknown_name__-rs0 at __unknown_name__-rs0/342c6c438131:20000
 data : 342KiB docs : 1325 chunks : 1
 estimated data per chunk : 342KiB
 estimated docs per chunk : 1325

Totals
 data : 342KiB docs : 1325 chunks : 1
 Shard __unknown_name__-rs0 contains 100% data, 100% docs in cluster, avg obj size on shard : 264B
```

### Sharding(2)
1. Pour activer le balancer, on lance startBalancer()
```sh
sh.startBalancer()
```
Dans le statut du sharding on observe que le balancer est activé mais il n'est pas lancé:
```sh
  balancer:
        Currently enabled:  yes
        Currently running:  no
        Failed balancer rounds in last 5 attempts:  0
        Migration Results for the last 24 hours: 
                No recent migrations
```
Cela est dû au fait que la fenêtre d'execution n'est pas activée.
Pour ce faire, on met à jour la base de données:
```sh
db.settings.update(
   { _id: "balancer" },
   { $set: { activeWindow : { start : "00:00", stop : "24:00" } } },
   { upsert: true }
)
```
Après avoir généré des données pendant 10 minutes, on observe qu'elles se répartissent sur les shards, par exemple avec la commande suivante:
```sh
>db.getCollection('posts').getShardDistribution()

Shard __unknown_name__-rs0 at __unknown_name__-rs0/342c6c438131:20000
 data : 2.22MiB docs : 8798 chunks : 2
 estimated data per chunk : 1.11MiB
 estimated docs per chunk : 4399

Shard __unknown_name__-rs2 at __unknown_name__-rs2/342c6c438131:20002
 data : 2KiB docs : 11 chunks : 1
 estimated data per chunk : 2KiB
 estimated docs per chunk : 11

Shard __unknown_name__-rs1 at __unknown_name__-rs1/342c6c438131:20001
 data : 1.29MiB docs : 5079 chunks : 2
 estimated data per chunk : 660KiB
 estimated docs per chunk : 2539

Totals
 data : 3.52MiB docs : 13888 chunks : 5
 Shard __unknown_name__-rs0 contains 63.27% data, 63.34% docs in cluster, avg obj size on shard : 265B
 Shard __unknown_name__-rs2 contains 0.07% data, 0.07% docs in cluster, avg obj size on shard : 265B
 Shard __unknown_name__-rs1 contains 36.64% data, 36.57% docs in cluster, avg obj size on shard : 266B
```
On voit bien que les 3 shards sont utilisé et possèdent tous une partie des donnée.
et avec `sh.status` on peut voir         
```sh
Migration Results for the last 24 hours: 
        4 : Success
```
1. Pour effectuer la répartition des données, on peut changer la clé de sharding par une clé hashed lors de la création de la clé de sharding:

```sh
sh.shardCollection("forum.posts", { _id : "hashed"})
sh.shardCollection("forum.threads", { _id : "hashed"})
sh.shardCollection("forum.users", { _id : "hashed"})
```
De cette manière, dès la création d'une nouvelle données, elle aura moins de chance qu'elle soit mise dans le même shard que les autres.
Et on peut voir après avoir généré quelques données que les shards sont bien mieux réparties:

```sh
> db.getCollection('users').getShardDistribution()
...
Totals
 data : 44KiB docs : 51 chunks : 6
 Shard __unknown_name__-rs1 contains 37% data, 35.29% docs in cluster, avg obj size on shard : 946B
 Shard __unknown_name__-rs0 contains 37.48% data, 41.17% docs in cluster, avg obj size on shard : 822B
 Shard __unknown_name__-rs2 contains 25.51% data, 23.52% docs in cluster, avg obj size on shard : 979B
```

2. Les requêtes principales ne sont plus optimisées, car les requêtes accèdent à plusieurs shards en même temps.

a. Lors d'une requête des posts par user, par exemple avec cette commande:
```js
db.users.find({"nickname":"Deadpool"},{"posts":1}).explain("executionStats")
```
on peut voir que la requête à scanné 2317 documents en ayant fusionné les shard
```json
"executionStats" : {
        "nReturned" : 1,
        "executionTimeMillis" : 2,
        "totalKeysExamined" : 0,
        "totalDocsExamined" : 2317,
        "executionStages" : {
                "stage" : "SHARD_MERGE",
```

b. De même pour trouver tous les posts d'un fil de discussion:
```sh
db.posts.find({"thread._id":"23"}).explain("executionStats")
```
```json
"executionStats" : {
        "nReturned" : 116,
        "executionTimeMillis" : 40,
        "totalKeysExamined" : 0,
        "totalDocsExamined" : 90261,
        "executionStages" : {
                "stage" : "SHARD_MERGE",
```

4. Les requêtes ne sont plus optimisées car la clé de sharding est positionnée sur l'id, donc la requête doit fusionner les shards et faire un scan de collection.

### Sharding(3)

1. Pour faire en sorte que les données soient correctement distribuées sur le cluster et que les requêtes précédentes soient optimisées, on peut par exemple positionner la clé de sharding en "hashed" sur le nickname de l'utilisateur.
```sh
sh.shardCollection("forum.posts", { _id : "hashed"})
sh.shardCollection("forum.threads", { _id : "hashed" })
sh.shardCollection("forum.users", { nickname : 1})
```
Nous n'avons pas eu le temps d'essayer avec une shardKey positionnée sur le thread._id dans la collection posts. Nous pourrions aussi optimiser la requête pour permettre de répartir équitablement.

Dans un contexte réel avec la configuration que nous avons mis en place, les users seraient mieux distribués sur les shards. Mais dû à la génération, beaucoup de users se retrouvent dans le même chunk.

Vis-à-vis des requêtes, elle sont plus optimales pour la requête d'utilisateurs, mais ne le sont pas pour les requêtes pas threads:
```js
db.users.find({"nickname":"Deadpool"},{"posts":1}).explain("executionStats")
```

```json
"executionStats" : {
        "nReturned" : 1,
        "executionTimeMillis" : 0,
        "totalKeysExamined" : 1,
        "totalDocsExamined" : 1,
        "executionStages" : {
                "stage" : "SINGLE_SHARD",
```

## Conclusion

Dans ce tp, nous avons pu aborder les aspects pratiques de mongoDB en passant par de la génération de données, du requêtage, des créations de différents modèles avec leurs avantages et inconvénients, puis nous avons manié des outils puissants de mongoDB comme les replica sets et le sharding, permettant d'accroître la scalabilité horizontale et la haute disponibilité.
