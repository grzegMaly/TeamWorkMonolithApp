use team-work-test

db.auth("team-work-tester", "password")

db.users.createIndex({
    userId: 1
})