package com.pkcs.chess.repository;

import com.pkcs.chess.model.GameDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<GameDocument, String> {
    /**
     * Find games by white or black player username with pagination.
     * @param whitePlayerUsername the username of the white player
     * @param blackPlayerUsername the username of the black player
     * @param pageable pagination information
     * @return a page of GameDocument objects.
     */
    Page<GameDocument> findByWhitePlayerUsernameOrBlackPlayerUsername(String whitePlayerUsername, String blackPlayerUsername, Pageable pageable);
}
