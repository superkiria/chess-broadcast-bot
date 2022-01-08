package com.github.superkiria.cbbot.lichess.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichessRound {

    private String id;
    private String name;
    private String slug;
    private String url;
    private Date startsAt;
    private Boolean ongoing;
    private Boolean finished;

}
