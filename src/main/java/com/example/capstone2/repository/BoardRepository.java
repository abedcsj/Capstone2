package com.example.capstone2.repository;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board>findByCategory(Category category);

}
