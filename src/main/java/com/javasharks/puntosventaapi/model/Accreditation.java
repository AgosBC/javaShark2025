package com.javasharks.puntosventaapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA para persistir Acreditaciones en PostgreSQL.
 */
@Entity
@Table(name = "acreditaciones", indexes = {
    @Index(name = "idx_selling_point_id", columnList = "selling_point_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Accreditation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "selling_point_id", nullable = false)
    private Long sellingPointId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "nm_selling_point", nullable = false, length = 100)
    private String sellingPointName;
    
    public Accreditation() {
    }

    public Accreditation(Long id, BigDecimal amount, Long sellingPointId,
                         LocalDateTime createdAt, String sellingPointName) {
        this.id = id;
        this.amount = amount;
        this.sellingPointId = sellingPointId;
        this.createdAt = createdAt;
        this.sellingPointName = sellingPointName;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getSellingPointId() {
        return sellingPointId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getSellingPointName() {
        return sellingPointName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSellingPointId(Long sellingPointId) {
        this.sellingPointId = sellingPointId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSellingPointName(String sellingPointName) {
        this.sellingPointName = sellingPointName;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accreditation that = (Accreditation) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(sellingPointId, that.sellingPointId) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(sellingPointName, that.sellingPointName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, sellingPointId, createdAt, sellingPointName);
    }

    @Override
    public String toString() {
        return "Acreditacion{" +
               "id=" + id +
               ", amount=" + amount +
               ", sellingPointId=" + sellingPointId +
               ", createdAt=" + createdAt +
               ", sellingPointName='" + sellingPointName + '\'' +
               '}';
    }

    // Patrón Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private Long sellingPointId;
        private LocalDateTime createdAt;
        private String sellingPointName;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder sellingPointId(Long sellingPointId) {
            this.sellingPointId = sellingPointId;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.createdAt = creationDate;
            return this;
        }

        public Builder sellingPointName(String name) {
            this.sellingPointName = name;
            return this;
        }

        public Accreditation build() {
            return new Accreditation(id, amount, sellingPointId, createdAt, sellingPointName);
        }
    }
}
