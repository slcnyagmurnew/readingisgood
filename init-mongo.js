db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles : [
            {
                role: "readWrite",
                db: "readingisgood"
            }
        ]
    }
);
db = new Mongo().getDB("readingisgood");
db.createCollection("role", {capped: false});
db.role.insertMany(
    [
        {name: "ROLE_USER"},
        {name: "ROLE_ADMIN"},
    ]
);