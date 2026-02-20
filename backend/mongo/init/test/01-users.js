db = db.getSiblingDB("team-work-test")
db.createUser({
    user: "team-work-tester",
    roles: [
        {role: "dbOwner", db: "team-work-test"},
        {role: "dbAdmin", db: "team-work-test"}
    ],
    pwd: "password"
})