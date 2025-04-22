package com.ih.itinerary_hub_service.elements.model;

import com.ih.itinerary_hub_service.elements.types.ElementStatus;
import com.ih.itinerary_hub_service.elements.types.ElementType;
import com.ih.itinerary_hub_service.passengers.responses.PassengerDetails;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Accommodation elements share the same baseElementID and elementID,
 * so the frontend cannot distinguish between two different accommodation elements
 * unless an optional enum is provided.
 *
 * Therefore, BaseElementDetails returns elementID, which—**in the case of accommodation elements**—
 * serves as an event ID that is unique for each element.
 *
 * For other element types, elementID refers to the actual element ID.
* */

@Getter
@Setter
public abstract class BaseElementDetails {
    private UUID baseElementID;
    private UUID elementID;
    private UUID optionID;
    private LocalDateTime lastUpdatedAt;
    private ElementType elementType;
    private String elementCategory;
    private String link;
    private BigDecimal price;
    private String notes;
    private ElementStatus status;
    private Integer order;
    private List<PassengerDetails> passengerDetailsList;

    protected BaseElementDetails(
        BaseElementDetailsBuilder<?> builder
    ) {
        this.baseElementID = builder.baseElementID;
        this.elementID = builder.elementID;
        this.optionID = builder.optionID;
        this.lastUpdatedAt = builder.lastUpdatedAt;
        this.elementType = builder.elementType;
        this.elementCategory = builder.elementCategory;
        this.link = builder.link;
        this.price = builder.price;
        this.notes = builder.notes;
        this.status = builder.status;
        this.order = builder.order;
        this.passengerDetailsList = builder.passengerDetailsList;
    }

    public static abstract class BaseElementDetailsBuilder<T extends BaseElementDetailsBuilder<T>> {
        private UUID baseElementID;
        private UUID elementID;
        private UUID optionID;
        private LocalDateTime lastUpdatedAt;
        private ElementType elementType;
        private String elementCategory;
        private String link;
        private BigDecimal price;
        private String notes;
        private ElementStatus status;
        private Integer order;
        private List<PassengerDetails> passengerDetailsList;

        public T baseElementID(UUID baseElementID) {
            this.baseElementID = baseElementID;
            return self();
        }

        public T elementID(UUID elementID) {
            this.elementID = elementID;
            return self();
        }

        public T optionID(UUID optionID) {
            this.optionID = optionID;
            return self();
        }

        public T lastUpdatedAt(LocalDateTime lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
            return self();
        }

        public T elementCategory(String elementCategory) {
            this.elementCategory = elementCategory;
            return self();
        }

        public T elementType(ElementType elementType) {
            this.elementType = elementType;
            return self();
        }

        public T link(String link) {
            this.link = link;
            return self();
        }

        public T price(BigDecimal price) {
            this.price = price;
            return self();
        }

        public T notes(String notes) {
            this.notes = notes;
            return self();
        }

        public T order(Integer order) {
            this.order = order;
            return self();
        }

        public T status(ElementStatus status) {
            this.status = status;
            return self();
        }

        public T passengerList(List<PassengerDetails> passengerDetailsList) {
            this.passengerDetailsList = passengerDetailsList;
            return self();
        }

        protected abstract T self();
        public abstract BaseElementDetails build();
    }
}
