package ru.hamming.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hamming.entity.RawData;

@Repository
public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
