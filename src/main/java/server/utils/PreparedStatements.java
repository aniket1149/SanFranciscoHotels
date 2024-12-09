package server.utils;
/** Prepared Statements All Static and Final */
public class PreparedStatements {

    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "hashed_password VARCHAR(255) NOT NULL, " +
                    "salt VARCHAR(255) NOT NULL," +
                    "last_login datetime);";
    /** For creating the hotel table */
    public static final String CREATE_HOTEL_TABLE="""
                     CREATE TABLE IF NOT EXISTS hotels (
                     id varchar(50) primary key,
                     name varchar(255) not null,
                     address varchar(255) not null,
                     city varchar(50) not null,
                     longitude varchar(128) not null,
                     latitude varchar(128) not null,
                     state varchar(50) not null,
                     link varchar(255) not null);
                    """;
    /** For creating the review table */
    public static final String CREATE_REVIEW_TABLE= """
                     CREATE TABLE IF NOT EXISTS reviews (
                     id varchar(255) primary key,
                     hotel_id varchar(50),
                     rating decimal(2,1) not null,
                     title text,
                     reviewText mediumtext not null,
                     user varchar(255) not null,
                     date date not null,
                     likes INT NOT NULL DEFAULT 0,
                     foreign key (hotel_id) references hotels(id));
            """;
    public static final String CREATE_REVIEW_LIKES = """
            CREATE TABLE IF NOT EXISTS review_likes (
                review_id VARCHAR(255),
                username VARCHAR(255),
                PRIMARY KEY (review_id, username),
                FOREIGN KEY (review_id) REFERENCES reviews(id)
            );
            """;
    public static final String CREATE_HISTORY_TABLE= """
            CREATE TABLE IF NOT EXISTS history (
            hotel_id varchar(50),
            user_name varchar(50),
            time datetime default CURRENT_TIMESTAMP,
            primary key (hotel_id, user_name),
            foreign key (hotel_id) references hotels(id));
            """;
    /** For geting the user details */
    public static final String GET_USER = "SELECT * FROM users WHERE username = ?";
    /** For geting the hotel details */
    public static final String GET_HOTEL = "SELECT * FROM hotels WHERE id = ?";
    public static final String GET_HOTEL_BYNAME = "SELECT * FROM hotels WHERE name like ?";
    public static final String GET_HOTELS = "SELECT * FROM hotels";
    public static final String GET_USER_HISTORY = """
            SELECT h.name, h.link, his.*
            FROM history his
            INNER JOIN hotels h
            ON h.id = his.hotel_id
            WHERE his.user_name = ?
            ORDER BY his.time DESC;
            """;
    /** For geting the review details */
    public static final String GET_REVIEW = "SELECT * FROM reviews WHERE id = ?";
    /** For geting the reviews for hotelid  */
    public static final String GET_REVIEW_HOTELID = "SELECT * FROM reviews WHERE hotel_id = ?";
    /** For inserting the user details */
    public static final String INSERT_USER = "INSERT INTO users (username, hashed_password, salt) VALUES (?, ?, ?)";
    public static final String INSERT_USER = "INSERT ignore INTO users (username, hashed_password, salt) VALUES (?, ?, ?)";
    public static final String INSERT_HOTEL = "INSERT ignore INTO hotels(id, name, address, city, longitude, latitude, state, link) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_REVIEW = "INSERT ignore INTO reviews(id, hotel_id, rating, title, reviewText, user, date, likes ) values (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String INSERT_HISTORY = "INSERT ignore INTO history (hotel_id, user_name, time) VALUES (?, ?, ?)";
    public static final String UPDATE_HISTORY = "UPDATE history SET time = ? WHERE hotel_id = ? and user_name = ?";
    public static final String UPDATE_LAST_LOGIN = """
            UPDATE users
            SET last_login = ?
            WHERE username = ?;
            """;
}
