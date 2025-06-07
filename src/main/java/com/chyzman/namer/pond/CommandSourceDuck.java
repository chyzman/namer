package com.chyzman.namer.pond;

import com.chyzman.namer.impl.NickSuggestionData;

import java.util.List;

public interface CommandSourceDuck {
    default List<NickSuggestionData> namer$getNickSuggestionData() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}
