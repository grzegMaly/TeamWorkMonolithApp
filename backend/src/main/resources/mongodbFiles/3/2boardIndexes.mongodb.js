use team-work-test

db.auth("team-work-tester", "password")

db.boards.createIndexes([
{
    _id: 1,
    "owner.userId": 1,
    "members.userId": 1
},
{
  _id: 1,
  "owner.userId": 1,
  "members.userId": 1,
  "taskCategories.tasks": 1
},
{
    _id: 1,
    "owner.userId": 1,
    deleted: 1,
    archived: 1
},
{
  teamId: 1,
  "owner.userId": 1,
  "members.userId": 1
},
{
  _id: 1,
  teamId: 1,
  "owner.userId": 1,
  "members.userId": 1
}
])