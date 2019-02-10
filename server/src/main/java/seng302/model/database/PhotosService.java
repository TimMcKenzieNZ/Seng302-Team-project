package seng302.model.database;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import seng302.model.security.AuthenticationTokenStore;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/**
 * Service to add, delete and get a photo
 */
public class PhotosService extends BaseService{

    public PhotosService(AuthenticationTokenStore token) {
        super(token);
        goodPhotoType.add("image/gif");
        goodPhotoType.add("image/png");
        goodPhotoType.add("image/jpeg");
    }

    private final String ADD_PHOTO = "INSERT INTO `Photo`(`photo`, `type`, `username`) VALUES (?, ?, ?)";
    private final String REMOVE_PHOTO = "DELETE FROM `Photo` WHERE username=?";
    private final String GET_PHOTO = "SELECT * FROM `Photo` WHERE username=?";
    public final static ArrayList<String> goodPhotoType = new ArrayList<>();
    public final static int MAX_BYTES = 3000000;

    /**
     * Adds a photo to the database
     * @param connection connection to the database
     * @param photo photo to be stored
     * @param imageType type of image stored
     * @param username username of the person who uses the photo
     * @return Http response
     */
    public ResponseEntity insertPhoto(Connection connection, InputStream photo, String imageType, String username) {
        if(deletePhoto(connection, username)) {
            try(PreparedStatement statement = connection.prepareStatement(ADD_PHOTO)) {
                statement.setBlob(1, photo);
                statement.setString(2, imageType);
                statement.setString(3, username);
                statement.executeUpdate();
                return new ResponseEntity(HttpStatus.ACCEPTED);
            } catch (SQLException e) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes the current photo of a user
     * @param connection connection to the database
     * @param username username of the user
     * @return whether the delete was successful
     */
    public boolean deletePhoto(Connection connection, String username) {
        try(PreparedStatement statement = connection.prepareStatement(REMOVE_PHOTO)) {
            statement.setString(1, username);
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Get the current photo of user
     * @param connection connection to the database
     * @param username username of the owning photo
     * @return Http Resposne with photo
     */
    public ResponseEntity getPhoto(Connection connection, String username) {
        Blob result = null;
        String type = "";
        try(PreparedStatement statement = connection.prepareStatement(GET_PHOTO)) {
            statement.setString(1, username);
            try(ResultSet set = statement.executeQuery()) {
                while(set.next()) {
                    result = set.getBlob("photo");
                    type = set.getString("type");
                }
                HttpHeaders headers = new HttpHeaders();
                if(type.contains("gif"))
                    headers.setContentType(MediaType.IMAGE_GIF);
                else if (type.contains("jpeg") || type.contains("jpg"))
                    headers.setContentType(MediaType.IMAGE_JPEG);
                else {
                    headers.setContentType(MediaType.IMAGE_PNG);
                }
                if (result == null || type.equals(""))
                    return new ResponseEntity(HttpStatus.NOT_FOUND);
                else
                    return new ResponseEntity<byte[]>(result.getBytes(1, (int) result.length()), headers, HttpStatus.OK);
            }
        } catch (SQLException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


}
