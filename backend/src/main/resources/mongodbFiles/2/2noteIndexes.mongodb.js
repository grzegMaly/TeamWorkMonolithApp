use team-work-test

db.auth("team-work-tester", "password")

db.notes.createIndex(
    {
        "title": "text",
        "content": "text"
    }
)