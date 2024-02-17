package com.example.aboutme.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AlarmResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAlarmListDTO {
        @JsonProperty("alarms")
        private List<AlarmResponse.GetAlarmDTO> alarmList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAlarmDTO {
        @JsonProperty("content")
        private String content;
        @JsonProperty("profile_serial_number")
        private Integer profileSerialNumber;
        @JsonProperty("space_id")
        private Long spaceId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO {
        private String content;
        private boolean isRead;
        private String subscriberNickname;
    }
}
