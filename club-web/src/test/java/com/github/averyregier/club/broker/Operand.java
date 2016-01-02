package com.github.averyregier.club.broker;

enum Operand {
    eq("="),
    gt(">"),
    ge(">="),
    lt("<"),
    le("<="),
    ne("is not");

    private String symbol;

    Operand(String symbol) {
        this.symbol = symbol;
    }

    public static Operand lookup(String operator) {
        for(Operand operand: values()) {
            if(operand.symbol.equalsIgnoreCase(operator)) {
                return operand;
            }
        }
        return eq;
    }
}
