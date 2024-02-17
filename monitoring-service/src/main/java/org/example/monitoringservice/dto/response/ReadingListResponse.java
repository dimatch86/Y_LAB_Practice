package org.example.monitoringservice.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * Output data model of readings list
 */
@Data
public class ReadingListResponse {

    private List<ReadingResponse> readingResponseList = new ArrayList<>();
}
