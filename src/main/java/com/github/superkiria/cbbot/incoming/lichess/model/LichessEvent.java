package com.github.superkiria.cbbot.incoming.lichess.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LichessEvent {

    private LichessTour tour;
    private List<LichessRound> rounds;

}
