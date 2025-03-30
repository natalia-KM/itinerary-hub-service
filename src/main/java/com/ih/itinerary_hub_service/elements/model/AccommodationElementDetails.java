package com.ih.itinerary_hub_service.elements.model;

import com.ih.itinerary_hub_service.elements.types.AccommodationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccommodationElementDetails extends BaseElementDetails {
    private String place;
    private String location;
    private AccommodationType accommodationType;
    private LocalDateTime dateTime;

    private AccommodationElementDetails(Builder builder) {
        super(builder);
        this.place = builder.place;
        this.location = builder.location;
        this.accommodationType = builder.accommodationType;
        this.dateTime = builder.dateTime;
    }

    public static class Builder extends BaseElementDetailsBuilder<Builder> {
        private String place;
        private String location;
        private AccommodationType accommodationType;
        private LocalDateTime dateTime;

        public Builder place(String place) {
            this.place = place;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder accommodationType(AccommodationType accommodationType) {
            this.accommodationType = accommodationType;
            return this;
        }

        public Builder dateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        @Override
        protected AccommodationElementDetails.Builder self() {
            return this;
        }

        @Override
        public AccommodationElementDetails build() {
            return new AccommodationElementDetails(this);
        }
    }
}
