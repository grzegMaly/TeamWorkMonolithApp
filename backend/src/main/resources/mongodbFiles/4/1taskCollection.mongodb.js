use team-work-test

db.auth("team-work-tester", "password")

db.runCommand({
    create: "tasks",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Tasks Schema Validation",
            additionalProperties: false,
            required: [
                "positionInCategory",
                "title",
                "description",
                "taskStatus",
                "activityElements",
                "createdBy",
                "createdAt",
                "assignedTo"
            ],
            properties: {
                _id: {
                    bsonType: "objectId"
                },
                _class: {
                    bsonType: "string",
                    enum: ["task"]
                },
                positionInCategory: {
                    bsonType: "int",
                    title: "Task Position In Category",
                    minimum: 0,
                    description: "Task Position Is Required And >= 0"
                },
                title: {
                    bsonType: "string",
                    title: "Task Title",
                    minLength: 3,
                    maxLength: 40,
                    description: "Task Title Is Required"
                },
                description: {
                    bsonType: "string",
                    title: "Task Description",
                    description: "Task Description Is Required",
                    maxLength: 1024
                },
                taskStatus: {
                    bsonType: "string",
                    title: "Task Status",
                    description: "Task Status Is Required",
                    enum: ["COMPLETED", "UNCOMPLETED"]
                },
                activityElements: {
                    bsonType: "array",
                    title: "Task Activity Elements",
                    description: "Task Activity Elements Arr Is Required",
                    items: {
                        bsonType: "object",
                        title: "Task Activity Element",
                        additionalProperties: false,
                        required: ["user", "createdAt", "_class"],
                        properties: {
                            _class: {
                                bsonType: "string",
                                enum: ["category_change", "status_change", "comment"],
                            },
                            user: {
                                bsonType: "binData",
                                title: "Id Of Referenced User",
                                description: "User Id Is Required"
                            },
                            createdAt: {
                                bsonType: "date",
                                title: "Created At",
                                description: "Creation Date Is Required"
                            },
                            prevCategory: {
                                bsonType: "string",
                                title: "Previous Category",
                                description: "Previous Category Id Is Required"
                            },
                            nextCategory: {
                                bsonType: "string",
                                title: "Next Category",
                                description: "Next Category Id Is Required"
                            },
                            comment: {
                                bsonType: "string",
                                title: "Task Comment",
                                description: "Task Comment Is Required",
                                minLength: 3,
                                maxLength: 256
                            },
                            updated: {
                                bsonType: "bool",
                                title: "Comment Updated Status",
                                description: "Comment Updated Status Is Required"
                            },
                            commentId: {
                                bsonType: "binData",
                                title: "Id Of Specific Comment",
                                description: "CommentId is Required"
                            },
                            prevStatus: {
                                bsonType: "string",
                                enum: ["COMPLETED", "UNCOMPLETED"]
                            },
                            nextStatus: {
                                bsonType: "string",
                                enum: ["COMPLETED", "UNCOMPLETED"]
                            }
                        },
                        oneOf: [
                            {
                                properties: {
                                    _class: {
                                        bsonType: "string",
                                        enum: ["category_change"]
                                    }
                                },
                                required: ["prevCategory", "nextCategory"]
                            },
                            {
                                properties: {
                                    _class: {
                                        bsonType: "string",
                                        enum: ["comment"]
                                    }
                                },
                                required: ["comment", "updated", "commentId"]
                            },
                            {
                                properties: {
                                    _class: {
                                        bsonType: "string",
                                        enum: ["status_change"]
                                    }
                                },
                                required: ["prevStatus", "nextStatus"]
                            }
                        ]
                    }
                },
                createdBy: {
                    bsonType: "binData",
                    title: "Task Author Id",
                    description: "Author Id Is Required"
                },
                assignedTo: {
                    bsonType: "array",
                    title: "Array Of Assigned User Ids",
                    items: {
                        bsonType: "binData",
                        title: "Assigned User Id"
                    }
                },
                createdAt: {
                    bsonType: "date"
                },
                updatedAt: {
                    bsonType: "date"
                },
                deadline: {
                    bsonType: "date",
                    title: "Optional Deadline"
                }
            }
        }
    }
})