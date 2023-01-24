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

## Pour débuter
3. 
```sh
use forum
```
> 2./3. insertion utilisateur
>```sh
>>db.collection.insert({"nickname":"jean","age":20})
>```
4.
```sh
db.collection.find()
> { "_id" : ObjectId("63cfe4054a461e15c728b3bc"), "nickname" : "jean", "age" : 20 }
```
5.
Recherche par le champ age
```sh
db.collection.find({"age":20})
> { "_id" : ObjectId("63cfe540d4286c9a88c98c00"), "nickname" : "jean", "age" : 20 }
```
Recherche par l'ID
```sh
db.collection.find({},{"_id":0})
> { "nickname" : "jean", "age" : 20 }
```
6.
Ajout d'un attribut interest tableau
```sh
db.collection.update({ "nickname" : "jean" }, { $set : { "interests" : ["sucre","poe"] }})
> WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```
Recherche par cet attribut
```sh
db.collection.find({"interests":{$all: ["sucre"]}})
> { "_id" : ObjectId("63cfe4054a461e15c728b3bc"), "nickname" : "jean", "age" : 20, "interests" : [ "sucre", "poe" ] }
```
7. Le champ _id est un champ généré automatiquement par mongodb si non spécifié, il contient un timestamp, une adresse MAC, un Process Identifier, ainsi qu'un counter.
8. Essai de modification de l'ID
```sh
db.collection.update({ "nickname" : "jean" }, { $set : { "_id" : "test" }})
> WriteResult({
        "nMatched" : 0,
        "nUpserted" : 0,
        "nModified" : 0,
        "writeError" : {
                "code" : 66,
                "errmsg" : "Performing an update on the path '_id' would modify the immutable field '_id'"
        }
})
```
9. Méthode pour mettre à jour l'id de l'utilisateur
```sh
doc = db.collection.findOne({_id: ObjectId("63cfe4054a461e15c728b3bc")})

doc._id = ObjectId("000123454a461e15c728b3bc")

db.collection.insert(doc)

db.collection.remove({_id: ObjectId("63cfe4054a461e15c728b3bc")})
```