use team-work-test

db.auth("team-work-tester", "password")

use team-work-test

db.auth("team-work-tester", "password")

db.runCommand({
    create: "boards",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Board Specification Schema",
            required: ["owner", "boardName"],
            additionalProperties: false,
            properties: {
                _id: {
                    bsonType: "objectId"
                },
                _class: {
                    bsonType: "string",
                    enum: ["board"]
                },
                owner: {
                    bsonType: "object",
                    title: "Owner Specification",
                    required: ["userId", "boardPermissions", "categoryPermissions", "taskPermissions", "commentPermissions"],
                    properties: {
                        userId: {
                            bsonType: "binData",
                            title: "Owner Id",
                            description: "Owner Id Is Required"
                        },
                        boardPermissions: {
                            bsonType: "array",
                            title: "User Board Permissions",
                            description: "Board Permissions Arr Is Required",
                            items: {
                                bsonType: "string",
                                enum: ["VIEW_BOARD", "EDIT_BOARD_SETTINGS", "DELETE_BOARD", "MANAGE_MEMBERS", "MANAGE_ROLES"]
                            }
                        },
                        categoryPermissions: {
                            bsonType: "array",
                            title: "User Category Permissions",
                            description: "Category Permissions Arr Is Required",
                            items: {
                                bsonType: "string",
                                enum: ["CREATE_CATEGORY", "RENAME_CATEGORY", "DELETE_CATEGORY", "MOVE_TASK_BETWEEN_CATEGORIES"]
                            }
                        },
                        taskPermissions: {
                            bsonType: "array",
                            title: "User Task Permissions",
                            description: "Task Permissions Arr Is Required",
                            items: {
                                bsonType: "string",
                                enum: ["CREATE_TASK", "DELETE_TASK", "EDIT_TASK", "CHANGE_TASK_STATUS", "ASSIGN_TASK", "UNASSIGN_TASK"]
                            }
                        },
                        commentPermissions: {
                            bsonType: "array",
                            title: "User Comment Permissions",
                            description: "Comment Permissions Arr Is Required",
                            items: {
                                bsonType: "string",
                                enum: ["COMMENT_TASK", "EDIT_OWN_COMMENT", "DELETE_OWN_COMMENT", "DELETE_ANY_COMMENT"]
                            }
                        }
                    }
                },
                boardName: {
                    bsonType: "string",
                    title: "Name of the Board",
                    description: "Board Name Is Required"
                },
                taskCategories: {
                    bsonType: "array",
                    title: "Categories Array For Tasks Inside a Board",
                    items: {
                        bsonType: "object",
                        title: "Task Category Definition",
                        required: ["position", "categoryName"],
                        properties: {
                            position: {
                                bsonType: "int",
                                minimum: 0,
                                description: "Position Cannot be less then 0"
                            },
                            categoryName: {
                                bsonType: "string",
                                minLength: 3,
                                maxLength: 40,
                                description: "Category Name Is Required"
                            },
                            tasks: {
                                bsonType: "array",
                                title: "Tasks Ids",
                                description: "Ids for referenced tasks",
                                items: {
                                    bsonType: "objectId",
                                    description: "Referenced Tasks"
                                }
                            }
                        }
                    }
                },
                members: {
                    bsonType: "array",
                    title: "Board Members",
                    description: "Members Referencing To Users Collection",
                    items: {
                        bsonType: "object",
                        title: "Member Specification",
                        required: ["userId", "boardPermissions", "categoryPermissions", "taskPermissions", "commentPermissions"],
                        properties: {
                            userId: {
                                bsonType: "binData",
                                title: "Member Id",
                                description: "Member Id Is Required"
                            },
                            boardPermissions: {
                                bsonType: "array",
                                title: "User Board Permissions",
                                description: "Board Permissions Arr Is Required",
                                items: {
                                    bsonType: "string",
                                    enum: ["VIEW_BOARD", "EDIT_BOARD_SETTINGS", "DELETE_BOARD", "MANAGE_MEMBERS", "MANAGE_ROLES"]
                                }
                            },
                            categoryPermissions: {
                                bsonType: "array",
                                title: "User Category Permissions",
                                description: "Category Permissions Arr Is Required",
                                items: {
                                    bsonType: "string",
                                    enum: ["CREATE_CATEGORY", "RENAME_CATEGORY", "DELETE_CATEGORY", "MOVE_TASK_BETWEEN_CATEGORIES"]
                                }
                            },
                            taskPermissions: {
                                bsonType: "array",
                                title: "User Task Permissions",
                                description: "Task Permissions Arr Is Required",
                                items: {
                                    bsonType: "string",
                                    enum: ["CREATE_TASK", "DELETE_TASK", "EDIT_TASK", "CHANGE_TASK_STATUS", "ASSIGN_TASK", "UNASSIGN_TASK"]
                                }
                            },
                            commentPermissions: {
                                bsonType: "array",
                                title: "User Comment Permissions",
                                description: "Comment Permissions Arr Is Required",
                                items: {
                                    bsonType: "string",
                                    enum: ["COMMENT_TASK", "EDIT_OWN_COMMENT", "DELETE_OWN_COMMENT", "DELETE_ANY_COMMENT"]
                                }
                            }
                        }
                    }
                },
                createdAt: {
                    bsonType: "date"
                }
            }
        }
    }
})