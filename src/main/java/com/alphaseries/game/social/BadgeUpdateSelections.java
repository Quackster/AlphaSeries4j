package com.alphaseries.game.social;

import com.alphaseries.util.StringUtils;

public record BadgeUpdateSelections(String first, String second, String third, String fourth, String fifth) {
    public BadgeUpdateSelections {
        first = StringUtils.text(first);
        second = StringUtils.text(second);
        third = StringUtils.text(third);
        fourth = StringUtils.text(fourth);
        fifth = StringUtils.text(fifth);
    }

    public int size() {
        return 5;
    }

    public String slot(int index) {
        if (index == 0) {
            return first;
        }
        if (index == 1) {
            return second;
        }
        if (index == 2) {
            return third;
        }
        if (index == 3) {
            return fourth;
        }
        if (index == 4) {
            return fifth;
        }
        return "";
    }
}
