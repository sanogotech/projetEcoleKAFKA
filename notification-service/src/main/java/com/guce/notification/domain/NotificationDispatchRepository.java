package com.guce.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationDispatchRepository extends JpaRepository<NotificationDispatch, UUID> {
}
