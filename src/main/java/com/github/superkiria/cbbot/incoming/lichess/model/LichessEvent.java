package com.github.superkiria.cbbot.incoming.lichess.model;

import lombok.Data;

import java.util.List;

@Data
public class LichessEvent {

    private LichessTour tour;
    private List<LichessRound> rounds;

}
