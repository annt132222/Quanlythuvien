package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.BorrowRecord;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for managing borrow_records table.
 */
public interface BorrowRecordDao extends Dao<BorrowRecord, Integer> {
    /**
     * Find the active (not-yet-returned) borrow record for a given user and book.
     *
     * @param userId the ID of the borrower
     * @param bookId the ID of the book
     * @return an Optional containing the active BorrowRecord if present
     */
    Optional<BorrowRecord> findActiveRecord(String userId, int bookId);

    /**
     * Find all borrow records for a user that have not yet been returned.
     *
     * @param userId the ID of the borrower
     * @return list of active BorrowRecord objects
     */
    List<BorrowRecord> findUnreturnedByUser(String userId);
}
