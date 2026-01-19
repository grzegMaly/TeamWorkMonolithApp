use team-work-test

db.auth("team-work-tester", "password")

db.tasks.createIndex(
    {
        title: 1
    }
)