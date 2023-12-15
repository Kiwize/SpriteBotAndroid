package fr.thomas.androiddevforbegginers.model;

public interface IModel {
	
	/**
	 * Insert new record into database
	 * @return
	 */
    boolean insert();
	
	/**
	 * Update record in database if exists
	 * @return
	 */
    boolean save();

}
