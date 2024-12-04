package server.utils;

public class PreparedStatements {
    /** Prepared Statements  */
    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "hashed_password VARCHAR(256) NOT NULL, " +
                    "salt VARCHAR(256) NOT NULL);";
    /** For creating the hotel table */
    public static final String CREATE_HOTEL_TABLE="""
                     CREATE TABLE IF NOT EXISTS hotels (id varchar(10) primary key,
                     name varchar(255) not null,
                     address varchar(255) not null,
                     city varchar(50) not null,
                     longitude varchar(50) not null,
                     latitude varchar(50) not null,
                     state varchar(50) not null);
                    """;
    /** For creating the review table */
    public static final String CREATE_REVIEW_TABLE= """
                     CREATE TABLE IF NOT EXISTS reviews (
                     id varchar(255) primary key,
                     hotel_id varchar(10),
                     rating decimal(3,1) not null,
                     title text not null,
                     content mediumtext not null,
                     author varchar(255) not null,
                     date date not null,
                     foreign key (hotel_id) references hotels(id) );
            """;
    /** For geting the user details */
    public static final String GET_USER = "SELECT * FROM users WHERE username = ?";
    /** For geting the hotel details */
    public static final String GET_HOTEL = "SELECT * FROM hotels WHERE id = ?";
    /** For geting the review details */
    public static final String GET_REVIEW = "SELECT * FROM reviews WHERE id = ?";
    /** For geting the reviews for hotelid  */
    public static final String GET_REVIEW_HOTELID = "SELECT * FROM reviews WHERE hotel_id = ?";
    /** For inserting the user details */
    public static final String INSERT_USER = "INSERT INTO users (username, hashed_password, salt) VALUES (?, ?, ?)";
}
