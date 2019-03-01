package com.mikerusoft.euroleague.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tournaments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Tournament {
    @Id @Column(name = "id")
    @GeneratedValue(
            strategy= GenerationType.IDENTITY
    )
    private Integer id;
    @Column(name = "tourn_name")
    private String tournName;
}
