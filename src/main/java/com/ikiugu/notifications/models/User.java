package com.ikiugu.notifications.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * created by alfred.ikiugu on 19-Oct-20
 */

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private long id;
    private String userName;
    private String userToken;
}
