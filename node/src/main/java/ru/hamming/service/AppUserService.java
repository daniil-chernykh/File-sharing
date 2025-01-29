package ru.hamming.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.hamming.entity.AppUser;

public interface AppUserService {
    public AppUser findOrSave(Update update);

    String setEmail(AppUser appUser, String email);

    String registerUser(AppUser appUser);
}
