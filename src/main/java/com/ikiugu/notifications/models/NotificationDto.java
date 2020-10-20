package com.ikiugu.notifications.models;

import lombok.Data;

import java.util.Map;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@Data
public class NotificationDto {
    private String subject;
    private String content;
    private String token;
    private Map<String, String> data;

}
