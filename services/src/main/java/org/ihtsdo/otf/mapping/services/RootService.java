package org.ihtsdo.otf.mapping.services;

import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserList;
import org.ihtsdo.otf.mapping.helpers.UserRole;

// TODO: Auto-generated Javadoc
/**
 * The Interface RootService. Manages Factory and lucene field names
 */
public interface RootService {

	/**
	 * Open the factory.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void openFactory() throws Exception;

	/**
	 * Close the factory.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void closeFactory() throws Exception;

	/**
	 * Initialize field names.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void initializeFieldNames() throws Exception;

	/**
	 * Gets the transaction per operation.
	 *
	 * @return the transaction per operation
	 */
	public boolean getTransactionPerOperation() throws Exception;

	/**
	 * Sets the transaction per operation.
	 *
	 * @param transactionPerOperation
	 *            the new transaction per operation
	 */
	public void setTransactionPerOperation(boolean transactionPerOperation)
			throws Exception;

	/**
	 * Commit.
	 */
	public void commit() throws Exception;

	/**
	 * Begin transaction.
	 */
	public void beginTransaction() throws Exception;

	/**
	 * Closes the manager
	 */
	public void close() throws Exception;

	/**
	 * Get User
	 */
	public User getUser(String username) throws Exception;

	// TODO
	public UserList getUsers();

	public User addUser(User user);
	public User removeUser(String id);
	public User updateUser(User user);

	public UserRole getUserRoleForProject(String username, Long projectId);
}