package com.ikiugu.notifications.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * created by alfred.ikiugu on 19-Oct-20
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
public class User extends BaseModel {
    private String userName;
    private String userToken;
    private boolean notifications = true;
}
