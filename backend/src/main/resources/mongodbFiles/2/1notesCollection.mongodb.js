use team-work-test

db.auth("team-work-tester", "password")

db.runCommand({
    create: "notes",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Note Object Validation",
            required: ["title", "ownerId", "content", "archived"],
            properties: {
                _class: {
                    bsonType: "string",
                    enum: ["regular", "deadline"]
                },
                title: {
                    bsonType: "string",
                    description: "Title is required and must be specified",
                    minLength: 3,
                    maxLength: 40
                },
                ownerId: {
                    bsonType: "binData",
                    description: "Owner must be specified"
                },
                content: {
                    bsonType: "string",
                    description: "Description Is Required",
                    maxLength: 512
                },
                archived: {
                    bsonType: "bool",
                    description: "Archived flag Is Required"
                },
                createdAt: {
                    bsonType: "date",
                    description: "Created Date Is Required"
                },
                updatedAt: {
                    bsonType: "date",
                    description: "Modification Date Is Required"
                },
                category: {
                    bsonType: "string",
                    enum: ["SHOPPING", "MEETING", "DIARY", "RECEIPT"]
                },
                status: {
                    bsonType: "string",
                    enum: ["NEW", "COMPLETED", "IN_PROGRESS", "CANCELED"]
                },
                priority: {
                    bsonType: "string",
                    enum: ["HIGH", "MEDIUM", "LOW"]
                },
                deadline: {
                    bsonType: "date",
                    description: "Deadline Is Required"
                }
            },
            oneOf: [
                {
                    properties: {
                        _class: {
                            bsonType: "string",
                            enum: ["regular"]
                        }
                    },
                    required: ["category"]
                },
                {
                    properties: {
                        _class: {
                            bsonType: "string",
                            enum: ["deadline"]
                        }
                    },
                    required: ["priority", "status", "deadline"]
                }
            ]
        }
    }
})