db = db.getSiblingDB("team-work")
db.createUser({
    user: "team-work-dev",
    roles: [
        {role: "readWrite", db: "team-work"}
    ],
    pwd: "password"
})