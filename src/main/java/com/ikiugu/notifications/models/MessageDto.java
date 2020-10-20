package com.ikiugu.notifications.models;

import lombok.Data;

/**
 * created by alfred.ikiugu on 20-Oct-20
 */

@Data
public class MessageDto extends BaseModel {
    private String senderToken;
    private String recipientUserName;
    private String message;
}
