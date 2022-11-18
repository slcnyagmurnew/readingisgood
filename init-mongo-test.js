db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles : [
            {
                role: "readWrite",
                db: "test"
            }
        ]
    }
);
db = new Mongo().getDB("test");
db.createCollection("role", {capped: false});
db.role.insertMany(
    [
        {name: "ROLE_USER"},
        {name: "ROLE_ADMIN"},
    ]
);