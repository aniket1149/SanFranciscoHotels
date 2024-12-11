package server.repositories;

import hotelapp.models.History;

import java.util.List;

public interface HistoryRepository {
    boolean saveHistory(History history);
    List<History> findUserClickHistory(String userName);
    boolean updateHistory(History history, String username);
    boolean deleteHistory(String username);
}
