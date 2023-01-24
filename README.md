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
La différence est que deltemany va retourner un booléen alors que remove va retourner object WriteResult.
