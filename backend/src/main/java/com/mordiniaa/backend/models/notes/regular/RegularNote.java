package com.mordiniaa.backend.models.notes.regular;


import com.mordiniaa.backend.models.notes.Note;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@TypeAlias("regular")
@Document(collection = "notes")
public class RegularNote extends Note {

    @Field(name = "category", order = 100)
    private Category category;
}
