package org.ihtsdo.otf.ts.jpa.client;

/**
 * A root implementation to handle methods that do not need to be implemented by clients.
 */
public class RootClientJpa {

  /**
   * Open factory.
   *
   * @throws Exception the exception
   */
  public void openFactory() throws Exception {
    // dummy implementation
  }

  /**
   * Close factory.
   *
   * @throws Exception the exception
   */
  public void closeFactory() throws Exception {
    // dummy implementation
  }

  /**
   * Initialize field names.
   *
   * @throws Exception the exception
   */
  public void initializeFieldNames() throws Exception {
    // dummy implementation
  }

  /**
   * Returns the transaction per operation.
   *
   * @return the transaction per operation
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  public boolean getTransactionPerOperation() throws Exception {
    // dummy implementation
    return false;
  }

  /**
   * Sets the transaction per operation.
   *
   * @param transactionPerOperation the transaction per operation
   * @throws Exception the exception
   */
  public void setTransactionPerOperation(boolean transactionPerOperation)
    throws Exception {
    // dummy implementation
  }

  /**
   * Commit.
   *
   * @throws Exception the exception
   */
  public void commit() throws Exception {
    // dummy implementation
  }

  /**
   * Begin transaction.
   *
   * @throws Exception the exception
   */
  public void beginTransaction() throws Exception {
    // dummy implementation
  }

  /**
   * Close.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception {
    // dummy implementation
  }
}
