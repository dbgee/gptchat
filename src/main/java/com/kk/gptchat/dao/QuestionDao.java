package com.kk.gptchat.dao;

import com.kk.gptchat.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionDao extends JpaRepository<Question,Long> {
    Question findQuestionByUuid(String uuid);
}
