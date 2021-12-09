package com.github.superkiria.lichess.model;

import lombok.Data;

import java.util.List;

@Data
public class LichessEvent {

    private LichessTour tour;
    private List<LichessRound> rounds;

}
