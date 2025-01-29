package ru.hamming.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hamming.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
