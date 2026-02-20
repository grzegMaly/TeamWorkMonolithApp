db = db.getSiblingDB("team-work-dev")
db.createUser({
    user: "team-work-dev",
    roles: [
        {role: "readWrite", db: "team-work-dev"}
    ],
    pwd: "password"
})