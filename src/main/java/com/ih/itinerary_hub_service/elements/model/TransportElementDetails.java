package com.ih.itinerary_hub_service.elements.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransportElementDetails extends BaseElementDetails {
    private String originPlace;
    private String destinationPlace;
    private LocalDateTime originDateTime;
    private LocalDateTime destinationDateTime;
    private String originProvider;
    private String destinationProvider;

    private TransportElementDetails(Builder builder) {
        super(builder);
        this.originPlace = builder.originPlace;
        this.destinationPlace = builder.destinationPlace;
        this.originDateTime = builder.originDateTime;
        this.destinationDateTime = builder.destinationDateTime;
        this.originProvider = builder.originProvider;
        this.destinationProvider = builder.destinationProvider;
    }

    public static class Builder extends BaseElementDetailsBuilder<Builder> {
        private String originPlace;
        private String destinationPlace;
        private LocalDateTime originDateTime;
        private LocalDateTime destinationDateTime;
        private String originProvider;
        private String destinationProvider;

        public Builder originPlace(String originPlace) {
            this.originPlace = originPlace;
            return this;
        }

        public Builder destinationPlace(String destinationPlace) {
            this.destinationPlace = destinationPlace;
            return this;
        }

        public Builder originDateTime(LocalDateTime originDateTime) {
            this.originDateTime = originDateTime;
            return this;
        }

        public Builder destinationDateTime(LocalDateTime destinationDateTime) {
            this.destinationDateTime = destinationDateTime;
            return this;
        }

        public Builder originProvider(String originProvider) {
            this.originProvider = originProvider;
            return this;
        }

        public Builder destinationProvider(String destinationProvider) {
            this.destinationProvider = destinationProvider;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public TransportElementDetails build() {
            return new TransportElementDetails(this);
        }
    }
}
