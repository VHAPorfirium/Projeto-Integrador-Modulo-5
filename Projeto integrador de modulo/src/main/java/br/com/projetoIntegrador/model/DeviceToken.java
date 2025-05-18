package br.com.projetoIntegrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "device_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","token"})
})
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private String userId;

    @Column(nullable=false)
    private String token;

    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    public DeviceToken() {}

    public DeviceToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getCreatedAt() { return createdAt; }
}
