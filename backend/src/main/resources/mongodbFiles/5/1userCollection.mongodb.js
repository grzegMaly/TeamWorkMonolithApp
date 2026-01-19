use team-work-test

db.auth("team-work-tester", "password")

db.runCommand({
    create: "users",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "User Schema Validation",
            required: ["userId", "username", "imageUrl", "deleted"],
            additionalProperties: false,
            properties: {
                _id: {
                    bsonType: "objectId"
                },
                _class: {
                    bsonType: "string",
                    enum: ["user"]
                },
                userId: {
                    bsonType: "binData",
                    title: "Id of Referenced User",
                    description: "User Id Is Required"
                },
                username: {
                    bsonType: "string",
                    title: "Name Of The User",
                    description: "Username Is Required",
                    minLength: 3,
                    maxLength: 40
                },
                imageUrl: {
                    bsonType: "string",
                    title: "Profile Image For Specific User",
                    description: "Image Url Is Required"
                },
                deleted: {
                    bsonType: "bool",
                    title: "User Status",
                    description: "Deleted Status Field Is Required"
                }
            }
        }
    }
})