package com.github.superkiria.cbbot.main;

import java.util.Random;

public enum Stickers {

    ELEPHANT("CAACAgIAAxkBAAIMgmHkYIIXK1t9GQKHEWdV-TYXqi6bAALfAAOzkT4WSXxaFUX3xd8jBA"),
    OCTOPUS("CAACAgIAAxkBAAIMhGHkYIljoGS1eZk4lA3B4vJ7x79pAALgAAOzkT4WvHuV0iRdGsEjBA"),
    MOUSE("CAACAgIAAxkBAAIMhmHkYRVTMqs6VvAuwClUyp0KfIgZAALhAAOzkT4WOdGJ4ZP-djAjBA"),
    NOSOROG("CAACAgIAAxkBAAIMh2HkYS_1QhHC8HAU4vmmQHznrNRdAALjAAOzkT4Wa36POquJrRojBA"),
    BLUE_FISH("CAACAgIAAxkBAAIMiGHkYWGJEZbzcImERxAfsvEvW5LDAALlAAOzkT4WHTpyajveNmcjBA"),
    SNAKE("CAACAgIAAxkBAAIMiWHkYYdcszOfasUioZcMICW5zN85AALmAAOzkT4WAjWI2kN29KkjBA"),
    SHEEP("CAACAgIAAxkBAAIM1mHkZOKb-LIibAy7nMQPsP_Mn1gdAALnAAOzkT4WwNMHt98af5EjBA"),
    SHARK("CAACAgIAAxkBAAIM12HkZP7SnkaMdixMhA6DpvkQhsa1AALoAAOzkT4WUn7tFG9g-fsjBA"),
    PIG("CAACAgIAAxkBAAIM2GHkZRWoaNkEzutqhVwf8_3esHrCAALpAAOzkT4WjsPOO55yBl8jBA")

    ;

    private static final Random random = new Random();

    private String id;

    Stickers(String s) {
        this.id = s;
    }


    public static String getRandomStickerId() {
        return Stickers.values()[random.nextInt(Stickers.values().length)].getId();
    }

    public String getId() {
        return id;
    }

}
