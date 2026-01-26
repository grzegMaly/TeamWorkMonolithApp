package com.mordiniaa.backend.services.task;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.mordiniaa.backend.models.board.TaskCategory;
import com.mordiniaa.backend.models.task.Task;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PositionUpdate {

    private Runnable positionRunnable;
    private final MongoTemplate mongoTemplate;

    public void moveBetweenCategories(TaskCategory prevCategory, TaskCategory nextCategory, int oldPosition, int newPosition) {

        positionRunnable = () -> {
            List<WriteModel<Document>> operations = new ArrayList<>();

            operations.add(
                    new UpdateManyModel<>(
                            Filters.and(
                                    Filters.in("_id", prevCategory.getTasks()),
                                    Filters.gt("positionInCategory", oldPosition)
                            ),
                            Updates.inc("positionInCategory", 1)
                    )
            );

            operations.add(
                    new UpdateManyModel<>(
                            Filters.and(
                                    Filters.in("_id", nextCategory.getTasks()),
                                    Filters.gte("positionInCategory", newPosition)
                            ),
                            Updates.inc("positionInCategory", 1)
                    )
            );

            mongoTemplate
                    .getCollection("tasks")
                    .bulkWrite(operations);
        };
    }

    public void moveUpInCategory(TaskCategory currentCategory, int currentPosition, int nextPosition) {

        positionRunnable = () -> {
            Query positionQuery = Query.query(
                    Criteria.where("_id").in(currentCategory.getTasks())
                            .and("positionInCategory")
                            .gt(currentPosition)
                            .lte(nextPosition)
            );
            Update positionUpdate = new Update()
                    .inc("positionInCategory", -1);
            mongoTemplate.updateMulti(positionQuery, positionUpdate, Task.class);
        };
    }

    public void moveDownInCategory(TaskCategory currentCategory, int currentPosition, int nextPosition) {

        positionRunnable = () -> {
            Query positionQuery = Query.query(
                    Criteria.where("_id").in(currentCategory.getTasks())
                            .and("positionInCategory")
                            .gte(nextPosition)
                            .lt(currentPosition)
            );
            Update positionUpdate = new Update()
                    .inc("positionInCategory", 1);
            mongoTemplate.updateMulti(positionQuery, positionUpdate, Task.class);
        };
    }

    public void update() {
        positionRunnable.run();
    }
}
