use team-work-test

db.auth("team-work-tester", "password")

db.boards.createIndex({
  "members.userId": 1,
  teamId: 1
})

db.boards.createIndex({
  "owner.userId": 1,
  deleted: 1,
  archived: 1
})