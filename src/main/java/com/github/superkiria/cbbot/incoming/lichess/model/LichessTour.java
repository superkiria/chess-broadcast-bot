package com.github.superkiria.cbbot.incoming.lichess.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LichessTour {

        private String description;
        private String id;
        private String name;
        private String slug;
        private String url;
        private String markup;

}
