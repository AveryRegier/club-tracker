package com.github.averyregier.club.broker;

class Condition {
    Condition(String column, Operand operand) {
        this.column = column;
        this.operand = operand;
    }

    String column;
    Operand operand;
}
