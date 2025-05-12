package com.chyzman.namer.pond;

import com.chyzman.namer.impl.NickSuggestion;

import java.util.List;

public interface CommandSourceDuck {
    default List<NickSuggestion.Data> namer$getNickSuggestionData() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}
