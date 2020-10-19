package com.ikiugu.notifications.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * created by alfred.ikiugu on 19-Oct-20
 */

@Data
@Entity
@Table(name = "forex")
public class Forex {
    @Id
    private long id;
    @OneToOne
    private User user;
    private String userToken;
}
