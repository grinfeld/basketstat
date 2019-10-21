package com.mikerusoft.euroleague.entities.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Match {
    @Id private ObjectId id;
    @Indexed private Date date;
    @Indexed private String season;
    @Indexed private Tournament tournament;

    private CommandMatchStat homeCommand;
    private CommandMatchStat awayCommand;
}
