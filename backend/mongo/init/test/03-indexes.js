use team-work-tester
db.auth("team-work-test", "password")

db.notes.createIndex(
    {
        "title": "text",
        "content": "text"
    }
)

db.boards.createIndex({
  "members.userId": 1,
  teamId: 1
})

db.boards.createIndex({
  "owner.userId": 1,
  deleted: 1,
  archived: 1
})

db.tasks.createIndex(
    {
        title: 1
    }
)

db.users.createIndex({
    userId: 1
})

db.image_metadata.createIndex({
    ownerId: 1
})