package hotelapp.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import rawsocketserver.HttpResponse;

public class HttpErrorResponse {
    public static void sendErrorResponse(HttpResponse response, int statusCode, String errorField, String message){
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        response.addHeader("Content-Type","application/json");
        responseJson.addProperty("success", Boolean.FALSE);
        response.setStatus(statusCode, String.valueOf(statusCode));
        responseJson.addProperty(errorField, "invalid");
        if(message != null){
        responseJson.addProperty("errorMessage", message);
        }
        response.setBody(gson.toJson(responseJson));
    }
}
