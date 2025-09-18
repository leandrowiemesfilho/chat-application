package com.chatapp.websocket.model;

import com.chatapp.websocket.model.enums.MessageDeliveryStatus;

public record DeliveryStatusUpdate(String messageId,
                                   MessageDeliveryStatus status) {
}
