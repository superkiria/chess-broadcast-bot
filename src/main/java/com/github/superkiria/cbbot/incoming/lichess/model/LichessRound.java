package com.github.superkiria.cbbot.incoming.lichess.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
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
