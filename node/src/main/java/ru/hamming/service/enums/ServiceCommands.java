package ru.hamming.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommands(String value) {
        this.value = value;
    }



    public static ServiceCommands fromValue(String value) {
        for (ServiceCommands commands : ServiceCommands.values()) {
            if (commands.value.equals(value)) {
                return commands;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
