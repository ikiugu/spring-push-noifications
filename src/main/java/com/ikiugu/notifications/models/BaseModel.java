package com.ikiugu.notifications.models;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@MappedSuperclass
@Data
public class BaseModel {
    @Id
    @GeneratedValue
    private long id;
    private boolean success;
    private String errorMessage;
}
