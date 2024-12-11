package server.repositories;

import hotelapp.models.History;

import java.util.List;

public interface HistoryRepository {
    /**
     * Saves the user history
     * @param history
     * @return boolean based on the rows affected.
     */
    boolean saveHistory(History history);

    /**
     * Get expedia link clicked history
     * @param userName
     * @return List of History obj that user clicked.
     */
    List<History> findUserClickHistory(String userName);

    /**
     * Updates history if user visits the same hotel via expedia link again.
     * @param history
     * @param username
     * @return boolean based on the rows affected.
     */
    boolean updateHistory(History history, String username);

    /**
     * Deletes history for a specific user.
     * @param username
     * @return boolean based on ros affected.
     */
    boolean deleteHistory(String username);
}
