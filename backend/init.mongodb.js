use team-work

db.createUser({
    user: "team-work-admin",
    roles: [
        {role: "dbAdmin", db: "team-work"},
        {role: "dbOwner", db: "team-work"},
        {role: "userAdmin", db: "team-work"}
    ],
    pwd: "admin"
})

db.auth("team-work-admin", "admin")

use team-work

db.createUser({
    user: "team-work-dev",
    roles: [
        {role: "readWrite", db: "team-work"}
    ],
    pwd: "password"
})