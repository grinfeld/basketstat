package com.mikerusoft.euroleague.entities.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "commands")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Command {
    @Id @Column(name = "id")
    @GeneratedValue(
            strategy= GenerationType.IDENTITY
    )
    private Integer id;
    @Column(name = "command_name")
    private String commandName;
}
