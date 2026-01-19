use team-work-test

db.auth("team-work-tester", "password")

db.boards.createIndexes([
    {
        owner: 1
    },
    {
        boardName: "text"
    }
])