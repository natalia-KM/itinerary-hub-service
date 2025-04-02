package com.ih.itinerary_hub_service.elements.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ActivityElementDetails extends BaseElementDetails{
    private String activityName;
    private String location;
    private LocalDateTime startsAt;
    private Integer duration;

    private ActivityElementDetails(Builder builder) {
        super(builder);
        this.activityName = builder.activityName;
        this.location = builder.location;
        this.startsAt = builder.startsAt;
        this.duration = builder.duration;
    }

    public static class Builder extends BaseElementDetailsBuilder<Builder> {
        private String activityName;
        private String location;
        private LocalDateTime startsAt;
        private Integer duration;

        public Builder activityName(String activityName) {
            this.activityName = activityName;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder startsAt(LocalDateTime startsAt) {
            this.startsAt = startsAt;
            return this;
        }

        public Builder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public ActivityElementDetails build() {
            return new ActivityElementDetails(this);
        }
    }
}
