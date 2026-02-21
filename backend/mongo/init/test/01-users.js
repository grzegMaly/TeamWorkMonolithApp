db = db.getSiblingDB("team-work")
db.createUser({
    user: "team-work-tester",
    roles: [
        {role: "dbOwner", db: "team-work"},
        {role: "dbAdmin", db: "team-work"}
    ],
    pwd: "password"
})