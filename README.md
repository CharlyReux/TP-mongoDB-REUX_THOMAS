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
Impossible car l'id est unique
### 4. Supprimer un utilisateur grâce à son identifiant
```sh
> db.collection.remove({_id: ObjectId("000123454a461e15c728b3bc")})
WriteResult({ "nRemoved" : 1 })
```

### 5. Suppression de plusieurs utilisateurs en même temps
```sh
> db.collection.remove({"age":20})
```
5.1. Suppression de tout les document en gardant la base
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

Pour avoir plus d'informations sur les collection on peut utiliser les méthodes suivantes:
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

### Explain
### 1. Execution de de la requête explain
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

La méthode explain nous donne (en plus de données générales sur la requête), le nombre d'indexes utilisé.<br> Un index est une structure de données qui stocke une partie des données d'une collection pour améliorer la performance d'accès à ces données.<br>
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

### 4. Plans de requêtes utilisé par mongoDB

Lors de la première requête l'optimizer de mongodb a utilisé comme "winning plan" un SORT car aucun index n'avait été créé. Lors de la seconde requête, l'optimizer a trouvé qu'un index existait et a donc utilisé un FETCH, ce qui améliore les performances.

### 5. Utilisation d'un indexe "Hashed"
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
On peut voir que l'index haché n'a pas été utilisé cette fois ci, l'optimizer à selectionné de refaire un SORT sur les données.

### 7. Intérêt d'un index haché
L'utilisation d'un index haché est moins intéressant dans ce cadre car la requête qu'on utilise à un .sort(). L'optimizer de mongoDB va donc refuser de prendre cet index haché car il ne donne aucun avantage sur la rapidité de la requête par rapport à un sort classique.
